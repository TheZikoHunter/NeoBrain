package insea.neobrain;

import insea.neobrain.config.HibernateUtil;
import insea.neobrain.ui.login.LoginWindow;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the Inventory Management System
 * 
 * This application provides a role-based inventory and sales management system
 * with support for three main roles:
 * - ADMIN: Personnel management
 * - RESPONSABLE_STOCK: Product and inventory management
 * - EMPLOYE_STOCK: Inventory task execution
 */
public class Main {
    
    public static void main(String[] args) {
        // Set system properties for better UI experience
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Configure Look and Feel
        configureLookAndFeel();
        
        // Initialize Hibernate and database connection
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("Database connection established successfully.");
        } catch (Exception e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
            showErrorDialog("Database Connection Error", 
                "Failed to connect to the database. Please check your configuration.");
            return;
        }
        
        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginWindow().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog("Application Startup Error", 
                    "Failed to start the application: " + e.getMessage());
            }
        });
    }
    
    /**
     * Configure the application's Look and Feel for a modern appearance
     */
    private static void configureLookAndFeel() {
        try {
            // Try to use the system Look and Feel for native appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Configure UI defaults for a more modern appearance
            UIManager.put("Button.arc", 5);
            UIManager.put("Component.arc", 5);
            UIManager.put("TextComponent.arc", 5);
            
            // Set default fonts
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 12);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("PasswordField.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", defaultFont.deriveFont(Font.BOLD));
            
        } catch (Exception e) {
            System.err.println("Warning: Could not set system Look and Feel. Using default.");
        }
    }
    
    /**
     * Display an error dialog to the user
     */
    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
}
