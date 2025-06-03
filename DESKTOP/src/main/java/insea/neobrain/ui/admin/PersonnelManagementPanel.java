package insea.neobrain.ui.admin;

import insea.neobrain.entity.*;
import insea.neobrain.service.PersonnelService;
import insea.neobrain.service.impl.PersonnelServiceImpl;
import insea.neobrain.repository.impl.PersonnelRepositoryImpl;
import insea.neobrain.ui.common.UIConstants;
import insea.neobrain.ui.common.UIUtils;
import insea.neobrain.service.impl.AuthenticationServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for managing personnel (add, edit, delete, view)
 */
public class PersonnelManagementPanel extends JPanel implements ActionListener {
    
    private final PersonnelService personnelService;
    
    // UI Components
    private JTable personnelTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JTextField searchField;
    private JButton searchButton;
    
    public PersonnelManagementPanel() {
        this.personnelService = new PersonnelServiceImpl(new PersonnelRepositoryImpl(), new AuthenticationServiceImpl(new PersonnelRepositoryImpl()));
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Rôle", "Date Embauche"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                return String.class;
            }
        };
        
        personnelTable = UIUtils.createStyledTable();
        personnelTable.setModel(tableModel);
        UIUtils.setupTableColumnWidths(personnelTable, new int[]{50, 120, 120, 180, 120, 120, 120});
        
        // Buttons
        addButton = UIUtils.createSuccessButton("Add");
        editButton = UIUtils.createPrimaryButton("Edit");
        deleteButton = UIUtils.createDangerButton("Delete");
        refreshButton = UIUtils.createSecondaryButton("Refresh");
        
        // Search components
        searchField = UIUtils.createStyledTextField();
        searchField.setPreferredSize(new Dimension(200, UIConstants.TEXTFIELD_HEIGHT));
        searchButton = UIUtils.createSecondaryButton("Search");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Personnel Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(personnelTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Top panel combining title, search, and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.add(searchPanel, BorderLayout.NORTH);
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
        
        // Enter key for search
        searchField.addActionListener(e -> performSearch());
        
        // Double-click to edit
        personnelTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editSelectedPersonnel();
                }
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            showAddPersonnelDialog();
        } else if (e.getSource() == editButton) {
            editSelectedPersonnel();
        } else if (e.getSource() == deleteButton) {
            deleteSelectedPersonnel();
        } else if (e.getSource() == refreshButton) {
            refreshData();
        } else if (e.getSource() == searchButton) {
            performSearch();
        }
    }
    
    public void refreshData() {
        SwingWorker<List<Personnel>, Void> worker = new SwingWorker<List<Personnel>, Void>() {
            @Override
            protected List<Personnel> doInBackground() throws Exception {
                return personnelService.findAllPersonnel();
            }
            @Override
            protected void done() {
                try {
                    List<Personnel> personnelList = get();
                    updateTable(personnelList);
                } catch (Exception e) {
                    UIUtils.showErrorMessage(PersonnelManagementPanel.this, 
                        "Error loading personnel data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void updateTable(List<Personnel> personnelList) {
        tableModel.setRowCount(0);
        for (Personnel personnel : personnelList) {
            Object[] row = {
                personnel.getIdPersonne(),
                personnel.getNom(),
                personnel.getPrenom(),
                personnel.getEmail(),
                personnel.getTelephone(),
                personnel.getRole(),
                personnel.getDateEmbauche() != null ? personnel.getDateEmbauche().toString() : ""
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
        SwingWorker<List<Personnel>, Void> worker = new SwingWorker<List<Personnel>, Void>() {
            @Override
            protected List<Personnel> doInBackground() throws Exception {
                return personnelService.searchByName(searchTerm);
            }
            @Override
            protected void done() {
                try {
                    List<Personnel> results = get();
                    updateTable(results);
                } catch (Exception e) {
                    UIUtils.showErrorMessage(PersonnelManagementPanel.this, 
                        "Error searching personnel: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void showAddPersonnelDialog() {
        PersonnelFormDialog dialog = new PersonnelFormDialog(
            SwingUtilities.getWindowAncestor(this), "Add Personnel", true);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Personnel personnel = dialog.getPersonnel();
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    personnelService.createPersonnel(personnel);
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        UIUtils.showSuccessMessage(PersonnelManagementPanel.this, 
                            "Personnel added successfully!");
                        refreshData();
                    } catch (Exception e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        String msg = cause.getMessage();
                        if (msg != null && msg.startsWith("Validation errors:")) {
                            String errors = msg.replace("Validation errors:", "").trim();
                            UIUtils.showErrorMessage(PersonnelManagementPanel.this, 
                                "Please correct the following errors:\n" + errors.replace(", ", "\n"));
                        } else {
                            UIUtils.showErrorMessage(PersonnelManagementPanel.this, 
                                "Error adding personnel: " + msg);
                        }
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void editSelectedPersonnel() {
        int selectedRow = personnelTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showWarningMessage(this, "Please select a personnel to edit.");
            return;
        }
        Object idObj = tableModel.getValueAt(selectedRow, 0);
        if (!(idObj instanceof Long)) {
            UIUtils.showErrorMessage(this, "Invalid personnel ID. (ID is not a Long)");
            return;
        }
        Long personnelId = (Long) idObj;
        System.out.println("[DEBUG] editSelectedPersonnel: selectedRow=" + selectedRow + ", personnelId=" + personnelId);
        SwingWorker<Personnel, Void> worker = new SwingWorker<Personnel, Void>() {
            @Override
            protected Personnel doInBackground() throws Exception {
                System.out.println("[DEBUG] doInBackground: calling findPersonnelById(" + personnelId + ")");
                return personnelService.findPersonnelById(personnelId).orElse(null);
            }
            @Override
            protected void done() {
                try {
                    Personnel personnel = get();
                    System.out.println("[DEBUG] done: loaded personnel=" + personnel);
                    if (personnel != null) {
                        PersonnelFormDialog dialog = new PersonnelFormDialog(
                            SwingUtilities.getWindowAncestor(PersonnelManagementPanel.this),
                            "Edit Personnel", true, personnel);
                        dialog.setVisible(true);
                        if (dialog.isConfirmed()) {
                            Personnel updatedPersonnel = dialog.getPersonnel();
                            System.out.println("[DEBUG] done: updatedPersonnel from dialog=" + updatedPersonnel);
                            // Ensure the idPersonne (Long) is preserved for update
                            updatedPersonnel.setIdPersonne(personnelId);
                            System.out.println("[DEBUG] done: updatedPersonnel after setIdPersonne=" + updatedPersonnel.getIdPersonne());
                            updatePersonnel(updatedPersonnel);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UIUtils.showErrorMessage(PersonnelManagementPanel.this,
                        "Error loading personnel: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void updatePersonnel(Personnel personnel) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                personnelService.updatePersonnel(personnel);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    UIUtils.showSuccessMessage(PersonnelManagementPanel.this, 
                        "Personnel updated successfully!");
                    refreshData();
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    String msg = cause.getMessage();
                    if (msg != null && msg.startsWith("Validation errors:")) {
                        String errors = msg.replace("Validation errors:", "").trim();
                        UIUtils.showErrorMessage(PersonnelManagementPanel.this, 
                            "Please correct the following errors:\n" + errors.replace(", ", "\n"));
                    } else {
                        UIUtils.showErrorMessage(PersonnelManagementPanel.this, 
                            "Error updating personnel: " + msg);
                    }
                }
            }
        };
        worker.execute();
    }
    
    private void deleteSelectedPersonnel() {
        int selectedRow = personnelTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showWarningMessage(this, "Please select a personnel to delete.");
            return;
        }
        Object idObj = tableModel.getValueAt(selectedRow, 0);
        if (!(idObj instanceof Long)) {
            UIUtils.showErrorMessage(this, "Invalid personnel ID. (ID is not a Long)");
            return;
        }
        Long personnelId = (Long) idObj;
        String personnelName = tableModel.getValueAt(selectedRow, 1) + " " +
                              tableModel.getValueAt(selectedRow, 2);

        boolean confirmed = UIUtils.showConfirmDialog(this,
            "Are you sure you want to delete " + personnelName + "?\nThis action cannot be undone.");

        if (confirmed) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    boolean deleted = personnelService.deletePersonnel(personnelId);
                    if (!deleted) {
                        throw new Exception("Personnel could not be deleted. It may have associated data or constraints.");
                    }
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        UIUtils.showSuccessMessage(PersonnelManagementPanel.this,
                            "Personnel deleted successfully!");
                        refreshData();
                    } catch (Exception e) {
                        UIUtils.showErrorMessage(PersonnelManagementPanel.this,
                            "Error deleting personnel: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }
}
