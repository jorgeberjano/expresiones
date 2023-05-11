package es.jbp.expresiones;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * @author Jorge
 */
public class ExpresionesTest {

    private static CompiladorExpresiones compilador;

    public ExpresionesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        compilador = new CompiladorExpresiones();
        compilador.setFactoriaIdentificadores(new FactoriaIdentificadores() {
            @Override
            public Variable crearVariable(String nombre) {
                return new Variable() {
                    @Override
                    public Valor getValor() {
                        return new Valor(nombre.length());
                    }
                };
            }

            @Override
            public Funcion crearFuncion(String nombre) {
                return new Funcion() {
                    @Override
                    public Valor evaluar(List<Valor> listaParametros) {
                        return new Valor((double) listaParametros.size());
                    }

                    @Override
                    public int getNumeroMinimoParametros() {
                        return 0;
                    }
                    public int getNumeroMaximoParametros() {
                        return NodoFuncion.MULTIPLES_VALORES;
                    }
                };
            }

            @Override
            public Funcion crearOperador(String nombre) {
                return null;
            }
        });
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testOperadores() {
        evaluar("2 + 5", 7.0);
        evaluar("1-1  ", 0.0);
        evaluar(" 1 or  0.0 ", true);
        evaluar("(1 - 1) or 0.0 ", false);
    }

    @Test
    public void testVariables() {
        evaluar("pepe + juan", 8.0);
        evaluar("pepe  - juan  ", 0.0);
        evaluar(" func(1, 23) ", 2.0);
        evaluar(" func() or 0.0 ", false);
    }

    private void evaluar(Object expresion, Object resultadoEsperado) {

        NodoExpresion nodo = compilador.compilar(expresion.toString());

        if (nodo == null) {
            System.out.println(compilador.getError());
            assert (false);
        } else {
            Valor resultado = nodo.evaluar();
            Assert.assertEquals(resultadoEsperado, resultado.getObject());
        }
    }
}
