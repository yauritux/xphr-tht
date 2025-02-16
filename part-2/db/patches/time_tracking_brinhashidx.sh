#!/bin/bash

echo "DROP INDEX IF EXISTS idx_time_records_time_from;" | docker container exec -i xphr-db psql -h localhost -U postgres -d xphr
echo "CREATE INDEX idx_time_records_time_from ON time_records USING BRIN (time_from);" | docker container exec -i xphr-db psql -h localhost -U postgres -d xphr
echo "DROP INDEX IF EXISTS idx_time_records_employee_id;" | docker container exec -i xphr-db psql -h localhost -U postgres -d xphr
echo "CREATE INDEX idx_time_records_employee_id ON time_records USING HASH (employee_id);" | docker container exec -i xphr-db psql -h localhost -U postgres -d xphr
echo "DROP INDEX IF EXISTS idx_time_records_project_id;" | docker container exec -i xphr-db psql -h localhost -U postgres -d xphr
echo "CREATE INDEX idx_time_records_project_id ON time_records USING HASH (project_id);" | docker container exec -i xphr-db psql -h localhost -U postgres -d xphr
