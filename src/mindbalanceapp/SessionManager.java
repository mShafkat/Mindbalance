package mindbalanceapp;

public class SessionManager {
    private static int currentUserId = 0;
    private static String currentUserRole = null;
    private static String currentUserEmail = null;
    private static boolean isUnregisteredUser = false;
    private static String currentUserName = null;

    // Set current logged-in user without name
    public static void setCurrentUser(int userId, String role, String email) {
        currentUserId = userId;
        currentUserRole = role;
        currentUserEmail = email;
        currentUserName = null;
    }

    // ✅ Overloaded method with name
    public static void setCurrentUser(int userId, String role, String email, String name) {
        currentUserId = userId;
        currentUserRole = role;
        currentUserEmail = email;
        currentUserName = name;
    }

    // Getter for name
    public static String getCurrentUserName() {
        return currentUserName != null ? currentUserName : "User";
    }
    
    // Set guest user (from Get Started Free)
    public static void setUnregisteredUser() {
        currentUserId = -1; // Use -1 to clearly indicate guest
        currentUserRole = "unregistered";
        currentUserEmail = "guest@mindbalance.com";
    }

    // Getters
    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    // Check if user is logged in
    public static boolean isLoggedIn() {
        return currentUserId > 0;
    }

    // ✅ New: Check if current user is admin
    public static boolean isAdmin() {
        return currentUserRole != null && currentUserRole.equalsIgnoreCase("admin");
    }

    // Clear session (logout)
    public static void clear() {
        currentUserId = 0;
        currentUserRole = null;
        currentUserEmail = null;
    }
}
