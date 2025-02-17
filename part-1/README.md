# Part 1: Database Query Optimization

## Background

The application tracks employee work hours in a PostgreSQL database. In addition to time tracking records, 
there are separate tables for employees and projects. 
This setup allows you to demonstrate how you handle RDBMS relationships.

## Provided Schema & Sample Data

```postgresql
-- Schema (schema.sql)
DROP TABLE IF EXISTS projects;
CREATE TABLE projects (
    id INT8 PRIMARY KEY,
    name VARCHAR(200)
);

DROP TABLE IF EXISTS employees;
CREATE TABLE employees (
    id INT8 PRIMARY KEY,
    name VARCHAR(60)
);

DROP TABLE IF EXISTS time_records;
CREATE TABLE time_records (
    id INT8 PRIMARY KEY,
    employee_id INT NOT NULL,
    project_id INT NOT NULL,
    time_from timestamp NOT NULL,
    time_to timestamp NOT NULL
);

-- Sample Data (sample_data.sql)
INSERT INTO employees(id, name) VALUES (101, 'Tom'), (102, 'Jerry');

INSERT INTO projects(id, name) VALUES (1, 'Sample Project A'), (2, 'Sample Project B');

INSERT INTO time_records(id, employee_id, project_id, time_from, time_to) 
VALUES (1, 101, 1, '2024-02-01 08:00:00', '2024-02-01 17:00:00'),
       (2, 102, 2, '2024-02-01 09:00:00', '2024-02-01 18:30:00'),
       (3, 101, 1, '2024-02-02 08:15:00', '2024-02-02 17:10:00');
```

## Setup Instructions

### Prerequisites

1. Docker
2. Docker compose

### Steps

1. Clone the repository.
   ```shell
   git clone https://github.com/yauritux/xphr-tht.git
   ```
2. Go to the project root directory.
   ```shell
   cd xphr-tht/part-1
   ```
3. Rename .env.example to .env and adjust the value as necessary.
   ```shell
   mv -v .env.example .env
   ```
4. Run docker compose.
   ```shell
   docker compose up -d
   ```

## Current Inefficient Query (Intentionally Made Worse)

Re-write the SQL command using inefficient constructs (such as multiple subqueries) so that it is non-performant.
For example:

```postgresql
SELECT
    t.employee_id,
    (SELECT name FROM employees e WHERE e.id = t.employee_id) AS employee_name,
    (SELECT name FROM projects p WHERE p.id = t.project_id) AS project_name,
    SUM(EXTRACT(EPOCH FROM (t.time_to - t.time_from)) / 3600) AS total_hours
FROM time_records t
WHERE t.time_from >= NOW() - INTERVAL '1 month'
GROUP BY 
    t.employee_id,
    (SELECT name FROM employees e WHERE e.id = t.employee_id),
    (SELECT name FROM projects p WHERE p.id = t.project_id)
ORDER BY
    (SELECT name FROM employees e WHERE e.id = t.employee_id),
    (SELECT name FROM projects p WHERE p.id = t.project_id);
```

## Your Tasks

1. *Analyze the Query Performance:*
    - Run `EXPLAIN ANALYZE` on the query.
    - Identify bottlenecks and provide a brief explanation of the issues you discover.
2. *Optimize the Query:*
    - Suggest and implement appropriate indexing strategies.
    - Propose improvements such as materialized views, partitioning, or caching where applicable.
    - Provide a revised version of the query along with your rationale.

# Solutions

***I intentionally made the solutions presented in the format of RCA (Root-Cause-Analysis), 
since this is something that I normally do in a situation where an issue is reported to me and I 
need to provide the solution while also making all steps clear for everyone to understand through analysis process 
to find the root cause, and what changes need to be made to solve the issue.***

***Check [SOLUTIONS.md](./SOLUTIONS.md) file for more details.***