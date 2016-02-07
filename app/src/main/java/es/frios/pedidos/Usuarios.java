package es.frios.pedidos;

import java.io.Serializable;

public class Usuarios implements Serializable {
    private int id, id_E;
    private String nombre, apellidos, correo, pass;


    public Usuarios(int id, int id_E, String nombre, String apellidos, String correo, String pass) {
        this.id = id;
        this.id_E = id_E;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.pass = pass;
    }


    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public int getId_E() {return id_E;}

    public void setId_E(int id_E) {this.id_E = id_E;}

    public String getNombre() {return nombre;}

    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getApellidos() {return apellidos;}

    public void setApellidos(String apellidos) {this.apellidos = apellidos;}

    public String getCorreo() {return correo;}

    public void setCorreo(String correo) {this.correo = correo;}

    public String getPass() {return pass;}

    public void setPass(String pass) {this.pass = pass;}

}
