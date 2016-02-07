package es.frios.pedidos;

import java.io.Serializable;
import java.util.Date;

public class Pedido implements Serializable{
    private int id, id_E, id_Cli, id_detalle, id_especialidad, id_Cat;
    private String fecha_entrega, fecha_detalle, comentarios, cliente, categoria, especialidad;
    //private Date fecha_entrega, fecha_detalle;                          //Ver si lo voy a tratar de String o de Date
    private double pvp_acordado, importe, pvp, kg;
    private boolean envioBool, pagado, cocido;

    /*
    ---------------------------------------------------------------------------------------------------------------------------------------
    OJO CON ESTA CLASE. LA UTILIZAMOS PARA CORRESPONDERSE CON DOS TABLAS: PEDIDOS Y DETALLEPEDIDO. OJO CON ESTO!!!
    TB UTILIZAMOS UN ARRAY COMUN POR LO QUE SIEMPRE HAY QUE LIMPIARLO ANTES DE USARLO. EL ARRAY ES listaPedidos
    ---------------------------------------------------------------------------------------------------------------------------------------
    */

    //CONSTRUCTOR PARA LA TABLA PEDIDO
    public Pedido (int id, int id_E, int id_Cli, String cliente, String fecha_entrega, boolean pagado, boolean envioBool, String comentarios, double importe){
        this.id=id;
        this.id_E=id_E;
        this.id_Cli=id_Cli;
        this.cliente=cliente;
        this.fecha_entrega=fecha_entrega;
        this.pagado=pagado;
        this.envioBool=envioBool;
        this.comentarios=comentarios;
        this.importe=importe;
    }

    //CONSTRUCTOR PARA LA TABLA DETALLE_PEDIDO
    public Pedido(int id, int id_Cat, int id_especialidad, double kg, boolean cocido, String fecha_detalle, double pvp) {
        this.id = id;
        this.id_Cat = id_Cat;
        this.id_especialidad = id_especialidad;
        this.kg = kg;
        this.cocido = cocido;
        this.pvp = pvp;
        this.fecha_detalle = fecha_detalle;
    }

    //CONSTRUCTOR PARA LA LISTA DE PEDIDOS AÑADIDOS EN LA ACTIVITY NUEVOPEDIDO
    public Pedido(int id_detalle, int id, String categoria, String especialidad, double pvp, double kg) {
        this.id_detalle = id_detalle;
        this.id = id;
        this.categoria = categoria;
        this.especialidad = especialidad;
        this.pvp = pvp;
        this.kg = kg;
    }

    //CONSTRUCTOR GENERAL
    public Pedido(int id, int id_detalle, int id_Cat, String categoria, String especialidad, int id_especialidad, double importe, String fecha_entrega, String cliente, String fecha_detalle, String comentarios,
                  double kg, double pvp, double pvp_acordado, boolean envioBool, boolean pagado, boolean cocido) {
        this.id = id;
        this.id_detalle = id_detalle;
        this.id_Cat = id_Cat;
        this.cliente = cliente;
        this.categoria = categoria;
        this.especialidad = especialidad;
        this.id_especialidad = id_especialidad;
        this.importe = importe;
        this.fecha_entrega = fecha_entrega;
        this.fecha_detalle = fecha_detalle;
        this.comentarios = comentarios;
        this.kg = kg;
        this.pvp = pvp;
        this.pvp_acordado = pvp_acordado;
        this.envioBool = envioBool;
        this.pagado = pagado;
        this.cocido = cocido;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId_detalle() {
        return id_detalle;
    }
    public void setId_detalle(int id_detalle) {
        this.id_detalle = id_detalle;
    }
    public String getCliente() {
        return cliente;
    }
    public void setCliente(String cliente) {
     this.cliente = cliente;
    }

    public int getId_E() {
        return id_E;
    }

    public void setId_E(int id_E) {
        this.id_E = id_E;
    }

    public int getId_Cli() {
        return id_Cli;
    }

    public void setId_Cli(int id_Cli) {
        this.id_Cli = id_Cli;
    }

    public int getId_Cat() {
        return id_Cat;
    }

    public void setId_Cat(int id_Cat) {
        this.id_Cat = id_Cat;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCat() {

        return categoria;
    }
    public void setCat(String categoria) {
        this.categoria = categoria;
    }
    public int getId_especialidad() {
        return id_especialidad;
    }
    public void setId_especialidad(int id_especialidad) {
        this.id_especialidad = id_especialidad;
    }
    public double getImporte() {
        return importe;
    }
    public void setImporte(double importe) {
        this.importe = importe;
    }
    public String getFecha_entrega() {
        return fecha_entrega;
    }
    public void setFecha_entrega(String fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }
    public String getFecha_detalle() {
        return fecha_detalle;
    }
    public void setFecha_detalle(String fecha_detalle) {
        this.fecha_detalle = fecha_detalle;
    }
    public String getComentarios() {
        return comentarios;
    }
    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
    public double getKg() {
        return kg;
    }
    public void setKg(double kg) {
        this.kg = kg;
    }
    public double getPvp() {
        return pvp;
    }
    public void setPvp(double pvp) {
        this.pvp = pvp;
    }
    public double getPvp_acordado() {
        return pvp_acordado;
    }
    public void setPvp_acordado(double pvp_acordado) {
        this.pvp_acordado = pvp_acordado;
    }
    public boolean isEnvioBool() {
        return envioBool;
    }
    public void setEnvioBool(boolean envioBool) {
        this.envioBool = envioBool;
    }
    public boolean isPagado() {
        return pagado;
    }
    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
    public boolean isCocido() {
        return cocido;
    }
    public void setCocido(boolean cocido) {
        this.cocido = cocido;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}
