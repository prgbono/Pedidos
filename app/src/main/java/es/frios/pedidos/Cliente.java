package es.frios.pedidos;

import java.io.Serializable;

public class Cliente implements Serializable{
    private String nombre, apellidos, correo, envio, ciudad, tlf;
    private int id, id_E;                              //id_E = id_Empresa
    private boolean bar;                                    //booleano para identificar los bares


    //Constructor para el alta de clientes
    public Cliente(int id_E, String nombre, String apellidos, String tlf, String correo, String envio, boolean bar, String ciudad) {
        this.id_E = id_E;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.tlf = tlf;
        this.correo = correo;
        this.envio = envio;
        this.bar = bar;
        this.ciudad = ciudad;
    }

    public Cliente(String nombre, String apellidos, String correo, String envio, String ciudad, String tlf, int id, int id_E, boolean bar) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.envio = envio;
        this.ciudad = ciudad;
        this.id = id;
        this.tlf = tlf;
        this.id_E = id_E;
        this.bar = bar;
    }

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getApellidos() {return apellidos;}
    public void setApellidos(String apellidos) {this.apellidos = apellidos;}
    public String getCorreo() {return correo;}
    public void setCorreo(String correo) {this.correo = correo;}
    public String getEnvio() {return envio;}
    public void setEnvio(String envio) {this.envio = envio;}
    public String getCiudad() {return ciudad;}
    public void setCiudad(String ciudad) {this.ciudad = ciudad;}
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getTlf() {return tlf;}
    public void setTlf(String tlf) {this.tlf = tlf;}
    public int getId_E() {return id_E;}
    public void setId_E(int id_E) {this.id_E = id_E;}
    public boolean isBar() {return bar;}
    public void setBar(boolean bar) {this.bar = bar;}

    //Este método es para mostrar el nombre en el Spinner de clientes
    @Override
    public String toString() {
        return this.getNombre()+" "+this.getApellidos();
    }
}


