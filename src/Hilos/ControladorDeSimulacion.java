/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hilos;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author Ian Suazo Palao
 */
public class ControladorDeSimulacion {
    private volatile boolean pausado=false;
    private volatile boolean detenido=false;
    private int paquetesGenerados=0;
    private int paquetesEntregados=0;
    private int paquetesDevueltos=0;
    private double tiempoTotalEntregas=0.0;
    private JTextArea logArea;
    private final SimpleDateFormat date=new SimpleDateFormat("HH:mm:ss");

    public ControladorDeSimulacion(JTextArea logArea) {
        this.logArea = logArea;
    }
    
    public synchronized void verificarPausa() throws InterruptedException{
        while (pausado && !detenido){
            wait();
        }
    }
    
    public synchronized void pausar(){
        pausado=true;
        registrarLog("Sistema: SIMULACIÓN PAUSADA.");
    }

    public synchronized void reanudar(){
        pausado=false;
        notifyAll();
        registrarLog("Sistema: SIMULACIÓN REANUDADA.");
    }

    public synchronized void detener(){
        detenido=true;
        pausado=false;
        notifyAll();
        registrarLog("Sistema: SIMULACIÓN DETENIDA.");
    }

    public synchronized void reiniciar(){
        detenido=false;
        pausado=false;
        paquetesGenerados=0;
        paquetesEntregados=0;
        paquetesDevueltos=0;
        tiempoTotalEntregas=0.0;
        registrarLog("Sistema: ESTADÍSTICAS Y SISTEMA REINICIADOS.");
    }
    
    public synchronized void registrarLog(String mensaje){
        String linea = String.format("%s | %s\n", date.format(new Date()), mensaje);
        if (logArea!=null){
            SwingUtilities.invokeLater(() ->{
                logArea.append(linea);
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        } 
        else{
            System.out.print(linea);
        }
    }
    
    public synchronized void incrementarGenerados(){
        paquetesGenerados++; 
    }
    public synchronized void registrarEntregaExitosa(double tiempo_seg){
        paquetesEntregados++;
        tiempoTotalEntregas+=tiempo_seg;
    }
    public synchronized void incrementarDevueltos(){
        paquetesDevueltos++; 
    }

    public synchronized int getPaquetesGenerados(){
        return paquetesGenerados; 
    }
    public synchronized int getPaquetesEntregados(){
        return paquetesEntregados; 
    }
    public synchronized int getPaquetesDevueltos(){
        return paquetesDevueltos; 
    }
    public synchronized int getPaquetesEnProceso(){
        return paquetesGenerados-(paquetesEntregados+paquetesDevueltos);
    }
    public synchronized double getTiempoPromedio(){
        return paquetesEntregados==0 ? 0.0:(tiempoTotalEntregas/paquetesEntregados);
    }
}
