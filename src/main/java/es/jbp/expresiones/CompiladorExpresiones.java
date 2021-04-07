package es.jbp.expresiones;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compilador de expresiones. Genera una estructura de árbol que representa la
 * expresión en memoria y sirve para evaluar su valor.
 */
public class CompiladorExpresiones {

    private final List<Token> listaTokens = new ArrayList<>();

    private String mensajeError;
    private int posicionError;
    private String tokensError;

    private FactoriaIdentificadores factoriaIdentificadores;
    private final List<String> listaNombresVariables = new ArrayList<>();

    public void setFactoriaIdentificadores(FactoriaIdentificadores factoriaIdentificadores) {
        this.factoriaIdentificadores = factoriaIdentificadores;
    }

    /**
     * Realiza el analisis lexico y sintactico de la expresion que se le inyecta
     */
    public NodoExpresion compilar(String expresion) {
        listaNombresVariables.clear();

        if (!analisisLexico(expresion)) {
            return null;
        }

        return analisisSintactico();
    }

    private void error(String mensaje, String tokens, int posicion) {
        this.mensajeError = mensaje;
        this.tokensError = tokens;
        this.posicionError = posicion;
    }

    /*
     * Analisis lexico: separa la expresión en tokens.
     */
    public boolean analisisLexico(String expresion) {
        listaTokens.clear();
        if (expresion.isEmpty()) {
            error("Falta la expresión", "", 0);
            return false;
        }

        int indiceBase = 0, indiceActual = 0;
        Token ultimoToken = null;

        while (indiceActual < expresion.length()) {
            String parte = mid(expresion, indiceBase, indiceActual - indiceBase + 1);

            if ("\"".equals(parte)) {
                indiceActual = expresion.indexOf('"', indiceActual + 1);
                parte = mid(expresion, indiceBase, indiceActual - indiceBase + 1);
            }

            Token.Tipo tipo = determinarTipoDeToken(parte);

            if (tipo == null) {
                if (ultimoToken != null) {
                    // se agrega el ultimo token valido que no sea un espacio
                    if (ultimoToken.tipo != Token.Tipo.ESPACIO) {
                        agregarToken(ultimoToken);
                        ultimoToken = null;
                    }
                    indiceBase = indiceActual;
                    continue;
                } else {
                    error("Token no reconocido", parte, indiceBase);
                    return false;
                }
            } else { // Token valido
                ultimoToken = new Token(tipo);
                ultimoToken.posicion = indiceBase;                
                ultimoToken.texto = parte;
            }
            indiceActual++;
        }

        if (ultimoToken != null && ultimoToken.tipo != null &&
                ultimoToken.tipo != Token.Tipo.ESPACIO) {
            agregarToken(ultimoToken);
        }

        return true;
    }

    /**
     * Agrega un token a la lista de tokens y le asignana la prioridad que le
     * corresponda.
     */
    private void agregarToken(Token token) {
        Token ultimoToken = null;

        if (listaTokens.size() > 0) {
            ultimoToken = listaTokens.get(listaTokens.size() - 1);
        }

        if (token.texto.compareToIgnoreCase("OR") == 0) {
            token.prioridad = 0;
        } else if (token.texto.compareToIgnoreCase("AND") == 0) {
            token.prioridad = 1;
        } else if ("+".equals(token.texto) || "-".equals(token.texto)) {
            if (ultimoToken != null &&
                    (ultimoToken.tipo == Token.Tipo.IDENTIFICADOR
                    || ultimoToken.tipo == Token.Tipo.NUMERO
                    || ultimoToken.tipo == Token.Tipo.CADENA
                    || ultimoToken.tipo == Token.Tipo.CERRAR_PARENTESIS)) {
                token.prioridad = 2;  // operador binario (suma y resta)
            } else {
                token.prioridad = 3; // operador unario (signo)
            }
        } else if ("*".equals(token.texto) || "/".equals(token.texto) || "%".equals(token.texto)) {
            token.prioridad = 4;
        } else {
            token.prioridad = 5;
        }

        listaTokens.add(token);
    }

    /**
     * Obtiene el tipo de token.
     */
    Token.Tipo determinarTipoDeToken(String token) {

        for (Token.Tipo tipoToken : Token.Tipo.values()) {
            // TODO: optimizar
            Pattern p = Pattern.compile(tipoToken.regex);
            Matcher m = p.matcher(token);
            if (m.matches()) {
                return tipoToken;
            }
        }
        return null;
    }

    /**
     * Realiza el analisis sintactico
     */
    NodoExpresion analisisSintactico() {
        mensajeError = "";
        return parsear(0, listaTokens.size());
    }

    /**
     * Parsea el listado de tokens
     */
    private NodoExpresion parsear(int indicePrimero, int indiceUltimo) {
        if (listaTokens.isEmpty()
                || indicePrimero >= listaTokens.size()
                || indiceUltimo > listaTokens.size()
                || indicePrimero >= indiceUltimo) {
            return null;
        }

        Token tokenOperador = null;
        int indiceOperador = -1;
        int nivelParentesis = 0;

        // Se busca el operarador de menor prioridad de derecha a izquierda
        for (int i = indiceUltimo - 1; i >= indicePrimero; i--) {
            Token token = listaTokens.get(i);

            if (token.tipo == Token.Tipo.ABRIR_PARENTESIS) {
                nivelParentesis--;
            } else if (token.tipo == Token.Tipo.CERRAR_PARENTESIS) {
                nivelParentesis++;
            } else if (token.tipo == Token.Tipo.OPERADOR && nivelParentesis == 0) {
                // Si hay algun operador a la izquierda, se omite este operador
                if (i - 1 >= indicePrimero && listaTokens.get(i - 1).tipo == Token.Tipo.OPERADOR) {
                    continue;
                }

                if (tokenOperador == null || tokenOperador.prioridad > token.prioridad) {
                    tokenOperador = token;
                    indiceOperador = i;
                }
            }
        }

        Token ultimoToken = listaTokens.get(indiceUltimo - 1);

        if (nivelParentesis > 0) {
            error("No se esperaba el paréntesis de cierre", "", ultimoToken.posicion);
            return null;
        } else if (nivelParentesis < 0) {
            error("Falta un paréntesis de cierre", "", ultimoToken.posicion);
            return null;
        }

        // Hay un operador: se parsean los operandos y se agregan
        if (tokenOperador != null) {
            Funcion funcion = crearOperador(tokenOperador.texto);
            if (funcion == null) {
                error("Operador inválido", tokenOperador.texto, ultimoToken.posicion);
                return null;
            }
            NodoFuncion nodoOperador = new NodoFuncion(funcion);
            NodoExpresion nodoOperando1 = parsear(indicePrimero, indiceOperador);
            if (nodoOperando1 != null) {
                nodoOperador.agregarOperando(nodoOperando1);
            } else if (tokenOperador.texto == "+" || tokenOperador.texto == "-") {
                nodoOperador.agregarOperando(new NodoConstante(new Valor(0.0)));
            } else {
                error("Falta el operando izquierdo", tokenOperador.texto, tokenOperador.posicion);
                return null;
            }

            NodoExpresion nodoOperando2 = parsear(indiceOperador + 1, indiceUltimo);
            if (nodoOperando2 != null) {
                nodoOperador.agregarOperando(nodoOperando2);
            } else {
                error("Falta el operando derecho", tokenOperador.texto, ultimoToken.posicion);
                return null;
            }
            return nodoOperador;
        }

        // No hay operadores...
        Token primerToken = listaTokens.get(indicePrimero);
        Token segundoToken = (indicePrimero + 1 < indiceUltimo) ?
                listaTokens.get(indicePrimero + 1) : null;

        // Es un literal (numero o cadena), una variable o un atributo
        if (segundoToken == null) {
            if (primerToken.tipo == Token.Tipo.NUMERO) {
                return crearNodoConstante(primerToken);
            } else if (primerToken.tipo == Token.Tipo.IDENTIFICADOR) {
                return crearNodoVariable(primerToken);
            } else if (primerToken.tipo == Token.Tipo.CADENA) {
                return crearNodoConstante(primerToken);
            }
        }

        // Es una función o un metodo
        if (segundoToken.tipo == Token.Tipo.ABRIR_PARENTESIS) {
            if (primerToken.tipo == Token.Tipo.IDENTIFICADOR) {
                if (ultimoToken.tipo != Token.Tipo.CERRAR_PARENTESIS) {
                    error("Falta un par�ntesis de cierre", "", ultimoToken.posicion);
                    return null;
                }

                NodoFuncion nodoFuncion = crearNodoFuncion(primerToken);
                return parsearParametrosFuncion(nodoFuncion, primerToken.texto, indicePrimero + 2, indiceUltimo - 1);
            }
        }

        // Es una expresión entre parentesis
        if (primerToken.tipo == Token.Tipo.ABRIR_PARENTESIS) {
            if (ultimoToken.tipo != Token.Tipo.CERRAR_PARENTESIS) {
                error("Falta un paréntesis de cierre", "", ultimoToken.posicion);
                return null;
            }
            return parsear(indicePrimero + 1, indiceUltimo - 1);
        }

        StringBuilder subexpresion = new StringBuilder();
        for (int i = indicePrimero; i < indiceUltimo; i++) {
            subexpresion.append(listaTokens.get(i).texto);
        }

        error("Error de sintaxis", subexpresion.toString(), ultimoToken.posicion);

        return null;
    }

    public NodoConstante crearNodoConstante(Token token) {
        if (token.texto.startsWith("\"")) {
            String valor = mid(token.texto, 1, token.texto.length() - 2);
            return new NodoConstante(new Valor(valor));
        } else if (token.texto.contains(".")) {
            double valor = Double.parseDouble(token.texto);
            return new NodoConstante(new Valor(valor));
        } else {
            long valor = Long.parseLong(token.texto);
            return new NodoConstante(new Valor(valor));
        }
    }

    private NodoVariable crearNodoVariable(Token token) {
        Variable variable = null;
        if (factoriaIdentificadores != null) {
            variable = factoriaIdentificadores.crearVariable(token.texto);
        }
        if (variable == null) {
            error("Variable no encontrada", token.texto, token.posicion);
            return null;
        }

        listaNombresVariables.add(token.texto);

        return new NodoVariable(variable);
    }

    private NodoFuncion crearNodoFuncion(Token token) {
        Funcion funcion = crearFuncion(token.texto);
        if (funcion == null) {
            error("Funci�n no reconocida", token.texto, token.posicion);
            return null;
        }
        return new NodoFuncion(funcion);
    }

    /**
     * Parsea todos los parametros de entrada a la funcion reconocida
     */
    private NodoFuncion parsearParametrosFuncion(NodoFuncion nodoFuncion, String nombreFuncion, int indicePrimero, int indiceUltimo) {
        if (nodoFuncion == null) {
            return null;
        }

        int nivelParentesis = 0;
        int contadorParametros = 0;
        Token token = null;

        for (int i = indicePrimero; i < indiceUltimo; i++) {
            token = listaTokens.get(i);
            if (token.tipo == Token.Tipo.ABRIR_PARENTESIS) {
                nivelParentesis++;
            } else if (token.tipo == Token.Tipo.CERRAR_PARENTESIS) {
                nivelParentesis--;
                if (nivelParentesis < 0) {
                    error("Demasiados paréntesis de cierre en la función", nombreFuncion, token.posicion);
                }
            }
            boolean esUltimoToken = i == indiceUltimo - 1;

            if (nivelParentesis == 0 && (token.tipo == Token.Tipo.COMA || esUltimoToken)) {
                contadorParametros++;

                if (nodoFuncion.numeroParametrosEntrada() != NodoFuncion.MULTIPLES_VALORES
                        && contadorParametros > nodoFuncion.numeroParametrosEntrada()) {
                    error("Número de parámetros excesivo", nombreFuncion, token.posicion);

                    return null;
                }
                NodoExpresion parametro = parsear(indicePrimero, esUltimoToken ? i + 1 : i);
                if (parametro != null) {
                    nodoFuncion.agregarOperando(parametro);
                }
                indicePrimero = i + 1;
            }
        }
        
        int posicion = token != null ? token.posicion : 0;

        if (nivelParentesis != 0) {
            error("Falta el paréntesis de cierre en la función", nombreFuncion, posicion);
            return null;
        }

        if (nodoFuncion.numeroParametrosEntrada() != NodoFuncion.MULTIPLES_VALORES
                && nodoFuncion.numeroParametrosEntrada() > contadorParametros) {
            error("Número de parámetros insuficiente", nombreFuncion, posicion);            
            return null;
        }

        return nodoFuncion;
    }

    /**
     * Crea un función asociada a un token operador. Si hay operadores de usuario
     * con el mismo nombre que el estándar se devuelve éste.
     */
    private Funcion crearOperador(String nombreFuncion) {
        Funcion operador = null;
        if (factoriaIdentificadores != null) {
            operador = factoriaIdentificadores.crearOperador(nombreFuncion);
            if (operador != null) {
                return operador;
            }
        }

        if ("+".equals(nombreFuncion)) {
            operador = new Operador.Suma();
        } else if ("-".equals(nombreFuncion)) {
            operador = new Operador.Resta();
        } else if ("*".equals(nombreFuncion)) {
            operador = new Operador.Multiplicacion();
        } else if ("/".equals(nombreFuncion)) {
            operador = new Operador.Division();
        } else if ("<".equals(nombreFuncion)) {
            operador = new Operador.Menor();
        } else if ("<=".equals(nombreFuncion)) {
            operador = new Operador.MenorIgual();
        } else if (">".equals(nombreFuncion)) {
            operador = new Operador.Mayor();
        } else if (">=".equals(nombreFuncion)) {
            operador = new Operador.MayorIgual();
        } else if ("and".equals(nombreFuncion)) {
            operador = new Operador.And();
        } else if ("or".equals(nombreFuncion)) {
            operador = new Operador.Or();
        }
        return operador;
    }

    /**
     * Crea la función asociada al token. El token puede ser un operador, una
     * función estándar o una función de usuario. Si hay funciones de usuario
     * con el mismo nombre que una estándar la se retorna la de usuario.
     */
    private Funcion crearFuncion(String nombreFuncion) {
//        if (nombreFuncion.contains(".")) {
//
//        }

        Funcion funcion = null;
        if (factoriaIdentificadores != null) {
            funcion = factoriaIdentificadores.crearFuncion(nombreFuncion);
        }
        return funcion;
    }

    public String getError() {
        return mensajeError + " (" + posicionError + "): " + tokensError; 
    }

    private String mid(String texto, int posicion, int n) {
        return texto.substring(posicion, posicion + n);
    }
}
