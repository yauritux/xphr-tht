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