package es.jbp.expresiones;

/**
 * @author Jorge Berjano
 */
public class Valor {

    public enum Tipo {
        VALOR_BOOLEANO,
        VALOR_ENTERO,
        VALOR_REAL,
        VALOR_CADENA
    }

    private Tipo tipo;
    private Object variant;

    public Valor(boolean valor) {
        tipo = Tipo.VALOR_BOOLEANO;
        variant = valor;
    }

    public Valor(long valor) {
        tipo = Tipo.VALOR_ENTERO;
        variant = valor;
    }

    public Valor(double valor) {

        tipo = Tipo.VALOR_REAL;
        variant = valor;
    }

    public Valor(String valor) {
        tipo = Tipo.VALOR_CADENA;
        variant = valor;
    }

    public String toString() {
        if (variant == null) {
            return "";
        }
        return variant.toString();
    }

    public Long toLong() {
        if (variant == null) {
            return 0L;
        }
        if (variant instanceof Boolean) {
            return (Boolean) variant ? 1L : 0L;
        }
        if (variant instanceof Double) {
            return ((Double) variant).longValue();
        }
        if (variant instanceof Long) {
            return (Long) variant;
        }
        return Long.parseLong(variant.toString());
    }

    public Double toDouble() {
        if (variant == null) {
            return 0.0;
        }
        if (variant instanceof Boolean) {
            return (Boolean) variant ? 1.0 : 0.0;
        }

        if (variant instanceof Double) {
            return (Double) variant;
        }
        return Double.parseDouble(variant.toString());
    }

    public Boolean toBoolean() {
        if (variant instanceof Boolean) {
            return (Boolean) variant;
        }
        if (variant instanceof Double || variant instanceof Float) {
            return (Double) variant != 0;
        }
        if (variant instanceof Long || variant instanceof Integer) {
            return (Long) variant != 0;
        }

        return variant != null;
    }

    public Object getObject() {
        switch (tipo) {
            case VALOR_BOOLEANO:
                return toBoolean();
            case VALOR_CADENA:
                return toString();
            case VALOR_ENTERO:
                return toLong();
            case VALOR_REAL:
                return toDouble();
            default:
                return null;
        }
    }
}
