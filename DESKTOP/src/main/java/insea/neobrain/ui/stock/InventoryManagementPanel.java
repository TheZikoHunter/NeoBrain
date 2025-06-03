package insea.neobrain.ui.stock;

import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.Personnel;
import insea.neobrain.service.InventaireService;
import insea.neobrain.service.impl.InventaireServiceImpl;
import insea.neobrain.repository.impl.InventaireRepositoryImpl;
import insea.neobrain.repository.impl.ProduitRepositoryImpl;
import insea.neobrain.ui.common.UIConstants;
import insea.neobrain.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing inventory operations
 */
public class InventoryManagementPanel extends JPanel implements ActionListener {
    
    private final InventaireService inventaireService;
    @SuppressWarnings("unused")
    private final Personnel currentUser;
    
    // UI Components
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton createButton;
    private JButton viewButton;
    private JButton refreshButton;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> statusFilter;
    
    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public InventoryManagementPanel(Personnel currentUser) {
        this.currentUser = currentUser;
        this.inventaireService = new InventaireServiceImpl(new InventaireRepositoryImpl(), new ProduitRepositoryImpl());
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Date Création", "Description", "Statut", "Produits", "Tâches Terminées"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        inventoryTable = UIUtils.createStyledTable();
        inventoryTable.setModel(tableModel);
        
        // Set column widths
        int[] columnWidths = {80, 150, 200, 120, 100, 150};
        UIUtils.setupTableColumnWidths(inventoryTable, columnWidths);
        
        // Create buttons
        createButton = UIUtils.createPrimaryButton("Nouvel Inventaire");
        viewButton = UIUtils.createSecondaryButton("Voir Détails");
        refreshButton = UIUtils.createSecondaryButton("Actualiser");
        searchButton = UIUtils.createSecondaryButton("Rechercher");
        
        // Create search field
        searchField = UIUtils.createStyledTextField();
        
        // Create status filter
        String[] inventoryStatuses = {
            "Tous les statuts", 
            "EN_COURS", 
            "EN_ATTENTE", 
            "TERMINEE", 
            "CLOS",
            "ANNULEE"
        };
        
        statusFilter = UIUtils.createStyledComboBox(inventoryStatuses);
        statusFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(getStatusDisplayNameFromEtat((String) value));
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
        JLabel titleLabel = new JLabel("Gestion des Inventaires");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titlePanel.add(titleLabel);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Recherche:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filterPanel.add(new JLabel("Statut:"));
        filterPanel.add(statusFilter);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(createButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);
        
        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Top panel combining title, filter, and buttons
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
        createButton.addActionListener(this);
        viewButton.addActionListener(this);
        refreshButton.addActionListener(this);
        searchButton.addActionListener(this);
        statusFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performFilter();
            }
        });
        inventoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    InventoryManagementPanel.this.viewSelectedInventory();
                }
            }
        });
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == createButton) {
            showCreateInventoryDialog();
        } else if (source == viewButton) {
            viewSelectedInventory();
        } else if (source == refreshButton) {
            refreshData();
        } else if (source == searchButton) {
            performSearch();
        }
    }
    
    public void refreshData() {
        SwingWorker<List<Inventaire>, Void> worker = new SwingWorker<List<Inventaire>, Void>() {
            @Override
            protected List<Inventaire> doInBackground() throws Exception {
                return inventaireService.findAllInventaires();
            }
            
            @Override
            protected void done() {
                try {
                    List<Inventaire> inventaires = get();
                    updateTable(inventaires);
                } catch (Exception e) {
                    UIUtils.showErrorMessage(InventoryManagementPanel.this, 
                        "Erreur lors du chargement des inventaires: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void updateTable(List<Inventaire> inventaires) {
        tableModel.setRowCount(0);
        for (Inventaire inventaire : inventaires) {
            // Count products
            String productsCount = getProductsCountDisplay(inventaire);
            
            // Avoid accessing lazy-loaded taches collection
            // Instead, show a placeholder or calculate in service layer
            String tasksCount = "N/A";
            try {
                if (inventaire.getTaches() != null) {
                    long completedTasks = inventaire.getTaches().stream()
                        .filter(tache -> tache.getEtatTache() == insea.neobrain.entity.EtatTache.TERMINEE)
                        .count();
                    tasksCount = completedTasks + "/" + inventaire.getTaches().size();
                }
            } catch (Exception e) {
                // Lazy initialization exception - just show N/A
                tasksCount = "N/A";
            }
            
            Object[] row = {
                inventaire.getIdInventaire(),
                inventaire.getDateCreation().format(dateFormatter),
                inventaire.getDescription(),
                getStatusDisplayNameFromEtat(inventaire.getEtatInventaire()),
                productsCount,
                tasksCount
            };
            tableModel.addRow(row);
        }
    }
    
    private String getProductsCountDisplay(Inventaire inventaire) {
        Integer total = inventaire.getNombreProduitsTotal();
        Integer counted = inventaire.getNombreProduitsComptes();
        
        if (total != null && counted != null) {
            return counted + "/" + total;
        } else if (total != null) {
            return "0/" + total;
        } else if (counted != null) {
            return counted + "/?";
        } else {
            return "0/0";
        }
    }

    private String getStatusDisplayNameFromEtat(String etat) {
        if (etat == null) return "Tous";
        switch (etat) {
            case "EN_ATTENTE": return "En Attente";
            case "EN_COURS": return "En Cours";
            case "TERMINEE": return "Terminée";
            case "ANNULEE": return "Annulée";
            case "CLOS": return "Clôturé";
            default: return etat;
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty() || searchTerm.equals("Rechercher par description...")) {
            refreshData();
            return;
        }
        
        SwingWorker<List<Inventaire>, Void> worker = new SwingWorker<List<Inventaire>, Void>() {
            @Override
            protected List<Inventaire> doInBackground() throws Exception {
                return inventaireService.searchInventairesByDescription(searchTerm);
            }
            
            @Override
            protected void done() {
                try {
                    List<Inventaire> results = get();
                    updateTable(results);
                } catch (Exception e) {
                    UIUtils.showErrorMessage(InventoryManagementPanel.this, 
                        "Erreur lors de la recherche: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void performFilter() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        if (selectedStatus == null || selectedStatus.equals("Tous les statuts")) {
            refreshData();
            return;
        }
        SwingWorker<List<Inventaire>, Void> worker = new SwingWorker<List<Inventaire>, Void>() {
            @Override
            protected List<Inventaire> doInBackground() throws Exception {
                List<Inventaire> all = inventaireService.findAllInventaires();
                return all.stream().filter(inv -> {
                    return selectedStatus.equals(inv.getEtatInventaire());
                }).toList();
            }
            @Override
            protected void done() {
                try {
                    List<Inventaire> results = get();
                    updateTable(results);
                } catch (Exception e) {
                    UIUtils.showErrorMessage(InventoryManagementPanel.this, 
                        "Erreur lors du filtrage: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void showCreateInventoryDialog() {
        String description = JOptionPane.showInputDialog(this, 
            "Description de l'inventaire:", 
            "Nouvel Inventaire", 
            JOptionPane.QUESTION_MESSAGE);
        if (description != null && !description.trim().isEmpty()) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Inventaire inv = new Inventaire();
                    inv.setDescription(description.trim());
                    inv.setDateDebut(java.time.LocalDate.now());
                    inv.setResponsable(currentUser.getNomComplet());
                    inventaireService.createInventaire(inv);
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        UIUtils.showSuccessMessage(InventoryManagementPanel.this, 
                            "Inventaire créé avec succès!");
                        refreshData();
                    } catch (Exception e) {
                        UIUtils.showErrorMessage(InventoryManagementPanel.this, 
                            "Erreur lors de la création: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void viewSelectedInventory() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showWarningMessage(this, "Veuillez sélectionner un inventaire à consulter.");
            return;
        }
        
        Long inventoryId = (Long) tableModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Inventaire, Void> worker = new SwingWorker<Inventaire, Void>() {
            @Override
            protected Inventaire doInBackground() throws Exception {
                return inventaireService.findInventaireById(inventoryId).orElse(null);
            }
            
            @Override
            protected void done() {
                try {
                    Inventaire inventaire = get();
                    if (inventaire != null) {
                        showInventoryDetails(inventaire);
                    } else {
                        UIUtils.showErrorMessage(InventoryManagementPanel.this, 
                            "Inventaire non trouvé.");
                    }
                } catch (Exception e) {
                    UIUtils.showErrorMessage(InventoryManagementPanel.this, 
                        "Erreur lors du chargement: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void showInventoryDetails(Inventaire inventaire) {
        // Use Dialog as parent for JDialog
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Détails Inventaire", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(UIConstants.NORMAL_FONT);
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(inventaire.getIdInventaire()).append("\n\n");
        details.append("Description: ").append(inventaire.getDescription()).append("\n\n");
        details.append("Date de création: ").append(inventaire.getDateCreation().format(dateFormatter)).append("\n\n");
        details.append("Statut: ").append(getStatusDisplayNameFromEtat(inventaire.getEtatInventaire())).append("\n\n");
        if (inventaire.getDateFin() != null) {
            details.append("Date de fin: ").append(inventaire.getDateFin().format(dateFormatter)).append("\n\n");
        }
        detailsArea.setText(details.toString());
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        JButton closeButton = UIUtils.createSecondaryButton("Fermer");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }
}
