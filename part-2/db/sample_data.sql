INSERT INTO employees(id, name) VALUES (101, 'Yauri'), (102, 'Jacky');

INSERT INTO projects(id, name) VALUES (1, 'AI-based Firewall Next Generation'), (2, 'AI Self Driving Car');

INSERT INTO time_records(id, employee_id, project_id, time_from, time_to)
VALUES (1, 101, 1, '2025-02-01 08:00:00', '2025-02-01 17:00:00'),
       (2, 102, 2, '2025-02-01 09:00:00', '2025-02-01 18:30:00'),
       (3, 101, 1, '2025-02-02 08:15:00', '2025-02-02 17:10:00'),
       (4, 102, 2, '2025-02-02 08:30:00', '2025-02-02 18:00:00'),
       (5, 101, 1, '2025-02-03 08:45:00', '2025-02-03 17:35:00'),
       (6, 102, 2, '2025-02-03 08:15:00', '2025-02-03 18:30:00'),
       (7, 101, 1, '2025-02-04 08:30:00', '2025-02-04 17:30:00'),
       (8, 102, 2, '2025-02-04 07:30:00', '2025-02-04 17:15:00'),
       (9, 101, 1, '2025-02-05 08:55:00', '2025-02-05 18:05:00'),
       (10, 102, 2, '2025-02-05 09:15:00', '2025-02-05 18:25:00'),
       (11, 101, 1, '2025-02-06 08:05:00', '2025-02-06 17:59:00'),
       (12, 102, 2, '2025-02-06 08:10:00', '2025-02-06 17:15:00'),
       (13, 101, 1, '2025-02-07 07:35:00', '2025-02-07 17:25:00'),
       (14, 102, 2, '2025-02-07 07:45:00', '2025-02-07 17:35:00'),
       (15, 101, 1, '2025-02-08 07:55:00', '2025-02-08 17:55:00'),
       (16, 102, 2, '2025-02-08 08:25:00', '2025-02-08 17:57:00'),
       (17, 101, 1, '2025-02-09 08:05:00', '2025-02-09 18:05:00'),
       (18, 102, 2, '2025-02-09 08:15:00', '2025-02-09 17:55:00'),
       (19, 101, 1, '2025-02-10 07:35:00', '2025-02-10 18:15:00');