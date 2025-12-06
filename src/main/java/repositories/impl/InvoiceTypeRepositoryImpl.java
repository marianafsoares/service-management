package repositories.impl;

import java.util.Arrays;
import java.util.List;
import models.InvoiceType;
import repositories.InvoiceTypeRepository;
import utils.Constants;

public class InvoiceTypeRepositoryImpl implements InvoiceTypeRepository {

    @Override
    public List<InvoiceType> findAll() {
        return Arrays.asList(
                create(Constants.COD_PRESUPUESTO, Constants.PRESUPUESTO, Constants.PRESUPUESTO_ABBR),
                create(Constants.COD_NOTA_DEVOLUCION, Constants.NOTA_DEVOLUCION, Constants.NOTA_DEVOLUCION_ABBR),
                create(Constants.COD_FACTURA_A, Constants.FACTURA_A, Constants.FACTURA_A_ABBR),
                create(Constants.COD_FACTURA_B, Constants.FACTURA_B, Constants.FACTURA_B_ABBR),
                create(Constants.COD_FACTURA_C, Constants.FACTURA_C, Constants.FACTURA_C_ABBR),
                create(Constants.COD_NOTA_CREDITO_A, Constants.NOTA_CREDITO_A, Constants.NOTA_CREDITO_A_ABBR),
                create(Constants.COD_NOTA_CREDITO_B, Constants.NOTA_CREDITO_B, Constants.NOTA_CREDITO_B_ABBR),
                create(Constants.COD_NOTA_CREDITO_C, Constants.NOTA_CREDITO_C, Constants.NOTA_CREDITO_C_ABBR),
                create(Constants.COD_NOTA_DEBITO_A, Constants.NOTA_DEBITO_A, Constants.NOTA_DEBITO_A_ABBR),
                create(Constants.COD_NOTA_DEBITO_B, Constants.NOTA_DEBITO_B, Constants.NOTA_DEBITO_B_ABBR),
                create(Constants.COD_NOTA_DEBITO_C, Constants.NOTA_DEBITO_C, Constants.NOTA_DEBITO_C_ABBR)
        );
    }

    private InvoiceType create(int id, String description, String abbreviation) {
        InvoiceType type = new InvoiceType();
        type.setId(id);
        type.setDescription(description);
        type.setAbbreviation(abbreviation);
        return type;
    }
}
