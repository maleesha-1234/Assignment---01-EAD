package com.clinic.model;

public class Doctor extends Person {

    private String specialization;
    private String availableDays;
    private String status;

    public Doctor() { super(); }

    public Doctor(int id, String firstName, String lastName, String specialization,
                  String contactNumber, String email, String availableDays, String status) {
        super(id, firstName, lastName, contactNumber, email);
        this.specialization = specialization;
        this.availableDays  = availableDays;
        this.status         = status;
    }

    @Override
    public String getRole() { return "Doctor"; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Dr. " + getFullName() + " (" + specialization + ")";
    }
}
