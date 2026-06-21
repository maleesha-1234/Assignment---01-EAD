package com.clinic.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    public enum Status { Pending, Confirmed, Completed, Cancelled }

    private int     appointmentId;
    private int     patientId;
    private int     doctorId;
    private String  patientName;
    private String  doctorName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String  reason;
    private Status  status;
    private String  notes;

    public Appointment() {}

    public Appointment(int appointmentId, int patientId, int doctorId,
                       LocalDate appointmentDate, LocalTime appointmentTime,
                       String reason, Status status, String notes) {
        this.appointmentId   = appointmentId;
        this.patientId       = patientId;
        this.doctorId        = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason          = reason;
        this.status          = status;
        this.notes           = notes;
    }

    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
