package com.clinic.model;

import java.time.LocalDate;

public class Patient extends Person {

    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String bloodGroup;
    private String medicalHistory;

    public Patient() { super(); }

    public Patient(int id, String firstName, String lastName, LocalDate dateOfBirth,
                   String gender, String contactNumber, String email,
                   String address, String bloodGroup, String medicalHistory) {
        super(id, firstName, lastName, contactNumber, email);
        this.dateOfBirth    = dateOfBirth;
        this.gender         = gender;
        this.address        = address;
        this.bloodGroup     = bloodGroup;
        this.medicalHistory = medicalHistory;
    }

    @Override
    public String getRole() { return "Patient"; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
}
