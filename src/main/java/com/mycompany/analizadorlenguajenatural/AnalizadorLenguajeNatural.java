package com.mycompany.analizadorlenguajenatural;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AnalizadorLenguajeNatural {

    // Definición de patrones para tokens
    private static final String NUMERO = "\\d+|cero|uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|veinte";
    private static final String SUMA = "\\b(suma|anade|mas)\\b";
    private static final String RESTA = "\\b(resta|quita|menos)\\b";
    private static final String MULTIPLICA = "\\b(multiplica|por)\\b";
    private static final String DIVIDE = "\\b(divide)\\b";
    private static final String POTENCIA = "\\b(eleva|potencia)\\b";
    private static final String RAIZ = "\\b(raiz)\\b";
    private static final String CONECTOR_Y = "\\b(y|luego)\\b";
    private static final String CONECTOR_CON = "\\b(a|con|de|entre)\\b";
    private static final String IDENTIFICADOR = "[a-zA-Z_][a-zA-Z_0-9]*";
    private static final String COMA = ",";
    private static final String ASIGNACION = "=";

    // Clase Token
    public static class Token {

        public final String tipo;
        public final String valor;
        public final String valorOriginal;

        public Token(String tipo, String valor, String valorOriginal) {
            this.tipo = tipo;
            this.valor = valor;
            this.valorOriginal = valorOriginal;
        }

        @Override
        public String toString() {
            return String.format("Token{tipo='%s', valor='%s', valorOriginal='%s'}",
                    tipo, valor, valorOriginal);
        }
    }

    // Resultado del análisis léxico
    public static class ResultadoLexico {

        public final ArrayList<Token> tokens;
        public final ArrayList<String> identificadores;
        public final String expresionOriginal;
        public final List<String> noReconocidos;

        public ResultadoLexico(ArrayList<Token> tokens, ArrayList<String> identificadores,
                               String expresionOriginal, List<String> noReconocidos) {
            this.tokens = tokens;
            this.identificadores = identificadores;
            this.expresionOriginal = expresionOriginal;
            this.noReconocidos = noReconocidos;
        }
    }

    // Excepción personalizada para errores sintácticos
    public static class ErrorSintactico extends Exception {

        private final String tipoError;
        private final String sugerencia;

        public ErrorSintactico(String mensaje, String tipoError, String sugerencia) {
            super(mensaje);
            this.tipoError = tipoError;
            this.sugerencia = sugerencia;
        }

        public String getTipoError() {
            return tipoError;
        }

        public String getSugerencia() {
            return sugerencia;
        }
    }

    // Resultado del análisis sintáctico
    public static class ResultadoSintactico {

        public final boolean esValido;
        public final List<String> arbolesExpresion;
        public final List<String> expresionesPostfijas;
        public final String error;
        public final String tipoError;
        public final String sugerencia;

        public ResultadoSintactico(boolean esValido, List<String> arbolesExpresion,
                                   List<String> expresionesPostfijas, String error,
                                   String tipoError, String sugerencia) {
            this.esValido = esValido;
            this.arbolesExpresion = arbolesExpresion;
            this.expresionesPostfijas = expresionesPostfijas;
            this.error = error;
            this.tipoError = tipoError;
            this.sugerencia = sugerencia;
        }
    }

    // Resultado completo del análisis
    public static class ResultadoAnalisis {

        public final ResultadoLexico resultadoLexico;
        public final ResultadoSintactico resultadoSintactico;

        public ResultadoAnalisis(ResultadoLexico resultadoLexico,
                                 ResultadoSintactico resultadoSintactico) {
            this.resultadoLexico = resultadoLexico;
            this.resultadoSintactico = resultadoSintactico;
        }
    }

    // Análisis léxico
    public ResultadoLexico analizarLexico(String entrada) {
        ArrayList<Token> tokens = new ArrayList<>();
        ArrayList<String> identificadores = new ArrayList<>();
        List<String> noReconocidos = new ArrayList<>();

        // Modificar el orden de los patrones para reconocer identificadores antes de conectores
        String patronCompleto = String.format(
                "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                SUMA, RESTA, MULTIPLICA, POTENCIA, RAIZ,
                NUMERO, IDENTIFICADOR, CONECTOR_Y, CONECTOR_CON, DIVIDE, COMA, ASIGNACION
        );

        Pattern patron = Pattern.compile(patronCompleto, Pattern.CASE_INSENSITIVE);
        Matcher matcher = patron.matcher(entrada.toLowerCase());

        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                // Obtener el texto no reconocido entre los tokens
                String noReconocido = entrada.substring(lastEnd, matcher.start());
                // Verificar si no es solo espacios en blanco
                if (!noReconocido.trim().isEmpty()) {
                    noReconocidos.add(noReconocido);
                }
            }

            String textoCoincidente = matcher.group();
            String textoOriginal = entrada.substring(matcher.start(), matcher.end());
            Token token = null;

            if (textoCoincidente.matches(SUMA)) {
                token = new Token("OperadorSuma", "+", textoOriginal);
            } else if (textoCoincidente.matches(RESTA)) {
                token = new Token("OperadorResta", "-", textoOriginal);
            } else if (textoCoincidente.matches(MULTIPLICA)) {
                token = new Token("OperadorMultiplicacion", "*", textoOriginal);
            } else if (textoCoincidente.matches(DIVIDE)) {
                token = new Token("OperadorDivision", "/", textoOriginal);
            } else if (textoCoincidente.matches(POTENCIA)) {
                token = new Token("OperadorPotencia", "^", textoOriginal);
            } else if (textoCoincidente.matches(RAIZ)) {
                token = new Token("OperadorRaiz", "√", textoOriginal);
            } else if (textoCoincidente.matches(NUMERO)) {
                String valorNumerico = convertirNumeroTextoADigito(textoCoincidente);
                token = new Token("Numero", valorNumerico, textoOriginal);
            } else if (textoCoincidente.matches(CONECTOR_Y)) {
                token = new Token("ConectorY", "y", textoOriginal);
            } else if (textoCoincidente.matches(CONECTOR_CON)) {
                token = new Token("ConectorCon", "con", textoOriginal);
            } else if (textoCoincidente.matches(IDENTIFICADOR)) {
                token = new Token("Identificador", textoCoincidente, textoOriginal);
                identificadores.add(textoOriginal);
            } else if (textoCoincidente.matches(COMA)) {
                token = new Token("Coma", ",", textoOriginal);
            } else if (textoCoincidente.matches(ASIGNACION)) {
                token = new Token("OperadorAsignacion", "=", textoOriginal);
            }

            if (token != null) {
                tokens.add(token);
            }

            lastEnd = matcher.end();
        }

        // Verificar si hay texto no reconocido al final de la entrada
        if (lastEnd < entrada.length()) {
            String noReconocido = entrada.substring(lastEnd);
            if (!noReconocido.trim().isEmpty()) {
                noReconocidos.add(noReconocido);
            }
        }

        return new ResultadoLexico(tokens, identificadores, entrada, noReconocidos);
    }

    private String convertirNumeroTextoADigito(String numeroTexto) {
        switch (numeroTexto.toLowerCase()) {
            case "cero":
                return "0";
            case "uno":
                return "1";
            case "dos":
                return "2";
            case "tres":
                return "3";
            case "cuatro":
                return "4";
            case "cinco":
                return "5";
            case "seis":
                return "6";
            case "siete":
                return "7";
            case "ocho":
                return "8";
            case "nueve":
                return "9";
            case "diez":
                return "10";
            case "veinte":
                return "20";
            default:
                return numeroTexto;
        }
    }

    // Analizador sintáctico
    private class AnalizadorSintactico {

        private final ArrayList<Token> tokens;
        private int posicionActual;
        private Token tokenActual;

        public AnalizadorSintactico(ArrayList<Token> tokens) {
            this.tokens = tokens;
            this.posicionActual = 0;
            this.tokenActual = tokens.isEmpty() ? null : tokens.get(0);
        }

        public ResultadoSintactico analizar() {
            try {
                List<String> arbolesExpresion = new ArrayList<>();

                while (tokenActual != null) {
                    String arbolExpresion = expresion();
                    arbolesExpresion.add(arbolExpresion);

                    if (coincide("Coma")) {
                        avanzar(); // Consumir la coma
                    } else if (tokenActual != null) {
                        throw new ErrorSintactico(
                                "Se esperaba una coma o fin de entrada en la posición " + posicionActual,
                                "Error de sintaxis",
                                "Asegúrate de separar las instrucciones con comas"
                        );
                    }
                }

                return new ResultadoSintactico(true, arbolesExpresion, null, null, null, null);
            } catch (ErrorSintactico e) {
                return new ResultadoSintactico(false, null, null, e.getMessage(), e.getTipoError(), e.getSugerencia());
            }
        }

        private void avanzar() {
            posicionActual++;
            tokenActual = posicionActual < tokens.size() ? tokens.get(posicionActual) : null;
        }

        private boolean coincide(String tipo) {
            return tokenActual != null && tokenActual.tipo.equals(tipo);
        }

        private boolean coincideConectorY() {
            return tokenActual != null && tokenActual.tipo.equals("ConectorY");
        }

        private boolean coincideConectorCon() {
            return tokenActual != null && tokenActual.tipo.equals("ConectorCon");
        }

        private boolean coincideOperador() {
            return tokenActual != null && esOperador(tokenActual);
        }

        private String expresion() throws ErrorSintactico {
            String resultado = asignacion();
            return resultado;
        }

        private String asignacion() throws ErrorSintactico {
            if (coincide("Identificador") && siguienteEs("OperadorAsignacion")) {
                String identificador = tokenActual.valorOriginal;
                avanzar(); // Avanzar desde el identificador
                avanzar(); // Avanzar desde el operador de asignación
                String valor = operacion(); // Analiza la expresión del lado derecho
                return identificador + " = " + valor;
            } else {
                return operacion();
            }
        }

        private String operacion() throws ErrorSintactico {
            String izquierda = termino();

            while (true) {
                // Consumir conectores opcionales
                while (coincideConectorY() || coincideConectorCon()) {
                    avanzar();
                }

                if (coincideOperador()) {
                    String operador = tokenActual.valor;
                    avanzar(); // Avanzar desde el operador

                    // Consumir conectores opcionales
                    while (coincideConectorY() || coincideConectorCon()) {
                        avanzar();
                    }

                    String derecha = operacion(); // Llamada recursiva para permitir anidación

                    izquierda = "(" + operador + " " + izquierda + " " + derecha + ")";
                } else {
                    break;
                }
            }

            return izquierda;
        }

        private String termino() throws ErrorSintactico {
            if (coincide("Numero") || coincide("Identificador")) {
                String valor = tokenActual.valorOriginal;
                avanzar();
                return valor;
            } else if (coincideOperador()) {
                String operador = tokenActual.valor;
                avanzar(); // Avanzar desde el operador

                // Consumir conectores opcionales
                while (coincideConectorY() || coincideConectorCon()) {
                    avanzar();
                }

                if (operadorRequiereDosOperandos(operador)) {
                    String operandoIzquierdo = operacion();

                    // Consumir conectores opcionales
                    while (coincideConectorY() || coincideConectorCon()) {
                        avanzar();
                    }

                    String operandoDerecho = operacion();

                    return "(" + operador + " " + operandoIzquierdo + " " + operandoDerecho + ")";
                } else {
                    String operando = operacion();
                    return "(" + operador + " " + operando + ")";
                }
            } else {
                throw new ErrorSintactico(
                        "Se esperaba un número, identificador u operador en la posición " + posicionActual,
                        "Error de sintaxis",
                        "Verifica que la expresión esté correctamente formada"
                );
            }
        }

        private boolean operadorRequiereDosOperandos(String operador) {
            return operador.equals("+") || operador.equals("-") || operador.equals("*")
                    || operador.equals("/") || operador.equals("^");
        }

        private boolean esOperador(Token token) {
            return token != null && (token.tipo.equals("OperadorSuma")
                    || token.tipo.equals("OperadorResta")
                    || token.tipo.equals("OperadorMultiplicacion")
                    || token.tipo.equals("OperadorDivision")
                    || token.tipo.equals("OperadorPotencia")
                    || token.tipo.equals("OperadorRaiz"));
        }

        private boolean siguienteEs(String tipo) {
            return posicionActual + 1 < tokens.size() && tokens.get(posicionActual + 1).tipo.equals(tipo);
        }
    }

    // Método principal para analizar una expresión
    public ResultadoAnalisis analizar(String expresion) {
        // Realizar el análisis léxico
        ResultadoLexico resultadoLexico = analizarLexico(expresion);

        // Mensaje de depuración: Imprimir elementos no reconocidos
        if (!resultadoLexico.noReconocidos.isEmpty()) {
            System.out.println("Elementos no reconocidos: " + resultadoLexico.noReconocidos);
        }

        // Continuar con el análisis sintáctico
        AnalizadorSintactico analizadorSintactico = new AnalizadorSintactico(resultadoLexico.tokens);
        ResultadoSintactico resultadoSintactico = analizadorSintactico.analizar();

        return new ResultadoAnalisis(resultadoLexico, resultadoSintactico);
    }

    // Método para mostrar resultados en un JDialog
    private void mostrarResultadoEnDialog(ResultadoAnalisis resultado) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Resultado del Análisis");
        dialog.setSize(600, 400);
        dialog.setModal(true);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        StringBuilder contenido = new StringBuilder();
        contenido.append("ANÁLISIS DE LA EXPRESIÓN: ").append(resultado.resultadoLexico.expresionOriginal).append("\n\n");

        // Análisis Léxico
        contenido.append("ANÁLISIS LÉXICO:\n");
        contenido.append("Tokens reconocidos:\n");
        for (Token token : resultado.resultadoLexico.tokens) {
            contenido.append("  ").append(token.tipo).append(": '").append(token.valorOriginal)
                    .append("' (valor interno: '").append(token.valor).append("')\n");
        }

        // Análisis Sintáctico
        contenido.append("\nANÁLISIS SINTÁCTICO:\n");
        if (resultado.resultadoSintactico.esValido) {
            contenido.append("La expresión es sintácticamente válida\n");
            for (int i = 0; i < resultado.resultadoSintactico.arbolesExpresion.size(); i++) {
                contenido.append("\nInstrucción ").append(i + 1).append(":\n");
                contenido.append("  Árbol de expresión: ")
                        .append(resultado.resultadoSintactico.arbolesExpresion.get(i)).append("\n");
            }
        } else {
            contenido.append("Se encontró un error sintáctico:\n");
            contenido.append("  Descripción: ").append(resultado.resultadoSintactico.error).append("\n");
            if (resultado.resultadoSintactico.tipoError != null) {
                contenido.append("  Tipo de error: ").append(resultado.resultadoSintactico.tipoError).append("\n");
            }
            if (resultado.resultadoSintactico.sugerencia != null) {
                contenido.append("  Sugerencia: ").append(resultado.resultadoSintactico.sugerencia).append("\n");
            }
        }

        textArea.setText(contenido.toString());
        dialog.add(scrollPane);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // Clase de resultado semántico
    public static class ResultadoSemantico {

        public final boolean esValido;
        public final List<String> errores;

        public ResultadoSemantico(boolean esValido, List<String> errores) {
            this.esValido = esValido;
            this.errores = errores;
        }
    }

    // Análisis semántico
    public ResultadoSemantico analizarSemantico(List<Token> tokens, Map<String, Integer> variablesDefinidas) {
        List<String> errores = new ArrayList<>();
        Map<String, Integer> operadoresYOperandos = new HashMap<>();
        operadoresYOperandos.put("OperadorSuma", 2);
        operadoresYOperandos.put("OperadorResta", 2);
        operadoresYOperandos.put("OperadorMultiplicacion", 2);
        operadoresYOperandos.put("OperadorDivision", 2);
        operadoresYOperandos.put("OperadorPotencia", 2);
        operadoresYOperandos.put("OperadorRaiz", 1);

        int i = 0;
        while (i < tokens.size()) {
            Token token = tokens.get(i);

            if (token.tipo.equals("Identificador")) {
                if (i + 1 < tokens.size() && tokens.get(i + 1).tipo.equals("OperadorAsignacion")) {
                    // Registrar la variable como definida
                    String variable = token.valor;
                    i += 2; // Avanzar desde el identificador y el operador de asignación

                    // Verificar si hay una expresión válida después del "="
                    if (i >= tokens.size()) {
                        errores.add("Error semántico: Falta una expresión después de la asignación a '" + variable + "'.");
                    } else {
                        ResultadoVerificacionExpresion resultadoExpresion = verificarExpresion(tokens, i, variablesDefinidas, operadoresYOperandos);
                        if (!resultadoExpresion.esValido) {
                            errores.addAll(resultadoExpresion.errores);
                        }
                        i = resultadoExpresion.posicionSiguiente; // Actualizar la posición
                    }

                    // Agregar la variable al mapa de variables definidas
                    variablesDefinidas.put(variable, 0); // Puedes ajustar el valor según tu lógica
                } else if (!variablesDefinidas.containsKey(token.valor)) {
                    errores.add("Error semántico: La variable '" + token.valor + "' no ha sido definida.");
                    i++;
                } else {
                    i++;
                }
            } else if (operadoresYOperandos.containsKey(token.tipo)) {
                ResultadoVerificacionExpresion resultadoExpresion = verificarExpresion(tokens, i, variablesDefinidas, operadoresYOperandos);
                if (!resultadoExpresion.esValido) {
                    errores.addAll(resultadoExpresion.errores);
                }
                i = resultadoExpresion.posicionSiguiente; // Actualizar la posición
            } else if (token.tipo.equals("Coma")) {
                i++; // Avanzar después de la coma
            } else {
                i++;
            }
        }

        boolean esValido = errores.isEmpty();
        return new ResultadoSemantico(esValido, errores);
    }

    private class ResultadoVerificacionExpresion {

        public boolean esValido;
        public List<String> errores;
        public int posicionSiguiente;

        public ResultadoVerificacionExpresion(boolean esValido, List<String> errores, int posicionSiguiente) {
            this.esValido = esValido;
            this.errores = errores;
            this.posicionSiguiente = posicionSiguiente;
        }
    }

    private ResultadoVerificacionExpresion verificarExpresion(List<Token> tokens, int posicionInicial,
                                                              Map<String, Integer> variablesDefinidas, Map<String, Integer> operadoresYOperandos) {

        List<String> errores = new ArrayList<>();
        int i = posicionInicial;
        int operandosNecesarios = 0;
        String operadorActual = null;
        boolean haEncontradoOperador = false;

        while (i < tokens.size()) {
            Token token = tokens.get(i);

            if (token.tipo.equals("Numero") || token.tipo.equals("Identificador")) {
                if (token.tipo.equals("Identificador") && !variablesDefinidas.containsKey(token.valor)) {
                    errores.add("Error semántico: La variable '" + token.valor + "' no ha sido definida.");
                }
                if (haEncontradoOperador) {
                    operandosNecesarios--;
                    if (operandosNecesarios < 0) {
                        errores.add("Error semántico: Operando inesperado '" + token.valorOriginal + "'.");
                        break;
                    }
                }
                // Si no se ha encontrado un operador, no se modifica operandosNecesarios
                i++;
            } else if (token.tipo.equals("ConectorY") || token.tipo.equals("ConectorCon")) {
                // Ignorar conectores
                i++;
            } else if (operadoresYOperandos.containsKey(token.tipo)) {
                operadorActual = token.valorOriginal;
                operandosNecesarios += operadoresYOperandos.get(token.tipo);
                haEncontradoOperador = true;
                i++;
            } else if (token.tipo.equals("Coma")) {
                // Fin de la expresión
                i++;
                break;
            } else {
                errores.add("Error semántico: Token inesperado '" + token.valorOriginal + "'.");
                break;
            }
        }

        if (operandosNecesarios > 0 && errores.isEmpty()) {
            errores.add("Error semántico: El operador '" + operadorActual + "' espera más operandos.");
        }

        boolean esValido = errores.isEmpty();
        return new ResultadoVerificacionExpresion(esValido, errores, i);
    }

    // Método para analizar y mostrar resultados en un JDialog
    public void analizarYMostrar(String expresion) {
        ResultadoAnalisis resultado = analizar(expresion);
        Map<String, Integer> variablesDefinidas = new HashMap<>();

        // Realizar el análisis semántico
        ResultadoSemantico resultadoSemantico = analizarSemantico(resultado.resultadoLexico.tokens, variablesDefinidas);

        // Mostrar resultados en el diálogo
        mostrarResultadoEnDialog(resultado);

        // Crear el mensaje para mostrar en el cuadro de diálogo
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("ANÁLISIS SEMÁNTICO:\n\n");
        if (resultadoSemantico.esValido) {
            mensaje.append("La expresión es semánticamente válida.\n");
        } else {
            mensaje.append("Se encontraron errores semánticos:\n");
            for (String error : resultadoSemantico.errores) {
                mensaje.append("- ").append(error).append("\n");
            }
        }

        // Mostrar el cuadro de diálogo con los resultados semánticos
        JOptionPane.showMessageDialog(null, mensaje.toString(), "Resultado del Análisis Semántico", JOptionPane.INFORMATION_MESSAGE);
    }
}