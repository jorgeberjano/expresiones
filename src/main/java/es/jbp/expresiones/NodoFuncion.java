package es.jbp.expresiones;

import java.util.ArrayList;
import java.util.List;


/**
 * Nodo del arbol de expresión que representa una función matemática.
 */
public class NodoFuncion implements NodoExpresion {
    private Funcion funcion;
    private List<NodoExpresion> listaNodosParametros = new ArrayList<>();
    
    public static int MULTIPLES_VALORES = -1;
    
    public NodoFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    /**
     * Devuelve el numero de parametros de entrada a la funcion
     */
    public int numeroParametrosEntrada() {
        return funcion.getNumeroParametros();
    }

    /**
     * Añade un nodo de parámetro de entrada a la función
     */
    public void agregarOperando(NodoExpresion nodoOperando) {
        listaNodosParametros.add(nodoOperando);
    }

    /**
     * Ejecuta la función asociada al nodo y devuelve el valor del calculo realizado
     */
    public Valor evaluar() {
        List<Valor> listaValores = new ArrayList<>();
        for(NodoExpresion nodoOperando : listaNodosParametros) {
            listaValores.add(nodoOperando.evaluar());
        }
        return funcion.evaluar(listaValores);
    };
}
