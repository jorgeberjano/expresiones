package es.jbp.expresiones;

/**
 *
 * @author Jorge
 */
public class NodoVariable implements NodoExpresion {
    
    private  Variable variable;
    
    public NodoVariable(Variable variable) {
        this.variable = variable;
    }
    
    /*!
     * Devuelve el valor de la variable
     */
    public Valor evaluar() {                 
        return variable.getValor();
    }

    /*!
     * Devuelve la variable contenida en el nodo.
     */
    public Variable getVariable() {
        return variable;
    }
}
