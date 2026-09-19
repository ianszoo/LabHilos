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
public enum Prioridad {
    URDENTE(4,"URGENTE"),ALTA(3,"ALTA"),NOMRAL(2,"NOMRAL"),BAJA(1,"BAJA");
    private final int nivel;
    private final String etiqueta;
    
    Prioridad(int nivel, String etiqueta){
        this.nivel=nivel;
        this.etiqueta=etiqueta;
    }
    
    public int getNivel(){
        return nivel;
    }
    
    public String getEtiquieta(){
        return etiqueta;
    }
    
}
