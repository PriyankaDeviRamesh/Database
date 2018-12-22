DROP SEQUENCE seq_log;

--Create a sequence for generating log ID's
CREATE SEQUENCE seq_log
MAXVALUE 999
START WITH 101
INCREMENT BY 1
ORDER
NOCACHE
NOCYCLE;