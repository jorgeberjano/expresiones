package es.jbp.expresiones;

/**
 *
 * @author Jorge
 */
public interface FactoriaIdentificadores {
    Variable crearVariable(String nombre);
    Funcion crearFuncion(String nombre);
    Funcion crearOperador(String nombre);
}
