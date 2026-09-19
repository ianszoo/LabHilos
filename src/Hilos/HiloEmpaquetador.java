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
public class HiloEmpaquetador extends Thread{
    private final String id;
    private final Logistica empaquetado;
    private final Logistica expedicion;
    private final ControladorDeSimulacion control;
    private String paqueteActual="Inactivo";
    
    public HiloEmpaquetador(String id, Logistica empaquetado, Logistica expedicion, ControladorDeSimulacion control) {
        this.id = id;
        this.empaquetado = empaquetado;
        this.expedicion = expedicion;
        this.control = control;
    }
    
    public void run() {
        try {
            while (!control.estaDetenido()) {
                control.verificarPausa();
                paqueteActual="Esperando...";
                Paquete p=empaquetado.retirarPrioritario(control);
                
                if (p==null){
                    continue;
                }

                p.setEstado(EstadoDelPaquete.EMPAQUETANDO);
                paqueteActual=p.getCodigo()+" ("+p.getPeso()+"kg)";
                control.registrarLog(String.format("%s en empaque con %s", p.getCodigo(),id));
                long tiempo=(p.getPeso() <= 2.0) ? 1000 : (p.getPeso() <= 5.0 ? 2000 : 3000);
                Thread.sleep(tiempo);

                p.setEstado(EstadoDelPaquete.EMPAQUETADO);
                control.registrarLog(String.format("%s empaquetado por %s", p.getCodigo(),id));

                p.setEstado(EstadoDelPaquete.EN_EXPEDICION);
                expedicion.depositar(p, control);
                paqueteActual="Disponible";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public String getEstadoVisual(){
        return id+" ----> "+paqueteActual;
    }
}
