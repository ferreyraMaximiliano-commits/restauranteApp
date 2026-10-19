package ar.restaurante.modelo;

import java.time.LocalDate;

public class Mozo extends Empleado {
    public Mozo(int idUsuario, String email, String contrasena, LocalDate fechaRegistro, String estado,
                String nombre, String apellido, String telefono, String turno, double salario) {
        super(idUsuario, email, contrasena, fechaRegistro, estado, nombre, apellido, telefono, turno, salario);
    }
}