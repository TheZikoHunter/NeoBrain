package insea.neobrain;

import javax.swing.*;
import insea.neobrain.dao.AdminDAO;
import insea.neobrain.entity.Admin;
import java.awt.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final AdminDAO adminDAO = new AdminDAO();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new Login());
    }

    public Login() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("NeoBrain - Connexion");
        
        // Main container using BorderLayout
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        
        // Center panel to hold the login form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        // Form panel with all components
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Add all components to the form container
        formContainer.add(createHeaderPanel());
        formContainer.add(Box.createVerticalStrut(20));

        formContainer.add(createFormPanel());
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(createButtonPanel());
        
        // Center the form container
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(formContainer, gbc);
        
        // Add center panel and footer
        container.add(centerPanel, BorderLayout.CENTER);
        container.add(createFooterPanel(), BorderLayout.SOUTH);
        
        setContentPane(container);
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        
        // Logo/Icon area (you can add an icon here later)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(Color.WHITE);
        
        // Create a circular colored panel as logo placeholder
        JPanel logoCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(52, 152, 219));
                g2d.fillOval(0, 0, 60, 60);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "NB";
                int x = (60 - fm.stringWidth(text)) / 2;
                int y = (60 - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, x, y);
                g2d.dispose();
            }
        };
        logoCircle.setPreferredSize(new Dimension(60, 60));
        logoCircle.setOpaque(false);
        
        logoPanel.add(logoCircle);
        
        JLabel titleLabel = new JLabel("NeoBrain", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JLabel subtitleLabel = new JLabel("Connectez-vous à votre compte", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        headerPanel.add(logoPanel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1, true),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Nom d'utilisateur :");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameLabel.setForeground(new Color(52, 73, 94));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(7));

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setMaximumSize(new Dimension(300, 38));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setToolTipText("Entrez votre nom d'utilisateur");
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        usernameField.setBackground(Color.WHITE);
        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                usernameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2, true),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                usernameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }
        });
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(18));

        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordLabel.setForeground(new Color(52, 73, 94));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(7));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setMaximumSize(new Dimension(300, 38));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setToolTipText("Entrez votre mot de passe");
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        passwordField.setBackground(Color.WHITE);
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2, true),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }
        });
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JButton loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setBorder(BorderFactory.createEmptyBorder(13, 40, 13, 40));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);
        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(52, 152, 219));
            }
        });
        buttonPanel.add(loginButton);
        return buttonPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel footerLabel = new JLabel("Pas de compte administrateur ?");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(127, 140, 141));
        
        JButton createAccountButton = new JButton("Créer un compte");
        createAccountButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        createAccountButton.setForeground(new Color(52, 152, 219));
        createAccountButton.setBackground(new Color(248, 249, 250));
        createAccountButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        createAccountButton.setFocusPainted(false);
        createAccountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountButton.setContentAreaFilled(false);
        
        createAccountButton.addActionListener(e -> {
            dispose();
            new App();
        });
        
        // Hover effect for create account button
        createAccountButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createAccountButton.setForeground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createAccountButton.setForeground(new Color(52, 152, 219));
            }
        });
        
        footerPanel.add(footerLabel);
        footerPanel.add(createAccountButton);
        
        return footerPanel;
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez remplir tous les champs.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Hash the entered password
            String hashedPassword = hashPassword(password);
            
            // Check credentials
            Admin admin = adminDAO.findByUsername(username);
            
            if (admin != null && admin.getPassword().equals(hashedPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Connexion réussie ! Bienvenue " + admin.getFirstName() + " " + admin.getLastName(), 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Close login window and open main application
                dispose();
                // Here you would open your main application window
                // For now, we'll just show a message
                JOptionPane.showMessageDialog(null, 
                    "Interface principale à implémenter", 
                    "Application principale", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Nom d'utilisateur ou mot de passe incorrect.", 
                    "Erreur de connexion", 
                    JOptionPane.ERROR_MESSAGE);
                
                // Clear password field
                passwordField.setText("");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la connexion: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
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
