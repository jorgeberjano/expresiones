package es.jbp.expresiones;

/**
 * Representa un token generado por el analizador lexico.
 *
 * @author Jorge Berjano
 */
public class Token {

    public enum Tipo {
        ESPACIO("[\\s\\t\\r\\n]+"),
        OPERADOR("(<|<=|>|>=|=|==|<>|\\+|\\-|\\*|/|\\^|%|[Aa][Nn][Dd]|[Oo][Rr])"),
        NUMERO("[0-9]*\\.?[0-9]*([eE][-+]?[0-9]*)?"),
        COMA(","),
        //IDENTIFICADOR("[A-Za-z_][A-Za-z_0-9\\.]*[A-Za-z_0-9]*"),
        IDENTIFICADOR("[A-Za-z_][A-Za-z_0-9\\.\\[\\]]*[A-Za-z_0-9]*"),
        CADENA("\"[^\"]*\""),
        ABRIR_PARENTESIS("\\("),
        CERRAR_PARENTESIS("\\)");
        
        public String regex;

        private Tipo(String regex) {
            this.regex = regex;
        }
    };

    Tipo tipo;
    String texto;
    int posicion;
    int prioridad;

    public Token(Tipo tipo) {
        this.tipo = tipo;
        posicion = 0;
        prioridad = 0;
    }
}
