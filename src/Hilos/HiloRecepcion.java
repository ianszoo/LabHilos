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
public class HiloRecepcion extends Thread{
    private final Logistica recepcion;
    private final Logistica almacen;
    private final ControladorDeSimulacion control;
    private final Random r=new Random();
    private int contador = 1;
    
    private final String[] clientes = {"Carlos López","Ana Gómez","Luis Torres","María Ruiz","Pedro Marín","Elena Saenz"};
    private final String[] ciudades = {"Barcelona Centro","Eixample","Gràcia","Sant Martí","Badalona"};
    private final String[] direcciones = {"Calle Mayor 12","Av. Diagonal 450","Gran Via 110","Paseo Gracia 30","Calle Marina 85"};

    public HiloRecepcion(Logistica recepcion, Logistica almacen, ControladorDeSimulacion control){
        this.recepcion = recepcion;
        this.almacen = almacen;
        this.control = control;
    }
    
    public void run() {
        try {
            while (!control.estaDetenido()){
                control.verificarPausa();

                String codigo=String.format("PKG-%03d", contador++);
                String cliente=clientes[r.nextInt(clientes.length)];
                String ciudad=ciudades[r.nextInt(ciudades.length)];
                String dir=direcciones[r.nextInt(direcciones.length)];
                
                double peso=Math.round((0.8+(9.0*r.nextDouble()))*10.0)/10.0;
                Prioridad pd=Prioridad.values()[r.nextInt(Prioridad.values().length)];

                Paquete p=new Paquete(codigo,cliente,dir,ciudad,peso,pd);
                control.incrementarGenerados();
                control.registrarLog(String.format("%s recibido en Recepción [%s | %.1fkg]",p.getCodigo(),pd.name(),peso));

                recepcion.depositar(p, control);
                Thread.sleep(800);

                control.verificarPausa();
                Paquete transferido=recepcion.retirarPrioritario(control);
                
                if (transferido!=null){
                    transferido.setEstado(EstadoDelPaquete.ALMACENADO);
                    almacen.depositar(transferido,control);
                    control.registrarLog(String.format("%s trasladado a Almacén",transferido.getCodigo()));
                }

                Thread.sleep(1200+r.nextInt(1000));
            }
            
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}


