/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hilos;

/**
 *
 * @author Ian Suazo Palao
 */
public class Paquete {
    private String codigo,cliente,direccion,ciudad;
    private double peso;
    private Prioridad prioridad;
    private EstadoDelPaquete estado;
    private String ruta_asig;
    private int intentos;
    private long tiempo_creado,tiempo_entrega;
    
    public Paquete(String codigo, String cliente, String direccion, String ciudad, double peso, Prioridad prioridad) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.peso = peso;
        this.prioridad = prioridad;
        this.estado=EstadoDelPaquete.RECIBIDO;
        this.ruta_asig="SIN_ASIGNAR";
        this.intentos=0;
        this.tiempo_creado=System.currentTimeMillis();
        this.tiempo_entrega=0;
    }
    
    public synchronized void incrementarIntentos(){
        this.intentos++;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public double getPeso() {
        return peso;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoDelPaquete getEstado() {
        return estado;
    }

    public String getRutaAsignada() {
        return ruta_asig;
    }

    public int getIntentos() {
        return intentos;
    }

    public long getTiempoEntrega() {
        return tiempo_entrega;
    }

    public void setEstado(EstadoDelPaquete estado) {
        this.estado = estado;
    }

    public void setRutaAsignada(String ruta_asig) {
        this.ruta_asig = ruta_asig;
    }

    public void setTiempoEntrega(long tiempo_entrega) {
        this.tiempo_entrega = tiempo_entrega;
    }
    
    
    public double getTiempoTotalSegundos() {
        if (tiempo_entrega == 0){
            return (System.currentTimeMillis()-tiempo_creado)/1000.0;
        }
        return (tiempo_entrega-tiempo_creado)/1000.0;
    }
    
    public String toString(){
        return String.format("[%s | %s | %.1fkg | %s | %s]",codigo,prioridad.name(),peso,ruta_asig,estado);
    }

    
}

