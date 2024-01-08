--Index for USR
CREATE UNIQUE INDEX u_id_usr ON USR(userId);
CREATE INDEX cred ON USER(userId,password);
CREATE INDEX user_name ON USER(name);

--Index for Work_exp
CREATE UNIQUE INDEX u_work ON WORK_EXPR(userId,company);

--Index for Educational_Details
CREATE UNIQUE INDEX u_edu ON EDUCATIONAL_DETAILS(userId,institutionName,major);


