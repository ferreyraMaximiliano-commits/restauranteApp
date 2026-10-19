package ar.restaurante.modelo;

import java.time.LocalDate;

public abstract class Usuario {
    private final int idUsuario;
    private String email;
    private String contrasena;
    private LocalDate fechaRegistro;
    private String estado;

    public Usuario(int idUsuario, String email, String contrasena, LocalDate fechaRegistro, String estado) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.contrasena = contrasena;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getEmail() { return email; }
}
