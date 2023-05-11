package es.jbp.expresiones;

import java.util.List;

/**
 * @author Jorge
 */
public abstract class Operador implements Funcion {

    @Override
    public Valor evaluar(List<Valor> listaParametros) {
        Valor valor1;
        Valor valor2;

        if (listaParametros.size() > 0) {
            valor1 = listaParametros.get(0);
        } else {
            valor1 = new Valor(0.0);
        }
        if (listaParametros.size() > 1) {
            valor2 = listaParametros.get(1);
        } else {
            valor2 = new Valor(0.0);
        }
        return operar(valor1, valor2);
    }

    @Override
    public int getNumeroMinimoParametros() {
        return 2;
    }

    @Override
    public int getNumeroMaximoParametros() {
        return 2;
    }

    public abstract Valor operar(Valor resultado, Valor valor);

    public static class Suma extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toDouble() + valor2.toDouble());
        }
    }

    public static class Resta extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toDouble() - valor2.toDouble());
        }
    }

    public static class Multiplicacion extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toDouble() * valor2.toDouble());
        }
    }

    public static class Division extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toDouble() / valor2.toDouble());
        }
    }

    static class Igual extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.equals(valor2));
        }
    }

    static class Menor extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toDouble() < valor2.toDouble());
        }
    }

    static class MenorIgual extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toDouble() <= valor2.toDouble());
        }
    }

    static class Mayor extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toDouble() > valor2.toDouble());
        }
    }

    static class MayorIgual extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toDouble() >= valor2.toDouble());
        }
    }

    static class And extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toBoolean() && valor2.toBoolean());
        }
    }

    static class Or extends Operador {

        @Override
        public Valor operar(Valor valor1, Valor valor2) {
            return new Valor(valor1.toBoolean() || valor2.toBoolean());
        }
    }
}
