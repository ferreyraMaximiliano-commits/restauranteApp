package ar.restaurante.modelo;
import java.time.LocalDate;

public class PersonalOperativo extends Empleado {
    private Sector sector;

    public PersonalOperativo
        (int idUsuario, String email, String contrasena, LocalDate fechaRegistro, String estado,
        String nombre, String apellido, String telefono, String turno, double salario, Sector sector) {
            
        super(idUsuario, email, contrasena, fechaRegistro, estado, nombre, apellido, telefono, turno, salario);
        this.sector = sector;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }
    
        
    
}
