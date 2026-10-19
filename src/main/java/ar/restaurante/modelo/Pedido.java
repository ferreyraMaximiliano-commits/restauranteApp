package ar.restaurante.modelo;

import ar.restaurante.modelo.RegistroPedido;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int numeroPedido;
    private LocalDate fecha;
    private String estado;
    private int idMesa;
    private Mozo idMozo;
    private List<RegistroPedido> detalles; // relacion 1 a n con las lineas del pedido


    public Pedido(int idMesa, Mozo idMozo) {
        this.fecha = LocalDate.now();
        this.estado = "En Preparación";
        this.idMesa = idMesa;
        this.idMozo = idMozo;
        this.detalles = new ArrayList<>();
    }

    public void agregarRenglon(RegistroPedido item) {
        this.detalles.add(item);
    }


    public int getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(int numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public Mozo getMozo() {
        return idMozo;
    }

    public void setMozo(Mozo idMozo) {
        this.idMozo = idMozo;
    }

    public List<RegistroPedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<RegistroPedido> detalles) {
        this.detalles = detalles;
    }
    
}
