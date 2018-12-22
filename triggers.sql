--Delete all enrollment entries that student was in before deleting student
CREATE or replace TRIGGER del_student BEFORE DELETE on students
FOR EACH ROW 
BEGIN
    DELETE FROM enrollments WHERE enrollments.sid = :old.sid;
END;
/

--Add to logs when adding a new student
create or replace trigger LOGS_AFTER_INSERT_ENROLLMENT
AFTER insert on ENROLLMENTS
for each row
begin
insert into logs(logid, who, time, table_name, operation, key_value) values (seq_log.nextval, USER, sysdate, 'enrollments', 'insert', :new.sid || ',' || :new.classid);
end;
/

--After adding to enrollments, add 1 to associated class size
create or replace trigger INC_CLASS_SIZE
AFTER insert on ENROLLMENTS
for each row
begin
update classes set class_size = class_size + 1 where classid = :new.classid;
end;
/

--After deleting an enrollment, subtract 1 from class size
create or replace trigger DEC_CLASS_SIZE
AFTER delete on ENROLLMENTS
for each row
begin
update classes set class_size = class_size - 1 where classid = :old.classid;
end;
/

--Add entry to logs after adding a new student
create or replace trigger LOGS_AFTER_INSERT_STUDENT
AFTER insert on STUDENTS
for each row
begin
insert into logs(logid, who, time, table_name, operation, key_value) values (seq_log.nextval, USER, sysdate, 'students', 'insert', :new.sid);
end;
/

--Add entry to logs after deleting an enrollment
create or replace trigger LOGS_AFTER_DELETE_ENROLLMENTS
AFTER delete on ENROLLMENTS
for each row
begin
insert into logs(logid, who, time, table_name, operation, key_value) values (seq_log.nextval, USER, sysdate, 'enrollments', 'delete', :old.sid || ',' || :old.classid);
end;
/

--Add entry to logs after deleting a student
create or replace trigger LOGS_AFTER_DELETE_STUDENT
AFTER delete on STUDENTS
for each row
begin
insert into logs(logid, who, time, table_name, operation, key_value) values(seq_log.nextval, USER, sysdate, 'students', 'delete', :old.sid);
end;
/