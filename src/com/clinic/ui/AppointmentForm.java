package com.clinic.ui;

import com.clinic.dao.*;
import com.clinic.model.*;
import com.clinic.model.Appointment.Status;
import com.clinic.util.ClinicException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Major Scenario: Patient Appointment Management
 * Handles booking, viewing, updating and cancelling appointments.
 */
public class AppointmentForm extends JFrame {

    private JComboBox<Patient> cmbPatient;
    private JComboBox<Doctor>  cmbDoctor;
    private JTextField         txtDate, txtTime, txtReason, txtNotes, txtSearch;
    private JComboBox<String>  cmbStatus;
    private JTable             table;
    private DefaultTableModel  tableModel;

    private final AppointmentDAO apptDAO = new AppointmentDAO();
    private final PatientDAO     patDAO  = new PatientDAO();
    private final DoctorDAO      docDAO  = new DoctorDAO();
    private int selectedId = -1;

    public AppointmentForm() {
        initComponents();
        loadComboBoxes();
        loadTable();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Appointment Management - Major Scenario: Patient Appointment");
        setSize(1100, 660);
        setLayout(new BorderLayout(8, 8));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Header banner showing this is the major scenario
        JLabel banner = new JLabel("  MAJOR SCENARIO: Patient Appointment Management", SwingConstants.LEFT);
        banner.setOpaque(true);
        banner.setBackground(new Color(30, 60, 114));
        banner.setForeground(Color.WHITE);
        banner.setFont(new Font("Arial", Font.BOLD, 14));
        banner.setPreferredSize(new Dimension(0, 36));
        add(banner, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Appointment Details"));
        formPanel.setPreferredSize(new Dimension(370, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int row = 0;
        // Patient
        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Patient *:"), g);
        cmbPatient = new JComboBox<>();
        g.gridx = 1; formPanel.add(cmbPatient, g); row++;

        // Doctor
        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Doctor *:"), g);
        cmbDoctor = new JComboBox<>();
        g.gridx = 1; formPanel.add(cmbDoctor, g); row++;

        // Date
        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Date * (YYYY-MM-DD):"), g);
        txtDate = new JTextField(16);
        g.gridx = 1; formPanel.add(txtDate, g); row++;

        // Time
        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Time * (HH:MM):"), g);
        txtTime = new JTextField(16);
        g.gridx = 1; formPanel.add(txtTime, g); row++;

        // Reason
        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Reason for Visit:"), g);
        txtReason = new JTextField(16);
        g.gridx = 1; formPanel.add(txtReason, g); row++;

        // Status
        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Status:"), g);
        cmbStatus = new JComboBox<>(new String[]{"Pending","Confirmed","Completed","Cancelled"});
        g.gridx = 1; formPanel.add(cmbStatus, g); row++;

        // Notes
        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Notes:"), g);
        txtNotes = new JTextField(16);
        g.gridx = 1; formPanel.add(txtNotes, g); row++;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        JButton btnBook   = styledBtn("Book",   new Color(46,204,113));
        JButton btnUpdate = styledBtn("Update", new Color(52,152,219));
        JButton btnCancel = styledBtn("Cancel Appt", new Color(231,76,60));
        JButton btnClear  = styledBtn("Clear",  new Color(149,165,166));
        btnPanel.add(btnBook); btnPanel.add(btnUpdate); btnPanel.add(btnCancel); btnPanel.add(btnClear);
        g.gridy = row; g.gridx = 0; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        btnBook.addActionListener(e   -> bookAppointment());
        btnUpdate.addActionListener(e -> updateAppointment());
        btnCancel.addActionListener(e -> cancelAppointment());
        btnClear.addActionListener(e  -> clearForm());

        // Table
        JPanel tablePanel = new JPanel(new BorderLayout(4, 4));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Appointment Records"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Filter by Date:"));
        txtSearch = new JTextField(12); txtSearch.setToolTipText("YYYY-MM-DD");
        searchPanel.add(txtSearch);
        JButton btnFilter = new JButton("Filter");
        btnFilter.addActionListener(e -> filterByDate());
        searchPanel.add(btnFilter);
        JButton btnAll = new JButton("Show All");
        btnAll.addActionListener(e -> { txtSearch.setText(""); loadTable(); });
        searchPanel.add(btnAll);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new String[]{"ID","Patient","Doctor","Date","Time","Reason","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateForm();
        });
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void loadComboBoxes() {
        try {
            cmbPatient.removeAllItems();
            for (Patient p : patDAO.getAllPatients()) cmbPatient.addItem(p);
            cmbDoctor.removeAllItems();
            for (Doctor d : docDAO.getActiveDoctors()) cmbDoctor.addItem(d);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void loadTable() {
        try {
            List<Appointment> list = apptDAO.getAllAppointments();
            tableModel.setRowCount(0);
            for (Appointment a : list) {
                tableModel.addRow(new Object[]{
                    a.getAppointmentId(), a.getPatientName(), a.getDoctorName(),
                    a.getAppointmentDate(), a.getAppointmentTime(), a.getReason(), a.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterByDate() {
        String dateStr = txtSearch.getText().trim();
        if (dateStr.isEmpty()) { loadTable(); return; }
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<Appointment> list = apptDAO.getByDate(date);
            tableModel.setRowCount(0);
            for (Appointment a : list) {
                tableModel.addRow(new Object[]{
                    a.getAppointmentId(), a.getPatientName(), a.getDoctorName(),
                    a.getAppointmentDate(), a.getAppointmentTime(), a.getReason(), a.getStatus()
                });
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Date format must be YYYY-MM-DD.", "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtDate.setText(tableModel.getValueAt(row, 3).toString());
        txtTime.setText(tableModel.getValueAt(row, 4).toString().substring(0, 5));
        txtReason.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());
    }

    private Appointment buildFromForm() throws ClinicException {
        Patient patient = (Patient) cmbPatient.getSelectedItem();
        Doctor  doctor  = (Doctor)  cmbDoctor.getSelectedItem();
        String  dateStr = txtDate.getText().trim();
        String  timeStr = txtTime.getText().trim();

        if (patient == null || doctor == null || dateStr.isEmpty() || timeStr.isEmpty())
            throw new ClinicException(ClinicException.Type.INVALID_INPUT, "Patient, Doctor, Date and Time are required.");

        LocalDate date;
        LocalTime time;
        try { date = LocalDate.parse(dateStr); }
        catch (DateTimeParseException e) {
            throw new ClinicException(ClinicException.Type.INVALID_DATE, "Date must be YYYY-MM-DD format.");
        }
        try { time = LocalTime.parse(timeStr); }
        catch (DateTimeParseException e) {
            throw new ClinicException(ClinicException.Type.INVALID_DATE, "Time must be HH:MM format.");
        }
        if (date.isBefore(LocalDate.now()) && selectedId < 0)
            throw new ClinicException(ClinicException.Type.INVALID_DATE, "Cannot book an appointment in the past.");

        Appointment a = new Appointment();
        a.setAppointmentId(selectedId);
        a.setPatientId(patient.getId());
        a.setDoctorId(doctor.getId());
        a.setAppointmentDate(date);
        a.setAppointmentTime(time);
        a.setReason(txtReason.getText().trim());
        a.setStatus(Status.valueOf((String) cmbStatus.getSelectedItem()));
        a.setNotes(txtNotes.getText().trim());
        return a;
    }

    private void bookAppointment() {
        try {
            Appointment a = buildFromForm();
            // Double-booking check
            if (apptDAO.isDoubleBooked(a.getDoctorId(), a.getAppointmentDate(),
                    a.getAppointmentTime().toString(), -1)) {
                throw new ClinicException(ClinicException.Type.DOUBLE_BOOKING,
                    "This doctor already has an appointment at that date/time.");
            }
            apptDAO.insert(a);
            JOptionPane.showMessageDialog(this, "Appointment booked successfully.");
            clearForm(); loadTable();
        } catch (ClinicException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAppointment() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select an appointment first."); return; }
        try {
            Appointment a = buildFromForm();
            if (apptDAO.isDoubleBooked(a.getDoctorId(), a.getAppointmentDate(),
                    a.getAppointmentTime().toString(), selectedId)) {
                throw new ClinicException(ClinicException.Type.DOUBLE_BOOKING,
                    "This doctor already has an appointment at that date/time.");
            }
            apptDAO.update(a);
            JOptionPane.showMessageDialog(this, "Appointment updated.");
            clearForm(); loadTable();
        } catch (ClinicException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select an appointment first."); return; }
        int c = JOptionPane.showConfirmDialog(this, "Cancel this appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try {
                apptDAO.updateStatus(selectedId, Status.Cancelled);
                JOptionPane.showMessageDialog(this, "Appointment cancelled.");
                clearForm(); loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtDate.setText(""); txtTime.setText(""); txtReason.setText(""); txtNotes.setText("");
        cmbStatus.setSelectedIndex(0); table.clearSelection();
    }

    private JButton styledBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 12));
        return b;
    }
}
