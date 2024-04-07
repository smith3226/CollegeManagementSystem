package org.example.collegemanagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/collegeconsole";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    // Method to establish a connection to the MySQL database
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
           //
            // System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    // Method to test the database connection
    public static void testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println("Database connection test successful.");
        } catch (SQLException e) {
            System.err.println("Error testing database connection: " + e.getMessage());
        }
    }


    //Using Arraylist and Hashmap to store the students
    public static List<Map<String, String>> getAllStudents() {
        List<Map<String, String>> students = new ArrayList<>();
        String query = "SELECT * FROM students";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Map<String, String> student = new HashMap<>();
                student.put("student_id", resultSet.getString("student_id"));
                student.put("full_name", resultSet.getString("full_name"));
                student.put("email", resultSet.getString("email"));
                student.put("address", resultSet.getString("address"));
                student.put("phone", resultSet.getString("phone"));
                student.put("emergencyContact", resultSet.getString("emergency_contact"));
                student.put("passportNo", resultSet.getString("passport_number"));
                student.put("dob", resultSet.getString("dob"));
                student.put("previousDegree", resultSet.getString("degree"));
                student.put("selected_courses", resultSet.getString("selected_courses"));
                student.put("status", resultSet.getString("status"));
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students from the database: " + e.getMessage());
        }
        return students;
    }


    //method to getstudentDetails by ID
    public static Map<String, String> getStudentDetailsById(String studentID) {
        Map<String, String> studentDetails = new HashMap<>();
        String query = "SELECT * FROM students WHERE student_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, studentID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    studentDetails.put("student_id", resultSet.getString("student_id"));
                    studentDetails.put("full_name", resultSet.getString("full_name"));
                    studentDetails.put("email", resultSet.getString("email"));
                    studentDetails.put("address", resultSet.getString("address"));
                    studentDetails.put("phone", resultSet.getString("phone"));
                    studentDetails.put("emergencyContact", resultSet.getString("emergency_contact"));
                    studentDetails.put("passportNo", resultSet.getString("passport_number"));
                    studentDetails.put("dob", resultSet.getString("dob"));
                    studentDetails.put("previousDegree", resultSet.getString("degree"));
                    studentDetails.put("selected_courses", resultSet.getString("selected_courses"));
                    studentDetails.put("status", resultSet.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student details from the database: " + e.getMessage());
        }
        return studentDetails;
    }



    //method to updateStudentStatus
    public static void updateStudentStatus(String studentId, String newStatus) {
        String query = "UPDATE students SET status = ? WHERE student_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newStatus);
            statement.setString(2, studentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    //method to delete student
    public static boolean deleteStudent(String studentID) {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
