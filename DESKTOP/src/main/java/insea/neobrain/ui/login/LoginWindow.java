package insea.neobrain.ui.login;

import insea.neobrain.entity.Personnel;
import insea.neobrain.service.AuthenticationService;
import insea.neobrain.service.impl.AuthenticationServiceImpl;
import insea.neobrain.repository.impl.PersonnelRepositoryImpl;
import insea.neobrain.ui.common.UIConstants;
import insea.neobrain.ui.common.UIUtils;
import insea.neobrain.ui.admin.AdminDashboard;
import insea.neobrain.ui.stock.StockDashboard;

import java.util.Optional;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Login window for the Inventory Management System
 * Provides authentication and role-based dashboard navigation
 */
public class LoginWindow extends JFrame implements ActionListener, KeyListener {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel statusLabel;
    private AuthenticationService authenticationService;
    
    public LoginWindow() {
        this.authenticationService = new AuthenticationServiceImpl(new PersonnelRepositoryImpl());
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
    }
    
    private void initializeComponents() {
        // Title
        JLabel titleLabel = new JLabel("Inventory Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Buttons
        loginButton = UIUtils.createPrimaryButton("Login");
        exitButton = UIUtils.createSecondaryButton("Exit");
        
        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Inventory Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        gbc.gridy = 1;
        JLabel subtitleLabel = new JLabel("Please enter your credentials", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        mainPanel.add(subtitleLabel, gbc);
        
        // Username
        gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Status label
        gbc.gridy = 5;
        mainPanel.add(statusLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);
        usernameField.addKeyListener(this);
        passwordField.addKeyListener(this);
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void configureWindow() {
        setTitle("Login - Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null); // Center on screen
        
        // Set application icon
        try {
            setIconImage(UIUtils.createApplicationIcon());
        } catch (Exception e) {
            // Icon loading failed, continue without it
        }
        
        // Focus on username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            performLogin();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            performLogin();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validate input
        if (username.isEmpty()) {
            showError("Please enter your username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        // Disable login button during authentication
        loginButton.setEnabled(false);
        loginButton.setText("Authenticating...");
        statusLabel.setText("Validating credentials...");
        statusLabel.setForeground(Color.BLUE);
        
        // Perform authentication in a background thread to keep UI responsive
        SwingWorker<Personnel, Void> loginWorker = new SwingWorker<Personnel, Void>() {
            @Override
            protected Personnel doInBackground() throws Exception {
                Optional<Personnel> result = authenticationService.authenticate(username, password);
                return result.orElse(null);
            }
            
            @Override
            protected void done() {
                try {
                    Personnel personnel = get();
                    if (personnel != null) {
                        onLoginSuccess(personnel);
                    } else {
                        onLoginFailure("Invalid username or password");
                    }
                } catch (Exception e) {
                    onLoginFailure("Authentication error: " + e.getMessage());
                }
            }
        };
        
        loginWorker.execute();
    }
    
    private void onLoginSuccess(Personnel personnel) {
        statusLabel.setText("Login successful! Opening dashboard...");
        statusLabel.setForeground(Color.GREEN);
        
        // Hide login window
        setVisible(false);
        
        // Open appropriate dashboard based on role
        SwingUtilities.invokeLater(() -> {
            try {
                switch (personnel.getRole()) {
                    case ADMIN:
                        new AdminDashboard(personnel).setVisible(true);
                        break;
                    case RESPONSABLE_STOCK:
                    case EMPLOYE_STOCK:
                        new StockDashboard(personnel).setVisible(true);
                        break;
                    default:
                        showError("Unknown role: " + personnel.getRole());
                        setVisible(true);
                        resetLoginForm();
                        return;
                }
                
                // Dispose login window
                dispose();
                
            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to open dashboard: " + e.getMessage());
                setVisible(true);
                resetLoginForm();
            }
        });
    }
    
    private void onLoginFailure(String message) {
        showError(message);
        resetLoginForm();
        passwordField.requestFocus();
        passwordField.selectAll();
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
    }
    
    private void resetLoginForm() {
        loginButton.setEnabled(true);
        loginButton.setText("Login");
        passwordField.setText("");
    }
}
