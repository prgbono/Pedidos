package es.frios.pedidos;

import java.io.Serializable;


public class Especialidad implements Serializable {
    int id, id_Cat;
    String especialidad;
    double pvp, existencias;

    public Especialidad(int id, int id_Cat, String especialidad, double pvp, double existencias) {
        this.id = id;
        this.id_Cat = id_Cat;
        this.especialidad = especialidad;
        this.pvp = pvp;
        this.existencias = existencias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_Cat() {
        return id_Cat;
    }

    public void setId_Cat(int id_Cat) {
        this.id_Cat = id_Cat;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public double getPvp() {
        return pvp;
    }

    public void setPvp(double pvp) {
        this.pvp = pvp;
    }

    public double getExistencias() {
        return existencias;
    }

    public void setExistencias(double existencias) {
        this.existencias = existencias;
    }

    //Este método es para mostrar el nombre en el Spinner de categorías
    @Override
    public String toString() {
        return this.getEspecialidad();
    }
}

