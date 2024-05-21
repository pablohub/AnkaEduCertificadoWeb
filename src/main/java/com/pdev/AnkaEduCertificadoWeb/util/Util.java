package com.pdev.AnkaEduCertificadoWeb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    public static String recortarYCompletar(String input, char fillChar, int length) {
        if (input == null) {
            input = "";
        }

        if (input.length() > length) {
            input = input.substring(0, length);
        }

        StringBuilder result = new StringBuilder(input);
        while (result.length() < length) {
            result.append(fillChar);
        }

        return result.toString();
    }

    public static char generateRandomLetter() {
        Random random = new Random();
        // Elige si la letra será mayúscula o minúscula
        boolean isUpperCase = random.nextBoolean();
        // Genera una letra aleatoria basada en el rango ASCII
        char randomLetter;
        if (isUpperCase) {
            randomLetter = (char) (random.nextInt(26) + 'A');
        } else {
            randomLetter = (char) (random.nextInt(26) + 'a');
        }
        logger.info("Random Letter: " + randomLetter);
        return randomLetter;
    }

}
