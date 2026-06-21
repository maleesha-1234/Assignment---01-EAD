package com.clinic.dao;

import com.clinic.model.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY first_name";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Doctor> getActiveDoctors() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE status = 'active' ORDER BY first_name";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Doctor getById(int id) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public boolean insert(Doctor d) throws SQLException {
        String sql = "INSERT INTO doctors (first_name, last_name, specialization, contact_number, email, available_days, status) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getSpecialization());
            ps.setString(4, d.getContactNumber());
            ps.setString(5, d.getEmail());
            ps.setString(6, d.getAvailableDays());
            ps.setString(7, d.getStatus() == null ? "active" : d.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Doctor d) throws SQLException {
        String sql = "UPDATE doctors SET first_name=?, last_name=?, specialization=?, contact_number=?, email=?, available_days=?, status=? WHERE doctor_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getSpecialization());
            ps.setString(4, d.getContactNumber());
            ps.setString(5, d.getEmail());
            ps.setString(6, d.getAvailableDays());
            ps.setString(7, d.getStatus());
            ps.setInt(8, d.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM doctors WHERE status='active'";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        return new Doctor(
            rs.getInt("doctor_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("specialization"),
            rs.getString("contact_number"),
            rs.getString("email"),
            rs.getString("available_days"),
            rs.getString("status")
        );
    }
}
