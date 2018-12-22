import java.sql.*;
import oracle.jdbc.*;
import java.math.*;
import java.io.*;
import java.awt.*;
import oracle.jdbc.pool.OracleDataSource;

public class ShowProcedures{

	public ShowProcedures(){}
	
	//calls show_students from student_reg_pkg. This displays the students table.
	public void show_students(Connection conn){

    	try{

	        //Prepare to call stored procedure:
	        CallableStatement cs = conn.prepareCall("{call student_reg_pkg.show_students(?)}");
	        cs.registerOutParameter(1, OracleTypes.CURSOR);
	        
	        // execute and retrieve the result set
	        cs.execute();
	        ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(1);
	        
	        // print the results
	        while (rs.next()) {
	            System.out.println(rs.getString(1) + "\t" +
	                              rs.getString(2) + "\t" + 
	                              rs.getString(3) +  "\t" +
	                              rs.getString(4) + "\t" + 
	                              rs.getDouble(5) + "\t" +
	                              rs.getString(6));
	        }

	        //close the result set, statement
	        rs.close();
	        cs.close();
		} 
		catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
		catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

	}

	//calls show_courses from student_reg_pkg. This displays the courses table.
	public void show_courses(Connection conn){

    	try{

	        //Prepare to call stored procedure:
	        CallableStatement cs = conn.prepareCall("{call student_reg_pkg.show_courses(?)}");
	        cs.registerOutParameter(1, OracleTypes.CURSOR);
	        
	        // execute and retrieve the result set
	        cs.execute();
	        ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(1);

	        // print the results
	        while (rs.next()) {
	            System.out.println(rs.getString(1) + "\t" +
	                              rs.getInt(2) + "\t" + 
	                              rs.getString(3));
	        }

	        //close the result set, statement
	        rs.close();
	        cs.close();
		} 
		catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
		catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

	}

	//calls show_prerequisites from student_reg_pkg. This displays the prerequisites table.
	public void show_prerequisites(Connection conn){

    	try{

	        //Prepare to call stored procedure:
	        CallableStatement cs = conn.prepareCall("{call student_reg_pkg.show_prerequisites(?)}");
	        cs.registerOutParameter(1, OracleTypes.CURSOR);
	        
	        // execute and retrieve the result set
	        cs.execute();
	        ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(1);

	        // print the results
	        while (rs.next()) {
	            System.out.println(rs.getString(1) + "\t" +
	                              rs.getInt(2) + "\t" + 
	                              rs.getString(3) + "\t" +
	                              rs.getInt(4));
	        }

	        //close the result set, statement
	        rs.close();
	        cs.close();
		} 
		catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
		catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

	}

	//calls show_classes from student_reg_pkg. This displays the classes table.
	public void show_classes(Connection conn){

    	try{

	        //Prepare to call stored procedure:
	        CallableStatement cs = conn.prepareCall("{call student_reg_pkg.show_classes(?)}");
	        cs.registerOutParameter(1, OracleTypes.CURSOR);
	        
	        // execute and retrieve the result set
	        cs.execute();
	        ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(1);

	        // print the results
	        while (rs.next()) {
	            System.out.println(rs.getString(1) + "\t" +
	                              rs.getString(2) + "\t" + 
	                              rs.getInt(3) + "\t" +
	                              rs.getInt(4) + "\t" +
	                              rs.getInt(5) + "\t" +
	                              rs.getString(6) + "\t" +
	                              rs.getInt(7) + "\t" +
	                              rs.getInt(8));
	        }

	        //close the result set, statement
	        rs.close();
	        cs.close();
		} 
		catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
		catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

	}

	//calls show_enrollments from student_reg_pkg. This displays the enrollments table.
	public void show_enrollments(Connection conn){

    	try{

	        //Prepare to call stored procedure:
	        CallableStatement cs = conn.prepareCall("{call student_reg_pkg.show_enrollments(?)}");
	        cs.registerOutParameter(1, OracleTypes.CURSOR);
	        
	        // execute and retrieve the result set
	        cs.execute();
	        ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(1);

	        // print the results
	        while (rs.next()) {
	            System.out.println(rs.getString(1) + "\t" +
	                              rs.getString(2) + "\t" + 
	                              rs.getString(3));
	        }

	        //close the result set, statement
	        rs.close();
	        cs.close();
		} 
		catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
		catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}

	}

	//calls show_logs from student_reg_pkg. This displays the logs table.
	public void show_logs(Connection conn){

    	try{

	        //Prepare to call stored procedure:
	        CallableStatement cs = conn.prepareCall("{call student_reg_pkg.show_logs(?)}");
	        cs.registerOutParameter(1, OracleTypes.CURSOR);
	        
	        // execute and retrieve the result set
	        cs.execute();
	        ResultSet rs = (ResultSet)((OracleCallableStatement)cs).getCursor(1);


	        // print the results
	        while (rs.next()) {

	            System.out.println(rs.getInt(1) + "\t" +
	                              rs.getString(2) + "\t" + 
	                              rs.getTimestamp(3) + "\t" + 
	                              rs.getString(4) + "\t" + 
	                              rs.getString(5) + "\t" + 
	                              rs.getString(6));
	        }

	        //close the result set, statement
	        rs.close();
	        cs.close();
		} 
		catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
		catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n" + e.toString());}

	}

}