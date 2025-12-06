
package utils.pyAfip;

import java.math.BigDecimal;

public class Iva {

    private int iva_id;
    private BigDecimal base_imp;
    private BigDecimal importe;

    public int getIva_id() {
        return iva_id;
    }

    public void setIva_id(int iva_id) {
        this.iva_id = iva_id;
    }

    public BigDecimal getBase_imp() {
        return base_imp;
    }

    public void setBase_imp(BigDecimal base_imp) {
        this.base_imp = base_imp;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
    
}
