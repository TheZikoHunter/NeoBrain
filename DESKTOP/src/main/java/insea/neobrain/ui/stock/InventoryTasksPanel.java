package insea.neobrain.ui.stock;

import insea.neobrain.entity.EtatTache;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.TacheInventaire;
import insea.neobrain.service.TacheInventaireService;
import insea.neobrain.service.impl.TacheInventaireServiceImpl;
import insea.neobrain.ui.common.UIConstants;
import insea.neobrain.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing inventory tasks
 */
public class InventoryTasksPanel extends JPanel {
    
    private final Personnel loggedInUser;
    private final TacheInventaireService taskService;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JButton updateTaskButton;
    private JButton viewDetailsButton;
    private JComboBox<EtatTache> statusFilterComboBox;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public InventoryTasksPanel(Personnel loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.taskService = new TacheInventaireServiceImpl();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadTasks();
    }
    
    private void initComponents() {
        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Tâches d'Inventaire");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel filterLabel = new JLabel("Filtrer par état: ");
        statusFilterComboBox = new JComboBox<>(EtatTache.values());
        statusFilterComboBox.insertItemAt(null, 0);
        statusFilterComboBox.setSelectedIndex(0);
        filterPanel.add(filterLabel);
        filterPanel.add(statusFilterComboBox);
        
        statusFilterComboBox.addActionListener(e -> loadTasks());
        
        titlePanel.add(filterPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"ID Tâche", "Produit", "Inventaire", "Date Tâche", "État", "Qté Physique"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        taskTable = UIUtils.createStyledTable();
        taskTable.setModel(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(taskTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        updateTaskButton = UIUtils.createPrimaryButton("Mettre à jour");
        viewDetailsButton = UIUtils.createSecondaryButton("Voir détails");
        
        updateTaskButton.addActionListener(e -> updateSelectedTask());
        viewDetailsButton.addActionListener(e -> viewTaskDetails());
        
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(updateTaskButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadTasks() {
        tableModel.setRowCount(0);
        
        try {
            List<TacheInventaire> tasks;
            EtatTache selectedStatus = (EtatTache) statusFilterComboBox.getSelectedItem();
            
            if (selectedStatus == null) {
                tasks = taskService.findByPersonnel(loggedInUser);
            } else {
                tasks = taskService.findByPersonnelAndStatus(loggedInUser, selectedStatus);
            }
            
            for (TacheInventaire task : tasks) {
                tableModel.addRow(new Object[]{
                    task.getIdTacheInventaire(),
                    task.getProduit().getNom(),
                    task.getInventaire().getIdInventaire(), // or replace with the correct method/field for reference
                    task.getDateTache().format(DATE_FORMATTER),
                    task.getEtatTache(),
                    task.getQuantitePhysique()
                });
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des tâches: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une tâche à mettre à jour",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Long taskId = (Long) tableModel.getValueAt(selectedRow, 0);
        TacheInventaire task = taskService.findById(taskId);
        
        if (task == null) {
            JOptionPane.showMessageDialog(this,
                    "Tâche non trouvée",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (task.getEtatTache() == EtatTache.TERMINEE) {
            JOptionPane.showMessageDialog(this,
                    "Cette tâche est déjà terminée.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        new TaskUpdateDialog(task, taskService).setVisible(true);
        loadTasks();
    }
    
    private void viewTaskDetails() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une tâche à visualiser",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Long taskId = (Long) tableModel.getValueAt(selectedRow, 0);
        TacheInventaire task = taskService.findById(taskId);
        
        if (task == null) {
            JOptionPane.showMessageDialog(this,
                    "Tâche non trouvée",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        new TaskDetailsDialog(task).setVisible(true);
    }
    
    /**
     * Dialog for updating an inventory task
     */
    private class TaskUpdateDialog extends JDialog {
        
        private final TacheInventaire task;
        private final TacheInventaireService service;
        private JTextField productField;
        private JTextField currentQtyField;
        private JTextField physicalQtyField;
        private JComboBox<EtatTache> statusComboBox;
        
        public TaskUpdateDialog(TacheInventaire task, TacheInventaireService service) {
            super((JFrame) SwingUtilities.getWindowAncestor(InventoryTasksPanel.this), "Mise à jour de tâche", true);
            this.task = task;
            this.service = service;
            
            initComponents();
            setLocationRelativeTo(InventoryTasksPanel.this);
        }
        
        private void initComponents() {
            setLayout(new BorderLayout(10, 10));
            setSize(400, 300);
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Product
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(new JLabel("Produit:"), gbc);
            
            gbc.gridx = 1;
            productField = new JTextField(task.getProduit().getNom());
            productField.setEditable(false);
            formPanel.add(productField, gbc);
            
            // Current quantity in system
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(new JLabel("Quantité système:"), gbc);
            
            gbc.gridx = 1;
            currentQtyField = new JTextField(String.valueOf(task.getProduit().getQuantiteStock()));
            currentQtyField.setEditable(false);
            formPanel.add(currentQtyField, gbc);
            
            // Physical quantity
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(new JLabel("Quantité physique:"), gbc);
            
            gbc.gridx = 1;
            Integer physQty = task.getQuantitePhysique();
            physicalQtyField = new JTextField(physQty != null ? physQty.toString() : "");
            formPanel.add(physicalQtyField, gbc);
            
            // Status
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(new JLabel("État:"), gbc);
            
            gbc.gridx = 1;
            statusComboBox = new JComboBox<>(new EtatTache[]{EtatTache.EN_COURS, EtatTache.TERMINEE});
            statusComboBox.setSelectedItem(task.getEtatTache());
            formPanel.add(statusComboBox, gbc);
            
            add(formPanel, BorderLayout.CENTER);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = UIUtils.createPrimaryButton("Enregistrer");
            JButton cancelButton = UIUtils.createSecondaryButton("Annuler");
            
            saveButton.addActionListener(e -> saveTask());
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void saveTask() {
            try {
                String qtyStr = physicalQtyField.getText().trim();
                Integer quantity = qtyStr.isEmpty() ? null : Integer.parseInt(qtyStr);
                
                if (statusComboBox.getSelectedItem() == EtatTache.TERMINEE && quantity == null) {
                    JOptionPane.showMessageDialog(this,
                            "Vous devez spécifier une quantité physique pour terminer la tâche.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                task.setQuantitePhysique(quantity);
                task.setEtatTache((EtatTache) statusComboBox.getSelectedItem());
                
                service.update(task);
                dispose();
                
                JOptionPane.showMessageDialog(InventoryTasksPanel.this,
                        "Tâche mise à jour avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Quantité invalide. Veuillez entrer un nombre entier.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la mise à jour: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Dialog for viewing task details
     */
    private class TaskDetailsDialog extends JDialog {
        
        public TaskDetailsDialog(TacheInventaire task) {
            super((JFrame) SwingUtilities.getWindowAncestor(InventoryTasksPanel.this), "Détails de la tâche", true);
            
            initComponents(task);
            setSize(500, 400);
            setLocationRelativeTo(InventoryTasksPanel.this);
        }
        
        private void initComponents(TacheInventaire task) {
            setLayout(new BorderLayout(10, 10));
            
            // Header
            JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel titleLabel = new JLabel("Détails de la Tâche #" + task.getIdTacheInventaire());
            titleLabel.setFont(UIConstants.SUBTITLE_FONT);
            headerPanel.add(titleLabel, BorderLayout.WEST);
            
            // Status label with color
            JLabel statusLabel = new JLabel(task.getEtatTache().toString());
            statusLabel.setFont(UIConstants.SUBTITLE_FONT);
            
            switch(task.getEtatTache()) {
                case EN_ATTENTE:
                    statusLabel.setForeground(UIConstants.WARNING_COLOR);
                    break;
                case EN_COURS:
                    statusLabel.setForeground(UIConstants.PRIMARY_COLOR);
                    break;
                case TERMINEE:
                    statusLabel.setForeground(UIConstants.SUCCESS_COLOR);
                    break;
                case ANNULEE:
                    statusLabel.setForeground(UIConstants.DANGER_COLOR);
                    break;
            }
            
            headerPanel.add(statusLabel, BorderLayout.EAST);
            add(headerPanel, BorderLayout.NORTH);
            
            // Details
            JPanel detailsPanel = new JPanel(new GridBagLayout());
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = 1;
            gbc.weightx = 0.3;
            
            // Labels on the left
            gbc.gridx = 0;
            gbc.gridy = 0;
            addDetailField(detailsPanel, gbc, "ID Inventaire:", 
                    task.getInventaire().getIdInventaire().toString());
            
            gbc.gridy++;
            addDetailField(detailsPanel, gbc, "Référence Inventaire:", 
                    String.valueOf(task.getInventaire().getIdInventaire()));
            
            gbc.gridy++;
            addDetailField(detailsPanel, gbc, "Produit:", 
                    task.getProduit().getNom());
            
            gbc.gridy++;
            addDetailField(detailsPanel, gbc, "Code Produit:", 
                    task.getProduit().getCodeProduit());
            
            gbc.gridy++;
            addDetailField(detailsPanel, gbc, "Date Tâche:", 
                    task.getDateTache().format(DATE_FORMATTER));
            
            // Labels on the right
            gbc.gridx = 2;
            gbc.gridy = 0;
            addDetailField(detailsPanel, gbc, "Quantité Système:", 
                    String.valueOf(task.getProduit().getQuantiteStock()));
            
            gbc.gridy++;
            addDetailField(detailsPanel, gbc, "Quantité Physique:", 
                    task.getQuantitePhysique() != null ? 
                    task.getQuantitePhysique().toString() : "Non définie");
            
            gbc.gridy++;
            Integer diff = task.getQuantitePhysique() != null ? 
                    task.getQuantitePhysique() - task.getProduit().getQuantiteStock() : null;
            addDetailField(detailsPanel, gbc, "Différence:", 
                    diff != null ? diff.toString() : "N/A");
            
            gbc.gridy++;
            addDetailField(detailsPanel, gbc, "Personnel assigné:", 
                    task.getPersonnel().getNom() + " " + task.getPersonnel().getPrenom());
            
            gbc.gridy++;
            addDetailField(detailsPanel, gbc, "État:", 
                    task.getEtatTache().toString());
            
            add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
            
            // Close button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton closeButton = UIUtils.createSecondaryButton("Fermer");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void addDetailField(JPanel panel, GridBagConstraints gbc,
                String labelText, String value) {
            JLabel label = new JLabel(labelText);
            label.setFont(UIConstants.NORMAL_FONT);
            panel.add(label, gbc);
            
            gbc.gridx++;
            gbc.weightx = 0.7;
            
            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
            panel.add(valueLabel, gbc);
            
            gbc.gridx--;
            gbc.weightx = 0.3;
        }
    }
    
    /**
     * Refresh the data in the table
     */
    public void refreshData() {
        loadTasks();
    }
}
