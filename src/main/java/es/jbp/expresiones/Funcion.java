package es.jbp.expresiones;

import java.util.List;

/**
 * Contrato que deben cumplir las funciones que se usan en el compilador de expresiones.
 */
public interface Funcion {
    Valor evaluar(List<Valor> listaParametros);
    int getNumeroMinimoParametros();
    int getNumeroMaximoParametros();
}
