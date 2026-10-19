package ar.restaurante.modelo;
import java.time.LocalDate;

public class Gerente extends Empleado {
    private int codigoAutorizacion;

    public Gerente(int idUsuario, String email, String contrasena, LocalDate fechaRegistro, String estado,
                   String nombre, String apellido, String telefono, String turno, double salario, int codigoAutorizacion) {
        super(idUsuario, email, contrasena, fechaRegistro, estado, nombre, apellido, telefono, turno, salario);
        this.codigoAutorizacion = codigoAutorizacion;
    }
    public int getCodigoAutorizacion() { return codigoAutorizacion; }
}
