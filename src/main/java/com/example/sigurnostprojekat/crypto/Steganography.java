package com.example.sigurnostprojekat.crypto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Steganography {
    private static final String IMAGES_PATH = "src/main/resources/images/";
    private static final String LINUX_IMAGE = "linux.png";
    private static final String PNG_SUFFIX = ".png";
    private static final String PNG = "png";

    public void hideDataInImage(String data, String messageId) throws IOException {
        File inputFile = new File(IMAGES_PATH + LINUX_IMAGE);

        BufferedImage image = ImageIO.read(inputFile);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        BufferedImage embeddedImage = embedDataIntoImage(image, dataBytes);

        File outputFile = new File(IMAGES_PATH + messageId + PNG_SUFFIX);
        ImageIO.write(embeddedImage, PNG, outputFile);
    }


    private BufferedImage embedDataIntoImage(BufferedImage originalImage, byte[] data) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (8 * (data.length + 1) > width * height) {
            throw new RuntimeException("Data too large to embed in image");
        }

        int dataIndex = 0;
        int bitMask = 0x80;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = originalImage.getRGB(x, y);
                int lsb = (data[dataIndex] & bitMask) == 0 ? 0 : 1;
                int newPixel = (pixel & 0xFFFFFFFE) | lsb;
                originalImage.setRGB(x, y, newPixel);

                bitMask >>>= 1;
                if (bitMask == 0) {
                    bitMask = 0x80;
                    dataIndex++;
                    if (dataIndex == data.length) {
                        data = new byte[]{0x03};
                        dataIndex = 0;
                    }
                }
            }
        }

        return originalImage;
    }

    public String retrieveDataFromImage(String messageId) throws IOException {
        File inputFile = new File(IMAGES_PATH + messageId + PNG_SUFFIX);
        BufferedImage image = ImageIO.read(inputFile);

        byte[] dataBytes = extractDataFromImage(image);

        if(!inputFile.delete()) {
            System.out.println("Failed to delete the image.");
        }

        return new String(dataBytes, StandardCharsets.UTF_8);
    }

    private byte[] extractDataFromImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        ByteArrayOutputStream extractedBytes = new ByteArrayOutputStream();
        byte currentByte = 0x00;
        int bitMask = 0x80;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                int lsb = pixel & 1;

                if (lsb == 1) {
                    currentByte |= bitMask;
                }

                bitMask >>>= 1;
                if (bitMask == 0) {
                    if (currentByte == 0x03) {
                        return extractedBytes.toByteArray();
                    }

                    extractedBytes.write(currentByte);
                    currentByte = 0x00;
                    bitMask = 0x80;
                }
            }
        }
        return extractedBytes.toByteArray();
    }

}
