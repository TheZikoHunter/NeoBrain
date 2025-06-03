package insea.neobrain.ui.admin;

import insea.neobrain.entity.*;
import insea.neobrain.ui.common.UIConstants;
import insea.neobrain.ui.common.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Dialog for adding or editing personnel information
 */
public class PersonnelFormDialog extends JDialog implements ActionListener {
    
    private boolean confirmed = false;
    private Personnel personnel;
    private boolean isEditMode;
    
    // Form components
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JTextField telephoneField;
    private JTextField nomUtilisateurField;
    private JPasswordField motDePasseField;
    private JComboBox<Civilite> civiliteCombo;
    private JComboBox<Nationalite> nationaliteCombo;
    private JComboBox<Role> roleCombo;
    private JSpinner dateNaissanceSpinner;
    private JSpinner dateEmbaucheSpinner;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    public PersonnelFormDialog(Window parent, String title, boolean modal) {
        this(parent, title, modal, null);
    }
    
    public PersonnelFormDialog(Window parent, String title, boolean modal, Personnel personnel) {
        super(parent, title, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        this.personnel = personnel;
        this.isEditMode = (personnel != null);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureDialog();
        
        if (isEditMode) {
            populateFields();
        }
    }
    
    private void initializeComponents() {
        nomField = UIUtils.createStyledTextField();
        prenomField = UIUtils.createStyledTextField();
        emailField = UIUtils.createStyledTextField();
        telephoneField = UIUtils.createStyledTextField();
        nomUtilisateurField = UIUtils.createStyledTextField();
        motDePasseField = UIUtils.createStyledPasswordField();
        
        civiliteCombo = UIUtils.createStyledComboBox(Civilite.values());
        nationaliteCombo = UIUtils.createStyledComboBox(Nationalite.values());
        roleCombo = UIUtils.createStyledComboBox(Role.values());
        
        // Date spinners with default values
        Date defaultDate = Date.from(LocalDate.now().minusYears(25).atStartOfDay(ZoneId.systemDefault()).toInstant());
        dateNaissanceSpinner = new JSpinner(new SpinnerDateModel(defaultDate, null, null, java.util.Calendar.DAY_OF_MONTH));
        dateNaissanceSpinner.setEditor(new JSpinner.DateEditor(dateNaissanceSpinner, "dd/MM/yyyy"));
        
        Date defaultHireDate = new Date();
        dateEmbaucheSpinner = new JSpinner(new SpinnerDateModel(defaultHireDate, null, null, java.util.Calendar.DAY_OF_MONTH));
        dateEmbaucheSpinner.setEditor(new JSpinner.DateEditor(dateEmbaucheSpinner, "dd/MM/yyyy"));
        
        saveButton = UIUtils.createSuccessButton(isEditMode ? "Update" : "Save");
        cancelButton = UIUtils.createSecondaryButton("Cancel");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Personal Information
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel personalInfoLabel = new JLabel("Personal Information");
        personalInfoLabel.setFont(UIConstants.SUBTITLE_FONT);
        personalInfoLabel.setForeground(UIConstants.PRIMARY_COLOR);
        mainPanel.add(personalInfoLabel, gbc);
        
        gbc.gridwidth = 1;
        
        // Civilité
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Civilité:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(civiliteCombo, gbc);
        
        // Nom
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nomField, gbc);
        
        // Prénom
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(prenomField, gbc);
        
        // Date de naissance
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Date de naissance:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateNaissanceSpinner, gbc);
        
        // Nationalité
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Nationalité:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nationaliteCombo, gbc);
        
        // Contact Information
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JLabel contactInfoLabel = new JLabel("Contact Information");
        contactInfoLabel.setFont(UIConstants.SUBTITLE_FONT);
        contactInfoLabel.setForeground(UIConstants.PRIMARY_COLOR);
        mainPanel.add(contactInfoLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        // Téléphone
        gbc.gridx = 0; gbc.gridy = 8;
        mainPanel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(telephoneField, gbc);
        
        // Employment Information
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JLabel employmentInfoLabel = new JLabel("Employment Information");
        employmentInfoLabel.setFont(UIConstants.SUBTITLE_FONT);
        employmentInfoLabel.setForeground(UIConstants.PRIMARY_COLOR);
        mainPanel.add(employmentInfoLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Date d'embauche
        gbc.gridx = 0; gbc.gridy = 10;
        mainPanel.add(new JLabel("Date d'embauche:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateEmbaucheSpinner, gbc);
        
        // Rôle
        gbc.gridx = 0; gbc.gridy = 11;
        mainPanel.add(new JLabel("Rôle:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(roleCombo, gbc);
        
        // Login Information
        gbc.gridx = 0; gbc.gridy = 12; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JLabel loginInfoLabel = new JLabel("Login Information");
        loginInfoLabel.setFont(UIConstants.SUBTITLE_FONT);
        loginInfoLabel.setForeground(UIConstants.PRIMARY_COLOR);
        mainPanel.add(loginInfoLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nom d'utilisateur
        gbc.gridx = 0; gbc.gridy = 13;
        mainPanel.add(new JLabel("Nom d'utilisateur:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nomUtilisateurField, gbc);
        
        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 14;
        JLabel passwordLabel = new JLabel(isEditMode ? "Nouveau mot de passe (optionnel):" : "Mot de passe:");
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(motDePasseField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        // Enter key to save
        getRootPane().setDefaultButton(saveButton);
    }
    
    private void configureDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void populateFields() {
        if (personnel != null) {
            civiliteCombo.setSelectedItem(personnel.getCivilite());
            nomField.setText(personnel.getNom());
            prenomField.setText(personnel.getPrenom());
            emailField.setText(personnel.getEmail());
            telephoneField.setText(personnel.getTelephone());
            nationaliteCombo.setSelectedItem(personnel.getNationalite());
            nomUtilisateurField.setText(personnel.getNomUtilisateur());
            roleCombo.setSelectedItem(personnel.getRole());

            if (personnel.getDateNaissance() != null) {
                java.util.Date date = java.util.Date.from(personnel.getDateNaissance().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                dateNaissanceSpinner.setValue(date);
            }
            if (personnel.getDateEmbauche() != null) {
                java.util.Date date = java.util.Date.from(personnel.getDateEmbauche().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                dateEmbaucheSpinner.setValue(date);
            }
            // Don't populate password field for security
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            if (validateAndSave()) {
                confirmed = true;
                dispose();
            }
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }
    
    private boolean validateAndSave() {
        // Validate required fields
        if (nomField.getText().trim().isEmpty()) {
            UIUtils.showErrorMessage(this, "Nom is required.");
            nomField.requestFocus();
            return false;
        }
        
        if (prenomField.getText().trim().isEmpty()) {
            UIUtils.showErrorMessage(this, "Prénom is required.");
            prenomField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            UIUtils.showErrorMessage(this, "Email is required.");
            emailField.requestFocus();
            return false;
        }
        
        if (nomUtilisateurField.getText().trim().isEmpty()) {
            UIUtils.showErrorMessage(this, "Nom d'utilisateur is required.");
            nomUtilisateurField.requestFocus();
            return false;
        }
        
        if (!isEditMode && new String(motDePasseField.getPassword()).trim().isEmpty()) {
            UIUtils.showErrorMessage(this, "Mot de passe is required for new personnel.");
            motDePasseField.requestFocus();
            return false;
        }
        
        // Validate email format
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            UIUtils.showErrorMessage(this, "Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        
        try {
            // Create or update personnel object
            if (personnel == null) {
                personnel = new Personnel();
            }
            
            personnel.setCivilite((Civilite) civiliteCombo.getSelectedItem());
            personnel.setNom(nomField.getText().trim());
            personnel.setPrenom(prenomField.getText().trim());
            personnel.setEmail(email);
            personnel.setTelephone(telephoneField.getText().trim());
            personnel.setNationalite((Nationalite) nationaliteCombo.getSelectedItem());
            personnel.setNomUtilisateur(nomUtilisateurField.getText().trim());
            personnel.setRole((Role) roleCombo.getSelectedItem());
            
            // Convert Date to LocalDate for setDateNaissance
            Date naissanceDate = (Date) dateNaissanceSpinner.getValue();
            if (naissanceDate != null) {
                personnel.setDateNaissance(naissanceDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            // Convert Date to LocalDate for setDateEmbauche
            Date embaucheDate = (Date) dateEmbaucheSpinner.getValue();
            if (embaucheDate != null) {
                personnel.setDateEmbauche(embaucheDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            
            // Only update password if provided
            String password = new String(motDePasseField.getPassword()).trim();
            if (!password.isEmpty()) {
                personnel.setMotDePasse(password); // Will be hashed in the service layer
            }
            
            return true;
            
        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error saving personnel data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Show backend validation errors and focus the first relevant field.
     * @param errors List of error messages from backend
     */
    public void showValidationErrors(java.util.List<String> errors) {
        if (errors == null || errors.isEmpty()) return;
        // Show all errors in a dialog, one per line
        StringBuilder sb = new StringBuilder();
        for (String err : errors) {
            sb.append("- ").append(err.trim()).append("\n");
        }
        UIUtils.showErrorMessage(this, sb.toString());

        // Try to focus the first relevant field based on error content
        for (String err : errors) {
            String lower = err.toLowerCase();
            if (lower.contains("email")) {
                emailField.requestFocus();
                break;
            } else if (lower.contains("phone")) {
                telephoneField.requestFocus();
                break;
            } else if (lower.contains("name") && lower.contains("last")) {
                nomField.requestFocus();
                break;
            } else if (lower.contains("name") && lower.contains("first")) {
                prenomField.requestFocus();
                break;
            } else if (lower.contains("personnel number")) {
                nomUtilisateurField.requestFocus();
                break;
            } else if (lower.contains("password")) {
                motDePasseField.requestFocus();
                break;
            }
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Personnel getPersonnel() {
        return personnel;
    }
}
