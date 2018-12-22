--David DeFazio
--Priyanka Ramesh

--Start package specification
CREATE or REPLACE PACKAGE student_reg_pkg AS

--define all procedure names and parameters
procedure show_students(s_recordset OUT SYS_REFCURSOR);
procedure show_courses(c_recordset OUT SYS_REFCURSOR);
procedure show_prerequisites(p_recordset OUT SYS_REFCURSOR);
procedure show_classes(cl_recordset OUT SYS_REFCURSOR);
procedure show_enrollments(e_recordset OUT SYS_REFCURSOR);
procedure show_logs(l_recordset OUT SYS_REFCURSOR);
procedure delete_student(s_id in students.sid%type, show_message OUT
varchar);

procedure add_student(
s_id in students.sid%type,
first_name in students.firstname%type,
last_name in students.lastname%type,
status_ in students.status%type,
gpa_ in students.gpa%type,
email_ in students.email%type,
show_message OUT varchar);

procedure student_details(s_id in students.sid%type, show_message OUT varchar, s_recordset OUT SYS_REFCURSOR);

procedure view_prereqs(dept_code_init in courses.dept_code%type, 
                          course_no_init in courses.course_no%type,
                          c_recordset OUT SYS_REFCURSOR);

PROCEDURE class_details(class_id in classes.classid%type, show_message OUT varchar, c_recordset OUT SYS_REFCURSOR);

procedure enroll_student(
s_id in students.sid%type,
class_id in classes.classid%type,
show_message OUT varchar);

procedure drop_class(
s_id in students.sid%type,
class_id in classes.classid%type,
show_message OUT varchar);

END student_reg_pkg;
/
--END PACKAGE SPECIFICATION

--START OF PACKAGE BODY
CREATE OR REPLACE PACKAGE BODY student_reg_pkg AS

--drop student with s_id from class with class_id.
--only do this if it passes all constraints
procedure drop_class(
s_id in students.sid%type,
class_id in classes.classid%type,
show_message OUT varchar)
IS
cid_count number := 0;
sid_count number := 0;
enroll_count number := 0;
total_classes number := 0;
students_left number := 0;
BEGIN

  --check if valid class and student is selected
  select count(*) into sid_count from students where sid = s_id;
  select count(*) into cid_count from classes where classid = class_id;

  IF cid_count = 0 then
    show_message := 'The classid is invalid';
      
  ELSIF sid_count = 0 then
    show_message := 'The sid is invalid';

  ELSE

  select count(*) into enroll_count from enrollments
  where enrollments.sid = s_id and enrollments.classid = class_id;

  select count(*) into total_classes from enrollments
  where enrollments.sid = s_id;

  select classes.class_size into students_left from classes
  where classes.classid = class_id;

    --check if the student is enrolled
    IF enroll_count = 0 then
      show_message := 'The student is not enrolled in the class';

    --check if dropping this violated prereqs of another class
    --ELSIF

    --drop the class
    ELSE

      IF total_classes = 1 then
        show_message := 'This student is not enrolled in any classes';
      END IF;

      IF students_left = 1 then
        show_message := 'The class now has no students';
      END IF;

      delete from enrollments where sid = s_id and classid = class_id;

      IF show_message is null then
        show_message := 'Successfully dropped class';
      END IF;

    END IF;

  END IF;

END drop_class;


--add student with s_id into class with class_id.
--Only do this if it passes all constraints
procedure enroll_student(
s_id in students.sid%type,
class_id in classes.classid%type,
show_message OUT varchar) 
IS

cid_count number := 0;
sid_count number := 0;
classSize number := 0;
classLimit number := 0;
classSemester varchar2(6);
classYear number;
alreadyIn number := 0;
numClasses number := 0;
deptCode varchar2(4);
courseNum number;
numPreReqs number := 0;

BEGIN

    --check if valid class and student is selected
    select count(*) into sid_count from students where sid = s_id;
    select count(*) into cid_count from classes where classid = class_id;

    IF cid_count = 0 then
      show_message := 'The classid is invalid';
      
    ELSIF sid_count = 0 then
      show_message := 'The sid is invalid';

    ELSE

      --get class size, limit, year, semester, dept_code, course_no
      select classes.class_size into classSize from classes where classes.classid = class_id;
      select classes.limit into classLimit from classes where classes.classid = class_id;
      select classes.semester into classSemester from classes where classes.classid = class_id;
      select classes.year into classYear from classes where classes.classid = class_id;
      select classes.dept_code into deptCode from classes where classes.classid = class_id;
      select classes.course_no into courseNum from classes where classes.classid = class_id;

      --check if student is already in class
      select count(s_id) into alreadyIn from enrollments
      join classes on enrollments.classid = classes.classid
      where classes.classid = class_id and enrollments.sid = s_id; 

      --find number of classes student is taking in same semester and same year
      select count(*) into numClasses from enrollments
      join classes on enrollments.classid = classes.classid
      where classes.year = classYear and classes.semester = classSemester
      and enrollments.sid = s_id;

      --find number of required prereqs that the student has not completed
      select count(*) into numPreReqs from
      (
      --get prereqs needed for course
      select prerequisites.pre_dept_code, prerequisites.pre_course_no from prerequisites
      connect by prior pre_dept_code = dept_code AND PRIOR pre_course_no = course_no
      START WITH dept_code = deptCode AND course_no = courseNum
      minus
      --get courses completed with a grade of C or better
      select classes.dept_code, classes.course_no from classes
      join enrollments on classes.classid = enrollments.classid
      where enrollments.sid = s_id and enrollments.lgrade < 'D' 
      and enrollments.lgrade is not null);


      IF classSize >= classLimit then
        show_message := 'The class is closed';

      ELSIF alreadyIn > 0 then
        show_message := 'The student is already in the class';

      ELSIF numClasses >= 3 then
        show_message := 'Students cannot be enrolled in more than three classes in the same semester';

      ELSIF numPreReqs > 0 then
        show_message := 'Prerequisite courses have not been completed';

      ELSE
        IF numClasses = 2 then
          show_message := 'You are overloaded';
        END IF;

        --enroll into class
        insert into enrollments values (s_id, class_id, null);
        show_message := 'Student Successfully Enrolled';

      END IF;

    END IF;

END enroll_student;

--display classid, title, semester, year, student sid, and lastname for an associated class
--one row for each student enrolled in the class
PROCEDURE class_details(class_id in classes.classid%type, show_message OUT varchar, 
  c_recordset OUT SYS_REFCURSOR) IS
cid_count number := 0;
sid_count number := 0;
BEGIN

  --check if student and class are valid
   select count(class_id) into cid_count from classes where classid = class_id;
   select count(sid) into sid_count from enrollments where classid = class_id;
   
   IF cid_count = 0 then
      show_message := 'The cid is invalid';
      
   ELSIF sid_count = 0 then
      show_message := 'No student is enrolled in the class';
      
    ELSE
      --select associated information
      OPEN c_recordset FOR
      select classes.classid, courses.title, classes.semester, classes.year,
      enrollments.sid, students.lastname
      from classes
      INNER JOIN courses ON classes.dept_code = courses.dept_code
      AND classes.course_no = courses.course_no
      INNER JOIN enrollments ON enrollments.classid = classes.classid
      INNER JOIN students ON students.sid = enrollments.sid
      WHERE classes.classid = class_id;
      
   END IF;
   
END class_details;

--Use a hierarchical query to find all prereqs needed for a 
--particular class 
PROCEDURE view_prereqs(dept_code_init in courses.dept_code%type, 
course_no_init in courses.course_no%type,  
c_recordset OUT SYS_REFCURSOR)
IS
BEGIN
  OPEN c_recordset FOR
  SELECT concat(pre_dept_code, pre_course_no)
  FROM prerequisites
  CONNECT BY PRIOR pre_dept_code = dept_code AND PRIOR pre_course_no = course_no --branch
  START WITH dept_code = dept_code_init AND course_no = course_no_init; --base
END view_prereqs;

--Add a new student to the students table
PROCEDURE add_student(s_id in students.sid%type,
first_name in students.firstname%type,
last_name in students.lastname%type,
status_ in students.status%type,
gpa_ in students.gpa%type,
email_ in students.email%type,
show_message OUT varchar)
IS

stu_count PLS_INTEGER := 0;

BEGIN
  --make sure student doesnt exist yet
  select count(*) into stu_count from students where sid = s_id;

  IF stu_count = 0
  THEN
        insert into students values (s_id, first_name, last_name,
        status_, gpa_, email_);
        show_message := 'Student Inserted Successfully';

  ELSE
  show_message := 'Student already exists';
   END IF;
END add_student;

--delete student from students table, with given sid
PROCEDURE delete_student(s_id in students.sid%type, show_message OUT
varchar) IS
e_count number;
NO_VALUE Exception;
BEGIN
  -- make sure student exists
   IF s_id is NULL then
      raise NO_VALUE;
   ELSE
      delete from students where sid = s_id;
      show_message := 'Student Successfully Deleted';
   END IF;

exception
   WHEN NO_VALUE THEN
   show_message := 'The sid is invalid';
END delete_student;

--display students table
PROCEDURE show_students (s_recordset OUT SYS_REFCURSOR)
IS
BEGIN
   OPEN s_recordset FOR
   SELECT * from students;
END show_students;

--display courses table
PROCEDURE show_courses (c_recordset OUT SYS_REFCURSOR) IS
BEGIN
   OPEN c_recordset FOR
   SELECT * from courses;
END show_courses;

--display prerequisites table
PROCEDURE show_prerequisites (p_recordset OUT SYS_REFCURSOR) IS
BEGIN
  OPEN p_recordset FOR
  SELECT * from prerequisites;
END show_prerequisites;

--display classes table
PROCEDURE show_classes (cl_recordset OUT SYS_REFCURSOR) IS
BEGIN
  OPEN cl_recordset FOR
  SELECT * from classes;
END show_classes;

--display enrollments table
PROCEDURE show_enrollments (e_recordset OUT SYS_REFCURSOR) IS
BEGIN
  OPEN e_recordset FOR
  SELECT * from enrollments;
END show_enrollments;

--display logs table
PROCEDURE show_logs (l_recordset OUT SYS_REFCURSOR) IS
BEGIN
  OPEN l_recordset FOR
  SELECT * from logs;
END show_logs;

--display students sid, lastname, status, and associated class information, given his/her sid
PROCEDURE student_details(s_id in students.sid%type, show_message OUT varchar, s_recordset OUT SYS_REFCURSOR) IS
e_count number :=0;
s_sid students.sid%type;

NO_VALUE Exception;
BEGIN
  IF s_id is null THEN
    raise NO_VALUE;
  ELSE
     BEGIN
        --ensure student exists
        select count(*) into e_count from enrollments where sid = s_id;
        IF e_count = 0
        THEN
                show_message := 'The student has not taken any course';
        ELSE
                --retrieve associated information
                OPEN s_recordset FOR
                SELECT students.sid, students.lastname, students.status,
                enrollments.classid, concat(classes.dept_code,
                classes.course_no) as dept_course_no,
                classes.year,classes.semester,
                courses.title 
                FROM students
                INNER JOIN enrollments ON students.sid = enrollments.sid
                INNER JOIN classes ON enrollments.classid = classes.classid
                INNER JOIN courses ON classes.dept_code = courses.dept_code
                AND classes.course_no = courses.course_no
                WHERE students.sid = s_id;
        END IF;
    END;
  END IF;
exception
  WHEN NO_VALUE THEN
  show_message := 'The Sid is Invalid';
END student_details;

END student_reg_pkg;
/
--END OF PACKAGE BODY
