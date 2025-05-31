package insea.neobrain;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Check if any admin exists in the database
                // If no admin exists, show registration form
                // If admin exists, show login form
                
                // For simplicity, we'll always start with login
                // Users can click "Create account" if needed
                new Login();
                
            } catch (Exception e) {
                // If there's a database error, show registration form
                // This handles the case where database doesn't exist yet
                System.out.println("Database not accessible, showing registration form: " + e.getMessage());
                new App();
            }
        });
    }
}
