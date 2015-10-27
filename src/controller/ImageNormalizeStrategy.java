package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

/**
 * Created by Mathieu on 2015-10-27.
 */
public class ImageNormalizeStrategy extends ImageConversionStrategy {

    public ImageX convert(ImageDouble image) {
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();
        int maxPixelValue = getImageMaxPixel(image);
        int minPixelValue = getImageMinPixel(image);
        ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
        PixelDouble curPixelDouble = null;

        newImage.beginPixelUpdate();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                curPixelDouble = image.getPixel(x, y);

                newImage.setPixel(x, y, normalize0To255(curPixelDouble, maxPixelValue, minPixelValue));
            }
        }
        newImage.endPixelUpdate();
        return newImage;
    }

    private Pixel normalize0To255(PixelDouble pixel, int maxValue, int minValue) {

        int range = maxValue - minValue;
        int min = minValue + range;

        int redValue = (int) (255.0 * ((pixel.getRed() - min) / range));
        int greenValue = (int) (255.0 * ((pixel.getGreen() - min) / range));
        int blueValue = (int) (255.0 * ((pixel.getBlue() - min) / range));

        Pixel newPixel = new Pixel(redValue, greenValue, blueValue);

        return newPixel;
    }

    private int getImageMaxPixel(ImageDouble image) {
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();
        int max = getIntValueOfPixel(image.getPixel(0, 0));
        PixelDouble curPixelDouble = null;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                curPixelDouble = image.getPixel(x, y);

                if (getIntValueOfPixel(curPixelDouble) > max) {
                    max = getIntValueOfPixel(curPixelDouble);
                }

            }
        }

        return max;
    }

    private int getImageMinPixel(ImageDouble image) {
        int imageWidth = image.getImageWidth();
        int imageHeight = image.getImageHeight();
        int min = getIntValueOfPixel(image.getPixel(0, 0));
        PixelDouble curPixelDouble = null;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                curPixelDouble = image.getPixel(x, y);

                if (getIntValueOfPixel(curPixelDouble) < min) {
                    min = getIntValueOfPixel(curPixelDouble);
                }

            }
        }

        return min;

    }

    private int getIntValueOfPixel(PixelDouble pixel) {
        if (pixel == null) {
            return 0;
        }
        return (0xff000000 | ((int) pixel.getRed() << 16) | ((int) pixel.getGreen() << 8) | ((int) pixel.getBlue() << 0)) / 65536;
    }

}

