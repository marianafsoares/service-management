package services;

import java.nio.file.Path;

/**
 * Represents the output paths generated while building VAT books and CITI files.
 */
public class VatBookGenerationResult {

    private final Path vatBookDirectory;
    private final Path citiDirectory;

    public VatBookGenerationResult(Path vatBookDirectory, Path citiDirectory) {
        this.vatBookDirectory = vatBookDirectory;
        this.citiDirectory = citiDirectory;
    }

    public Path getVatBookDirectory() {
        return vatBookDirectory;
    }

    public Path getCitiDirectory() {
        return citiDirectory;
    }
}

