package com.clinic.util;

import com.clinic.model.User;

public class SessionManager {

    private static User currentUser = null;

    private SessionManager() {}

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static void logout() { currentUser = null; }
    public static boolean isLoggedIn() { return currentUser != null; }
}
