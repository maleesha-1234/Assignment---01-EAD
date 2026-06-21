package com.clinic.dao;

import com.clinic.model.Appointment;
import com.clinic.model.Appointment.Status;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name, " +
                     "CONCAT('Dr. ',d.first_name,' ',d.last_name) AS doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors  d ON a.doctor_id  = d.doctor_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Appointment> getByDate(LocalDate date) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name, " +
                     "CONCAT('Dr. ',d.first_name,' ',d.last_name) AS doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors  d ON a.doctor_id  = d.doctor_id " +
                     "WHERE a.appointment_date = ? ORDER BY a.appointment_time";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean isDoubleBooked(int doctorId, LocalDate date, String time, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=? AND appointment_time=? AND appointment_id<>? AND status NOT IN ('Cancelled')";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, time);
            ps.setInt(4, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public boolean insert(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id,doctor_id,appointment_date,appointment_time,reason,status,notes) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setDate(3, Date.valueOf(a.getAppointmentDate()));
            ps.setString(4, a.getAppointmentTime().toString());
            ps.setString(5, a.getReason());
            ps.setString(6, a.getStatus().name());
            ps.setString(7, a.getNotes());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Appointment a) throws SQLException {
        String sql = "UPDATE appointments SET patient_id=?,doctor_id=?,appointment_date=?,appointment_time=?,reason=?,status=?,notes=? WHERE appointment_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setDate(3, Date.valueOf(a.getAppointmentDate()));
            ps.setString(4, a.getAppointmentTime().toString());
            ps.setString(5, a.getReason());
            ps.setString(6, a.getStatus().name());
            ps.setString(7, a.getNotes());
            ps.setInt(8, a.getAppointmentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int appointmentId, Status status) throws SQLException {
        String sql = "UPDATE appointments SET status=? WHERE appointment_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int getTodayCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getPendingCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status='Pending'";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setPatientName(rs.getString("patient_name"));
        a.setDoctorName(rs.getString("doctor_name"));
        a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        a.setReason(rs.getString("reason"));
        a.setStatus(Status.valueOf(rs.getString("status")));
        a.setNotes(rs.getString("notes"));
        return a;
    }
}
