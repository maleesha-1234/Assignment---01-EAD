package com.clinic.dao;

import com.clinic.model.Diagnosis;
import java.sql.*;

public class DiagnosisDAO {

    public Diagnosis getByAppointmentId(int appointmentId) throws SQLException {
        String sql = "SELECT * FROM diagnoses WHERE appointment_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Diagnosis(
                    rs.getInt("diagnosis_id"),
                    rs.getInt("appointment_id"),
                    rs.getString("symptoms"),
                    rs.getString("diagnosis"),
                    rs.getString("prescription"),
                    rs.getString("notes")
                );
            }
        }
        return null;
    }

    public boolean insert(Diagnosis d) throws SQLException {
        String sql = "INSERT INTO diagnoses (appointment_id,symptoms,diagnosis,prescription,notes) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getAppointmentId());
            ps.setString(2, d.getSymptoms());
            ps.setString(3, d.getDiagnosis());
            ps.setString(4, d.getPrescription());
            ps.setString(5, d.getNotes());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Diagnosis d) throws SQLException {
        String sql = "UPDATE diagnoses SET symptoms=?,diagnosis=?,prescription=?,notes=? WHERE appointment_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getSymptoms());
            ps.setString(2, d.getDiagnosis());
            ps.setString(3, d.getPrescription());
            ps.setString(4, d.getNotes());
            ps.setInt(5, d.getAppointmentId());
            return ps.executeUpdate() > 0;
        }
    }
}
