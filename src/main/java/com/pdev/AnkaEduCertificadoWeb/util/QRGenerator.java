package com.pdev.AnkaEduCertificadoWeb.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRGenerator {

    public BufferedImage generarCodigoQR(String datos) throws Exception {
        int ancho = 300;
        int alto = 300;

        BitMatrix matriz = new MultiFormatWriter().encode(datos, BarcodeFormat.QR_CODE, ancho, alto);

        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {
                //imagen.setRGB(i, j, matriz.get(i, j) ? 0xFF000000 : 0xFFFFFFFF);
                imagen.setRGB(i, j, matriz.get(i, j) ? 0xFF000000 : 0x00000000);
                //imagen.setRGB(i, j, matriz.get(i, j) ? 0xFFFF0000 : 0x00000000);//rojo
            }
        }

        return imagen;
    }

    public byte[] imageToBytes(BufferedImage imagen) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagen, "png", baos);
        return baos.toByteArray();
    }

}