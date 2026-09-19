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
public class ListaEnlazadas<T>{
    private Nodo<T> head;
    private int size;
    
    public ListaEnlazadas(){
        this.head=null;
        this.size=0;
    }
    
    public void agregarFinal(T dato) {
        Nodo<T> nuevo=new Nodo<>(dato);
        if (head==null) {
            head=nuevo;
        } else{
            Nodo<T> aux=head;
            while (aux.siguiente!=null) {
                aux=aux.siguiente;
            }
            aux.siguiente=nuevo;
        }
        size++;
    }
    
    public void agregarInicio(T dato) {
        Nodo<T> nuevo=new Nodo<>(dato);
        nuevo.siguiente=head;
        head=nuevo;
        size++;
    }
    
    public T eliminarPrimero(){
        if(estaVacia()){
            return null;
        }
        T dato=head.dato;
        head=head.siguiente;
        size--;
        
        return dato;
    }
    
    public boolean eliminar(T dato){
        if(estaVacia()){
            return false;
        }
        
        if (head.dato.equals(dato)){
            head=head.siguiente;
            size--;
            return true;
        }
        
        Nodo<T> anterior=head;
        Nodo<T> actual=head.siguiente;
        
        while(actual!=null){
            if(actual.dato.equals(dato)){
                anterior.siguiente=actual.siguiente;
                size--;
                return true;
            }
            anterior=actual;
            actual=actual.siguiente;
        }
        return false;
    }
    
    public T obtener(int indice){
        if(indice<0 || indice>=size){
            return null;
        }
        
        Nodo<T> aux=head;
        for (int i = 0; i < indice; i++){
            aux=aux.siguiente;
        }
        return aux.dato;
    }
    
    public boolean estaVacia(){
        return head==null;
    }

    public int getSize() {
        return size;
    }
    
    public void limpiar(){
        head=null;
        size=0;
    }
    
    public int contar(){
        return contarAux(head);
    }
    
    private int contarAux(Nodo<T> actual){
        if(actual==null){
            return 0;
        }
        return 1+contarAux(actual.siguiente);
    }
    
    public boolean buscar(T dato){
        return buscarAux(head,dato);
    }
    
    private boolean buscarAux(Nodo<T> actual, T dato){
        if(actual==null){
            return false;
        }
        
        if (actual.dato.equals(dato)){
            return true;
        }
        return buscarAux(actual.siguiente,dato);
    }
    
    public synchronized Paquete extraerMayorPrioridad(){
        if(estaVacia() || !(head.dato instanceof Paquete)){
            return (Paquete) eliminarPrimero();
        }
        
        Nodo<T> mejorPrev=null;
        Nodo<T> mejorNodo=head;
        Nodo<T> prev=head;
        Nodo<T> actual=head.siguiente;
        
        while (actual!=null){
            Paquete pActual=(Paquete) actual.dato;
            Paquete pMejor=(Paquete) mejorNodo.dato;

            if (pActual.getPrioridad().getNivel()>pMejor.getPrioridad().getNivel()) {
                mejorNodo=actual;
                mejorPrev=prev;
            }
            prev=actual;
            actual=actual.siguiente;
        }
        
        if (mejorPrev==null){
            head=head.siguiente;
        } 
        else{
            mejorPrev.siguiente=mejorNodo.siguiente;
        }
        size--;
        return (Paquete) mejorNodo.dato;
    }
    
    public synchronized Paquete extraerPorRuta(String ruta){
        if (estaVacia() || !(head.dato instanceof Paquete)){
            return null;
        }

        if (((Paquete) head.dato).getRutaAsignada().equalsIgnoreCase(ruta)){
            return (Paquete) eliminarPrimero();
        }

        Nodo<T> prev=head;
        Nodo<T> actual=head.siguiente;

        while (actual!=null){
            Paquete p=(Paquete) actual.dato;
            if (p.getRutaAsignada().equalsIgnoreCase(ruta)){
                prev.siguiente=actual.siguiente;
                size--;
                return p;
            }
            prev=actual;
            actual=actual.siguiente;
        }
        return null;
    }
}
