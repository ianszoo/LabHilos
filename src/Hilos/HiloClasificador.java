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
public class HiloClasificador{
    
    private final String id;
    private final Logistica almacen;
    private final Logistica clasificacion;
    private final Logistica empaquetado;
    private final ControladorDeSimulacion control;
    private String paqueteActual="Inactivo";

    public HiloClasificador(String id, Logistica almacen, Logistica clasificacion, Logistica empaquetado, ControladorDeSimulacion control) {
        this.id = id;
        this.almacen = almacen;
        this.clasificacion = clasificacion;
        this.empaquetado = empaquetado;
        this.control = control;
    }
    
    public void run() {
        try {
            while (!control.estaDetenido()) {
                control.verificarPausa();

                paqueteActual="Esperando...";
                Paquete p=almacen.retirarPrioritario(control);
                if (p==null){
                    continue;
                }

                p.setEstado(EstadoDelPaquete.CLASIFICANDO);
                paqueteActual=p.getCodigo();
                control.registrarLog(String.format("%s tomado por %s", p.getCodigo(),id));

                String ruta=asignarRuta(p.getCiudad());
                p.setRutaAsignada(ruta);

                clasificacion.depositar(p, control);
                Thread.sleep(1200);

                p.setEstado(EstadoDelPaquete.CLASIFICADO);
                control.registrarLog(String.format("%s clasificado por %s → %s", p.getCodigo(),id,ruta));

                Paquete clasificado=clasificacion.retirarPrioritario(control);
                if (clasificado!=null) {
                    empaquetado.depositar(clasificado, control);
                }
                paqueteActual="Disponible";
            }
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    
    private String asignarRuta(String ciudad){
        switch(ciudad){
            case "Barcelona Centro":
            case "Eixample":
                return "Ruta 1";
            case "Gracia":
                return "Ruta 2";
            case "Sant Marti":
                return "Ruta 3";
            default:
                return "Ruta 4";
        }
    }
    
    public String getEstadoVisual(){
        return id+" ---> "+paqueteActual;
    }
}
