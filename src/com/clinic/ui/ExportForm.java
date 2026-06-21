package com.clinic.ui;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.PatientDAO;
import com.clinic.util.DataExporter;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class ExportForm extends JFrame {

    private final PatientDAO     patDAO  = new PatientDAO();
    private final AppointmentDAO apptDAO = new AppointmentDAO();

    public ExportForm() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Export Data - IO Streams");
        setSize(440, 260);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel header = new JPanel();
        header.setBackground(new Color(46, 204, 113));
        header.setPreferredSize(new Dimension(0, 50));
        JLabel lbl = new JLabel("Export Data to CSV/Text Files", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 15));
        header.add(lbl);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        center.setBackground(Color.WHITE);

        JButton btnPatients     = exportBtn("Export Patient List to CSV",     new Color(52,152,219));
        JButton btnAppointments = exportBtn("Export Appointments to CSV",      new Color(155,89,182));

        btnPatients.addActionListener(e     -> exportPatients());
        btnAppointments.addActionListener(e -> exportAppointments());

        center.add(btnPatients);
        center.add(btnAppointments);
        add(center, BorderLayout.CENTER);

        JLabel note = new JLabel("Uses Java IO Streams (BufferedWriter/FileWriter).", SwingConstants.CENTER);
        note.setFont(new Font("Arial", Font.ITALIC, 11));
        note.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(note, BorderLayout.SOUTH);
    }

    private void exportPatients() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Patient Export");
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        fc.setSelectedFile(new File("patients_export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";
            try {
                DataExporter.exportPatients(patDAO.getAllPatients(), path);
                JOptionPane.showMessageDialog(this, "Patients exported to:\n" + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportAppointments() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Appointment Export");
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        fc.setSelectedFile(new File("appointments_export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";
            try {
                DataExporter.exportAppointments(apptDAO.getAllAppointments(), path);
                JOptionPane.showMessageDialog(this, "Appointments exported to:\n" + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton exportBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 13));
        return b;
    }
}
