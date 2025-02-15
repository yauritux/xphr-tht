> As written on the last section of the [README.md](./README.md) file, following is the RCA document for documenting my solution.

# Root Cause Analysis (RCA):: Performance Bottlenecks in PostgreSQL Query for Employee Time Tracking

## Problem Statement

We have a query for aggregating employee work hours over the past month that apparently takes a long time to execute, 
hence we need to optimize it for better performance. 
Despite that the execution time might be acceptable in the case of small dataset, but looking at the query design.. 
it will introduce scalability issues that will degrade performance as the dataset grows.

## Root Cause Analysis

**Based on the current data we have in our current database**, 
following is the sample of query that can help us to assess in order to figure out how to optimize:
```postgresql
SELECT
    t.employee_id,
    (SELECT name FROM employees e WHERE e.id = t.employee_id) AS employee_name,
    (SELECT name FROM projects p WHERE p.id = t.project_id) AS project_name,
    SUM(EXTRACT(EPOCH FROM (t.time_to - t.time_from)) / 3600) AS total_hours
FROM time_records t
WHERE concat(extract('month' from t.time_from), extract('year' from t.time_from))::int = (concat(extract('month' from now()), extract('year' from now()))::int - 1)
GROUP BY
    t.employee_id,
    (SELECT name FROM employees e WHERE e.id = t.employee_id),
    (SELECT name FROM projects p WHERE p.id = t.project_id)
ORDER BY
    (SELECT name FROM employees e WHERE e.id = t.employee_id),
    (SELECT name FROM projects p WHERE p.id = t.project_id);
```

Running the `EXPLAIN ANALYZE` on the query, will give us the following output:
```shell
GroupAggregate  (cost=185.59..300.11 rows=7 width=592) (actual time=0.080..0.084 rows=2 loops=1)
  Group Key: ((SubPlan 1)), ((SubPlan 2)), t.employee_id
  ->  Sort  (cost=185.59..185.61 rows=7 width=576) (actual time=0.074..0.074 rows=3 loops=1)
        Sort Key: ((SubPlan 1)), ((SubPlan 2)), t.employee_id
        Sort Method: quicksort  Memory: 25kB
        ->  Seq Scan on time_records t  (cost=0.00..185.49 rows=7 width=576) (actual time=0.061..0.066 rows=3 loops=1)
              Filter: ((concat(EXTRACT(month FROM time_from), EXTRACT(year FROM time_from)))::integer = ((concat(EXTRACT(month FROM now()), EXTRACT(year FROM now())))::integer - 1))
              SubPlan 1
                ->  Index Scan using employees_pkey on employees e  (cost=0.15..8.17 rows=1 width=138) (actual time=0.005..0.006 rows=1 loops=3)
                      Index Cond: (id = t.employee_id)
              SubPlan 2
                ->  Index Scan using projects_pkey on projects p  (cost=0.14..8.16 rows=1 width=418) (actual time=0.007..0.007 rows=1 loops=3)
                      Index Cond: (id = t.project_id)
Planning Time: 0.239 ms
Execution Time: 0.114 ms
```

The execution time is acceptable, i.e., 0.114 ms, but from the output we can see several things:
1. Repeated Subqueries:
   - The first subquery `(SELECT name FROM employees e WHERE e.id = t.employee_id)` is executed 3 times.
   - The second subquery `(SELECT name FROM projects p WHERE p.id = t.project_id)` is executed 3 times.
   This might be our best-case scenario for now since we only have 3 rows in the `time_records` table. 
   However, the execution of these two subqueries might increase once the data grows or filter conditions are changed (worst-case scenario), which will increase the execution time and also increasing computational overhead.
2. Lack of Indexing:
   - The absence of an index on `time_records.time_from` forces PostgreSQL to perform a sequential scan when filtering rows based on the `time_from` column.
   - The absence of missing indexes on `time_records.employee_id` and `time_records.project_id` make joins less efficient.
3. Inefficient Grouping and Ordering:
   - Grouping and ordering the aforementioned two subqueries forces PostgreSQL to repeatedly evaulate these subqueries both during aggregation and sorting.
4. Sequential Scan on `time_records` table:
   - The query performs a sequential scan on the `time_records` table which is inefficient for large datasets due to the missing index.

### Impacts

1. Performance Degradation: 
   - The execution time of the query might increase as the dataset grows. Potentially causing delays in reporting or application responsiveness.
2. Scalability Issues:
   - The current query design is not scalable and will struggle to handle larger datasets efficiently.
3. Resource Utilization:
   - Repeated subqueries and sequential scans increase CPU and memory usages which might impact system performance and lead to service unavailability.

## Solution Approach

To address the identified root causes, following are the proposed solutions:
1. Add Indexes:
   - Create index on `time_records.time_from` to improve filtering performance.
   ```postgresql
   CREATE INDEX idx_time_records_time_from ON time_records(time_from);
   ```
   - Create indexes on `time_records.employee_id` and `time_records.project_id` to optimize joins.
   ```postgresql
   CREATE INDEX idx_time_records_employee_id ON time_records(employee_id);
   CREATE INDEX idx_time_records_project_id ON time_records(project_id);
   ```
2. Replace All Subqueries with Joins:
   ```postgresql
   SELECT t.employee_id, e.name AS employee_name, p.name AS project_name,
      SUM(EXTRACT(EPOCH FROM (t.time_to - t.time_from)) / 3600) AS total_hours
   FROM time_records t
      JOIN employees e ON t.employee_id = e.id
      JOIN projects p ON t.project_id = p.id
   WHERE concat(extract('month' from t.time_from), extract('year' from t.time_from))::int = (concat(extract('month' from now()), extract('year' from now()))::int - 1)
   GROUP BY t.employee_id, e.name, p.name
   ORDER BY e.name, p.name;   
   ```
   
Running the `EXPLAIN ANALYZE` against the query on this proposed solutions will give us the following results:
```shell
GroupAggregate  (cost=17.60..17.64 rows=1 width=592) (actual time=0.047..0.050 rows=2 loops=1)
  Group Key: e.name, p.name, t.employee_id
  ->  Sort  (cost=17.60..17.61 rows=1 width=576) (actual time=0.040..0.040 rows=3 loops=1)
        Sort Key: e.name, p.name, t.employee_id
        Sort Method: quicksort  Memory: 25kB
        ->  Nested Loop  (cost=0.29..17.59 rows=1 width=576) (actual time=0.025..0.030 rows=3 loops=1)
              ->  Nested Loop  (cost=0.15..9.33 rows=1 width=162) (actual time=0.022..0.025 rows=3 loops=1)
                    ->  Seq Scan on time_records t  (cost=0.00..1.14 rows=1 width=24) (actual time=0.010..0.012 rows=3 loops=1)
                          Filter: ((concat(EXTRACT(month FROM time_from), EXTRACT(year FROM time_from)))::integer = ((concat(EXTRACT(month FROM now()), EXTRACT(year FROM now())))::integer - 1))
                    ->  Index Scan using employees_pkey on employees e  (cost=0.15..8.17 rows=1 width=146) (actual time=0.004..0.004 rows=1 loops=3)
                          Index Cond: (id = t.employee_id)
              ->  Index Scan using projects_pkey on projects p  (cost=0.14..8.16 rows=1 width=426) (actual time=0.001..0.001 rows=1 loops=3)
                    Index Cond: (id = t.project_id)
Planning Time: 0.169 ms
Execution Time: 0.076 ms
```
that proves that the new query is much more efficient and faster than the original query, especially after we add the necessary indexes.




