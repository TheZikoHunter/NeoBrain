package insea.neobrain.ui.common;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UI Utilities for creating consistent components across the application
 */
public class UIUtils {
    
    // Color constants for easy access (delegate to UIConstants)
    public static final Color SUCCESS_COLOR = UIConstants.SUCCESS_COLOR;
    public static final Color DANGER_COLOR = UIConstants.DANGER_COLOR;
    public static final Color PRIMARY_COLOR = UIConstants.PRIMARY_COLOR;
    public static final Color INFO_COLOR = UIConstants.SECONDARY_COLOR; // Use secondary as info color
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    /**
     * Create a primary button with consistent styling
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(UIConstants.NORMAL_FONT);
        button.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        button.setFocusPainted(false);
        button.setBorder(createRoundedBorder(UIConstants.PRIMARY_COLOR, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.SECONDARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Create a primary button with action listener
     */
    public static JButton createPrimaryButton(String text, java.awt.event.ActionListener listener) {
        JButton button = createPrimaryButton(text);
        if (listener != null) {
            button.addActionListener(listener);
        }
        return button;
    }
    
    /**
     * Create a secondary button with consistent styling
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(UIConstants.LIGHT_GRAY);
        button.setForeground(UIConstants.DARK_GRAY);
        button.setFont(UIConstants.NORMAL_FONT);
        button.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        button.setFocusPainted(false);
        button.setBorder(createRoundedBorder(UIConstants.DARK_GRAY, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Create a secondary button with action listener
     */
    public static JButton createSecondaryButton(String text, java.awt.event.ActionListener listener) {
        JButton button = createSecondaryButton(text);
        if (listener != null) {
            button.addActionListener(listener);
        }
        return button;
    }
    
    /**
     * Create a success button (green)
     */
    public static JButton createSuccessButton(String text) {
        JButton button = createPrimaryButton(text);
        button.setBackground(UIConstants.SUCCESS_COLOR);
        return button;
    }
    
    /**
     * Create a warning button (orange)
     */
    public static JButton createWarningButton(String text) {
        JButton button = createPrimaryButton(text);
        button.setBackground(UIConstants.WARNING_COLOR);
        return button;
    }
    
    /**
     * Create a danger button (red)
     */
    public static JButton createDangerButton(String text) {
        JButton button = createPrimaryButton(text);
        button.setBackground(UIConstants.DANGER_COLOR);
        return button;
    }
    
    /**
     * Create a styled text field
     */
    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(UIConstants.NORMAL_FONT);
        textField.setPreferredSize(new Dimension(200, UIConstants.TEXTFIELD_HEIGHT));
        textField.setBorder(createRoundedBorder(UIConstants.LIGHT_GRAY, 1));
        return textField;
    }
    
    /**
     * Create a styled text field with initial value
     */
    public static JTextField createStyledTextField(String initialValue) {
        JTextField textField = createStyledTextField();
        if (initialValue != null) {
            textField.setText(initialValue);
        }
        return textField;
    }
    
    /**
     * Create a styled password field
     */
    public static JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(UIConstants.NORMAL_FONT);
        passwordField.setPreferredSize(new Dimension(200, UIConstants.TEXTFIELD_HEIGHT));
        passwordField.setBorder(createRoundedBorder(UIConstants.LIGHT_GRAY, 1));
        return passwordField;
    }
    
    /**
     * Create a styled combo box
     */
    public static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(UIConstants.NORMAL_FONT);
        comboBox.setPreferredSize(new Dimension(200, UIConstants.TEXTFIELD_HEIGHT));
        return comboBox;
    }
    
    /**
     * Create a styled table with proper formatting
     */
    public static JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(UIConstants.NORMAL_FONT);
        table.setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(UIConstants.LIGHT_GRAY);
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(UIConstants.TABLE_HEADER_BACKGROUND);
        header.setFont(UIConstants.SUBTITLE_FONT);
        header.setForeground(UIConstants.DARK_GRAY);
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(UIConstants.TABLE_ALTERNATE_ROW);
                    }
                }
                return this;
            }
        });
        
        return table;
    }
    
    /**
     * Create a titled panel with border
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.LIGHT_GRAY),
            title,
            0,
            0,
            UIConstants.SUBTITLE_FONT,
            UIConstants.DARK_GRAY
        ));
        return panel;
    }
    
    /**
     * Create a card-style panel
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(UIConstants.STANDARD_PADDING, 
                                          UIConstants.STANDARD_PADDING, 
                                          UIConstants.STANDARD_PADDING, 
                                          UIConstants.STANDARD_PADDING)
        ));
        return panel;
    }
    
    /**
     * Create a rounded border
     */
    public static Border createRoundedBorder(Color color, int thickness) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, thickness),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }
    
    /**
     * Format date for display
     */
    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }
    
    /**
     * Format datetime for display
     */
    public static String formatDateTime(Date date) {
        return date != null ? DATETIME_FORMAT.format(date) : "";
    }
    
    /**
     * Show success message
     */
    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show error message
     */
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show warning message
     */
    public static void showWarningMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmDialog(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent, 
            message, 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Create application icon (placeholder)
     */
    public static Image createApplicationIcon() {
        // Create a simple icon for now
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(UIConstants.PRIMARY_COLOR);
        g2d.fillRoundRect(2, 2, 28, 28, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("I", 13, 22);
        g2d.dispose();
        return icon;
    }
    
    /**
     * Center window on screen
     */
    public static void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        window.setLocation(x, y);
    }
    
    /**
     * Setup table column widths
     */
    public static void setupTableColumnWidths(JTable table, int[] widths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < widths.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(widths[i]);
        }
    }
    
    /**
     * Create a styled button with specified color
     */
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(UIConstants.NORMAL_FONT);
        button.setPreferredSize(new Dimension(100, UIConstants.BUTTON_HEIGHT));
        button.setFocusPainted(false);
        button.setBorder(createRoundedBorder(backgroundColor, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect - darken the color slightly
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color darker = backgroundColor.darker();
                button.setBackground(darker);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
}
