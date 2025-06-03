package insea.neobrain.ui.stock;

import insea.neobrain.entity.Reclamation;
import insea.neobrain.entity.EtatReclamation;
import insea.neobrain.entity.TypeReclamation;
import insea.neobrain.service.ReclamationService;
import insea.neobrain.ui.common.SearchFilterPanel;
import insea.neobrain.ui.common.UIUtils;
import insea.neobrain.util.ExportUtil;
import insea.neobrain.util.AuditLogger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing reclamations (complaints and returns)
 */
public class ReclamationManagementPanel extends JPanel {
    
    private final ReclamationService reclamationService;
    private final String currentUsername;
    
    private JTable reclamationTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private SearchFilterPanel searchPanel;
    
    private JButton validateButton;
    private JButton refuseButton;
    private JButton refreshButton;
    private JButton detailsButton;
    
    private JLabel statsLabel;
    
    public ReclamationManagementPanel(ReclamationService reclamationService, String currentUsername) {
        this.reclamationService = reclamationService;
        this.currentUsername = currentUsername;
        
        initComponents();
        setupLayout();
        setupEventHandlers();
        loadReclamations();
        updateStats();
    }
    
    private void initComponents() {
        // Create table model
        String[] columns = {
            "ID", "Date", "Type", "État", "Description", "Commande", "Client"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table
        reclamationTable = new JTable(tableModel);
        reclamationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reclamationTable.setRowHeight(25);
        
        // Setup row sorter
        rowSorter = new TableRowSorter<>(tableModel);
        reclamationTable.setRowSorter(rowSorter);
        
        // Create search panel
        String[] filterOptions = {"Tous", "En Attente", "Validées", "Refusées", "Retours", "Échecs Réception"};
        searchPanel = new SearchFilterPanel(rowSorter, filterOptions, true);
        
        // Create buttons
        validateButton = UIUtils.createStyledButton("Valider", UIUtils.SUCCESS_COLOR);
        refuseButton = UIUtils.createStyledButton("Refuser", UIUtils.DANGER_COLOR);
        refreshButton = UIUtils.createStyledButton("Actualiser", UIUtils.PRIMARY_COLOR);
        detailsButton = UIUtils.createStyledButton("Détails", UIUtils.INFO_COLOR);
        
        // Stats label
        statsLabel = new JLabel();
        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Gestion des Réclamations"));
        
        // Top panel with search and stats
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(statsLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(reclamationTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(validateButton);
        buttonPanel.add(refuseButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Table selection handler
        reclamationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        // Button handlers
        validateButton.addActionListener(e -> validateSelectedReclamation());
        refuseButton.addActionListener(e -> refuseSelectedReclamation());
        detailsButton.addActionListener(e -> showReclamationDetails());
        refreshButton.addActionListener(e -> {
            loadReclamations();
            updateStats();
        });
        
        // Export handler
        searchPanel.addExportActionListener(e -> exportReclamations());
        
        // Filter handler for search panel
        searchPanel.addExportActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filter = searchPanel.getSelectedFilter();
                applyFilter(filter);
            }
        });
    }
    
    private void loadReclamations() {
        try {
            List<Reclamation> reclamations = reclamationService.findAll();
            updateTable(reclamations);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des réclamations: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable(List<Reclamation> reclamations) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Reclamation reclamation : reclamations) {
            Object[] row = {
                reclamation.getIdReclamation(),
                reclamation.getDateReclamation().format(formatter),
                reclamation.getTypeReclamation().getDisplayName(),
                reclamation.getEtatReclamation().getDisplayName(),
                truncateText(reclamation.getDescription(), 50),
                reclamation.getLigneCommande().getIdLigneVente(),
                getClientName(reclamation)
            };
            tableModel.addRow(row);
        }
    }
    
    private String getClientName(Reclamation reclamation) {
        try {
            if (reclamation.getLigneCommande() != null && 
                reclamation.getLigneCommande().getCommandeVente() != null &&
                reclamation.getLigneCommande().getCommandeVente().getClient() != null) {
                var client = reclamation.getLigneCommande().getCommandeVente().getClient();
                return client.getPrenom() + " " + client.getNom();
            }
        } catch (Exception e) {
            // Handle null safely
        }
        return "N/A";
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
    
    private void updateButtonStates() {
        boolean hasSelection = reclamationTable.getSelectedRow() != -1;
        boolean isPending = false;
        
        if (hasSelection) {
            int selectedRow = reclamationTable.getSelectedRow();
            if (selectedRow >= 0) {
                String status = (String) tableModel.getValueAt(
                    reclamationTable.convertRowIndexToModel(selectedRow), 3);
                isPending = "En Attente".equals(status);
            }
        }
        
        validateButton.setEnabled(hasSelection && isPending);
        refuseButton.setEnabled(hasSelection && isPending);
        detailsButton.setEnabled(hasSelection);
    }
    
    private void validateSelectedReclamation() {
        int selectedRow = reclamationTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long reclamationId = (Long) tableModel.getValueAt(
            reclamationTable.convertRowIndexToModel(selectedRow), 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir valider cette réclamation ?",
            "Confirmer Validation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = reclamationService.validateReclamation(reclamationId, currentUsername);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Réclamation validée avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadReclamations();
                    updateStats();
                    AuditLogger.logUserAction(currentUsername, "VALIDATE_RECLAMATION",
                        "Validated reclamation ID: " + reclamationId);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la validation de la réclamation",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refuseSelectedReclamation() {
        int selectedRow = reclamationTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long reclamationId = (Long) tableModel.getValueAt(
            reclamationTable.convertRowIndexToModel(selectedRow), 0);
        
        // Ask for reason
        String reason = JOptionPane.showInputDialog(this,
            "Veuillez saisir la raison du refus:",
            "Refuser Réclamation",
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null && !reason.trim().isEmpty()) {
            try {
                boolean success = reclamationService.refuseReclamation(reclamationId, currentUsername, reason);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Réclamation refusée avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadReclamations();
                    updateStats();
                    AuditLogger.logUserAction(currentUsername, "REFUSE_RECLAMATION",
                        "Refused reclamation ID: " + reclamationId + ", Reason: " + reason);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du refus de la réclamation",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showReclamationDetails() {
        int selectedRow = reclamationTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long reclamationId = (Long) tableModel.getValueAt(
            reclamationTable.convertRowIndexToModel(selectedRow), 0);
        
        try {
            var reclamationOpt = reclamationService.findById(reclamationId);
            if (reclamationOpt.isPresent()) {
                showReclamationDetailsDialog(reclamationOpt.get());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des détails: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showReclamationDetailsDialog(Reclamation reclamation) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Détails de la Réclamation", true);
        dialog.setLayout(new BorderLayout());
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Add details
        addDetailRow(detailsPanel, gbc, 0, "ID:", reclamation.getIdReclamation().toString());
        addDetailRow(detailsPanel, gbc, 1, "Date:", 
            reclamation.getDateReclamation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        addDetailRow(detailsPanel, gbc, 2, "Type:", reclamation.getTypeReclamation().getDisplayName());
        addDetailRow(detailsPanel, gbc, 3, "État:", reclamation.getEtatReclamation().getDisplayName());
        addDetailRow(detailsPanel, gbc, 4, "Client:", getClientName(reclamation));
        addDetailRow(detailsPanel, gbc, 5, "Commande:", 
            reclamation.getLigneCommande().getIdLigneVente().toString());
        
        // Description area
        gbc.gridx = 0; gbc.gridy = 6;
        detailsPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JTextArea descArea = new JTextArea(reclamation.getDescription(), 5, 30);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        detailsPanel.add(new JScrollPane(descArea), gbc);
        
        dialog.add(detailsPanel, BorderLayout.CENTER);
        
        // Close button
        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField field = new JTextField(value);
        field.setEditable(false);
        panel.add(field, gbc);
    }
    
    private void updateStats() {
        try {
            var stats = reclamationService.getReclamationStats();
            String statsText = String.format(
                "Total: %d | En Attente: %d | Validées: %d | Refusées: %d | Retours: %d | Échecs: %d",
                stats.getTotalReclamations(),
                stats.getPendingReclamations(),
                stats.getValidatedReclamations(),
                stats.getRefusedReclamations(),
                stats.getReturnRequests(),
                stats.getReceptionFailures()
            );
            statsLabel.setText(statsText);
        } catch (Exception e) {
            statsLabel.setText("Erreur lors du chargement des statistiques");
        }
    }
    
    private void applyFilter(String filter) {
        // This would be implemented to filter based on the selected option
        // For now, we'll just refresh the table
        loadReclamations();
    }
    
    private void exportReclamations() {
        ExportUtil.exportTableToCSV(reclamationTable, "reclamations");
    }
    
    /**
     * Refresh data in the panel
     */
    public void refreshData() {
        loadReclamations();
        updateStats();
    }
}
