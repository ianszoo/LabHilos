/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hilos;
import java.util.Random;
/**
 *
 * @author Ian Suazo Palao
 */
public class HiloRepartidor {
    private final String id;
    private final String nombre;
    private final int capacidad;
    private final String rutaAsignada;
    private EstadoDelRepartidor estado;
    private int entregadosCount = 0;
    private final ListaEnlazadas<Paquete> paquetesCargados;
    private final Logistica expedicion;
    private final ListaEnlazadas<Paquete> listaEntregados;
    private final ListaEnlazadas<Paquete> listaDevueltos;
    private final ControladorDeSimulacion control;
    private final Random r = new Random();

    public HiloRepartidor(String id, String nombre, int capacidad, String ruta, ListaEnlazadas<Paquete> paquetesCargados, Logistica expedicion, ListaEnlazadas<Paquete> listaEntregados, ListaEnlazadas<Paquete> listaDevueltos, ControladorDeSimulacion control) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.rutaAsignada = ruta;
        this.estado=EstadoDelRepartidor.DISPONIBLE;
        this.expedicion = expedicion;
        this.listaEntregados = listaEntregados;
        this.listaDevueltos = listaDevueltos;
        this.control = control;
        this.paquetesCargados=new ListaEnlazadas<>();
    }
    
    public void run(){
        try{
            while (!control.estaDetenido()){
                control.verificarPausa();
                estado=EstadoDelRepartidor.DISPONIBLE;
                Paquete p=expedicion.retirarPorRuta(rutaAsignada,control);

                if (p!=null){
                    estado=EstadoDelRepartidor.CARGANDO;
                    p.setEstado(EstadoDelPaquete.EN_REPARTO);
                    paquetesCargados.agregarFinal(p);
                    control.registrarLog(String.format("%s cargado en %s (%s)", p.getCodigo(),nombre,rutaAsignada));

                    long t_inicio=System.currentTimeMillis();
                    while (paquetesCargados.getSize()<capacidad && (System.currentTimeMillis()-t_inicio<2000)){
                        Paquete extra=expedicion.retirarPorRuta(rutaAsignada, control);
                        
                        if (extra!=null){
                            extra.setEstado(EstadoDelPaquete.EN_REPARTO);
                            paquetesCargados.agregarFinal(extra);
                            control.registrarLog(String.format("%s cargado en %s (%s)",extra.getCodigo(),nombre,rutaAsignada));
                            
                        } 
                        else{
                            Thread.sleep(300);
                        }
                    }

                    estado=EstadoDelRepartidor.EN_RUTA;
                    control.registrarLog(String.format("%s inició ruta %s con %d/%d paquetes",nombre,rutaAsignada,paquetesCargados.getSize(),capacidad));
                    Thread.sleep(2000);

                    while (!paquetesCargados.estaVacia() && !control.estaDetenido()){
                        control.verificarPausa();
                        estado=EstadoDelRepartidor.ENTREGANDO;
                        Paquete paqueteEnEntrega=paquetesCargados.eliminarPrimero();

                        Thread.sleep(1500);
                        boolean clienteAusente=r.nextInt(100)<20;

                        if (clienteAusente){
                            paqueteEnEntrega.incrementarIntentos();
                            control.registrarLog(String.format("%s - Intento %d fallido (Cliente Ausente)", paqueteEnEntrega.getCodigo(),paqueteEnEntrega.getIntentos()));

                            if (paqueteEnEntrega.getIntentos() >= 3){
                                paqueteEnEntrega.setEstado(EstadoDelPaquete.DEVUELTO);
                                listaDevueltos.agregarFinal(paqueteEnEntrega);
                                control.incrementarDevueltos();
                                control.registrarLog(String.format("%s DEVUELTO tras 3 intentos", paqueteEnEntrega.getCodigo()));
                            } else {
                                paqueteEnEntrega.setEstado(EstadoDelPaquete.NUEVO_INTENTO);
                                expedicion.depositar(paqueteEnEntrega, control);
                                control.registrarLog(String.format("%s reingresado a Expedición para nuevo intento", paqueteEnEntrega.getCodigo()));
                            }
                        } else {
                            paqueteEnEntrega.setEstado(EstadoDelPaquete.ENTREGADO);
                            paqueteEnEntrega.setTiempoEntrega(System.currentTimeMillis());
                            listaEntregados.agregarFinal(paqueteEnEntrega);
                            entregadosCount++;
                            control.registrarEntregaExitosa(paqueteEnEntrega.getTiempoTotalSegundos());
                            control.registrarLog(String.format("%s ENTREGADO exitosamente por %s", paqueteEnEntrega.getCodigo(),nombre));
                        }
                    }

                    estado=EstadoDelRepartidor.REGRESANDO;
                    Thread.sleep(1500);
                } 
                else{
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    
    
    
    
}
