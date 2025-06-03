package insea.neobrain.ui.stock;

import insea.neobrain.entity.Produit;
import insea.neobrain.entity.CategorieProduit;
import insea.neobrain.service.ProduitService;
import insea.neobrain.ui.common.UIConstants;
import insea.neobrain.ui.common.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Dialog for adding and editing products
 */
public class ProductFormDialog extends JDialog {
    
    private final ProduitService produitService;
    private final ProductManagementPanel parentPanel;
    private Produit editingProduct;
    
    // Form components
    private JTextField codeField;
    private JTextField nomField;
    private JTextArea descriptionArea;
    private JComboBox<CategorieProduit> categorieComboBox;
    private JTextField prixField;
    private JTextField quantiteField;
    private JTextField seuilField;
    private JTextField codeBarreField;
    private JCheckBox disponibleCheckBox;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    /**
     * Constructor for adding a new product
     */
    public ProductFormDialog(Window parent, ProduitService produitService, ProductManagementPanel parentPanel) {
        this(parent, produitService, parentPanel, null);
    }
    
    /**
     * Constructor for editing an existing product
     */
    public ProductFormDialog(Window parent, ProduitService produitService, 
                           ProductManagementPanel parentPanel, Produit product) {
        super(parent, product == null ? "Nouveau Produit" : "Modifier Produit", ModalityType.APPLICATION_MODAL);
        
        this.produitService = produitService;
        this.parentPanel = parentPanel;
        this.editingProduct = product;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (editingProduct != null) {
            populateFields();
        }
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        UIUtils.centerWindow(this);
    }
    
    private void initializeComponents() {
        // Create form fields
        codeField = UIUtils.createStyledTextField();
        codeField.setToolTipText("Code produit (généré automatiquement si vide)");
        
        nomField = UIUtils.createStyledTextField();
        nomField.setToolTipText("Nom du produit (obligatoire)");
        
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setFont(UIConstants.NORMAL_FONT);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        categorieComboBox = UIUtils.createStyledComboBox(CategorieProduit.values());
        categorieComboBox.setToolTipText("Catégorie du produit");
        
        prixField = UIUtils.createStyledTextField();
        prixField.setToolTipText("Prix unitaire (ex: 15.50)");
        
        quantiteField = UIUtils.createStyledTextField();
        quantiteField.setToolTipText("Quantité en stock");
        
        seuilField = UIUtils.createStyledTextField();
        seuilField.setToolTipText("Seuil d'alerte stock minimum");
        
        codeBarreField = UIUtils.createStyledTextField();
        codeBarreField.setToolTipText("Code-barres du produit (optionnel)");
        
        disponibleCheckBox = new JCheckBox("Produit disponible");
        disponibleCheckBox.setFont(UIConstants.NORMAL_FONT);
        disponibleCheckBox.setSelected(true);
        
        // Create buttons
        saveButton = UIUtils.createPrimaryButton("Enregistrer");
        cancelButton = UIUtils.createSecondaryButton("Annuler");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Code produit
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Code Produit:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(codeField, gbc);
        
        // Nom
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Nom *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(nomField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(300, 80));
        formPanel.add(descScrollPane, gbc);
        
        // Catégorie
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Catégorie *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(categorieComboBox, gbc);
        
        // Prix
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Prix Unitaire *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(prixField, gbc);
        
        // Quantité
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Quantité Stock *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(quantiteField, gbc);
        
        // Seuil
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Seuil Stock *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(seuilField, gbc);
        
        // Code-barres
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Code-barres:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(codeBarreField, gbc);

        // Disponible
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(disponibleCheckBox, gbc);
        // Reset gridwidth for future use
        gbc.gridwidth = 1;

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProduct();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Enter key to save
        getRootPane().setDefaultButton(saveButton);
    }

    private void populateFields() {
        if (editingProduct != null) {
            codeField.setText(editingProduct.getCodeProduit() != null ? editingProduct.getCodeProduit().trim() : "");
            nomField.setText(editingProduct.getNom() != null ? editingProduct.getNom().trim() : "");
            descriptionArea.setText(editingProduct.getDescription() != null ? editingProduct.getDescription().trim() : "");
            categorieComboBox.setSelectedItem(editingProduct.getCategorie());
            prixField.setText(editingProduct.getPrix() != null ? editingProduct.getPrix().toString() : "");
            quantiteField.setText(editingProduct.getQuantiteStock() != null ? String.valueOf(editingProduct.getQuantiteStock()) : "");
            seuilField.setText(editingProduct.getSeuilStock() != null ? String.valueOf(editingProduct.getSeuilStock()) : "");
            codeBarreField.setText(editingProduct.getCodeBarre() != null ? editingProduct.getCodeBarre().trim() : "");
            disponibleCheckBox.setSelected(Boolean.TRUE.equals(editingProduct.getDisponible()));
        }
    }

    private void saveProduct() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Create or update product
            Produit produit = editingProduct != null ? editingProduct : new Produit();

            // Set basic fields
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                // Generate code if empty
                code = "PROD_" + System.currentTimeMillis();
            }
            produit.setCodeProduit(code);
            produit.setNom(nomField.getText().trim());
            produit.setDescription(descriptionArea.getText().trim());
            Object selectedCategorie = categorieComboBox.getSelectedItem();
            if (selectedCategorie instanceof CategorieProduit) {
                produit.setCategorie((CategorieProduit) selectedCategorie);
            } else {
                produit.setCategorie(null);
            }
            produit.setPrix(new java.math.BigDecimal(prixField.getText().trim()));
            produit.setQuantiteStock(Integer.parseInt(quantiteField.getText().trim()));
            produit.setSeuilStock(Integer.parseInt(seuilField.getText().trim()));
            produit.setCodeBarre(codeBarreField.getText().trim());
            produit.setDisponible(disponibleCheckBox.isSelected());

            // Set timestamps
            if (editingProduct == null) {
                produit.setDateCreation(java.time.LocalDateTime.now());
            }
            produit.setDerniereModification(java.time.LocalDateTime.now());

            // Save product
            if (editingProduct == null) {
                produitService.createProduit(produit);
                UIUtils.showSuccessMessage(this, "Produit créé avec succès!");
            } else {
                produitService.updateProduit(produit);
                UIUtils.showSuccessMessage(this, "Produit modifié avec succès!");
            }

            // Refresh parent panel on EDT
            if (parentPanel != null) {
                SwingUtilities.invokeLater(() -> parentPanel.refreshTable());
            }

            dispose();

        } catch (NumberFormatException e) {
            UIUtils.showErrorMessage(this, "Veuillez vérifier les valeurs numériques saisies.");
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String msg = cause.getMessage();
            if (msg != null && msg.startsWith("Validation errors:")) {
                String errors = msg.replace("Validation errors:", "").trim();
                UIUtils.showErrorMessage(this, 
                    "Veuillez corriger les erreurs suivantes :\n" + errors.replace(", ", "\n"));
            } else {
                UIUtils.showErrorMessage(this, "Erreur lors de l'enregistrement: " + msg);
            }
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        // Validate required fields
        if (nomField.getText().trim().isEmpty()) {
            UIUtils.showErrorMessage(this, "Le nom du produit est obligatoire.");
            nomField.requestFocus();
            return false;
        }

        if (categorieComboBox.getSelectedItem() == null) {
            UIUtils.showErrorMessage(this, "Veuillez sélectionner une catégorie.");
            categorieComboBox.requestFocus();
            return false;
        }

        // Validate price
        String prixText = prixField.getText().trim();
        try {
            BigDecimal prix = new BigDecimal(prixText);
            if (prix.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showErrorMessage(this, "Le prix doit être supérieur à 0.");
                prixField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            UIUtils.showErrorMessage(this, "Veuillez saisir un prix valide.");
            prixField.requestFocus();
            return false;
        }

        // Validate quantity
        String quantiteText = quantiteField.getText().trim();
        try {
            int quantite = Integer.parseInt(quantiteText);
            if (quantite < 0) {
                UIUtils.showErrorMessage(this, "La quantité ne peut pas être négative.");
                quantiteField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            UIUtils.showErrorMessage(this, "Veuillez saisir une quantité valide.");
            quantiteField.requestFocus();
            return false;
        }

        // Validate threshold
        String seuilText = seuilField.getText().trim();
        try {
            int seuil = Integer.parseInt(seuilText);
            if (seuil < 0) {
                UIUtils.showErrorMessage(this, "Le seuil ne peut pas être négatif.");
                seuilField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            UIUtils.showErrorMessage(this, "Veuillez saisir un seuil valide.");
            seuilField.requestFocus();
            return false;
        }

        return true;
    }
}
