/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.analizadorlenguajenatural;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author desan
 */
public class ConvertirNumeroTextoADigito {

   

    public String convertirNumeroTextoADigito(String numeroTexto) {
        numeroTexto = numeroTexto.toLowerCase().trim();

        Map<String, Integer> numeros = new HashMap<>();
        // Números del 0 al 15
        numeros.put("cero", 0);
        numeros.put("uno", 1);
        numeros.put("una", 1);
        numeros.put("dos", 2);
        numeros.put("tres", 3);
        numeros.put("cuatro", 4);
        numeros.put("cinco", 5);
        numeros.put("seis", 6);
        numeros.put("siete", 7);
        numeros.put("ocho", 8);
        numeros.put("nueve", 9);
        numeros.put("diez", 10);
        numeros.put("once", 11);
        numeros.put("doce", 12);
        numeros.put("trece", 13);
        numeros.put("catorce", 14);
        numeros.put("quince", 15);
        // Números especiales del 16 al 29
        numeros.put("dieciséis", 16);
        numeros.put("dieciseis", 16);
        numeros.put("diecisiete", 17);
        numeros.put("dieciocho", 18);
        numeros.put("diecinueve", 19);
        numeros.put("veinte", 20);
        numeros.put("veintiuno", 21);
        numeros.put("veintidós", 22);
        numeros.put("veintidos", 22);
        numeros.put("veintitrés", 23);
        numeros.put("veintitres", 23);
        numeros.put("veinticuatro", 24);
        numeros.put("veinticinco", 25);
        numeros.put("veintiséis", 26);
        numeros.put("veintiseis", 26);
        numeros.put("veintisiete", 27);
        numeros.put("veintiocho", 28);
        numeros.put("veintinueve", 29);
        // Decenas exactas
        numeros.put("treinta", 30);
        numeros.put("cuarenta", 40);
        numeros.put("cincuenta", 50);
        numeros.put("sesenta", 60);
        numeros.put("setenta", 70);
        numeros.put("ochenta", 80);
        numeros.put("noventa", 90);
        numeros.put("cien", 100);

        // Verificar si el número está en el mapa
        if (numeros.containsKey(numeroTexto)) {
            return String.valueOf(numeros.get(numeroTexto));
        }

        // Manejar números compuestos (31-99)
        if (numeroTexto.contains(" y ")) {
            String[] partes = numeroTexto.split("\\s+y\\s+");
            if (partes.length == 2) {
                String decenaTexto = partes[0].trim();
                String unidadTexto = partes[1].trim();

                Integer decena = numeros.get(decenaTexto);
                Integer unidad = numeros.get(unidadTexto);

                if (decena != null && unidad != null) {
                    int resultado = decena + unidad;
                    return String.valueOf(resultado);
                }
            }
        }

        // Si no se encontró coincidencia, devolver el texto original
        return numeroTexto;
    }

}
