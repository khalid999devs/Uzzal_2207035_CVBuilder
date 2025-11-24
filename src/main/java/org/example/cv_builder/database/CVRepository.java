package org.example.cv_builder.database;

import org.example.cv_builder.CVData;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CVRepository {
    private static CVRepository instance;
    private final DatabaseManager dbManager;
    private final DateTimeFormatter formatter;

    private CVRepository() {
        this.dbManager = DatabaseManager.getInstance();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static CVRepository getInstance() {
        if (instance == null) {
            instance = new CVRepository();
        }
        return instance;
    }

    public int insertCV(CVData cv) {
        String sql = """
            INSERT INTO cvs (fullName, email, phone, address, education, skills, 
                           workExperience, projects, photoPath, createdAt, updatedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String timestamp = LocalDateTime.now().format(formatter);
            
            pstmt.setString(1, cv.getFullName());
            pstmt.setString(2, cv.getEmail());
            pstmt.setString(3, cv.getPhone());
            pstmt.setString(4, cv.getAddress());
            pstmt.setString(5, cv.getEducation());
            pstmt.setString(6, cv.getSkills());
            pstmt.setString(7, cv.getWorkExperience());
            pstmt.setString(8, cv.getProjects());
            pstmt.setString(9, cv.getPhotoPath());
            pstmt.setString(10, timestamp);
            pstmt.setString(11, timestamp);

            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting CV: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateCV(CVData cv) {
        String sql = """
            UPDATE cvs SET fullName = ?, email = ?, phone = ?, address = ?, 
                          education = ?, skills = ?, workExperience = ?, projects = ?,
                          photoPath = ?, updatedAt = ?
            WHERE id = ?
            """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String timestamp = LocalDateTime.now().format(formatter);
            
            pstmt.setString(1, cv.getFullName());
            pstmt.setString(2, cv.getEmail());
            pstmt.setString(3, cv.getPhone());
            pstmt.setString(4, cv.getAddress());
            pstmt.setString(5, cv.getEducation());
            pstmt.setString(6, cv.getSkills());
            pstmt.setString(7, cv.getWorkExperience());
            pstmt.setString(8, cv.getProjects());
            pstmt.setString(9, cv.getPhotoPath());
            pstmt.setString(10, timestamp);
            pstmt.setInt(11, cv.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating CV: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCV(int id) {
        String sql = "DELETE FROM cvs WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting CV: " + e.getMessage());
        }
        return false;
    }

    public CVData getCVById(int id) {
        String sql = "SELECT * FROM cvs WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCVFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting CV by ID: " + e.getMessage());
        }
        return null;
    }

    public List<CVData> getAllCVs() {
        List<CVData> cvs = new ArrayList<>();
        String sql = "SELECT * FROM cvs ORDER BY createdAt DESC";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cvs.add(extractCVFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all CVs: " + e.getMessage());
        }
        return cvs;
    }

    private CVData extractCVFromResultSet(ResultSet rs) throws SQLException {
        CVData cv = new CVData();
        cv.setId(rs.getInt("id"));
        cv.setFullName(rs.getString("fullName"));
        cv.setEmail(rs.getString("email"));
        cv.setPhone(rs.getString("phone"));
        cv.setAddress(rs.getString("address"));
        cv.setEducation(rs.getString("education"));
        cv.setSkills(rs.getString("skills"));
        cv.setWorkExperience(rs.getString("workExperience"));
        cv.setProjects(rs.getString("projects"));
        cv.setPhotoPath(rs.getString("photoPath"));
        cv.setCreatedAt(rs.getString("createdAt"));
        cv.setUpdatedAt(rs.getString("updatedAt"));
        return cv;
    }
}
