package es.jbp.expresiones;

/**
 *
 * @author Jorge
 */
public class NodoConstante implements NodoExpresion {
    
    public NodoConstante(Valor valor) {
        this.valor = valor;
    }
    /*!
     * Devuelve el valor de la constante
     */
    public Valor evaluar() {
        return valor;
    };
    
    private Valor valor;
}
