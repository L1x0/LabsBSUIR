CREATE TABLE suppliers (
    supplier_id VARCHAR(10) PRIMARY KEY, 
    name        VARCHAR(100) NOT NULL,   
    status      INTEGER, 
    city        VARCHAR(100)             
);

CREATE TABLE parts (
    part_id VARCHAR(10) PRIMARY KEY,  
    name    VARCHAR(100) NOT NULL,     
    color   VARCHAR(50),               
    size    INTEGER,                    
    city    VARCHAR(100)               
);

CREATE TABLE projects (
    project_id VARCHAR(10) PRIMARY KEY, 
    name       VARCHAR(100) NOT NULL,   
    city       VARCHAR(100)             
);

CREATE TABLE spj (
    supplier_id VARCHAR(10) NOT NULL, 
    part_id     VARCHAR(10) NOT NULL, 
    project_id  VARCHAR(10) NOT NULL, 
    quantity    INTEGER NOT NULL, 
    PRIMARY KEY (supplier_id, part_id, project_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    FOREIGN KEY (part_id)     REFERENCES parts(part_id),
    FOREIGN KEY (project_id)  REFERENCES projects(project_id)
);
