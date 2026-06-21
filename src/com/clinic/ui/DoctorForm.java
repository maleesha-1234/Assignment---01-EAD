package com.clinic.ui;

import com.clinic.dao.DoctorDAO;
import com.clinic.model.Doctor;
import com.clinic.util.ClinicException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorForm extends JFrame {

    private JTextField txtFirst, txtLast, txtSpec, txtContact, txtEmail, txtDays;
    private JComboBox<String> cmbStatus;
    private JTable table;
    private DefaultTableModel tableModel;
    private final DoctorDAO dao = new DoctorDAO();
    private int selectedId = -1;

    public DoctorForm() {
        initComponents();
        loadTable();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Doctor Management");
        setSize(950, 600);
        setLayout(new BorderLayout(8, 8));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Doctor Details"));
        formPanel.setPreferredSize(new Dimension(340, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        String[] fields = {"First Name *","Last Name *","Specialization *","Contact","Email","Available Days","Status"};
        int row = 0;
        for (String lbl : fields) {
            g.gridy = row; g.gridx = 0;
            formPanel.add(new JLabel(lbl + ":"), g);
            g.gridx = 1;
            if ("First Name *".equals(lbl)) {
                txtFirst = new JTextField(16); formPanel.add(txtFirst, g);
            } else if ("Last Name *".equals(lbl)) {
                txtLast = new JTextField(16); formPanel.add(txtLast, g);
            } else if ("Specialization *".equals(lbl)) {
                txtSpec = new JTextField(16); formPanel.add(txtSpec, g);
            } else if ("Contact".equals(lbl)) {
                txtContact = new JTextField(16); formPanel.add(txtContact, g);
            } else if ("Email".equals(lbl)) {
                txtEmail = new JTextField(16); formPanel.add(txtEmail, g);
            } else if ("Available Days".equals(lbl)) {
                txtDays = new JTextField(16); txtDays.setToolTipText("e.g. Mon,Tue,Wed");
                formPanel.add(txtDays, g);
            } else if ("Status".equals(lbl)) {
                cmbStatus = new JComboBox<>(new String[]{"active","inactive"});
                formPanel.add(cmbStatus, g);
            }
            row++;
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton btnSave   = styledBtn("Save",   new Color(46,204,113));
        JButton btnUpdate = styledBtn("Update", new Color(52,152,219));
        JButton btnDelete = styledBtn("Delete", new Color(231,76,60));
        JButton btnClear  = styledBtn("Clear",  new Color(149,165,166));
        btnPanel.add(btnSave); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        g.gridy = row; g.gridx = 0; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        btnSave.addActionListener(e   -> saveDoctor());
        btnUpdate.addActionListener(e -> updateDoctor());
        btnDelete.addActionListener(e -> deleteDoctor());
        btnClear.addActionListener(e  -> clearForm());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Doctor Records"));
        tableModel = new DefaultTableModel(
            new String[]{"ID","Name","Specialization","Contact","Email","Available Days","Status"}, 0) {
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

    private void loadTable() {
        try {
            List<Doctor> list = dao.getAllDoctors();
            tableModel.setRowCount(0);
            for (Doctor d : list) {
                tableModel.addRow(new Object[]{
                    d.getId(), "Dr. " + d.getFullName(), d.getSpecialization(),
                    d.getContactNumber(), d.getEmail(), d.getAvailableDays(), d.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        try {
            Doctor d = dao.getById(selectedId);
            if (d == null) return;
            txtFirst.setText(d.getFirstName()); txtLast.setText(d.getLastName());
            txtSpec.setText(d.getSpecialization()); txtContact.setText(d.getContactNumber());
            txtEmail.setText(d.getEmail()); txtDays.setText(d.getAvailableDays());
            cmbStatus.setSelectedItem(d.getStatus());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Doctor buildFromForm() throws ClinicException {
        String first = txtFirst.getText().trim();
        String last  = txtLast.getText().trim();
        String spec  = txtSpec.getText().trim();
        if (first.isEmpty() || last.isEmpty() || spec.isEmpty())
            throw new ClinicException(ClinicException.Type.INVALID_INPUT, "First Name, Last Name and Specialization are required.");
        Doctor d = new Doctor();
        d.setId(selectedId);
        d.setFirstName(first); d.setLastName(last); d.setSpecialization(spec);
        d.setContactNumber(txtContact.getText().trim());
        d.setEmail(txtEmail.getText().trim());
        d.setAvailableDays(txtDays.getText().trim());
        d.setStatus((String) cmbStatus.getSelectedItem());
        return d;
    }

    private void saveDoctor() {
        try { dao.insert(buildFromForm()); JOptionPane.showMessageDialog(this,"Doctor saved."); clearForm(); loadTable(); }
        catch (ClinicException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(),"Validation",JOptionPane.WARNING_MESSAGE); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void updateDoctor() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this,"Select a doctor first."); return; }
        try { dao.update(buildFromForm()); JOptionPane.showMessageDialog(this,"Doctor updated."); clearForm(); loadTable(); }
        catch (ClinicException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(),"Validation",JOptionPane.WARNING_MESSAGE); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void deleteDoctor() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this,"Select a doctor first."); return; }
        int c = JOptionPane.showConfirmDialog(this,"Delete this doctor?","Confirm",JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try { dao.delete(selectedId); JOptionPane.showMessageDialog(this,"Doctor deleted."); clearForm(); loadTable(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtFirst.setText(""); txtLast.setText(""); txtSpec.setText("");
        txtContact.setText(""); txtEmail.setText(""); txtDays.setText("");
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
