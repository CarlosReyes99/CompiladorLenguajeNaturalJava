/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlenguajenatural;

/**
 *
 * @author desan
 */
public class ConvertirNumeroTextoADigito {
    private static final String[] UNIDADES = {
        "cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"
    };
    
    private static final String[] DECENAS = {
        "", "diez", "veinte", "treinta", "cuarenta", "cincuenta", 
        "sesenta", "setenta", "ochenta", "noventa"
    };
    
    private static final String[] ESPECIALES = {
        "once", "doce", "trece", "catorce", "quince", "dieciséis",
        "diecisiete", "dieciocho", "diecinueve"
    };

    public String convertirNumeroTextoADigito(String numeroTexto) {
        numeroTexto = numeroTexto.toLowerCase().trim();
        
        // Buscar en unidades
        for (int i = 0; i < UNIDADES.length; i++) {
            if (UNIDADES[i].equals(numeroTexto)) {
                return String.valueOf(i);
            }
        }
        
        // Buscar en especiales (11-19)
        for (int i = 0; i < ESPECIALES.length; i++) {
            if (ESPECIALES[i].equals(numeroTexto)) {
                return String.valueOf(i + 11);
            }
        }
        
        // Buscar en decenas exactas
        for (int i = 1; i < DECENAS.length; i++) {
            if (DECENAS[i].equals(numeroTexto)) {
                return String.valueOf(i * 10);
            }
        }
        
        // Manejar números compuestos (21-99)
        if (numeroTexto.contains(" y ")) {
            String[] partes = numeroTexto.split(" y ");
            if (partes.length == 2) {
                // Encontrar la decena
                for (int i = 2; i < DECENAS.length; i++) {
                    if (DECENAS[i].equals(partes[0])) {
                        // Encontrar la unidad
                        for (int j = 1; j < UNIDADES.length; j++) {
                            if (UNIDADES[j].equals(partes[1])) {
                                return String.valueOf(i * 10 + j);
                            }
                        }
                    }
                }
            }
        }
        
        // Si no se encontró coincidencia, devolver el texto original
        return numeroTexto;
    }
}
