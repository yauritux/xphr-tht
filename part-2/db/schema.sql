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

DROP INDEX IF EXISTS idx_time_records_time_from;
CREATE INDEX idx_time_records_time_from ON time_records USING BRIN (time_from);
DROP INDEX IF EXISTS idx_time_records_time_to;
CREATE INDEX idx_time_records_time_to ON time_records USING BRIN (time_to);
DROP INDEX IF EXISTS idx_time_records_employee_id;
CREATE INDEX idx_time_records_employee_id ON time_records USING HASH (employee_id);
DROP INDEX IF EXISTS idx_time_records_project_id;
CREATE INDEX idx_time_records_project_id ON time_records USING HASH (project_id);