package com.pdev.AnkaEduCertificadoWeb.util;

public class Util {

    public static String recortarYCompletar(String input, char fillChar) {
        if (input == null) {
            input = "";
        }

        // Recortar la cadena a 20 caracteres si es más larga
        if (input.length() > 20) {
            input = input.substring(0, 20);
        }

        // Completar la cadena a 20 caracteres si es más corta
        StringBuilder result = new StringBuilder(input);
        while (result.length() < 20) {
            result.append(fillChar);
        }

        return result.toString();
    }

}
