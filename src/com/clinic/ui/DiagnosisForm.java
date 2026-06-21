package com.clinic.ui;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.DiagnosisDAO;
import com.clinic.model.Appointment;
import com.clinic.model.Diagnosis;
import com.clinic.util.ClinicException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DiagnosisForm extends JFrame {

    private JTextField    txtApptId, txtSymptoms, txtPrescription;
    private JTextArea     txtDiagnosis, txtNotes;
    private JTable        table;
    private DefaultTableModel tableModel;

    private final DiagnosisDAO  diagDAO = new DiagnosisDAO();
    private final AppointmentDAO apptDAO = new AppointmentDAO();
    private int selectedApptId = -1;

    public DiagnosisForm() {
        initComponents();
        loadCompletedAppointments();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Diagnosis & Treatment Records");
        setSize(1000, 620);
        setLayout(new BorderLayout(8, 8));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Record Diagnosis (Completed Appointments)"));
        formPanel.setPreferredSize(new Dimension(380, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Appointment ID *:"), g);
        txtApptId = new JTextField(16); txtApptId.setEditable(false);
        txtApptId.setBackground(new Color(240, 240, 240));
        g.gridx = 1; formPanel.add(txtApptId, g); row++;

        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Symptoms:"), g);
        txtSymptoms = new JTextField(16);
        g.gridx = 1; formPanel.add(txtSymptoms, g); row++;

        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Diagnosis *:"), g);
        txtDiagnosis = new JTextArea(3, 16); txtDiagnosis.setLineWrap(true);
        g.gridx = 1; formPanel.add(new JScrollPane(txtDiagnosis), g); row++;

        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Prescription:"), g);
        txtPrescription = new JTextField(16);
        g.gridx = 1; formPanel.add(txtPrescription, g); row++;

        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Notes:"), g);
        txtNotes = new JTextArea(3, 16); txtNotes.setLineWrap(true);
        g.gridx = 1; formPanel.add(new JScrollPane(txtNotes), g); row++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton btnSave   = styledBtn("Save Diagnosis", new Color(46,204,113));
        JButton btnUpdate = styledBtn("Update",         new Color(52,152,219));
        JButton btnClear  = styledBtn("Clear",          new Color(149,165,166));
        btnPanel.add(btnSave); btnPanel.add(btnUpdate); btnPanel.add(btnClear);
        g.gridy = row; g.gridx = 0; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        btnSave.addActionListener(e   -> saveDiagnosis());
        btnUpdate.addActionListener(e -> updateDiagnosis());
        btnClear.addActionListener(e  -> clearForm());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Completed Appointments (select to record/view diagnosis)"));
        tableModel = new DefaultTableModel(
            new String[]{"Appt ID","Patient","Doctor","Date","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) loadDiagnosis();
        });
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void loadCompletedAppointments() {
        try {
            List<Appointment> list = apptDAO.getAllAppointments();
            tableModel.setRowCount(0);
            for (Appointment a : list) {
                if (a.getStatus() == Appointment.Status.Completed || a.getStatus() == Appointment.Status.Confirmed) {
                    tableModel.addRow(new Object[]{
                        a.getAppointmentId(), a.getPatientName(), a.getDoctorName(),
                        a.getAppointmentDate(), a.getStatus()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDiagnosis() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedApptId = (int) tableModel.getValueAt(row, 0);
        txtApptId.setText(String.valueOf(selectedApptId));
        try {
            Diagnosis d = diagDAO.getByAppointmentId(selectedApptId);
            if (d != null) {
                txtSymptoms.setText(d.getSymptoms());
                txtDiagnosis.setText(d.getDiagnosis());
                txtPrescription.setText(d.getPrescription());
                txtNotes.setText(d.getNotes());
            } else {
                clearFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Diagnosis buildFromForm() throws ClinicException {
        if (selectedApptId < 0)
            throw new ClinicException(ClinicException.Type.INVALID_INPUT, "Select an appointment from the table first.");
        String diag = txtDiagnosis.getText().trim();
        if (diag.isEmpty())
            throw new ClinicException(ClinicException.Type.INVALID_INPUT, "Diagnosis field is required.");
        Diagnosis d = new Diagnosis();
        d.setAppointmentId(selectedApptId);
        d.setSymptoms(txtSymptoms.getText().trim());
        d.setDiagnosis(diag);
        d.setPrescription(txtPrescription.getText().trim());
        d.setNotes(txtNotes.getText().trim());
        return d;
    }

    private void saveDiagnosis() {
        try {
            diagDAO.insert(buildFromForm());
            JOptionPane.showMessageDialog(this, "Diagnosis saved successfully.");
            clearForm(); loadCompletedAppointments();
        } catch (ClinicException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDiagnosis() {
        try {
            diagDAO.update(buildFromForm());
            JOptionPane.showMessageDialog(this, "Diagnosis updated.");
            clearForm(); loadCompletedAppointments();
        } catch (ClinicException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() { selectedApptId = -1; txtApptId.setText(""); clearFields(); table.clearSelection(); }
    private void clearFields() { txtSymptoms.setText(""); txtDiagnosis.setText(""); txtPrescription.setText(""); txtNotes.setText(""); }

    private JButton styledBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 12));
        return b;
    }
}
