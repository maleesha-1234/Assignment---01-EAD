package com.clinic.model;

/**
 * Abstract base class demonstrating OOP inheritance.
 * Both Doctor and Patient are Persons.
 */
public abstract class Person {

    protected int id;
    protected String firstName;
    protected String lastName;
    protected String contactNumber;
    protected String email;

    public Person() {}

    public Person(int id, String firstName, String lastName, String contactNumber, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public abstract String getRole();

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return getFullName();
    }
}
