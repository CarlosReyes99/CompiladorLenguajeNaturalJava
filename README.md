# 📝 Analizador de Lenguaje Natural

> Proyecto para el análisis léxico, sintáctico y semántico de expresiones matemáticas en lenguaje natural utilizando Java. Este analizador reconoce patrones en español y genera representaciones de tokens, árboles de expresión, y valida la semántica de las expresiones.

## ✨ Características

- **Análisis Léxico:** Convierte una expresión en una serie de tokens reconocibles (números, operadores, identificadores, etc.).
- **Análisis Sintáctico:** Verifica la estructura de las expresiones mediante la construcción de árboles sintácticos.
- **Análisis Semántico:** Valida el uso correcto de operadores y operandos en el contexto de la expresión.
- **Interfaz Gráfica (JDialog):** Muestra los resultados del análisis en una ventana emergente con formato amigable.

## 🚀 Instalación

Asegúrate de tener instalado **Java 8** o superior en tu máquina.

1. Clona este repositorio:
   ```bash
   git clone https://github.com/usuario/analizador-lenguaje-natural.git
   cd analizador-lenguaje-natural
🧩 Uso
El analizador se utiliza principalmente mediante el método analizarYMostrar(String expresion), que procesa la expresión ingresada y muestra los resultados en una ventana emergente.

Ejemplo de Código
java
Copiar código
import com.mycompany.analizadorlenguajenatural.AnalizadorLenguajeNatural;

public class Main {
    public static void main(String[] args) {
        AnalizadorLenguajeNatural analizador = new AnalizadorLenguajeNatural();
        String expresion = "suma uno y dos";
        analizador.analizarYMostrar(expresion);
    }
}
