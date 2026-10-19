package ar.restaurante.modelo;

import java.time.LocalDate;

public abstract class Empleado extends Usuario {
    private String nombre;
    private String apellido;
    private String telefono;
    private String turno;
    private double salario;

    public Empleado(int idUsuario, String email, String contrasena, LocalDate fechaRegistro, String estado,
                    String nombre, String apellido, String telefono, String turno, double salario) {
        super(idUsuario, email, contrasena, fechaRegistro, estado);
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.turno = turno;
        this.salario = salario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    
}

