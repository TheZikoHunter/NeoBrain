package insea.neobrain.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for ExportUtil class
 */
public class ExportUtilTest {

    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = tempDir.resolve("test_export.csv").toFile();
    }

    @Test
    @DisplayName("Should export simple data to CSV")
    public void testExportToCsvSimpleData() throws IOException {
        // Given
        String[] headers = {"Name", "Age", "City"};
        String[][] data = {
            {"John", "25", "Paris"},
            {"Jane", "30", "London"},
            {"Bob", "35", "New York"}
        };

        // When
        ExportUtil.exportToCsv(tempFile, headers, data);

        // Then
        assertThat(tempFile).exists();
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertThat(lines).hasSize(4); // 1 header + 3 data rows
        assertThat(lines.get(0)).isEqualTo("Name,Age,City");
        assertThat(lines.get(1)).isEqualTo("John,25,Paris");
        assertThat(lines.get(2)).isEqualTo("Jane,30,London");
        assertThat(lines.get(3)).isEqualTo("Bob,35,New York");
    }

    @Test
    @DisplayName("Should handle empty data array")
    public void testExportToCsvEmptyData() throws IOException {
        // Given
        String[] headers = {"Name", "Age"};
        String[][] data = {};

        // When
        ExportUtil.exportToCsv(tempFile, headers, data);

        // Then
        assertThat(tempFile).exists();
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertThat(lines).hasSize(1); // Only header
        assertThat(lines.get(0)).isEqualTo("Name,Age");
    }

    @Test
    @DisplayName("Should handle data with commas and quotes")
    public void testExportToCsvWithSpecialCharacters() throws IOException {
        // Given
        String[] headers = {"Name", "Description"};
        String[][] data = {
            {"John, Jr.", "A \"special\" person"},
            {"Company, Inc.", "Software, \"AI\" solutions"}
        };

        // When
        ExportUtil.exportToCsv(tempFile, headers, data);

        // Then
        assertThat(tempFile).exists();
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertThat(lines).hasSize(3);
        // OpenCSV should properly escape commas and quotes
        assertThat(lines.get(1)).contains("\"John, Jr.\"");
        assertThat(lines.get(1)).contains("\"A \"\"special\"\" person\"");
    }

    @Test
    @DisplayName("Should handle null values in data")
    public void testExportToCsvWithNullValues() throws IOException {
        // Given
        String[] headers = {"Name", "Optional"};
        String[][] data = {
            {"John", null},
            {null, "Value"},
            {"Jane", "Normal"}
        };

        // When
        ExportUtil.exportToCsv(tempFile, headers, data);

        // Then
        assertThat(tempFile).exists();
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertThat(lines).hasSize(4);
        assertThat(lines.get(1)).isEqualTo("John,");
        assertThat(lines.get(2)).isEqualTo(",Value");
        assertThat(lines.get(3)).isEqualTo("Jane,Normal");
    }

    @Test
    @DisplayName("Should throw exception for null file")
    public void testExportToCsvNullFile() {
        // Given
        String[] headers = {"Name"};
        String[][] data = {{"John"}};

        // When/Then
        assertThatThrownBy(() -> ExportUtil.exportToCsv(null, headers, data))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("File cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null headers")
    public void testExportToCsvNullHeaders() {
        // When/Then
        assertThatThrownBy(() -> ExportUtil.exportToCsv(tempFile, null, new String[][]{}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Headers cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null data")
    public void testExportToCsvNullData() {
        // Given
        String[] headers = {"Name"};

        // When/Then
        assertThatThrownBy(() -> ExportUtil.exportToCsv(tempFile, headers, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Data cannot be null");
    }

    @Test
    @DisplayName("Should handle mismatched data row lengths")
    public void testExportToCsvMismatchedRowLengths() throws IOException {
        // Given
        String[] headers = {"Name", "Age", "City"};
        String[][] data = {
            {"John", "25"}, // Missing city
            {"Jane", "30", "London", "Extra"}, // Extra field
            {"Bob", "35", "New York"} // Complete
        };

        // When
        ExportUtil.exportToCsv(tempFile, headers, data);

        // Then
        assertThat(tempFile).exists();
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertThat(lines).hasSize(4);
        assertThat(lines.get(1)).isEqualTo("John,25");
        assertThat(lines.get(2)).isEqualTo("Jane,30,London,Extra");
        assertThat(lines.get(3)).isEqualTo("Bob,35,New York");
    }

    @Test
    @DisplayName("Should handle large dataset")
    public void testExportToCsvLargeDataset() throws IOException {
        // Given
        String[] headers = {"ID", "Name", "Value"};
        String[][] data = new String[1000][];
        for (int i = 0; i < 1000; i++) {
            data[i] = new String[]{"ID" + i, "Name" + i, "Value" + i};
        }

        // When
        ExportUtil.exportToCsv(tempFile, headers, data);

        // Then
        assertThat(tempFile).exists();
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertThat(lines).hasSize(1001); // 1 header + 1000 data rows
        assertThat(lines.get(0)).isEqualTo("ID,Name,Value");
        assertThat(lines.get(1)).isEqualTo("ID0,Name0,Value0");
        assertThat(lines.get(1000)).isEqualTo("ID999,Name999,Value999");
    }

    @Test
    @DisplayName("Should handle empty strings in data")
    public void testExportToCsvWithEmptyStrings() throws IOException {
        // Given
        String[] headers = {"Name", "Description"};
        String[][] data = {
            {"", "Non-empty description"},
            {"John", ""},
            {"", ""}
        };

        // When
        ExportUtil.exportToCsv(tempFile, headers, data);

        // Then
        assertThat(tempFile).exists();
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertThat(lines).hasSize(4);
        assertThat(lines.get(1)).isEqualTo(",Non-empty description");
        assertThat(lines.get(2)).isEqualTo("John,");
        assertThat(lines.get(3)).isEqualTo(",");
    }

    @Test
    @DisplayName("Should create file in non-existent directory")
    public void testExportToCsvCreateDirectory() throws IOException {
        // Given
        File newDir = tempDir.resolve("new_directory").toFile();
        File fileInNewDir = new File(newDir, "test.csv");
        String[] headers = {"Name"};
        String[][] data = {{"John"}};

        // When
        ExportUtil.exportToCsv(fileInNewDir, headers, data);

        // Then
        assertThat(fileInNewDir).exists();
        assertThat(newDir).exists();
        List<String> lines = Files.readAllLines(fileInNewDir.toPath());
        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("Should overwrite existing file")
    public void testExportToCsvOverwriteFile() throws IOException {
        // Given
        Files.write(tempFile.toPath(), Arrays.asList("Old content"));
        String[] headers = {"Name"};
        String[][] data = {{"John"}};

        // When
        ExportUtil.exportToCsv(tempFile, headers, data);

        // Then
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertThat(lines).hasSize(2);
        assertThat(lines.get(0)).isEqualTo("Name");
        assertThat(lines.get(1)).isEqualTo("John");
        assertThat(lines).doesNotContain("Old content");
    }
}
