package insea.neobrain.ui.admin;

import insea.neobrain.entity.Personnel;
import insea.neobrain.ui.common.UIConstants;
import insea.neobrain.ui.common.UIUtils;
import insea.neobrain.ui.login.LoginWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Admin Dashboard for personnel management
 */
public class AdminDashboard extends JFrame implements ActionListener {
    
    private final Personnel currentUser;
    private JLabel welcomeLabel;
    private JButton personnelManagementButton;
    private JButton systemReportsButton;
    private JButton logoutButton;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Panels for different functionalities
    private PersonnelManagementPanel personnelPanel;
    
    public AdminDashboard(Personnel currentUser) {
        this.currentUser = currentUser;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
    }
    
    private void initializeComponents() {
        // Welcome label
        welcomeLabel = new JLabel("Welcome, " + currentUser.getPrenom() + " " + currentUser.getNom(), 
                                 SwingConstants.CENTER);
        welcomeLabel.setFont(UIConstants.TITLE_FONT);
        welcomeLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        // Navigation buttons
        personnelManagementButton = UIUtils.createPrimaryButton("Personnel Management");
        personnelManagementButton.setPreferredSize(new Dimension(200, 40));
        
        systemReportsButton = UIUtils.createSecondaryButton("System Reports");
        systemReportsButton.setPreferredSize(new Dimension(200, 40));
        
        logoutButton = UIUtils.createDangerButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 32));
        
        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Initialize panels
        personnelPanel = new PersonnelManagementPanel();
        
        // Add panels to content
        contentPanel.add(createWelcomePanel(), "WELCOME");
        contentPanel.add(personnelPanel, "PERSONNEL");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Title and welcome
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(welcomeLabel, BorderLayout.CENTER);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(UIConstants.LIGHT_GRAY);
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        // Navigation title
        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(UIConstants.SUBTITLE_FONT);
        navTitle.setForeground(UIConstants.DARK_GRAY);
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        navPanel.add(navTitle);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(personnelManagementButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(systemReportsButton);
        navPanel.add(Box.createVerticalGlue());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        personnelManagementButton.addActionListener(this);
        systemReportsButton.addActionListener(this);
        logoutButton.addActionListener(this);
        
        // Window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleLogout();
            }
        });
    }
    
    private void configureWindow() {
        setTitle("Admin Dashboard - Inventory Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(UIConstants.DASHBOARD_WINDOW_SIZE);
        setLocationRelativeTo(null);
        
        // Set application icon
        try {
            setIconImage(UIUtils.createApplicationIcon());
        } catch (Exception e) {
            // Icon loading failed, continue without it
        }
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Welcome message
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel welcomeTitle = new JLabel("Welcome to Admin Dashboard", SwingConstants.CENTER);
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(UIConstants.PRIMARY_COLOR);
        centerPanel.add(welcomeTitle, gbc);
        
        gbc.gridy = 1;
        JLabel userInfo = new JLabel("Logged in as: " + currentUser.getPrenom() + " " + currentUser.getNom() + 
                                   " (" + currentUser.getRole() + ")", SwingConstants.CENTER);
        userInfo.setFont(UIConstants.NORMAL_FONT);
        userInfo.setForeground(UIConstants.DARK_GRAY);
        centerPanel.add(userInfo, gbc);
        
        gbc.gridy = 2;
        JLabel instructions = new JLabel("Use the navigation menu to access administrative functions", 
                                       SwingConstants.CENTER);
        instructions.setFont(UIConstants.NORMAL_FONT);
        instructions.setForeground(UIConstants.DARK_GRAY);
        centerPanel.add(instructions, gbc);
        
        // Quick actions
        gbc.gridy = 3; gbc.insets = new Insets(30, 10, 10, 10);
        JPanel quickActions = new JPanel(new FlowLayout());
        quickActions.setBackground(Color.WHITE);
        
        JButton quickPersonnelBtn = UIUtils.createPrimaryButton("Manage Personnel");
        quickPersonnelBtn.addActionListener(e -> showPersonnelManagement());
        quickActions.add(quickPersonnelBtn);
        
        centerPanel.add(quickActions, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == personnelManagementButton) {
            showPersonnelManagement();
        } else if (e.getSource() == systemReportsButton) {
            showSystemReports();
        } else if (e.getSource() == logoutButton) {
            handleLogout();
        }
    }
    
    private void showPersonnelManagement() {
        cardLayout.show(contentPanel, "PERSONNEL");
        personnelPanel.refreshData();
    }
    
    private void showSystemReports() {
        UIUtils.showWarningMessage(this, "System Reports functionality will be implemented in a future version.");
    }
    
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginWindow().setVisible(true);
            });
        }
    }
}
