package insea.neobrain.util;

import com.opencsv.CSVWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for data export functionality
 */
public class ExportUtil {
    
    /**
     * Export JTable data to CSV file
     */
    public static void exportTableToCSV(JTable table, String defaultFileName) {
        if (table == null || table.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, 
                "Aucune donnée à exporter", 
                "Export CSV", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));
        fileChooser.setSelectedFile(new java.io.File(generateFileName(defaultFileName, "csv")));
        
        int result = fileChooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        exportTableToCSV(table, fileChooser.getSelectedFile().getAbsolutePath());
        JOptionPane.showMessageDialog(null, 
            "Export réussi vers: " + fileChooser.getSelectedFile().getAbsolutePath(), 
            "Export CSV", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Export list data to CSV file
     */
    public static <T> void exportListToCSV(List<T> data, String[] headers, 
                                          DataExtractor<T> extractor, String defaultFileName) {
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "Aucune donnée à exporter", 
                "Export CSV", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));
        fileChooser.setSelectedFile(new java.io.File(generateFileName(defaultFileName, "csv")));
        
        int result = fileChooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileChooser.getSelectedFile()))) {
            // Write headers
            writer.writeNext(headers);
            
            // Write data
            for (T item : data) {
                String[] row = extractor.extractData(item);
                writer.writeNext(row);
            }
            
            JOptionPane.showMessageDialog(null, 
                "Export réussi vers: " + fileChooser.getSelectedFile().getAbsolutePath(), 
                "Export CSV", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de l'export: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Generate a unique filename with timestamp
     */
    private static String generateFileName(String baseName, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        return baseName + "_" + timestamp + "." + extension;
    }
    
    /**
     * Interface for extracting data from objects for CSV export
     */
    @FunctionalInterface
    public interface DataExtractor<T> {
        String[] extractData(T item);
    }
    
    /**
     * Export inventory report
     */
    public static void exportInventoryReport(List<Object[]> inventoryData) {
        String[] headers = {"Produit", "Catégorie", "Stock Système", "Stock Physique", "Écart", "Statut"};
        
        exportListToCSV(inventoryData, headers, data -> {
            return new String[]{
                data[0] != null ? data[0].toString() : "",
                data[1] != null ? data[1].toString() : "",
                data[2] != null ? data[2].toString() : "",
                data[3] != null ? data[3].toString() : "",
                data[4] != null ? data[4].toString() : "",
                data[5] != null ? data[5].toString() : ""
            };
        }, "rapport_inventaire");
    }
    
    /**
     * Export sales report
     */
    public static void exportSalesReport(List<Object[]> salesData) {
        String[] headers = {"Date", "Commande", "Client", "Produit", "Quantité", "Prix Unitaire", "Total"};
        
        exportListToCSV(salesData, headers, data -> {
            return new String[]{
                data[0] != null ? data[0].toString() : "",
                data[1] != null ? data[1].toString() : "",
                data[2] != null ? data[2].toString() : "",
                data[3] != null ? data[3].toString() : "",
                data[4] != null ? data[4].toString() : "",
                data[5] != null ? data[5].toString() : "",
                data[6] != null ? data[6].toString() : ""
            };
        }, "rapport_ventes");
    }
    
    /**
     * Export data to CSV file (method expected by tests)
     */
    public static void exportToCsv(java.io.File file, String[] headers, String[][] data) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (headers == null) {
            throw new IllegalArgumentException("Headers cannot be null");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        
        // Create parent directories if they don't exist
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            // Write headers
            writer.writeNext(headers);
            
            // Write data
            for (String[] row : data) {
                // Handle null values in rows
                String[] cleanRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    cleanRow[i] = row[i] != null ? row[i] : "";
                }
                writer.writeNext(cleanRow);
            }
        }
    }
}
