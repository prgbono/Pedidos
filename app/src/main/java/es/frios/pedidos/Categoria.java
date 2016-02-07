package es.frios.pedidos;

import java.io.Serializable;


public class Categoria implements Serializable {
    private String nombre;
    private int id;
    private int id_E;


    public Categoria(int id, String nombre, int id_E) {
        this.id = id;
        this.nombre = nombre;
        this.id_E = id_E;

    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId_E() {
        return id_E;
    }
    public void setId_E(int id_E) {
        this.id_E = id_E;
    }

    //Este método es para mostrar el nombre en el Spinner de categorías
    @Override
    public String toString() {
        return this.getNombre();
    }
}
