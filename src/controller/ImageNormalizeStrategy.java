package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

/**
 * Created by Mathieu on 2015-10-27.
 */
public class ImageNormalizeStrategy extends ImageConversionStrategy {

    final int RED = 0;
    final int GREEN = 1;
    final int BLUE = 2;

    /**
     * Cette méthode convertit une image ayant des valeurs quantifiés en double
     * en une image normale ayant des valeurs entières normalisé entre 0 et 255.
     *
     * @param image
     */
    public ImageX convert(ImageDouble image) {

        // Les dimensions de l'image
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();

        int[] maxValues = new int[3];
        int[] minValues = new int[3];

        maxValues[RED] = getImageMaxPixel(image, RED);
        maxValues[GREEN] = getImageMaxPixel(image, GREEN);
        maxValues[BLUE] = getImageMaxPixel(image, BLUE);

        minValues[RED] = getImageMinPixel(image, RED);
        minValues[GREEN] = getImageMinPixel(image, GREEN);
        minValues[BLUE] = getImageMinPixel(image, BLUE);

        //Notre nouvelle image généré à partir de celle fournit en paramètre
        ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
        PixelDouble curPixelDouble = null;

        newImage.beginPixelUpdate();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                // Pour chaque pixel de l'ancienne image
                curPixelDouble = image.getPixel(x, y);

                // On créé un nouveau pixel avec valeur normalisé pour le mettre dans la nouvelle image
                newImage.setPixel(x, y, normalize0To255(curPixelDouble, maxValues, minValues));
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
     * @param maxValues Les valeurs maximales par rapport auxquelles on normalise
     * @param minValues Les valeurs minimales par rapport auxquelles on normalise
     */
    private Pixel normalize0To255(PixelDouble pixel, int[] maxValues, int[] minValues) {

        int redRange = maxValues[RED] - minValues[RED];
        int redMin = minValues[RED] + redRange;

        int greenRange = maxValues[GREEN] - minValues[GREEN];
        int greenMin = minValues[GREEN] + greenRange;

        int blueRange = maxValues[BLUE] - minValues[BLUE];
        int blueMin = minValues[BLUE] + blueRange;

        int redValue = (int) (255.0 * ((pixel.getRed() - redMin) / redRange));
        int greenValue = (int) (255.0 * ((pixel.getGreen() - greenMin) / greenRange));
        int blueValue = (int) (255.0 * ((pixel.getBlue() - blueMin) / blueRange));

        Pixel newPixel = new Pixel(redValue, greenValue, blueValue);

        return newPixel;
    }

    /**
     * Cette méthode permet de trouver la valeur maximale parmis tous
     * les pixels d'une image ayant des valeurs en quantifié en double.
     * Dans notre cas, on va chercher la valeur maximale du rouge, du vert
     * et du bleu indépendamment.
     *
     * @param image
     * @param color La couleur dont on veut aller chercher le max
     */
    private int getImageMaxPixel(ImageDouble image, int color) {
        // On prend les tailles de l'image
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();

        //La valeur maximale
        double max = 0;
        double colorValue = 0;

        // On initialise notre valeur minimale avec le premier pixel
        switch (color) {
            case RED:
                max = image.getPixel(0, 0).getRed();
                break;
            case GREEN:
                max = image.getPixel(0, 0).getGreen();
                break;
            case BLUE:
                max = image.getPixel(0, 0).getBlue();
                break;
        }

        PixelDouble curPixelDouble = null;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                // Pour chaque pixel de l'image
                curPixelDouble = image.getPixel(x, y);

                //On choisit notre couleur
                switch (color) {
                    case RED:
                        colorValue = curPixelDouble.getRed();
                        break;
                    case GREEN:
                        colorValue = curPixelDouble.getGreen();
                        break;
                    case BLUE:
                        colorValue = curPixelDouble.getBlue();
                        break;
                }

                // Si celui-ci est plus grand que notre valeur maximale deja enregistre
                if (colorValue > max) {
                    // La valeur de ce pixel devient notre nouveau maximum
                    max = colorValue;
                }

            }
        }

        return (int) Math.round(max);
    }


    /**
     * Cette méthode permet de trouver la valeur minimale parmis tous
     * les pixels d'une image ayant des valeurs en quantifié en double.
     * Dans notre cas, on va chercher la valeur minimale du rouge, du vert
     * et du bleu indépendamment.
     *
     * @param image
     * @param color La couleur dont on veut aller chercher le min
     */
    private int getImageMinPixel(ImageDouble image, int color) {
        // On prend les tailles de l'image
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();

        //La valeur minimale
        double min = 0;
        double colorValue = 0;

        // On initialise notre valeur minimale avec le premier pixel
        switch (color) {
            case RED:
                min = image.getPixel(0, 0).getRed();
                break;
            case GREEN:
                min = image.getPixel(0, 0).getGreen();
                break;
            case BLUE:
                min = image.getPixel(0, 0).getBlue();
                break;
        }

        PixelDouble curPixelDouble = null;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                // Pour chaque pixel de l'image
                curPixelDouble = image.getPixel(x, y);

                //On choisit notre couleur
                switch (color) {
                    case RED:
                        colorValue = curPixelDouble.getRed();
                        break;
                    case GREEN:
                        colorValue = curPixelDouble.getGreen();
                        break;
                    case BLUE:
                        colorValue = curPixelDouble.getBlue();
                        break;
                }

                // Si celui-ci est plus grand que notre valeur minimale deja enregistre
                if (colorValue < min) {
                    // La valeur de ce pixel devient notre nouveau minimum
                    min = colorValue;
                }

            }
        }

        return (int) Math.round(min);
    }
}

