package com.clinic.model;

public class Diagnosis {

    private int    diagnosisId;
    private int    appointmentId;
    private String symptoms;
    private String diagnosis;
    private String prescription;
    private String notes;

    public Diagnosis() {}

    public Diagnosis(int diagnosisId, int appointmentId, String symptoms,
                     String diagnosis, String prescription, String notes) {
        this.diagnosisId   = diagnosisId;
        this.appointmentId = appointmentId;
        this.symptoms      = symptoms;
        this.diagnosis     = diagnosis;
        this.prescription  = prescription;
        this.notes         = notes;
    }

    public int getDiagnosisId() { return diagnosisId; }
    public void setDiagnosisId(int diagnosisId) { this.diagnosisId = diagnosisId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
