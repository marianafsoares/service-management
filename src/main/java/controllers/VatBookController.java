package controllers;

import java.util.Date;
import java.util.Optional;
import services.VatBookService;
import services.VatBookGenerationResult;

public class VatBookController {

    private final VatBookService vatBookService;

    public VatBookController(VatBookService vatBookService) {
        this.vatBookService = vatBookService;
    }

    public Optional<VatBookGenerationResult> processSales(Date start, Date end, String issuerCuit) throws Exception {
        return vatBookService.processSales(start, end, issuerCuit);
    }

    public Optional<VatBookGenerationResult> processPurchases(Date start, Date end, String issuerCuit) throws Exception {
        return vatBookService.processPurchases(start, end, issuerCuit);
    }
}

