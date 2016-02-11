COPY USR
FROM '/Users/bxiao001/Desktop/project/my_working_project/cs166_project/data/User.csv'
WITH DELIMITER ',';

COPY WORK_EXPR
FROM '/Users/bxiao001/Desktop/project/my_working_project/cs166_project/data/Work_xp.csv'
WITH DELIMITER ',';

COPY EDUCATIONAL_DETAILS
FROM '/Users/bxiao001/Desktop/project/my_working_project/cs166_project/data/Edu_det.csv'
WITH DELIMITER ',';

COPY MESSAGE
FROM '/Users/bxiao001/Desktop/project/my_working_project/cs166_project/data/Message.csv'
WITH DELIMITER '	';

COPY CONNECTION_USR 
FROM '/Users/bxiao001/Desktop/project/my_working_project/cs166_project/data/Connection.csv'
WITH DELIMITER ',';

