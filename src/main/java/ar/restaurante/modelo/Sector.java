package ar.restaurante.modelo;

public class Sector {
    private int idSector;
    private String nombreSector;

    public Sector() {
    }

    public Sector(int idSector, String nombreSector) {
        this.idSector = idSector;
        this.nombreSector = nombreSector;
    }

    public int getIdSector() {
        return idSector;
    }

    public void setIdSector(int idSector) {
        this.idSector = idSector;
    }

    public String getNombreSector() {
        return nombreSector;
    }

    public void setNombreSector(String nombreSector) {
        this.nombreSector = nombreSector;
    }
    
    

    @Override
    public String toString() {
        return "Sector{" + "idSector=" + idSector + 
                ", nombre=" + nombreSector + '}';
    }
    
    
}
