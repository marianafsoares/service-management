
package utils.pyAfip;

import java.util.List;

public class Comprobante {

    private int tipo_cbte;
    private int punto_vta;
    private int cbt_desde;
    private int cbt_hasta;
    private String fecha_cbte;
    private int tipo_doc;
    private String nro_doc;
    private int concepto;
    private String fecha_venc_pago;
    private String imp_neto;
    private String imp_iva;
    private String imp_op_ex;
    private String imp_tot_conc;
    private String imp_trib;
    private String imp_total;
    private String moneda_id;
    private String moneda_ctz;
    private List<Iva> ivas;
    private List<Tributo> tributos;
    private List<CbteAsoc> cbtes_asoc;
    private List<String> opcionales;
    private List<String> compradores;
    
    private String resultado;
    private String cae;
    private String fecha_vto;
    private String emision_tipo;
    private String fch_venc_cae;
    private String motivos_obs;
    private String err_code;
    private String err_msg;
    private String reproceso;

    public int getTipo_cbte() {
        return tipo_cbte;
    }

    public void setTipo_cbte(int tipo_cbte) {
        this.tipo_cbte = tipo_cbte;
    }

    public int getPunto_vta() {
        return punto_vta;
    }

    public void setPunto_vta(int punto_vta) {
        this.punto_vta = punto_vta;
    }

    public int getCbt_desde() {
        return cbt_desde;
    }

    public void setCbt_desde(int cbt_desde) {
        this.cbt_desde = cbt_desde;
    }

    public int getCbt_hasta() {
        return cbt_hasta;
    }

    public void setCbt_hasta(int cbt_hasta) {
        this.cbt_hasta = cbt_hasta;
    }

    public String getFecha_cbte() {
        return fecha_cbte;
    }

    public void setFecha_cbte(String fecha_cbte) {
        this.fecha_cbte = fecha_cbte;
    }

    public int getTipo_doc() {
        return tipo_doc;
    }

    public void setTipo_doc(int tipo_doc) {
        this.tipo_doc = tipo_doc;
    }

    public String getNro_doc() {
        return nro_doc;
    }

    public void setNro_doc(String nro_doc) {
        this.nro_doc = nro_doc;
    }

    public int getConcepto() {
        return concepto;
    }

    public void setConcepto(int concepto) {
        this.concepto = concepto;
    }

    public String getFecha_venc_pago() {
        return fecha_venc_pago;
    }

    public void setFecha_venc_pago(String fecha_venc_pago) {
        this.fecha_venc_pago = fecha_venc_pago;
    }

    public String getImp_neto() {
        return imp_neto;
    }

    public void setImp_neto(String imp_neto) {
        this.imp_neto = imp_neto;
    }

    public String getImp_iva() {
        return imp_iva;
    }

    public void setImp_iva(String imp_iva) {
        this.imp_iva = imp_iva;
    }

    public String getImp_op_ex() {
        return imp_op_ex;
    }

    public void setImp_op_ex(String imp_op_ex) {
        this.imp_op_ex = imp_op_ex;
    }

    public String getImp_tot_conc() {
        return imp_tot_conc;
    }

    public void setImp_tot_conc(String imp_tot_conc) {
        this.imp_tot_conc = imp_tot_conc;
    }

    public String getImp_trib() {
        return imp_trib;
    }

    public void setImp_trib(String imp_trib) {
        this.imp_trib = imp_trib;
    }

    public String getImp_total() {
        return imp_total;
    }

    public void setImp_total(String imp_total) {
        this.imp_total = imp_total;
    }

    public String getMoneda_id() {
        return moneda_id;
    }

    public void setMoneda_id(String moneda_id) {
        this.moneda_id = moneda_id;
    }

    public String getMoneda_ctz() {
        return moneda_ctz;
    }

    public void setMoneda_ctz(String moneda_ctz) {
        this.moneda_ctz = moneda_ctz;
    }

    public List<Iva> getIvas() {
        return ivas;
    }

    public void setIvas(List<Iva> ivas) {
        this.ivas = ivas;
    }

    public List<Tributo> getTributos() {
        return tributos;
    }

    public void setTributos(List<Tributo> tributos) {
        this.tributos = tributos;
    }

    public List<CbteAsoc> getCbtes_asoc() {
        return cbtes_asoc;
    }

    public void setCbtes_asoc(List<CbteAsoc> cbtes_asoc) {
        this.cbtes_asoc = cbtes_asoc;
    }

    public List<String> getOpcionales() {
        return opcionales;
    }

    public void setOpcionales(List<String> opcionales) {
        this.opcionales = opcionales;
    }

    public List<String> getCompradores() {
        return compradores;
    }

    public void setCompradores(List<String> compradores) {
        this.compradores = compradores;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getCae() {
        return cae;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public String getFecha_vto() {
        return fecha_vto;
    }

    public void setFecha_vto(String fecha_vto) {
        this.fecha_vto = fecha_vto;
    }

    public String getEmision_tipo() {
        return emision_tipo;
    }

    public void setEmision_tipo(String emision_tipo) {
        this.emision_tipo = emision_tipo;
    }

    public String getFch_venc_cae() {
        return fch_venc_cae;
    }

    public void setFch_venc_cae(String fch_venc_cae) {
        this.fch_venc_cae = fch_venc_cae;
    }

    public String getMotivos_obs() {
        return motivos_obs;
    }

    public void setMotivos_obs(String motivos_obs) {
        this.motivos_obs = motivos_obs;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public String getReproceso() {
        return reproceso;
    }

    public void setReproceso(String reproceso) {
        this.reproceso = reproceso;
    }
    
}