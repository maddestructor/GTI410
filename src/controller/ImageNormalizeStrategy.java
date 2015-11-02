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
     * Cette méthode convertit une image ayant des valeurs quantifiés en double
     * en une image normale ayant des valeurs entières normalisé entre 0 et 255.
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

        //Notre nouvelle image généré à partir de celle fournit en paramètre
        ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
        PixelDouble curPixelDouble = null;

        newImage.beginPixelUpdate();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                // Pour chaque pixel de l'ancienne image
                curPixelDouble = image.getPixel(x, y);

                // On créé un nouveau pixel avec valeur normalisé pour le mettre dans la nouvelle image
                newImage.setPixel(x, y, normalize0To255(curPixelDouble, maxPixelValue, minPixelValue));
            }
        }
        newImage.endPixelUpdate();
        return newImage;
    }

    /**
     * Cette méthode permet de normaliser entre 0 et 255 la valeur d'un pixel
     * par rapport à un valeur maximale et minimale. Elle prend un pixel en paramètre
     * afin de l'évaluer et d'en retourner un nouveau avec la normalisation.
     *
     * @param pixel    Le pixel à évaluer
     * @param maxValue La valeur maximale par rapport à laquelle on normalise
     * @param minValue La valeur minimale par rapport à laquelle on normalise
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
     * Cette méthode permet de trouver la valeur maximale parmis tous
     * les pixels d'une image ayant des valeurs en quantifié en double
     *
     * @param image
     */
    private int getImageMaxPixel(ImageDouble image) {
        // On prend les tailles de l'image
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();

        // On initialise le premier pixel comme étant la valeur maximale
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
     * Cette méthode permet de trouver la valeur minimale parmis tous
     * les pixels d'une image ayant des valeurs en quantifié en double
     *
     * @param image
     */
    private int getImageMinPixel(ImageDouble image) {
        // On prend les tailles de l'image
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();

        // On initialise le premier pixel comme étant la valeur minimale
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
     * Cette méthode utilise du bitshifting afin de retourner
     * une valeur ARGB entière représentant la quantification d'un pixel
     *
     * @param pixel
     */
    private int getIntValueOfPixel(PixelDouble pixel) {
        if (pixel == null) {
            return 0;
        }

        // A = FF, R = déphasé de 16 bits, G = déphasé de 8 bits, B = pas de déphasage
        return (0x000000 | ((int) pixel.getRed() << 16) | ((int) pixel.getGreen() << 8) | ((int) pixel.getBlue() << 0)) / 65536;
    }

}

