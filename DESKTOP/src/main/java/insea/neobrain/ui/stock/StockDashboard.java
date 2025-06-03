package insea.neobrain.ui.stock;

import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Role;
import insea.neobrain.repository.impl.ReclamationRepositoryImpl;
import insea.neobrain.service.ReclamationService;
import insea.neobrain.service.impl.ReclamationServiceImpl;
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
 * Stock Dashboard for both Stock Manager and Stock Employee roles
 */
public class StockDashboard extends JFrame implements ActionListener {
    
    private final Personnel currentUser;
    private final boolean isStockManager;
    private final ReclamationService reclamationService;
    
    private JLabel welcomeLabel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Navigation buttons
    private JButton productManagementButton;
    private JButton inventoryManagementButton;
    private JButton inventoryTasksButton;
    private JButton reclamationManagementButton;
    private JButton salesOrdersButton;
    private JButton logoutButton;
    
    // Panels for different functionalities
    private ProductManagementPanel productPanel;
    private InventoryManagementPanel inventoryPanel;
    private InventoryTasksPanel tasksPanel;
    private ReclamationManagementPanel reclamationPanel;
    
    public StockDashboard(Personnel currentUser) {
        this.currentUser = currentUser;
        this.isStockManager = (currentUser.getRole() == Role.RESPONSABLE_STOCK);
        this.reclamationService = new ReclamationServiceImpl(new ReclamationRepositoryImpl());
        
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
        
        // Navigation buttons - visibility depends on role
        productManagementButton = UIUtils.createPrimaryButton("Product Management");
        productManagementButton.setPreferredSize(new Dimension(200, 40));
        productManagementButton.setVisible(isStockManager);
        
        inventoryManagementButton = UIUtils.createPrimaryButton("Inventory Management");
        inventoryManagementButton.setPreferredSize(new Dimension(200, 40));
        inventoryManagementButton.setVisible(isStockManager);
        
        inventoryTasksButton = UIUtils.createSecondaryButton("Inventory Tasks");
        inventoryTasksButton.setPreferredSize(new Dimension(200, 40));
        
        reclamationManagementButton = UIUtils.createSecondaryButton("Complaint Management");
        reclamationManagementButton.setPreferredSize(new Dimension(200, 40));
        
        salesOrdersButton = UIUtils.createSecondaryButton("Sales Orders");
        salesOrdersButton.setPreferredSize(new Dimension(200, 40));
        salesOrdersButton.setVisible(isStockManager);
        
        logoutButton = UIUtils.createDangerButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 32));
        
        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Initialize panels based on role
        if (isStockManager) {
            productPanel = new ProductManagementPanel();
            inventoryPanel = new InventoryManagementPanel(currentUser);
        }
        tasksPanel = new InventoryTasksPanel(currentUser);
        reclamationPanel = new ReclamationManagementPanel(reclamationService, currentUser.getEmail());
        
        // Add panels to content
        contentPanel.add(createWelcomePanel(), "WELCOME");
        if (isStockManager) {
            contentPanel.add(productPanel, "PRODUCTS");
            contentPanel.add(inventoryPanel, "INVENTORY");
        }
        contentPanel.add(tasksPanel, "TASKS");
        contentPanel.add(reclamationPanel, "RECLAMATIONS");
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
        
        String dashboardTitle = isStockManager ? "Stock Manager Dashboard" : "Stock Employee Dashboard";
        JLabel titleLabel = new JLabel(dashboardTitle, SwingConstants.LEFT);
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
        
        // Add buttons based on role
        if (isStockManager) {
            navPanel.add(productManagementButton);
            navPanel.add(Box.createVerticalStrut(10));
            navPanel.add(inventoryManagementButton);
            navPanel.add(Box.createVerticalStrut(10));
            navPanel.add(salesOrdersButton);
            navPanel.add(Box.createVerticalStrut(10));
        }
        navPanel.add(inventoryTasksButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(reclamationManagementButton);
        navPanel.add(Box.createVerticalGlue());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        productManagementButton.addActionListener(this);
        inventoryManagementButton.addActionListener(this);
        inventoryTasksButton.addActionListener(this);
        reclamationManagementButton.addActionListener(this);
        salesOrdersButton.addActionListener(this);
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
        String title = isStockManager ? "Stock Manager Dashboard" : "Stock Employee Dashboard";
        setTitle(title + " - Inventory Management System");
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
        String welcomeTitle = isStockManager ? "Welcome to Stock Manager Dashboard" : "Welcome to Stock Employee Dashboard";
        JLabel welcomeTitleLabel = new JLabel(welcomeTitle, SwingConstants.CENTER);
        welcomeTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        centerPanel.add(welcomeTitleLabel, gbc);
        
        gbc.gridy = 1;
        JLabel userInfo = new JLabel("Logged in as: " + currentUser.getPrenom() + " " + currentUser.getNom() + 
                                   " (" + currentUser.getRole() + ")", SwingConstants.CENTER);
        userInfo.setFont(UIConstants.NORMAL_FONT);
        userInfo.setForeground(UIConstants.DARK_GRAY);
        centerPanel.add(userInfo, gbc);
        
        gbc.gridy = 2;
        String instructions = isStockManager ? 
            "Use the navigation menu to manage products, inventory, and sales orders" :
            "Use the navigation menu to view and complete inventory tasks";
        JLabel instructionsLabel = new JLabel(instructions, SwingConstants.CENTER);
        instructionsLabel.setFont(UIConstants.NORMAL_FONT);
        instructionsLabel.setForeground(UIConstants.DARK_GRAY);
        centerPanel.add(instructionsLabel, gbc);
        
        // Quick actions
        gbc.gridy = 3; gbc.insets = new Insets(30, 10, 10, 10);
        JPanel quickActions = new JPanel(new FlowLayout());
        quickActions.setBackground(Color.WHITE);
        
        if (isStockManager) {
            JButton quickProductBtn = UIUtils.createPrimaryButton("Manage Products");
            quickProductBtn.addActionListener(e -> showProductManagement());
            quickActions.add(quickProductBtn);
            
            JButton quickInventoryBtn = UIUtils.createSecondaryButton("Manage Inventory");
            quickInventoryBtn.addActionListener(e -> showInventoryManagement());
            quickActions.add(quickInventoryBtn);
        }
        
        JButton quickTasksBtn = UIUtils.createWarningButton("My Tasks");
        quickTasksBtn.addActionListener(e -> showInventoryTasks());
        quickActions.add(quickTasksBtn);
        
        JButton quickReclamationBtn = UIUtils.createSecondaryButton("Manage Complaints");
        quickReclamationBtn.addActionListener(e -> showReclamationManagement());
        quickActions.add(quickReclamationBtn);
        
        centerPanel.add(quickActions, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == productManagementButton) {
            showProductManagement();
        } else if (e.getSource() == inventoryManagementButton) {
            showInventoryManagement();
        } else if (e.getSource() == inventoryTasksButton) {
            showInventoryTasks();
        } else if (e.getSource() == reclamationManagementButton) {
            showReclamationManagement();
        } else if (e.getSource() == salesOrdersButton) {
            showSalesOrders();
        } else if (e.getSource() == logoutButton) {
            handleLogout();
        }
    }
    
    private void showProductManagement() {
        if (isStockManager && productPanel != null) {
            cardLayout.show(contentPanel, "PRODUCTS");
            productPanel.refreshData();
        }
    }
    
    private void showInventoryManagement() {
        if (isStockManager && inventoryPanel != null) {
            cardLayout.show(contentPanel, "INVENTORY");
            inventoryPanel.refreshData();
        }
    }
    
    private void showInventoryTasks() {
        cardLayout.show(contentPanel, "TASKS");
        tasksPanel.refreshData();
    }
    
    private void showReclamationManagement() {
        cardLayout.show(contentPanel, "RECLAMATIONS");
        reclamationPanel.refreshData();
    }
    
    private void showSalesOrders() {
        UIUtils.showWarningMessage(this, "Sales Orders functionality will be implemented in a future version.");
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
