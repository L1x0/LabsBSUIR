-- 32

WITH parts AS (
    SELECT DISTINCT part_id
    FROM spj
    WHERE supplier_id = 'П1'
)
SELECT project_id
FROM spj
WHERE supplier_id = 'П1'
GROUP BY project_id
HAVING COUNT(DISTINCT part_id) = (SELECT COUNT(*) FROM parts);

---------------------------

-- 11

SELECT suppliers.city AS first_city, projects.city AS second_city 
FROM spj 
JOIN suppliers ON spj.supplier_id = suppliers.supplier_id 
JOIN projects ON spj.project_id = projects.project_id;

----------------------------

-- 19

SELECT DISTINCT projects.name 
FROM spj 
JOIN projects ON spj.project_id = projects.project_id
WHERE spj.supplier_id = 'П1';


--------------------------

-- 14

SELECT DISTINCT a.supplier_id,
       a.part_id AS part_a,
       b.part_id AS part_b
FROM spj a
CROSS JOIN spj b
WHERE a.supplier_id = b.supplier_id
  AND a.part_id < b.part_id   
ORDER BY a.supplier_id, a.part_id;



SELECT DISTINCT a.supplier_id,
       a.part_id AS part_a,
       b.part_id AS part_b
FROM spj a 
JOIN spj b
on a.supplier_id = b.supplier_id
  AND a.part_id < b.part_id   
ORDER BY a.supplier_id, a.part_id;

---------------------
--21

SELECT DISTINCT spj.part_id 
FROM spj 
JOIN projects ON spj.project_id = projects.project_id
WHERE projects.city = 'Лондон'

----------------------
--1
SELECT * FROM projects;

---------------------
--5
SELECT color, city from parts;

-----------------------
--30

SELECT DISTINCT spj.part_id 
FROM spj 
JOIN projects ON spj.project_id = projects.project_id
WHERE projects.city = 'Лондон'

---------------------
--9
SELECT part_id from spj 
join suppliers on spj.supplier_id = suppliers.supplier_id 
WHERE suppliers.city = 'Лондон';

---------------------
--25
SELECT project_id from projects ORDER by city LIMIT 1;


SELECT project_id FROM projects 
WHERE city = (SELECT city FROM projects ORDER BY project_id LIMIT 3);

