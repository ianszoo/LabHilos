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
public class Logistica {
    private final String nombre;
    private final int capacidadMaxima;
    private final ListaEnlazadas<Paquete> lista;

    public Logistica(String nombre, int capacidadMaxima) {
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.lista=new ListaEnlazadas<>();
    }
    
    public synchronized void depositar(Paquete p, ControladorDeSimulacion control) throws InterruptedException{
        while (lista.getSize() >= capacidadMaxima && !control.estaDetenido()){
            wait();
        }
        if (control.estaDetenido()) return;

        lista.agregarFinal(p);
        notifyAll();
    }
    
    public synchronized Paquete retirarPrioritario(ControladorDeSimulacion control) throws InterruptedException{
        while(lista.estaVacia() && !control.estaDetenido()){
            wait();
        }
        if(control.estaDetenido()){
            return null;
        }
        
        Paquete p=lista.extraerMayorPrioridad();
        notifyAll();
        return p;
    }
    
    public synchronized Paquete retirarPorRuta(String ruta, ControladorDeSimulacion control){
        if (lista.estaVacia() || control.estaDetenido()){
            return null;
        }
        Paquete p=lista.extraerPorRuta(ruta);
        if (p!=null){
            notifyAll();
        }
        return p;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public synchronized int getOcupacion() {
        return lista.getSize();
    }
    
    public synchronized String getResumen(int maxelem){
        StringBuilder sb=new StringBuilder();
        int cant=Math.min(lista.getSize(),maxelem);
        
        for (int i = 0; i < cant; i++) {
            Paquete p=lista.obtener(i);
            if (p!=null){
                sb.append(p.getCodigo()).append(" (").append(p.getPrioridad().name().charAt(0)).append(") ");
                if ((i+1)%4==0) sb.append("\n");
            }
        }
        if (lista.getSize() > maxelem) {
            sb.append("\n...+").append(lista.getSize()-maxelem).append(" más");
        }
        return sb.toString();
    }
    
    public synchronized void vaciar(){
        lista.limpiar();
        notifyAll();
    }
}
