package com.clinic.ui;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.PaymentDAO;
import com.clinic.model.Appointment;
import com.clinic.model.Payment;
import com.clinic.util.ClinicException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaymentForm extends JFrame {

    private JTextField     txtApptId, txtAmount, txtDate;
    private JComboBox<String> cmbMethod, cmbStatus;
    private JTable         table;
    private DefaultTableModel tableModel;

    private final PaymentDAO     payDAO  = new PaymentDAO();
    private final AppointmentDAO apptDAO = new AppointmentDAO();
    private int selectedApptId = -1;

    public PaymentForm() {
        initComponents();
        loadCompletedAppointments();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Payment Management");
        setSize(960, 600);
        setLayout(new BorderLayout(8, 8));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Record Payment"));
        formPanel.setPreferredSize(new Dimension(340, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 8, 7, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(formPanel, g, row++, "Appointment ID:", txtApptId = new JTextField(16));
        txtApptId.setEditable(false); txtApptId.setBackground(new Color(240,240,240));

        addRow(formPanel, g, row++, "Amount (LKR) *:", txtAmount = new JTextField(16));
        addRow(formPanel, g, row++, "Payment Date *:", txtDate   = new JTextField(16));
        txtDate.setText(LocalDate.now().toString());

        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Method:"), g);
        cmbMethod = new JComboBox<>(new String[]{"Cash","Card","Online"});
        g.gridx = 1; formPanel.add(cmbMethod, g); row++;

        g.gridy = row; g.gridx = 0; formPanel.add(new JLabel("Status:"), g);
        cmbStatus = new JComboBox<>(new String[]{"Paid","Pending","Waived"});
        g.gridx = 1; formPanel.add(cmbStatus, g); row++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton btnSave   = styledBtn("Save Payment", new Color(46,204,113));
        JButton btnUpdate = styledBtn("Update",       new Color(52,152,219));
        JButton btnClear  = styledBtn("Clear",        new Color(149,165,166));
        btnPanel.add(btnSave); btnPanel.add(btnUpdate); btnPanel.add(btnClear);
        g.gridy = row; g.gridx = 0; g.gridwidth = 2;
        formPanel.add(btnPanel, g);

        btnSave.addActionListener(e   -> savePayment());
        btnUpdate.addActionListener(e -> updatePayment());
        btnClear.addActionListener(e  -> clearForm());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Completed Appointments - Select to Record Payment"));
        tableModel = new DefaultTableModel(
            new String[]{"Appt ID","Patient","Doctor","Date","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) loadExistingPayment();
        });
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JTextField field) {
        g.gridy = row; g.gridx = 0; g.gridwidth = 1; p.add(new JLabel(label), g);
        g.gridx = 1; p.add(field, g);
    }

    private void loadCompletedAppointments() {
        try {
            List<Appointment> list = apptDAO.getAllAppointments();
            tableModel.setRowCount(0);
            for (Appointment a : list) {
                if (a.getStatus() == Appointment.Status.Completed) {
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

    private void loadExistingPayment() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedApptId = (int) tableModel.getValueAt(row, 0);
        txtApptId.setText(String.valueOf(selectedApptId));
        try {
            Payment p = payDAO.getByAppointmentId(selectedApptId);
            if (p != null) {
                txtAmount.setText(p.getAmount().toPlainString());
                txtDate.setText(p.getPaymentDate().toString());
                cmbMethod.setSelectedItem(p.getPaymentMethod());
                cmbStatus.setSelectedItem(p.getStatus());
            } else {
                txtAmount.setText(""); txtDate.setText(LocalDate.now().toString());
                cmbMethod.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Payment buildFromForm() throws ClinicException {
        if (selectedApptId < 0)
            throw new ClinicException(ClinicException.Type.INVALID_INPUT, "Select an appointment first.");
        String amtStr = txtAmount.getText().trim();
        if (amtStr.isEmpty())
            throw new ClinicException(ClinicException.Type.INVALID_INPUT, "Amount is required.");
        BigDecimal amt;
        try { amt = new BigDecimal(amtStr); }
        catch (NumberFormatException e) {
            throw new ClinicException(ClinicException.Type.PAYMENT_ERROR, "Invalid amount value.");
        }
        if (amt.compareTo(BigDecimal.ZERO) <= 0)
            throw new ClinicException(ClinicException.Type.PAYMENT_ERROR, "Amount must be greater than zero.");

        LocalDate date;
        try { date = LocalDate.parse(txtDate.getText().trim()); }
        catch (Exception e) {
            throw new ClinicException(ClinicException.Type.INVALID_DATE, "Payment date must be YYYY-MM-DD.");
        }
        return new Payment(0, selectedApptId, amt,
            (String) cmbMethod.getSelectedItem(), date,
            (String) cmbStatus.getSelectedItem());
    }

    private void savePayment() {
        try {
            payDAO.insert(buildFromForm());
            JOptionPane.showMessageDialog(this, "Payment saved successfully.");
            clearForm(); loadCompletedAppointments();
        } catch (ClinicException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePayment() {
        try {
            payDAO.update(buildFromForm());
            JOptionPane.showMessageDialog(this, "Payment updated.");
            clearForm(); loadCompletedAppointments();
        } catch (ClinicException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedApptId = -1;
        txtApptId.setText(""); txtAmount.setText(""); txtDate.setText(LocalDate.now().toString());
        cmbMethod.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
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
