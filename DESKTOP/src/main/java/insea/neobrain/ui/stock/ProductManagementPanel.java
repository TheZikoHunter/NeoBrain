package insea.neobrain.ui.stock;

import insea.neobrain.entity.CategorieProduit;
import insea.neobrain.entity.Produit;
import insea.neobrain.service.ProduitService;
import insea.neobrain.service.impl.ProduitServiceImpl;
import insea.neobrain.repository.impl.ProduitRepositoryImpl;
import insea.neobrain.ui.common.UIConstants;
import insea.neobrain.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

/**
 * Panel for managing products (add, edit, delete, view)
 */
public class ProductManagementPanel extends JPanel implements ActionListener {
    
    private final ProduitService produitService;
    
    // UI Components
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<CategorieProduit> categoryFilter;
    
    public ProductManagementPanel() {
        this.produitService = new ProduitServiceImpl(new ProduitRepositoryImpl());
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Reference", "Nom", "Description", "Prix", "Catégorie", "Stock", "Disponible"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        productTable = UIUtils.createStyledTable();
        productTable.setModel(tableModel);
        UIUtils.setupTableColumnWidths(productTable, new int[]{50, 100, 150, 200, 80, 120, 80, 80});
        
        // Buttons
        addButton = UIUtils.createSuccessButton("Add");
        editButton = UIUtils.createPrimaryButton("Edit");
        deleteButton = UIUtils.createDangerButton("Delete");
        refreshButton = UIUtils.createSecondaryButton("Refresh");
        
        // Search components
        searchField = UIUtils.createStyledTextField();
        searchField.setPreferredSize(new Dimension(200, UIConstants.TEXTFIELD_HEIGHT));
        searchButton = UIUtils.createSecondaryButton("Search");
        
        // Category filter
        CategorieProduit[] categories = new CategorieProduit[CategorieProduit.values().length + 1];
        categories[0] = null; // "All categories" option
        System.arraycopy(CategorieProduit.values(), 0, categories, 1, CategorieProduit.values().length);
        categoryFilter = UIUtils.createStyledComboBox(categories);
        categoryFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("All Categories");
                }
                return this;
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Filter and search panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Top panel combining title, filters, and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.add(filterPanel, BorderLayout.NORTH);
        controlsPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(controlsPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        refreshButton.addActionListener(this);
        searchButton.addActionListener(this);
        
        // Category filter change
        categoryFilter.addActionListener(e -> performFilter());
        
        // Enter key for search
        searchField.addActionListener(e -> performSearch());
        
        // Double-click to edit
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editSelectedProduct();
                }
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            showAddProductDialog();
        } else if (e.getSource() == editButton) {
            editSelectedProduct();
        } else if (e.getSource() == deleteButton) {
            deleteSelectedProduct();
        } else if (e.getSource() == refreshButton) {
            refreshData();
        } else if (e.getSource() == searchButton) {
            performSearch();
        }
    }
    
    public void refreshData() {
        SwingWorker<List<Produit>, Void> worker = new SwingWorker<List<Produit>, Void>() {
            @Override
            protected List<Produit> doInBackground() throws Exception {
                return produitService.findAllProduits();
            }
            
            @Override
            protected void done() {
                try {
                    List<Produit> products = get();
                    updateTable(products);
                } catch (Exception e) {
                    UIUtils.showErrorMessage(ProductManagementPanel.this, 
                        "Error loading product data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Refresh the product table (alias for refreshData)
     */
    public void refreshTable() {
        refreshData();
    }
    
    private void updateTable(List<Produit> products) {
        tableModel.setRowCount(0);
        for (Produit product : products) {
            Object[] row = {
                product.getIdProduit(),
                product.getCodeProduit(), // Use getCodeProduit instead of getReference
                product.getNom(), // Use getNom instead of getNomProduit 
                product.getDescription(),
                product.getPrix(), // Use getPrix instead of getPrixUnitaire
                product.getCategorie(),
                product.getQuantiteStock(),
                product.getDisponible() ? "Yes" : "No" // Use getDisponible instead of isDisponible
            };
            tableModel.addRow(row);
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            refreshData();
            return;
        }
        
        SwingWorker<List<Produit>, Void> worker = new SwingWorker<List<Produit>, Void>() {
            @Override
            protected List<Produit> doInBackground() throws Exception {
                return produitService.findProduitsByName(searchTerm);
            }
            
            @Override
            protected void done() {
                try {
                    List<Produit> results = get();
                    updateTable(results);
                } catch (Exception e) {
                    UIUtils.showErrorMessage(ProductManagementPanel.this, 
                        "Error searching products: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void performFilter() {
        CategorieProduit selectedCategory = (CategorieProduit) categoryFilter.getSelectedItem();
        if (selectedCategory == null) {
            refreshData();
            return;
        }
        
        SwingWorker<List<Produit>, Void> worker = new SwingWorker<List<Produit>, Void>() {
            @Override
            protected List<Produit> doInBackground() throws Exception {
                return produitService.findProduitsByCategorie(selectedCategory);
            }
            
            @Override
            protected void done() {
                try {
                    List<Produit> results = get();
                    updateTable(results);
                } catch (Exception e) {
                    UIUtils.showErrorMessage(ProductManagementPanel.this, 
                        "Error filtering products: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void showAddProductDialog() {
        ProductFormDialog dialog = new ProductFormDialog(
            SwingUtilities.getWindowAncestor(this), produitService, this);
        dialog.setVisible(true);
    }
    
    private void editSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showWarningMessage(this, "Please select a product to edit.");
            return;
        }
        
        Long productId = (Long) tableModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Optional<Produit>, Void> worker = new SwingWorker<Optional<Produit>, Void>() {
            @Override
            protected Optional<Produit> doInBackground() throws Exception {
                return produitService.findProduitById(productId);
            }
            
            @Override
            protected void done() {
                try {
                    Optional<Produit> productOpt = get();
                    if (productOpt.isPresent()) {
                        Produit product = productOpt.get();
                        ProductFormDialog dialog = new ProductFormDialog(
                            SwingUtilities.getWindowAncestor(ProductManagementPanel.this), 
                            produitService, ProductManagementPanel.this, product);
                        dialog.setVisible(true);
                    } else {
                        UIUtils.showErrorMessage(ProductManagementPanel.this, 
                            "Produit non trouvé.");
                    }
                } catch (Exception e) {
                    UIUtils.showErrorMessage(ProductManagementPanel.this, 
                        "Error loading product: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void updateProduct(Produit product) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                produitService.updateProduit(product);
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    UIUtils.showSuccessMessage(ProductManagementPanel.this, 
                        "Product updated successfully!");
                    refreshData();
                } catch (Exception e) {
                    UIUtils.showErrorMessage(ProductManagementPanel.this, 
                        "Error updating product: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showWarningMessage(this, "Please select a product to delete.");
            return;
        }
        
        Long productId = (Long) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 2);
        
        boolean confirmed = UIUtils.showConfirmDialog(this, 
            "Are you sure you want to delete product '" + productName + "'?\nThis action cannot be undone.");
        
        if (confirmed) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    produitService.deleteProduit(productId);
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        UIUtils.showSuccessMessage(ProductManagementPanel.this, 
                            "Product deleted successfully!");
                        refreshData();
                    } catch (Exception e) {
                        UIUtils.showErrorMessage(ProductManagementPanel.this, 
                            "Error deleting product: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }
}
