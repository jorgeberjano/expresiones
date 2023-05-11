package es.jbp.expresiones;

import java.util.Objects;

/**
 * @author Jorge Berjano
 */
public class Valor {

    public enum Tipo {
        VALOR_NULO,
        VALOR_BOOLEANO,
        VALOR_ENTERO,
        VALOR_REAL,
        VALOR_CADENA
    }

    private final Tipo tipo;
    private Object variant;

    public Valor(Object valor) {
        variant = valor;
        if (valor == null) {
            tipo = Tipo.VALOR_NULO;
        } else if (valor instanceof Boolean) {
            tipo = Tipo.VALOR_BOOLEANO;
        } else if (valor instanceof Integer || valor instanceof Long) {
            tipo = Tipo.VALOR_ENTERO;
        } else if (valor instanceof Float || valor instanceof Double) {
            tipo = Tipo.VALOR_REAL;
        } else {
            tipo = Tipo.VALOR_CADENA;
            variant = Objects.toString(valor);
        }
    }

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

    public Tipo getTipo() {
        return tipo;
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

    public boolean parseBoolean() {
        if (tipo == Tipo.VALOR_CADENA) {
            String cadena = toString();
            if (Objects.equals(cadena, "true")) {
                return true;
            }
            Long entero = toLong();
            if (entero != null && entero != 0) {
                return true;
            }
        }
        return toBoolean();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Valor valor = (Valor) o;
        return tipo == valor.tipo && Objects.equals(variant, valor.variant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, variant);
    }
}
