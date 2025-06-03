package insea.neobrain.ui.common;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Reusable search and filter panel for data tables
 */
public class SearchFilterPanel extends JPanel {
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private TableRowSorter<?> sorter;
    private JButton clearButton;
    private JButton exportButton;
    
    public SearchFilterPanel(TableRowSorter<?> sorter, String[] filterOptions) {
        this.sorter = sorter;
        initComponents(filterOptions);
        setupLayout();
        setupEventHandlers();
    }
    
    public SearchFilterPanel(TableRowSorter<?> sorter, String[] filterOptions, boolean includeExport) {
        this.sorter = sorter;
        initComponents(filterOptions);
        if (includeExport) {
            exportButton = new JButton("Exporter CSV");
            exportButton.setToolTipText("Exporter les données vers un fichier CSV");
        }
        setupLayout();
        setupEventHandlers();
    }
    
    private void initComponents(String[] filterOptions) {
        searchField = new JTextField(20);
        searchField.setToolTipText("Tapez pour rechercher...");
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher...");
        
        filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setSelectedIndex(0);
        filterCombo.setToolTipText("Sélectionner un filtre");
        
        clearButton = new JButton("Effacer");
        clearButton.setToolTipText("Effacer la recherche et les filtres");
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBorder(BorderFactory.createTitledBorder("Recherche et Filtres"));
        
        add(new JLabel("Recherche:"));
        add(searchField);
        
        add(Box.createHorizontalStrut(10));
        
        add(new JLabel("Filtrer par:"));
        add(filterCombo);
        
        add(Box.createHorizontalStrut(10));
        add(clearButton);
        
        if (exportButton != null) {
            add(Box.createHorizontalStrut(10));
            add(exportButton);
        }
    }
    
    private void setupEventHandlers() {
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });
        
        filterCombo.addActionListener(e -> performSearch());
        
        clearButton.addActionListener(e -> clearSearch());
    }
    
    private void performSearch() {
        String text = searchField.getText().trim();
        
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Case-insensitive search across all columns
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
    
    public void clearSearch() {
        searchField.setText("");
        filterCombo.setSelectedIndex(0);
        sorter.setRowFilter(null);
    }
    
    public String getSearchText() {
        return searchField.getText().trim();
    }
    
    public String getSelectedFilter() {
        return (String) filterCombo.getSelectedItem();
    }
    
    public void setSearchText(String text) {
        searchField.setText(text);
        performSearch();
    }
    
    public void setSelectedFilter(String filter) {
        filterCombo.setSelectedItem(filter);
        performSearch();
    }
    
    /**
     * Add export button action listener
     */
    public void addExportActionListener(java.awt.event.ActionListener listener) {
        if (exportButton != null) {
            exportButton.addActionListener(listener);
        }
    }
    
    /**
     * Enable/disable the export button
     */
    public void setExportEnabled(boolean enabled) {
        if (exportButton != null) {
            exportButton.setEnabled(enabled);
        }
    }
    
    /**
     * Advanced search with multiple columns
     */
    public void performAdvancedSearch(String searchText, int columnIndex) {
        if (searchText == null || searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        
        if (columnIndex >= 0) {
            // Search in specific column
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText.trim(), columnIndex));
        } else {
            // Search in all columns
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText.trim()));
        }
    }
    
    /**
     * Set custom filter based on column values
     */
    @SuppressWarnings("unchecked")
    public void setCustomFilter(RowFilter<?, ?> filter) {
        sorter.setRowFilter((RowFilter<? super Object, ? super Integer>) filter);
    }
}
