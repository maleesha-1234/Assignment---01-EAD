package com.clinic.ui;

import com.clinic.dao.PatientDAO;
import com.clinic.model.Patient;
import com.clinic.util.ClinicException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PatientForm extends JFrame {

    private JTextField txtFirst, txtLast, txtDOB, txtContact, txtEmail, txtAddress, txtBlood, txtSearch;
    private JTextArea  txtHistory;
    private JComboBox<String> cmbGender;
    private JTable     table;
    private DefaultTableModel tableModel;
    private final PatientDAO dao = new PatientDAO();
    private int selectedId = -1;

    public PatientForm() {
        initComponents();
        loadTable("");
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Patient Management");
        setSize(1000, 650);
        setLayout(new BorderLayout(8, 8));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        formPanel.setPreferredSize(new Dimension(360, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        String[] labels = {"First Name *","Last Name *","Date of Birth * (YYYY-MM-DD)","Gender *","Contact","Email","Blood Group","Address","Medical History"};
        int row = 0;
        for (String lbl : labels) {
            g.gridy = row; g.gridx = 0; g.gridwidth = 1;
            formPanel.add(new JLabel(lbl + ":"), g);
            g.gridx = 1;
            if ("First Name *".equals(lbl)) {
                txtFirst = new JTextField(16); formPanel.add(txtFirst, g);
            } else if ("Last Name *".equals(lbl)) {
                txtLast = new JTextField(16); formPanel.add(txtLast, g);
            } else if ("Date of Birth * (YYYY-MM-DD)".equals(lbl)) {
                txtDOB = new JTextField(16); formPanel.add(txtDOB, g);
            } else if ("Gender *".equals(lbl)) {
                cmbGender = new JComboBox<>(new String[]{"Male","Female","Other"});
                formPanel.add(cmbGender, g);
            } else if ("Contact".equals(lbl)) {
                txtContact = new JTextField(16); formPanel.add(txtContact, g);
            } else if ("Email".equals(lbl)) {
                txtEmail = new JTextField(16); formPanel.add(txtEmail, g);
            } else if ("Blood Group".equals(lbl)) {
                txtBlood = new JTextField(16); formPanel.add(txtBlood, g);
            } else if ("Address".equals(lbl)) {
                txtAddress = new JTextField(16); formPanel.add(txtAddress, g);
            } else if ("Medical History".equals(lbl)) {
                txtHistory = new JTextArea(3, 16);
                txtHistory.setLineWrap(true);
                g.gridwidth = 1;
                formPanel.add(new JScrollPane(txtHistory), g);
            }
            row++;
        }

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton btnSave   = styledBtn("Save",   new Color(46,204,113));
        JButton btnUpdate = styledBtn("Update", new Color(52,152,219));
        JButton btnDelete = styledBtn("Delete", new Color(231,76,60));
        JButton btnClear  = styledBtn("Clear",  new Color(149,165,166));
        btnPanel.add(btnSave); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        g.gridy = row; g.gridx = 0; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        btnSave.addActionListener(e   -> savePatient());
        btnUpdate.addActionListener(e -> updatePatient());
        btnDelete.addActionListener(e -> deletePatient());
        btnClear.addActionListener(e  -> clearForm());

        // --- Table Panel ---
        JPanel tablePanel = new JPanel(new BorderLayout(4, 4));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Patient Records"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> loadTable(txtSearch.getText()));
        searchPanel.add(btnSearch);
        JButton btnAll = new JButton("Show All");
        btnAll.addActionListener(e -> { txtSearch.setText(""); loadTable(""); });
        searchPanel.add(btnAll);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new String[]{"ID","Name","DOB","Gender","Contact","Email","Blood Group"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateForm();
        });
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void loadTable(String keyword) {
        try {
            List<Patient> list = keyword.isEmpty() ? dao.getAllPatients() : dao.searchPatients(keyword);
            tableModel.setRowCount(0);
            for (Patient p : list) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getFullName(), p.getDateOfBirth(),
                    p.getGender(), p.getContactNumber(), p.getEmail(), p.getBloodGroup()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        try {
            Patient p = dao.getById(selectedId);
            if (p == null) return;
            txtFirst.setText(p.getFirstName());
            txtLast.setText(p.getLastName());
            txtDOB.setText(p.getDateOfBirth().toString());
            cmbGender.setSelectedItem(p.getGender());
            txtContact.setText(p.getContactNumber());
            txtEmail.setText(p.getEmail());
            txtBlood.setText(p.getBloodGroup());
            txtAddress.setText(p.getAddress());
            txtHistory.setText(p.getMedicalHistory());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Patient buildFromForm() throws ClinicException {
        String first = txtFirst.getText().trim();
        String last  = txtLast.getText().trim();
        String dob   = txtDOB.getText().trim();
        if (first.isEmpty() || last.isEmpty() || dob.isEmpty())
            throw new ClinicException(ClinicException.Type.INVALID_INPUT, "First Name, Last Name and DOB are required.");
        LocalDate date;
        try { date = LocalDate.parse(dob); }
        catch (DateTimeParseException e) {
            throw new ClinicException(ClinicException.Type.INVALID_DATE, "Date of Birth must be in YYYY-MM-DD format.");
        }
        Patient p = new Patient();
        p.setId(selectedId);
        p.setFirstName(first); p.setLastName(last); p.setDateOfBirth(date);
        p.setGender((String) cmbGender.getSelectedItem());
        p.setContactNumber(txtContact.getText().trim());
        p.setEmail(txtEmail.getText().trim());
        p.setBloodGroup(txtBlood.getText().trim());
        p.setAddress(txtAddress.getText().trim());
        p.setMedicalHistory(txtHistory.getText().trim());
        return p;
    }

    private void savePatient() {
        try {
            Patient p = buildFromForm();
            dao.insert(p);
            JOptionPane.showMessageDialog(this, "Patient saved successfully.");
            clearForm(); loadTable("");
        } catch (ClinicException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a patient first."); return; }
        try {
            Patient p = buildFromForm();
            dao.update(p);
            JOptionPane.showMessageDialog(this, "Patient updated successfully.");
            clearForm(); loadTable("");
        } catch (ClinicException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a patient first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this patient?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.delete(selectedId);
                JOptionPane.showMessageDialog(this, "Patient deleted.");
                clearForm(); loadTable("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Delete error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtFirst.setText(""); txtLast.setText(""); txtDOB.setText("");
        txtContact.setText(""); txtEmail.setText(""); txtBlood.setText("");
        txtAddress.setText(""); txtHistory.setText("");
        cmbGender.setSelectedIndex(0);
        table.clearSelection();
    }

    private JButton styledBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 12));
        return b;
    }
}
