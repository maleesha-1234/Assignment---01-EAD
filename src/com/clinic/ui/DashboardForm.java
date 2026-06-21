package com.clinic.ui;

import com.clinic.dao.*;
import com.clinic.util.AppointmentStatusThread;
import com.clinic.util.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardForm extends JFrame {

    private JLabel lblTodayAppts, lblTotalPatients, lblTotalDoctors, lblRevenue, lblPending, lblClock;
    private AppointmentStatusThread statusThread;

    private final AppointmentDAO apptDAO   = new AppointmentDAO();
    private final PatientDAO     patDAO    = new PatientDAO();
    private final DoctorDAO      docDAO    = new DoctorDAO();
    private final PaymentDAO     payDAO    = new PaymentDAO();

    public DashboardForm() {
        initComponents();
        loadStats();
        startStatusThread();
        setLocationRelativeTo(null);
    }

    private void startStatusThread() {
        statusThread = new AppointmentStatusThread(this::loadStats);
        statusThread.start();
    }

    private void loadStats() {
        SwingUtilities.invokeLater(() -> {
            try {
                lblTodayAppts.setText(String.valueOf(apptDAO.getTodayCount()));
                lblTotalPatients.setText(String.valueOf(patDAO.getTotalCount()));
                lblTotalDoctors.setText(String.valueOf(docDAO.getTotalCount()));
                lblRevenue.setText(String.format("LKR %.2f", payDAO.getTotalRevenueToday()));
                lblPending.setText(String.valueOf(apptDAO.getPendingCount()));
                lblClock.setText("Last updated: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                System.err.println("Dashboard load error: " + e.getMessage());
            }
        });
    }

    private void initComponents() {
        setTitle("Clinic Management System - Dashboard");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1000, 660);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (statusThread != null) statusThread.stopThread();
                SessionManager.logout();
                new LoginForm().setVisible(true);
                dispose();
            }
        });

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 60, 114));
        p.setPreferredSize(new Dimension(0, 60));
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("CLINIC MANAGEMENT SYSTEM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.WEST);

        String user = SessionManager.getCurrentUser() != null ?
            SessionManager.getCurrentUser().getUsername().toUpperCase() : "USER";
        JLabel userLabel = new JLabel("Logged in as: " + user + "   ");
        userLabel.setForeground(new Color(200, 225, 255));
        p.add(userLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel buildSidebar() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(20, 40, 80));
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] labels = {"Dashboard","Appointments","Patients","Doctors","Diagnosis","Payments","Reports","Export Data","Logout"};
        for (String lbl : labels) {
            JButton btn = createSidebarButton(lbl);
            p.add(btn);
            p.add(Box.createVerticalStrut(6));
        }
        return p;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(40, 70, 130));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(255,165,0)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(40,70,130)); }
        });

        btn.addActionListener(e -> handleNav(text));
        return btn;
    }

    private void handleNav(String target) {
        if ("Appointments".equals(target)) {
            new AppointmentForm().setVisible(true);
        } else if ("Patients".equals(target)) {
            new PatientForm().setVisible(true);
        } else if ("Doctors".equals(target)) {
            new DoctorForm().setVisible(true);
        } else if ("Diagnosis".equals(target)) {
            new DiagnosisForm().setVisible(true);
        } else if ("Payments".equals(target)) {
            new PaymentForm().setVisible(true);
        } else if ("Reports".equals(target)) {
            new ReportForm().setVisible(true);
        } else if ("Export Data".equals(target)) {
            new ExportForm().setVisible(true);
        } else if ("Logout".equals(target)) {
            if (statusThread != null) statusThread.stopThread();
            SessionManager.logout();
            new LoginForm().setVisible(true);
            dispose();
        } else {
            loadStats();
        }
    }

    private JPanel buildContent() {
        JPanel p = new JPanel(new GridLayout(2, 3, 16, 16));
        p.setBackground(new Color(245, 247, 252));
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        lblTodayAppts    = addStatCard(p, "Today's Appointments", "0", new Color(52, 152, 219));
        lblTotalPatients = addStatCard(p, "Total Patients",       "0", new Color(46, 204, 113));
        lblTotalDoctors  = addStatCard(p, "Active Doctors",       "0", new Color(155, 89, 182));
        lblRevenue       = addStatCard(p, "Today's Revenue",      "LKR 0.00", new Color(230, 126, 34));
        lblPending       = addStatCard(p, "Pending Appointments", "0", new Color(231, 76, 60));
        addStatCard(p, "Major Scenario", "Patient Appointment\nManagement", new Color(30, 60, 114));
        return p;
    }

    private JLabel addStatCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(color);
        card.add(lbl, BorderLayout.NORTH);

        JLabel valLbl = new JLabel(value, SwingConstants.CENTER);
        valLbl.setFont(new Font("Arial", Font.BOLD, 26));
        valLbl.setForeground(new Color(50, 50, 50));
        card.add(valLbl, BorderLayout.CENTER);

        parent.add(card);
        return valLbl;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(230, 235, 245));
        p.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        lblClock = new JLabel("Last updated: -");
        lblClock.setFont(new Font("Arial", Font.ITALIC, 11));
        p.add(lblClock, BorderLayout.EAST);
        JLabel copy = new JLabel("Clinic Management System");
        copy.setFont(new Font("Arial", Font.PLAIN, 11));
        p.add(copy, BorderLayout.WEST);
        return p;
    }
}
