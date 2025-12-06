package views.products;

import controllers.BrandController;
import controllers.CategoryController;
import controllers.ProductController;
import controllers.ProviderController;
import controllers.SubcategoryController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import models.Brand;
import models.Category;
import models.Product;
import models.Provider;
import models.Subcategory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ProductImportView extends JInternalFrame {

    public static boolean isOpen = false;

    private final ProductController productController;
    private final ProviderController providerController;
    private final BrandController brandController;
    private final CategoryController categoryController;
    private final SubcategoryController subcategoryController;

    private JComboBox<Provider> providerComboBox;
    private JTextField filePathField;
    private JButton importButton;
    private JTextArea logArea;
    private JProgressBar progressBar;

    public ProductImportView(ProductController productController,
            ProviderController providerController,
            BrandController brandController,
            CategoryController categoryController,
            SubcategoryController subcategoryController) {
        super("Importar productos", true, true, true, true);
        this.productController = productController;
        this.providerController = providerController;
        this.brandController = brandController;
        this.categoryController = categoryController;
        this.subcategoryController = subcategoryController;
        initComponents();
        loadProviders();
        isOpen = true;
    }

    private void initComponents() {
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(10, 10));
        content.setPreferredSize(new Dimension(520, 360));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setPreferredSize(new Dimension(520, 180));

        JLabel providerLabel = new JLabel("Proveedor:");
        providerLabel.setBounds(20, 20, 100, 25);
        formPanel.add(providerLabel);

        providerComboBox = new JComboBox<>();
        providerComboBox.setBounds(130, 20, 320, 25);
        formPanel.add(providerComboBox);

        JLabel fileLabel = new JLabel("Archivo Excel:");
        fileLabel.setBounds(20, 60, 100, 25);
        formPanel.add(fileLabel);

        filePathField = new JTextField();
        filePathField.setEditable(false);
        filePathField.setBounds(130, 60, 240, 25);
        formPanel.add(filePathField);

        JButton browseButton = new JButton("Buscar...");
        browseButton.setBounds(380, 60, 100, 25);
        browseButton.addActionListener(this::onBrowseFile);
        formPanel.add(browseButton);

        importButton = new JButton("Importar");
        importButton.setBounds(130, 110, 120, 30);
        importButton.addActionListener(this::onImport);
        formPanel.add(importButton);

        JButton closeButton = new JButton("Cerrar");
        closeButton.setBounds(270, 110, 120, 30);
        closeButton.addActionListener(e -> dispose());
        formPanel.add(closeButton);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setBounds(20, 150, 430, 20);
        progressBar.setVisible(false);
        formPanel.add(progressBar);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(520, 160));

        content.add(formPanel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        setContentPane(content);
        pack();
    }

    @Override
    public void dispose() {
        super.dispose();
        isOpen = false;
    }

    private void loadProviders() {
        List<Provider> providers = providerController.findAll();
        DefaultComboBoxModel<Provider> model = new DefaultComboBoxModel<>();
        for (Provider provider : providers) {
            model.addElement(provider);
        }
        providerComboBox.setModel(model);
        providerComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = value == null ? "" : value.getName();
            return new javax.swing.DefaultListCellRenderer().getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        });
        if (providers.size() == 1) {
            providerComboBox.setSelectedIndex(0);
        }
    }

    private void onBrowseFile(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onImport(ActionEvent event) {
        Provider provider = (Provider) providerComboBox.getSelectedItem();
        if (provider == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor.", "Importación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (filePathField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un archivo de Excel.", "Importación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Path path = Paths.get(filePathField.getText());
        if (!Files.exists(path)) {
            JOptionPane.showMessageDialog(this, "El archivo seleccionado no existe.", "Importación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        importButton.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setString("0%");
        progressBar.setVisible(true);
        logArea.setText("Procesando importación...\n");

        SwingWorker<ImportResult, String> worker = new SwingWorker<>() {
            @Override
            protected ImportResult doInBackground() {
                return importProducts(path, provider, (current, total) -> {
                    int progress = total <= 0 ? 0 : (int) Math.min(100, Math.round((current * 100.0) / total));
                    setProgress(progress);
                    publish(String.format("Procesando fila %d de %d...", current, total));
                });
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    logArea.append(chunk + "\n");
                }
            }

            @Override
            protected void done() {
                importButton.setEnabled(true);
                progressBar.setValue(100);
                progressBar.setString("100%");
                try {
                    ImportResult result = get();
                    logArea.append(formatResult(result) + "\n");
                } catch (Exception ex) {
                    logArea.append("Error durante la importación: " + ex.getMessage() + "\n");
                }
                ProductManagementView.refreshTable();
            }
        };

        worker.addPropertyChangeListener(propertyChangeEvent -> {
            if ("progress".equals(propertyChangeEvent.getPropertyName())) {
                int value = (int) propertyChangeEvent.getNewValue();
                progressBar.setValue(value);
                progressBar.setString(value + "%");
            }
        });
        worker.execute();
    }

    private ImportResult importProducts(Path filePath, Provider provider, ProgressCallback progressCallback) {
        ImportResult result = new ImportResult();
        BigDecimal priceMultiplier;
        try {
            priceMultiplier = calculatePriceMultiplier(provider);
        } catch (IllegalArgumentException ex) {
            result.errors.add(ex.getMessage());
            return result;
        }
        try (InputStream inputStream = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                result.errors.add("El archivo no contiene filas para importar.");
                return result;
            }

            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            Map<ColumnType, Integer> columnIndexes = detectColumns(headerRow);
            if (!columnIndexes.containsKey(ColumnType.CODE) || !columnIndexes.containsKey(ColumnType.PRICE)) {
                result.errors.add("No se encontraron las columnas obligatorias de código y precio.");
                return result;
            }

            int totalRows = sheet.getLastRowNum() - sheet.getFirstRowNum();
            int processed = 0;

            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                try {
                    processRow(row, columnIndexes, provider, priceMultiplier);
                    result.imported++;
                } catch (Exception ex) {
                    result.errors.add("Fila " + (i + 1) + ": " + ex.getMessage());
                }

                processed++;
                if (progressCallback != null) {
                    progressCallback.onProgress(processed, totalRows);
                }
            }
        } catch (IOException ex) {
            result.errors.add("No se pudo leer el archivo: " + ex.getMessage());
        } catch (InvalidFormatException ex) {
            result.errors.add("El archivo no tiene un formato de Excel válido: " + ex.getMessage());
        }
        return result;
    }

    private void processRow(Row row, Map<ColumnType, Integer> columns, Provider provider, BigDecimal priceMultiplier) {
        String code = getCellString(row, columns.get(ColumnType.CODE));
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        code = code.trim();
        String finalCode = buildProductCode(provider, code);

        BigDecimal price = getCellDecimal(row, columns.get(ColumnType.PRICE));
        if (price == null) {
            throw new IllegalArgumentException("El precio es obligatorio.");
        }
        price = applyPriceAdjustment(price, priceMultiplier);

        String description = getCellString(row, columns.get(ColumnType.DESCRIPTION));
        String brandName = getCellString(row, columns.get(ColumnType.BRAND));
        String categoryName = getCellString(row, columns.get(ColumnType.CATEGORY));
        String subcategoryName = getCellString(row, columns.get(ColumnType.SUBCATEGORY));

        Brand brand = resolveBrand(brandName);
        Category category = resolveCategory(categoryName);
        Subcategory subcategory = resolveSubcategory(subcategoryName, category);

        Product existing = productController.findByCode(finalCode);
        Product product = existing == null ? new Product() : existing;
        product.setCode(finalCode);
        product.setDescription(description == null || description.isBlank() ? finalCode : description.trim());
        product.setBrand(brand);
        product.setCategory(category);
        product.setSubcategory(subcategory);
        product.setProvider(provider);
        product.setPurchasePrice(price);
        product.setCashPrice(price);
        product.setFinancedPrice(price);
        product.setStockQuantity(100f);
        product.setInPromotion(false);
        product.setEnabled(true);

        if (existing == null) {
            productController.save(product);
        } else {
            productController.update(product);
        }
    }

    private BigDecimal applyPriceAdjustment(BigDecimal price, BigDecimal priceMultiplier) {
        if (priceMultiplier == null) {
            priceMultiplier = BigDecimal.ONE;
        }
        return price.multiply(priceMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private String buildProductCode(Provider provider, String originalCode) {
        if (originalCode == null) {
            return null;
        }

        String trimmedCode = originalCode.trim();
        if (provider == null) {
            return trimmedCode;
        }

        String prefix = provider.getCodePrefix();
        if (prefix == null || prefix.isBlank()) {
            return trimmedCode;
        }

        String normalizedPrefix = prefix.trim();
        while (normalizedPrefix.endsWith("-")) {
            normalizedPrefix = normalizedPrefix.substring(0, normalizedPrefix.length() - 1);
        }

        if (normalizedPrefix.isEmpty()) {
            return trimmedCode;
        }

        return normalizedPrefix + "-" + trimmedCode;
    }

    private BigDecimal calculatePriceMultiplier(Provider provider) {
        if (provider == null || provider.getPriceAdjustmentFormula() == null
                || provider.getPriceAdjustmentFormula().isBlank()) {
            return BigDecimal.ONE;
        }

        String formula = provider.getPriceAdjustmentFormula();
        String normalized = formula.replaceAll("\\s+", "");
        if (!normalized.matches("[+-]?\\d+(?:\\.\\d+)?([+-]\\d+(?:\\.\\d+)?)*")) {
            throw new IllegalArgumentException("Formato de ajuste de precio inválido: " + formula);
        }
        Matcher matcher = Pattern.compile("([+-]?\\d+(?:\\.\\d+)?)").matcher(normalized);

        BigDecimal multiplier = BigDecimal.ONE;
        boolean found = false;
        while (matcher.find()) {
            found = true;
            String token = matcher.group(1);
            char firstChar = token.charAt(0);
            boolean isNegative = firstChar == '-';
            String numeric = (firstChar == '+' || firstChar == '-') ? token.substring(1) : token;
            if (numeric.isEmpty()) {
                throw new IllegalArgumentException("Formato de ajuste de precio inválido: " + formula);
            }
            BigDecimal percentage = new BigDecimal(numeric).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            BigDecimal factor = isNegative ? BigDecimal.ONE.subtract(percentage) : BigDecimal.ONE.add(percentage);
            multiplier = multiplier.multiply(factor);
        }

        if (!found) {
            throw new IllegalArgumentException("Formato de ajuste de precio inválido: " + formula);
        }

        return multiplier;
    }

    private Brand resolveBrand(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        Brand brand = brandController.findByName(name);
        if (brand != null) {
            return brand;
        }
        Brand newBrand = new Brand();
        newBrand.setName(name.trim());
        brandController.create(newBrand);
        return newBrand;
    }

    private Category resolveCategory(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        Category category = categoryController.findByName(name);
        if (category != null) {
            return category;
        }
        Category newCategory = new Category();
        newCategory.setName(name.trim());
        categoryController.create(newCategory);
        return newCategory;
    }

    private Subcategory resolveSubcategory(String name, Category category) {
        if (name == null || name.isBlank() || category == null || category.getId() == 0) {
            return null;
        }
        Subcategory subcategory = subcategoryController.findByNameAndCategoryId(name, category.getId());
        if (subcategory != null) {
            return subcategory;
        }
        Subcategory newSubcategory = new Subcategory();
        newSubcategory.setName(name.trim());
        newSubcategory.setCategory(category);
        subcategoryController.create(newSubcategory);
        return newSubcategory;
    }

    private Map<ColumnType, Integer> detectColumns(Row headerRow) {
        Map<ColumnType, Integer> indexes = new EnumMap<>(ColumnType.class);
        if (headerRow == null) {
            return indexes;
        }
        Map<String, ColumnType> aliases = new HashMap<>();
        aliases.put("codigo", ColumnType.CODE);
        aliases.put("code", ColumnType.CODE);
        aliases.put("cod", ColumnType.CODE);
        aliases.put("codigoarticulo", ColumnType.CODE);
        aliases.put("articulo", ColumnType.CODE);

        aliases.put("descripcion", ColumnType.DESCRIPTION);
        aliases.put("descripcionarticulo", ColumnType.DESCRIPTION);
        aliases.put("detalle", ColumnType.DESCRIPTION);
        aliases.put("nombre", ColumnType.DESCRIPTION);

        aliases.put("marca", ColumnType.BRAND);
        aliases.put("brand", ColumnType.BRAND);

        aliases.put("rubro", ColumnType.CATEGORY);
        aliases.put("categoria", ColumnType.CATEGORY);
        aliases.put("category", ColumnType.CATEGORY);

        aliases.put("subrubro", ColumnType.SUBCATEGORY);
        aliases.put("subcategoria", ColumnType.SUBCATEGORY);
        aliases.put("subcategory", ColumnType.SUBCATEGORY);

        aliases.put("precio", ColumnType.PRICE);
        aliases.put("price", ColumnType.PRICE);
        aliases.put("importe", ColumnType.PRICE);
        aliases.put("valor", ColumnType.PRICE);

        for (Cell cell : headerRow) {
            if (cell == null) {
                continue;
            }
            String value = cell.getCellTypeEnum() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
            String normalized = normalize(value);
            ColumnType type = aliases.get(normalized);
            if (type != null) {
                indexes.put(type, cell.getColumnIndex());
            }
        }
        return indexes;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
        return normalized.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private String getCellString(Row row, Integer index) {
        if (index == null) {
            return null;
        }
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            if (numericValue == Math.rint(numericValue)) {
                return String.valueOf((long) numericValue);
            }
            return String.valueOf(numericValue);
        }
        return cell.getStringCellValue();
    }

    private BigDecimal getCellDecimal(Row row, Integer index) {
        if (index == null) {
            return null;
        }
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        if (cell.getCellTypeEnum() == CellType.STRING) {
            try {
                String value = cell.getStringCellValue().replace("$", "").replace(",", ".").trim();
                return new BigDecimal(value);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private String formatResult(ImportResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("Importación finalizada.\n");
        builder.append("Productos importados: ").append(result.imported).append("\n");
        if (!result.errors.isEmpty()) {
            builder.append("Errores: ").append(result.errors.size()).append("\n");
            for (String error : result.errors) {
                builder.append(" - ").append(error).append("\n");
            }
        }
        return builder.toString();
    }

    private enum ColumnType {
        CODE,
        DESCRIPTION,
        BRAND,
        CATEGORY,
        SUBCATEGORY,
        PRICE
    }

    private static class ImportResult {
        private int imported = 0;
        private final List<String> errors = new ArrayList<>();
    }

    @FunctionalInterface
    private interface ProgressCallback {
        void onProgress(int current, int total);
    }
}

