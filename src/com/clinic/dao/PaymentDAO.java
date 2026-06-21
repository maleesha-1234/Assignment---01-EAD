package com.clinic.dao;

import com.clinic.model.Payment;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public Payment getByAppointmentId(int appointmentId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE appointment_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public boolean insert(Payment p) throws SQLException {
        String sql = "INSERT INTO payments (appointment_id,amount,payment_method,payment_date,status) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getAppointmentId());
            ps.setBigDecimal(2, p.getAmount());
            ps.setString(3, p.getPaymentMethod());
            ps.setDate(4, Date.valueOf(p.getPaymentDate()));
            ps.setString(5, p.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Payment p) throws SQLException {
        String sql = "UPDATE payments SET amount=?,payment_method=?,payment_date=?,status=? WHERE appointment_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, p.getAmount());
            ps.setString(2, p.getPaymentMethod());
            ps.setDate(3, Date.valueOf(p.getPaymentDate()));
            ps.setString(4, p.getStatus());
            ps.setInt(5, p.getAppointmentId());
            return ps.executeUpdate() > 0;
        }
    }

    public double getTotalRevenueToday() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payments WHERE payment_date = CURDATE() AND status='Paid'";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        return new Payment(
            rs.getInt("payment_id"),
            rs.getInt("appointment_id"),
            rs.getBigDecimal("amount"),
            rs.getString("payment_method"),
            rs.getDate("payment_date").toLocalDate(),
            rs.getString("status")
        );
    }
}
