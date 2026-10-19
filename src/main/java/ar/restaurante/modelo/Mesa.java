package ar.restaurante.modelo;

public class Mesa {
    private final int idMesa;
    private String estado;
    private int capacidad;
    private String zona;

    public Mesa(int idMesa, String estado, int capacidad, String zona) {
        this.idMesa = idMesa; this.estado = estado; this.capacidad = capacidad; this.zona = zona;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }
    
    public int getIdMesa(){
        return idMesa;
    }
    
    @Override public String toString() {
        return "Mesa " + idMesa + " | " + zona + " | " + capacidad + " personas | " + estado;
    }
}
