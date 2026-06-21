package com.clinic.util;

import com.clinic.model.Appointment;
import com.clinic.model.Patient;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Handles file export using Java IO Streams.
 * Demonstrates IO Streams concept in EAD-1.
 */
public class DataExporter {

    public static String exportPatients(List<Patient> patients, String filePath) throws ClinicException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Clinic Management System - Patient Export");
            writer.newLine();
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.newLine();
            writer.write("=".repeat(80));
            writer.newLine();
            writer.write("ID,Full Name,DOB,Gender,Contact,Email,Blood Group,Address");
            writer.newLine();

            for (Patient p : patients) {
                String line = p.getId() + "," +
                              p.getFullName() + "," +
                              p.getDateOfBirth() + "," +
                              p.getGender() + "," +
                              p.getContactNumber() + "," +
                              p.getEmail() + "," +
                              p.getBloodGroup() + "," +
                              (p.getAddress() != null ? p.getAddress().replace(",", ";") : "");
                writer.write(line);
                writer.newLine();
            }
            writer.write("=".repeat(80));
            writer.newLine();
            writer.write("Total Records: " + patients.size());
            return filePath;
        } catch (IOException e) {
            throw new ClinicException(ClinicException.Type.DATABASE_ERROR, "Export failed: " + e.getMessage());
        }
    }

    public static String exportAppointments(List<Appointment> appointments, String filePath) throws ClinicException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Clinic Management System - Appointment Export");
            writer.newLine();
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.newLine();
            writer.write("=".repeat(80));
            writer.newLine();
            writer.write("ID,Patient,Doctor,Date,Time,Reason,Status");
            writer.newLine();

            for (Appointment a : appointments) {
                String line = a.getAppointmentId() + "," +
                              a.getPatientName() + "," +
                              a.getDoctorName() + "," +
                              a.getAppointmentDate() + "," +
                              a.getAppointmentTime() + "," +
                              (a.getReason() != null ? a.getReason().replace(",", ";") : "") + "," +
                              a.getStatus();
                writer.write(line);
                writer.newLine();
            }
            writer.write("=".repeat(80));
            writer.newLine();
            writer.write("Total Records: " + appointments.size());
            return filePath;
        } catch (IOException e) {
            throw new ClinicException(ClinicException.Type.DATABASE_ERROR, "Export failed: " + e.getMessage());
        }
    }
}
