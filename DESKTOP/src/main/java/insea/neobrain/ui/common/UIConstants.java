package insea.neobrain.ui.common;

import java.awt.*;

/**
 * UI Constants for consistent styling across the application
 */
public class UIConstants {
    
    // Color scheme
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blue
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Light Blue
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);       // Green
    public static final Color WARNING_COLOR = new Color(243, 156, 18);      // Orange
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // Red
    public static final Color LIGHT_GRAY = new Color(236, 240, 241);        // Light Gray
    public static final Color DARK_GRAY = new Color(127, 140, 141);         // Dark Gray
    public static final Color BORDER_COLOR = new Color(189, 195, 199);      // Border Gray
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 10);
    
    // Dimensions
    public static final int BUTTON_HEIGHT = 32;
    public static final int TEXTFIELD_HEIGHT = 28;
    public static final int STANDARD_PADDING = 10;
    public static final int LARGE_PADDING = 20;
    
    // Window sizes
    public static final Dimension LOGIN_WINDOW_SIZE = new Dimension(400, 300);
    public static final Dimension DASHBOARD_WINDOW_SIZE = new Dimension(1200, 800);
    public static final Dimension DIALOG_WINDOW_SIZE = new Dimension(500, 400);
    
    // Table settings
    public static final int TABLE_ROW_HEIGHT = 25;
    public static final Color TABLE_HEADER_BACKGROUND = LIGHT_GRAY;
    public static final Color TABLE_ALTERNATE_ROW = new Color(248, 249, 250);
    
    // Border radius
    public static final int BORDER_RADIUS = 5;
    
    private UIConstants() {
        // Private constructor to prevent instantiation
    }
}
