package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

/**
 * Created by Mathieu on 2015-10-27.
 */
public class ImageNormalizeStrategy extends ImageConversionStrategy {

    /**
     * Cette m�thode convertit une image ayant des valeurs quantifi�s en double
     * en une image normale ayant des valeurs enti�res normalis� entre 0 et 255.
     *
     * @param image
     */
    public ImageX convert(ImageDouble image) {
        // Les dimensions de l,image
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();

        // La valeur maximale et minimale de l'image
        int maxPixelValue = getImageMaxPixel(image);
        int minPixelValue = getImageMinPixel(image);

        //Notre nouvelle image g�n�r� � partir de celle fournit en param�tre
        ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
        PixelDouble curPixelDouble = null;

        newImage.beginPixelUpdate();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                // Pour chaque pixel de l'ancienne image
                curPixelDouble = image.getPixel(x, y);

                // On cr�� un nouveau pixel avec valeur normalis� pour le mettre dans la nouvelle image
                newImage.setPixel(x, y, normalize0To255(curPixelDouble, maxPixelValue, minPixelValue));
            }
        }
        newImage.endPixelUpdate();
        return newImage;
    }

    /**
     * Cette m�thode permet de normaliser entre 0 et 255 la valeur d'un pixel
     * par rapport � un valeur maximale et minimale. Elle prend un pixel en param�tre
     * afin de l'�valuer et d'en retourner un nouveau avec la normalisation.
     *
     * @param pixel    Le pixel � �valuer
     * @param maxValue La valeur maximale par rapport � laquelle on normalise
     * @param minValue La valeur minimale par rapport � laquelle on normalise
     */
    private Pixel normalize0To255(PixelDouble pixel, int maxValue, int minValue) {

        int range = maxValue - minValue;
        int min = minValue + range;

        int redValue = (int) (255.0 * ((pixel.getRed() - min) / range));
        int greenValue = (int) (255.0 * ((pixel.getGreen() - min) / range));
        int blueValue = (int) (255.0 * ((pixel.getBlue() - min) / range));

        Pixel newPixel = new Pixel(redValue, greenValue, blueValue);

        return newPixel;
    }

    /**
     * Cette m�thode permet de trouver la valeur maximale parmis tous
     * les pixels d'une image ayant des valeurs en quantifi� en double
     *
     * @param image
     */
    private int getImageMaxPixel(ImageDouble image) {
        // On prend les tailles de l'image
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();

        // On initialise le premier pixel comme �tant la valeur maximale
        int max = getIntValueOfPixel(image.getPixel(0, 0));
        PixelDouble curPixelDouble = null;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                // Pour chaque pixel de l'image
                curPixelDouble = image.getPixel(x, y);

                // Si celui-ci est plus grand que notre valeur maximale deja enregistre
                if (getIntValueOfPixel(curPixelDouble) > max) {
                    // La valeur de ce pixel devient notre nouveau maximum
                    max = getIntValueOfPixel(curPixelDouble);
                }

            }
        }

        return max;
    }

    /**
     * Cette m�thode permet de trouver la valeur minimale parmis tous
     * les pixels d'une image ayant des valeurs en quantifi� en double
     *
     * @param image
     */
    private int getImageMinPixel(ImageDouble image) {
        // On prend les tailles de l'image
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();

        // On initialise le premier pixel comme �tant la valeur minimale
        int min = getIntValueOfPixel(image.getPixel(0, 0));
        PixelDouble curPixelDouble = null;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                // Pour chaque pixel de l'image
                curPixelDouble = image.getPixel(x, y);

                // Si celui-ci est plus grand que notre valeur minimale deja enregistre
                if (getIntValueOfPixel(curPixelDouble) < min) {
                    // La valeur de ce pixel devient notre nouveau minimum
                    min = getIntValueOfPixel(curPixelDouble);
                }

            }
        }

        return min;

    }

    /**
     * Cette m�thode utilise du bitshifting afin de retourner
     * une valeur ARGB enti�re repr�sentant la quantification d'un pixel
     *
     * @param pixel
     */
    private int getIntValueOfPixel(PixelDouble pixel) {
        if (pixel == null) {
            return 0;
        }

        // A = FF, R = d�phas� de 16 bits, G = d�phas� de 8 bits, B = pas de d�phasage
        return (0x000000 | ((int) pixel.getRed() << 16) | ((int) pixel.getGreen() << 8) | ((int) pixel.getBlue() << 0)) / 65536;
    }

}

