//David DeFazio
//Priyanka Ramesh

import java.sql.*;
import oracle.jdbc.*;
import java.math.*;
import java.io.*;
import java.awt.*;
import oracle.jdbc.pool.OracleDataSource;

public class RunProcedures {

  public static void main (String args []) throws SQLException {


    //used to check if we should exit the inerface
    boolean exit = false;

    try{

        //Connection to Oracle server
        OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
        ds.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:ACAD111");
        Connection conn = ds.getConnection("ddefazi1", "Melons12");

        //create a ShowProcedures object, to access show functions
        ShowProcedures sp = new ShowProcedures();

        //dont leave unless an exit or quit command is given
        while(!exit){
          System.out.println();
          String line;
          BufferedReader readKeyBoard = new BufferedReader(new InputStreamReader(System.in)); 

          //display options
          System.out.println("Enter one of the Following Options: ");
          System.out.println("1: Display a Table");
          System.out.println("2: Enroll a Student into a Class");
          System.out.println("3: View All Prerequisites for Particular Course");
          System.out.println("4: View a Student's Details");
          System.out.println("5: View a class's Details");
          System.out.println("6: Add a Student");
          System.out.println("7: Delete a Student");
          System.out.println("8: Drop student from class");

          line = readKeyBoard.readLine();

          boolean exit1 = false;

          if(line.equals("1")){ //SHOW A TABLE
            while(!exit1){
              System.out.println();
              String tableSelection;

              //display table options
              System.out.println("Select a Table to Display: ");
              System.out.println("1: Students");
              System.out.println("2: Courses");
              System.out.println("3: Prerequisites");
              System.out.println("4: Classes");
              System.out.println("5: Enrollments");
              System.out.println("6: Logs");
              tableSelection = readKeyBoard.readLine();
              if(tableSelection.equals("1")){ //show students table
                sp.show_students(conn);
                exit1 = true;
              }else if(tableSelection.equals("2")){ //show courses table
                sp.show_courses(conn);
                exit1 = true;
              }else if(tableSelection.equals("3")){ //show prerequisites table
                sp.show_prerequisites(conn);
                exit1 = true;
              }else if(tableSelection.equals("4")){ //show classes table
                sp.show_classes(conn);
                exit1 = true;
              }else if(tableSelection.equals("5")){ //show enrollments table
                sp.show_enrollments(conn);
                exit1 = true;
              }else if(tableSelection.equals("6")){ //show logs table
                sp.show_logs(conn);
                exit1 = true;
              }else{
                System.out.println("Invalid input. Please Try Again: ");
              }
            }
          }else if(line.equals("2")){//ENROLL STUDENT
            String sid;
            String classid;
            System.out.println("Please Enter Student's sid: ");
            sid = readKeyBoard.readLine();
            System.out.println("Please Enter class's classid: ");
            classid = readKeyBoard.readLine();
            enroll_student(sid, classid, conn);

          }else if(line.equals("3")){ //INDIRECT PREREQS
            String dept_code;
            int course_no;
            System.out.println("Please Enter Department Code: ");
            dept_code = readKeyBoard.readLine();
            System.out.println("Please Enter Course Number: ");
            course_no = Integer.parseInt(readKeyBoard.readLine());
            view_prereqs(dept_code, course_no, conn);
          }else if(line.equals("4")){ //STUDENT DETAILS
            String sid;
            System.out.println("Please Enter Student's sid: ");
            sid = readKeyBoard.readLine();
            student_details(sid, conn);
          }else if(line.equals("5")){ //CLASS DETAILS
            String classid;
            System.out.println("Please Enter classid: ");
            classid = readKeyBoard.readLine();
            class_details(classid, conn);
          }else if(line.equals("6")){ //ADD STUDENT
            String sid, fName, lName, status, email;
            double gpa;
            System.out.println("Please Enter Student's sid: ");
            sid = readKeyBoard.readLine();
            System.out.println("Please Enter Student's First Name: ");
            fName = readKeyBoard.readLine();
            System.out.println("Please Enter Student's Last Name: ");
            lName = readKeyBoard.readLine();
            System.out.println("Please Enter Student's Status: ");
            status = readKeyBoard.readLine();
            System.out.println("Please Enter Student's GPA: ");
            gpa = Double.parseDouble(readKeyBoard.readLine());
            System.out.println("Please Enter Student's Email: ");
            email = readKeyBoard.readLine();
            add_student(sid, fName, lName, status, gpa, email, conn);

          }else if(line.equals("7")){//DELETE STUDENT
            String sid;
            System.out.println("Please Enter Student's sid: ");
            sid = readKeyBoard.readLine();
            delete_student(sid, conn);
          }else if(line.equals("8")){//DROP CLASS
            String sid;
            String classid;
            System.out.println("Please Enter Student's sid: ");
            sid = readKeyBoard.readLine();
            System.out.println("Please Enter class's classid: ");
            classid = readKeyBoard.readLine();
            drop_clas(sid, classid, conn);
          }else if(line.equals("exit") || line.equals("quit")){ //exit program
            exit = true;
          }else{
            System.out.println("Invalid input. Please Try Again: ");
          }
      }

      //close the connection
      conn.close();

    } 
    catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
    catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

   }

   //Call pl/sql drop_class function found in student_reg_pkg
   //takes sid and classid as input, and either drops the student from the class,
   //or outputs a message explaining why it can't
   public static void drop_clas(String sid, String classid, Connection conn){

    try{
      //Prepare to call stored procedure:
      CallableStatement cs = conn.prepareCall("{call student_reg_pkg.drop_class(?, ?, ?)}");

      //set parameters
      cs.setString(1, sid);
      cs.setString(2, classid);
      cs.registerOutParameter(3, Types.VARCHAR);

      // execute and retrieve the result set
      cs.execute();

      //get the out parameter result.
      String status = cs.getString(3);
      System.out.println(status);

      //close the statement
      cs.close();

    }
    catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
    catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

   }

   //Call pl/sql enroll_students function found in student_reg_pkg
   //takes sid and classid as input, and either enrolls the student, or
   //outputs a message explaining why it can't
   public static void enroll_student(String sid, String classid, Connection conn){

    try{
      //Prepare to call stored procedure:
      CallableStatement cs = conn.prepareCall("{call student_reg_pkg.enroll_student(?, ?, ?)}");

      //set parameters
      cs.setString(1, sid);
      cs.setString(2, classid);
      cs.registerOutParameter(3, Types.VARCHAR);

      // execute and retrieve the result set
      cs.execute();

      //get the out parameter result.
      String status = cs.getString(3);
      System.out.println(status);

      //close the statement
      cs.close();

    }
    catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
    catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

   }

   //calls class_details from student_reg_pkg. This returns information
   //about a class with classid
   public static void class_details(String classid, Connection conn){

    try{

      //Prepare to call stored procedure:
      CallableStatement cs = conn.prepareCall("{call student_reg_pkg.class_details(?, ?, ?)}");

      //set parameters
      cs.setString(1, classid);
      cs.registerOutParameter(2, Types.VARCHAR);
      cs.registerOutParameter(3, OracleTypes.CURSOR);

      // execute and retrieve the result set
      cs.execute();

      //get the out parameter result.
      String status = cs.getString(2);
      if(status != null){
        System.out.println(status);
        return;
      }

      ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(3);

      // print the results
      while (rs.next()) {
          System.out.println(rs.getString(1) + "\t" + //classid
                            rs.getString(2) + "\t" +  //title
                            rs.getString(3) +  "\t" + //semester
                            rs.getInt(4) + "\t" +  //year
                            rs.getString(5) + "\t" + //enrollment sid
                            rs.getString(6)); //lastname
      }

      //close resultset and statement
      rs.close();
      cs.close();

    }
    catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
    catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

   }

   //calls view_prereqs from student_reg_pkg. This displays all required prereqs for course
   //associated with given dept_code and course_no
  public static void view_prereqs(String dept_code, int course_no, Connection conn){

    try{

      //Prepare to call stored procedure:
      CallableStatement cs = conn.prepareCall("{call student_reg_pkg.view_prereqs(?, ?, ?)}");

      //set parameters
      cs.setString(1, dept_code);
      cs.setInt(2, course_no);
      cs.registerOutParameter(3, OracleTypes.CURSOR);

      // execute and retrieve the result set
      cs.execute();
      ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(3);

      // print the results
      while (rs.next()) {
          System.out.println(rs.getString(1)); //title 
      }

      //close the result set, statement
      rs.close();
      cs.close();

    }
    catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
    catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

  }

  //calls student_details from student_reg_pkg. This displays information for student
  //of id sid
  public static void student_details(String sid, Connection conn){

    try{
      //Prepare to call stored procedure:
      CallableStatement cs = conn.prepareCall("{call student_reg_pkg.student_details(?, ?, ?)}");

      //set parameters
      cs.setString(1, sid);
      cs.registerOutParameter(2, Types.VARCHAR);
      cs.registerOutParameter(3, OracleTypes.CURSOR);

      // execute and retrieve the result set
      cs.execute();

      //get the out parameter result.
      String status = cs.getString(2);
      if(status != null){
        System.out.println(status);
        return;
      }


      ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(3);

      // print the results
      while (rs.next()) {
          System.out.println(rs.getString(1) + "\t" + //sid
                            rs.getString(2) + "\t" +  //lastname
                            rs.getString(3) +  "\t" + //status
                            rs.getString(4) + "\t" +  //classid
                            rs.getString(5) + "\t" + //dept_code and course#
                            rs.getInt(6) + "\t" + //year
                            rs.getString(7) + "\t" + //semester
                            rs.getString(8)); //title 
      }

      //close the result set, statement
      rs.close();
      cs.close();

    }
    catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
    catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

  }

  //calls add_student from student_reg_pkg. This adds a student to the student table, or outputs a message explaining why it can't
  public static void add_student(String sid, String fName, String lName, String status, double gpa, String email, Connection conn){

    try{

        CallableStatement cs = conn.prepareCall("begin student_reg_pkg.add_student(?,?,?,?,?,?,?); end;");

        //set the in parameters  
        cs.setString(1, sid);
        cs.setString(2, fName);
        cs.setString(3, lName);
        cs.setString(4, status);
        cs.setDouble(5, gpa);
        cs.setString(6, email);

        //register the out parameter
        cs.registerOutParameter(7, Types.VARCHAR);

        //execute the store procedure
        cs.executeQuery();

        //get the out parameter result.
        String message = cs.getString(7);
        System.out.println(message);
        
        //close the statement
        cs.close();
   } 
   catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
   catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

  }

  //calls delete_student from student_reg_pkg. This deletes a student from the student table, 
  //or outputs a message explaining why it can't
  public static void delete_student(String sid, Connection conn){

    try{

        CallableStatement cs = conn.prepareCall("begin student_reg_pkg.delete_student(?,?); end;");

        //set the in parameters  
        cs.setString(1, sid);

        //register the out parameter
        cs.registerOutParameter(2, Types.VARCHAR);

        //execute the store procedure
        cs.executeQuery();

        //get the out parameter result.
        String message = cs.getString(2);
        System.out.println(message);
        
        //close the statement
        cs.close();
   } 
   catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
   catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}
  }

} 
