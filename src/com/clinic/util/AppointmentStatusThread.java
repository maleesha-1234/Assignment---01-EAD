package com.clinic.util;

import com.clinic.dao.AppointmentDAO;
import com.clinic.model.Appointment;
import com.clinic.model.Appointment.Status;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Background thread that automatically marks past Confirmed appointments
 * as Completed and updates the dashboard stats every 60 seconds.
 * Demonstrates Multithreading in EAD-1.
 */
public class AppointmentStatusThread extends Thread {

    private volatile boolean running = true;
    private final AppointmentDAO dao = new AppointmentDAO();
    private Runnable onUpdate;

    public AppointmentStatusThread(Runnable onUpdate) {
        this.onUpdate = onUpdate;
        setDaemon(true);
        setName("AppointmentStatusMonitor");
    }

    @Override
    public void run() {
        while (running) {
            try {
                autoCompletePassedAppointments();
                if (onUpdate != null) {
                    onUpdate.run();
                }
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                System.err.println("[StatusThread] Error: " + e.getMessage());
            }
        }
    }

    private void autoCompletePassedAppointments() {
        try {
            List<Appointment> all = dao.getAllAppointments();
            LocalDate today = LocalDate.now();
            LocalTime now   = LocalTime.now();
            for (Appointment a : all) {
                if (a.getStatus() == Status.Confirmed) {
                    boolean isPast = a.getAppointmentDate().isBefore(today) ||
                        (a.getAppointmentDate().isEqual(today) && a.getAppointmentTime().isBefore(now));
                    if (isPast) {
                        dao.updateStatus(a.getAppointmentId(), Status.Completed);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[StatusThread] Auto-complete failed: " + e.getMessage());
        }
    }

    public void stopThread() {
        running = false;
        interrupt();
    }
}
