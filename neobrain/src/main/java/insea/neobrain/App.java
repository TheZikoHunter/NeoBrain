package insea.neobrain;

import javax.swing.*;

import insea.neobrain.entity.Admin;

import java.awt.*;

public class App extends JFrame {
    // Instance variables for form fields
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JSpinner birthDateSpinner;
    private final insea.neobrain.dao.AdminDAO adminDAO = new insea.neobrain.dao.AdminDAO();

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new App());
    }

    public App() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("NeoBrain - Configuration Admin");
        
        // Main container with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Form
        JPanel formPanel = createFormPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Window settings
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("Configuration du compte admin", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JLabel subtitleLabel = new JLabel("Créez votre compte administrateur pour commencer", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        String[] labels = {"Nom d'utilisateur", "Mot de passe", "Email", "Prénom", "Nom", "Date de naissance", "Téléphone"};
        String[] placeholders = {"admin", "mot de passe", "admin@email.com", "Ahmed", "Ben Ali", "1990-01-01", "0600000000"};
        usernameField = createStyledTextField(placeholders[0]);
        passwordField = createStyledPasswordField(placeholders[1]);
        emailField = createStyledTextField(placeholders[2]);
        firstNameField = createStyledTextField(placeholders[3]);
        lastNameField = createStyledTextField(placeholders[4]);
        // Use a date picker for birthDateField
        birthDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd");
        birthDateSpinner.setEditor(dateEditor);
        birthDateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        birthDateSpinner.setPreferredSize(new Dimension(0, 40));
        phoneField = createStyledTextField(placeholders[6]);
        JTextField[] fields = {usernameField, passwordField, emailField, firstNameField, lastNameField, null, phoneField};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(15, 0, 5, 0);
            gbc.fill = GridBagConstraints.NONE;
            JLabel label = new JLabel(labels[i] + " :");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(new Color(52, 73, 94));
            formPanel.add(label, gbc);
            gbc.gridy = i;
            gbc.gridx = 1;
            gbc.insets = new Insets(0, 10, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            if (i == 5) {
                formPanel.add(birthDateSpinner, gbc);
            } else {
                formPanel.add(fields[i], gbc);
            }
        }
        return formPanel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(25);
        styleTextField(field);
        field.setText("");
        field.setToolTipText(placeholder);
        return field;
    }
    
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(25);
        styleTextField(field);
        field.setText("");
        field.setToolTipText(placeholder);
        return field;
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(0, 40));
        
        // Add focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                    BorderFactory.createEmptyBorder(9, 11, 9, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        });
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        JButton createButton = new JButton("Créer le compte administrateur");
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(46, 204, 113));
        createButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        createButton.setFocusPainted(false);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add action listener for form submission
        createButton.addActionListener(e -> saveAdminToDatabase());
        // Hover effect
        createButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createButton.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createButton.setBackground(new Color(46, 204, 113));
            }
        });
        buttonPanel.add(createButton);
        return buttonPanel;
    }

    // Save admin to database
    private void saveAdminToDatabase() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        java.util.Date date = (java.util.Date) birthDateSpinner.getValue();
        java.time.LocalDate birthDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            // Hash the password before saving
            String hashedPassword = hashPassword(password);
            Admin admin = new Admin(username, hashedPassword, email, firstName, lastName, birthDate, phone);
            adminDAO.saveAdmin(admin);
            
            int option = JOptionPane.showConfirmDialog(this, 
                "Compte administrateur créé avec succès!\nVoulez-vous aller à la page de connexion?", 
                "Succès", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            
            if (option == JOptionPane.YES_OPTION) {
                dispose();
                new Login();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la création du compte: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
