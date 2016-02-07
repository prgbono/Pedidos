package es.frios.pedidos;

import java.io.Serializable;
import java.util.Date;

public class Empresa implements Serializable{
    private int id;
    private String nombre, codigo, gerente, fecha_creacion;
    //private Date fecha_creacion;                      //Estudiar si tratar la fecha como String o como Date
    private boolean notificaciones, lE;
    //private image logo;                               //Estudiar el tema de añadirle el logo de la empresa como imágen


    public Empresa(String nombre, String gerente, String codigo, boolean notificaciones, boolean lE) {
        this.nombre = nombre;
        this.gerente = gerente;
        this.codigo = codigo;
        this.notificaciones = notificaciones;
        this.lE = lE;
    }

    public Empresa(int id, String nombre, String codigo, String gerente, String fecha_creacion, boolean notificaciones, boolean lE) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.gerente = gerente;
        this.fecha_creacion = fecha_creacion;
        this.notificaciones = notificaciones;
        this.lE = lE;
    }


    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getCodigo() {return codigo;}
    public void setCodigo(String codigo) {this.codigo = codigo;}
    public String getGerente() {return gerente;}
    public void setGerente(String gerente) {this.gerente = gerente;}
    public String getFecha_creacion() {return fecha_creacion;}
    public void setFecha_creacion(String fecha_creacion) {this.fecha_creacion = fecha_creacion;}
    public boolean isNotificaciones() {return notificaciones;}
    public void setNotificaciones(boolean notificaciones) {this.notificaciones = notificaciones;}
    public boolean islE() {return lE;}
    public void setlE(boolean lE) {this.lE = lE;}

    //Este método es para mostrar el nombre en el Spinner de SelectorE
    @Override
    public String toString() {
        return this.getNombre();
    }
}
