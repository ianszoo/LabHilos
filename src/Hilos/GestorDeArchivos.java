/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hilos;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author Ian Suazo Palao
 */
public class GestorDeArchivos {
    public static void guardarReporte(String ruta, ControladorDeSimulacion control, ListaEnlazadas<Paquete> entregados, ListaEnlazadas<Paquete> retornados)throws IOException{
        try (BufferedWriter bw=new BufferedWriter(new FileWriter(ruta))){
            bw.write("====================================================\n");
            bw.write("      REPORTE DE CENTRO DE DISTRIBUCIÓN LOGÍSTICA   \n");
            bw.write("====================================================\n");
            
            
            bw.write("Fecha: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"\n\n");
            bw.write("MÉTRICAS GENERALES:\n");
            bw.write(" - Paquetes Generados:   "+control.getPaquetesGenerados()+"\n");
            bw.write(" - Paquetes Entregados:   "+control.getPaquetesEntregados()+"\n");
            bw.write(" - Paquetes Devueltos:    "+control.getPaquetesDevueltos()+"\n");
            bw.write(" - Paquetes en Proceso:   "+control.getPaquetesEnProceso()+"\n");
            bw.write(String.format(" - Tiempo Promedio:       %.2f segundos\n\n",control.getTiempoPromedio()));
            
            bw.write("PAQUETES ENTREGADOS CON ÉXITO:\n");
            
            for (int i = 0; i < entregados.getSize(); i++){
                Paquete p = entregados.obtener(i);
                bw.write(String.format("  [%s] Cliente: %s | Ruta: %s | Prioridad: %s | Tiempo: %.1fs\n",p.getCodigo(),p.getCliente(),p.getRutaAsignada(),p.getPrioridad().name(),p.getTiempoTotalSegundos()));
            }
            
            bw.write("\nPAQUETES DEVUELTOS TRAS 3 INTENTOS:\n");
            
            for (int i = 0; i < retornados.getSize(); i++){
                Paquete p = retornados.obtener(i);
                bw.write(String.format("  [%s] Cliente: %s | Intentos: %d | Ruta: %s\n",p.getCodigo(),p.getCliente(),p.getIntentos(),p.getRutaAsignada()));
            }
        }
    }
    
    public static void guardarBinario(String rutaArchivo, ControladorDeSimulacion control) throws IOException{
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(rutaArchivo))){
            dos.writeInt(control.getPaquetesGenerados());
            dos.writeInt(control.getPaquetesEntregados());
            dos.writeInt(control.getPaquetesDevueltos());
            dos.writeDouble(control.getTiempoPromedio());
        }
    }
    
    
}
