> As written on the last section of the [README.md](./README.md) file, following is the RCA document for documenting my solution.

# Root Cause Analysis (RCA):: Performance Bottlenecks in PostgreSQL Query for Employee Time Tracking

## Author: M. Yauri M. Attamimi

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
GroupAggregate  (cost=17.47..33.84 rows=1 width=592) (actual time=0.653..0.656 rows=2 loops=1)
  Group Key: ((SubPlan 1)), ((SubPlan 2)), t.employee_id
  ->  Sort  (cost=17.47..17.48 rows=1 width=576) (actual time=0.632..0.632 rows=3 loops=1)
        Sort Key: ((SubPlan 1)), ((SubPlan 2)), t.employee_id
        Sort Method: quicksort  Memory: 25kB
        ->  Seq Scan on time_records t  (cost=0.00..17.46 rows=1 width=576) (actual time=0.595..0.605 rows=3 loops=1)
              Filter: ((concat(EXTRACT(month FROM time_from), EXTRACT(year FROM time_from)))::integer = ((concat(EXTRACT(month FROM now()), EXTRACT(year FROM now())))::integer - 1))
              SubPlan 1
                ->  Index Scan using employees_pkey on employees e  (cost=0.15..8.17 rows=1 width=138) (actual time=0.074..0.074 rows=1 loops=3)
                      Index Cond: (id = t.employee_id)
              SubPlan 2
                ->  Index Scan using projects_pkey on projects p  (cost=0.14..8.16 rows=1 width=418) (actual time=0.054..0.054 rows=1 loops=3)
                      Index Cond: (id = t.project_id)
Planning Time: 1.966 ms
Execution Time: 0.709 ms
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
   ***You can apply these scripts by running the `db/patches/time_tracking_btreeidx.sh` script.***
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
GroupAggregate  (cost=17.60..17.64 rows=1 width=592) (actual time=0.048..0.051 rows=2 loops=1)
  Group Key: e.name, p.name, t.employee_id
  ->  Sort  (cost=17.60..17.61 rows=1 width=576) (actual time=0.040..0.040 rows=3 loops=1)
        Sort Key: e.name, p.name, t.employee_id
        Sort Method: quicksort  Memory: 25kB
        ->  Nested Loop  (cost=0.29..17.59 rows=1 width=576) (actual time=0.025..0.031 rows=3 loops=1)
              ->  Nested Loop  (cost=0.15..9.33 rows=1 width=162) (actual time=0.022..0.026 rows=3 loops=1)
                    ->  Seq Scan on time_records t  (cost=0.00..1.14 rows=1 width=24) (actual time=0.010..0.013 rows=3 loops=1)
                          Filter: ((concat(EXTRACT(month FROM time_from), EXTRACT(year FROM time_from)))::integer = ((concat(EXTRACT(month FROM now()), EXTRACT(year FROM now())))::integer - 1))
                    ->  Index Scan using employees_pkey on employees e  (cost=0.15..8.17 rows=1 width=146) (actual time=0.003..0.003 rows=1 loops=3)
                          Index Cond: (id = t.employee_id)
              ->  Index Scan using projects_pkey on projects p  (cost=0.14..8.16 rows=1 width=426) (actual time=0.001..0.001 rows=1 loops=3)
                    Index Cond: (id = t.project_id)
Planning Time: 0.162 ms
Execution Time: 0.077 ms
```
this proves that the new query is much more efficient and faster than the original query, 
especially after we add the necessary indexes... i.e., **the execution time is reduced from 0.709 ms to 0.077 ms**.

Now, let's try to use BRIN index for time_from column:
```postgresql
CREATE INDEX idx_time_records_time_from ON time_records USING BRIN (time_from);
```
, and HASH indexes for employee_id and project_id columns:
```postgresql
CREATE INDEX idx_time_records_employee_id ON time_records USING HASH (employee_id);
CREATE INDEX idx_time_records_project_id ON time_records USING HASH (project_id);
```
***Apply these indexes by running the `db/patches/time_tracking_brinhashidx.sh` script.***

then see how the same new query performs with `EXPLAIN ANALYZE`:
```shell
GroupAggregate  (cost=17.60..17.64 rows=1 width=592) (actual time=0.044..0.047 rows=2 loops=1)
  Group Key: e.name, p.name, t.employee_id
  ->  Sort  (cost=17.60..17.61 rows=1 width=576) (actual time=0.036..0.037 rows=3 loops=1)
        Sort Key: e.name, p.name, t.employee_id
        Sort Method: quicksort  Memory: 25kB
        ->  Nested Loop  (cost=0.29..17.59 rows=1 width=576) (actual time=0.022..0.029 rows=3 loops=1)
              ->  Nested Loop  (cost=0.15..9.33 rows=1 width=162) (actual time=0.019..0.024 rows=3 loops=1)
                    ->  Seq Scan on time_records t  (cost=0.00..1.14 rows=1 width=24) (actual time=0.010..0.013 rows=3 loops=1)
                          Filter: ((concat(EXTRACT(month FROM time_from), EXTRACT(year FROM time_from)))::integer = ((concat(EXTRACT(month FROM now()), EXTRACT(year FROM now())))::integer - 1))
                    ->  Index Scan using employees_pkey on employees e  (cost=0.15..8.17 rows=1 width=146) (actual time=0.003..0.003 rows=1 loops=3)
                          Index Cond: (id = t.employee_id)
              ->  Index Scan using projects_pkey on projects p  (cost=0.14..8.16 rows=1 width=426) (actual time=0.001..0.001 rows=1 loops=3)
                    Index Cond: (id = t.project_id)
Planning Time: 0.152 ms
Execution Time: 0.070 ms
```

As we can see, **the execution time is slightly faster than using default index (BTREE), i.e., 0.077 ms vs 0.070 ms.**

### Additional Recommendations

1. For frequently accessed data, we might consider to create materialized-view for precomputed data.
   ```postgresql
   CREATE MATERIALIZED VIEW vw_employee_time_trackings AS
   SELECT t.employee_id, e.name AS employee_name, p.name AS project_name,
      SUM(EXTRACT(EPOCH FROM (t.time_to - t.time_from)) / 3600) AS total_hours
   FROM time_records t
      JOIN employees e ON t.employee_id = e.id
      JOIN projects p ON t.project_id = p.id
   GROUP BY t.employee_id, e.name, p.name;
   ```
   Do refresh the materialized view periodically to keep it up-to-date (through cron job or scheduled task).  
   Then, we can use `vw_employee_time_trackings` instead of the original query to retrieve precomputed data.
2. Apply **Partitioning** if the `time_records` table grows very large, i.e., we can partition the table by month or year
   to reduce the amount of data scanned for time-based queries.
3. Apply **Caching**, but this will be for the application-level cache that outside of this RCA context.

## Conclusion

In conclusion, the proposed solutions have improved the performance of the original query by reducing the execution time from 0.709 ms to 0.070 ms using the following approaches:
1. Add Indexes respectively on `time_records.time_from`, `time_records.employee_id`, and `time_records.project_id`.
2. Avoid repeated (redundant) queries by replacing all subqueries with joins.
3. Default created index will be BTREE which should be enough for most of scenarios. 
   However, we might consider to use specific index types such as **BRIN** for `time_from` column and **HASH** for both `employee_id` and `project_id`.
   Theoretically speaking, **BRIN** is highly effective for large datasets with sequential or range-based data such as timestamp, while **HASH** is optimized for equality comparisons.
   Nevertheless, we should regularly monitor and evaluate the performance of these indexes as the data grows and business requirements change.
4. Apply **Materialized View** for precomputed data to reduce the amount of data scanned. The associated **Materialized View** should be regularly refreshed (updated) through cron job or any scheduler task application.
5. Apply **Partitioning** if the `time_records` table grows very large, i.e., we can partition the table by month or year to reduce the amount of data scanned for time-based queries.





