package com.clinic.ui;

import com.clinic.dao.DatabaseConnection;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ReportForm extends JFrame {

    public ReportForm() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Reports - Clinic Management System");
        setSize(500, 320);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel header = new JPanel();
        header.setBackground(new Color(30, 60, 114));
        header.setPreferredSize(new Dimension(0, 50));
        JLabel lbl = new JLabel("Generate Reports", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(lbl);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        center.setBackground(new Color(245, 247, 252));

        JButton btnMonthly   = reportBtn("Monthly Clinic Activity Report (Major)", new Color(30, 60, 114));
        JButton btnPatients  = reportBtn("Patient List Report",                    new Color(46, 204, 113));
        JButton btnRevenue   = reportBtn("Revenue Summary Report",                 new Color(230, 126, 34));

        btnMonthly.addActionListener(e  -> generateReport("monthly_clinic_report"));
        btnPatients.addActionListener(e -> generateReport("patient_list_report"));
        btnRevenue.addActionListener(e  -> generateReport("revenue_report"));

        center.add(btnMonthly);
        center.add(btnPatients);
        center.add(btnRevenue);
        add(center, BorderLayout.CENTER);

        JLabel note = new JLabel("View Reports.", SwingConstants.CENTER);
        note.setFont(new Font("Arial", Font.ITALIC, 11));
        note.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(note, BorderLayout.SOUTH);
    }

    private void generateReport(String reportName) {
        try {
            String jrxmlPath = "reports/" + reportName + ".jrxml";
            java.io.File jrxmlFile = new java.io.File(jrxmlPath);
            if (!jrxmlFile.exists()) {
                JOptionPane.showMessageDialog(this,
                    "Report file not found: " + jrxmlPath + "\n" +
                    "Make sure the reports/ folder exists in the project root.",
                    "Report Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Connection con = DatabaseConnection.getConnection();
            Map<String, Object> params = new HashMap<>();
            params.put("CLINIC_NAME", "City Clinic");

            JasperReport compiled = JasperCompileManager.compileReport(jrxmlPath);
            JasperPrint  print    = JasperFillManager.fillReport(compiled, params, con);
            JasperViewer.viewReport(print, false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Report generation error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton reportBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(0, 44));
        return b;
    }
}
