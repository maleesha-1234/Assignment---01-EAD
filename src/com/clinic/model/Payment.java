package com.clinic.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {

    private int        paymentId;
    private int        appointmentId;
    private BigDecimal amount;
    private String     paymentMethod;
    private LocalDate  paymentDate;
    private String     status;

    public Payment() {}

    public Payment(int paymentId, int appointmentId, BigDecimal amount,
                   String paymentMethod, LocalDate paymentDate, String status) {
        this.paymentId     = paymentId;
        this.appointmentId = appointmentId;
        this.amount        = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate   = paymentDate;
        this.status        = status;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
