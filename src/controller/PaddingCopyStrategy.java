package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

public class PaddingCopyStrategy extends PaddingStrategy {
    Pixel zeroPixel = new Pixel(0, 0, 0, 0);
    PixelDouble zeroPixelDouble = new PixelDouble(0, 0, 0, 0);

    /**
     * Returns and validates the Pixel at the specified coordinate.
     * If the Pixel is invalid, value mirror returned.
     *
     * @param image source Image
     * @param x     x coordinate
     * @param y     y coordinate
     * @return the validated Pixel value at the specified coordinates
     */
    public Pixel pixelAt(ImageX image, int x, int y) {
        if ((x > 0) && (x < image.getImageWidth()) &&
                (y > 0) && (y < image.getImageHeight())) {
            return image.getPixel(x, y);
        } else {
            if (x < 0 && y < 0) {
                return new Pixel(image.getPixel(x + 1, y + 1).getARGB());
            } else if (x < 0 && y > image.getImageHeight() - 1) {
                return new Pixel(image.getPixel(x + 1, y - 1).getARGB());
            } else if (x > image.getImageWidth() - 1 && y < 0) {
                return new Pixel(image.getPixel(x - 1, y + 1).getARGB());
            } else if (x > image.getImageWidth() - 1 && y > image.getImageHeight() - 1) {
                return new Pixel(image.getPixel(x - 1, y - 1).getARGB());
            } else if (x > image.getImageWidth() - 1)
                return new Pixel(image.getPixel(x - 1, y).getARGB());
            else if (x < 0) {
                return new Pixel(image.getPixel(x + 1, y).getARGB());
            } else if (y > image.getImageHeight() - 1)
                return new Pixel(image.getPixel(x, y - 1).getARGB());
            else
                return new Pixel(image.getPixel(x, y + 1).getARGB());
        }
    }

    /**
     * Returns and validates the PixelDouble at the specified coordinate.
     * Original Pixel is converted to PixelDouble.
     * If the Pixel is invalid, value mirror returned.
     *
     * @param image source ImageDouble
     * @param x     x coordinate
     * @param y     y coordinate
     * @return the validated PixelDouble value at the specified coordinates
     */
    public PixelDouble pixelAt(ImageDouble image, int x, int y) {
        PixelDouble pixel = null;

        if ((x >= 0) && (x < image.getImageWidth()) &&
                (y >= 0) && (y < image.getImageHeight())) {
            return image.getPixel(x, y);
        } else {
            if (x < 0 && y < 0) {
                return new PixelDouble(image.getPixel(x + 1, y + 1));
            } else if (x < 0 && y > image.getImageHeight() - 1) {
                return new PixelDouble(image.getPixel(x + 1, y - 1));
            } else if (x > image.getImageWidth() - 1 && y < 0) {
                return new PixelDouble(image.getPixel(x - 1, y + 1));
            } else if (x > image.getImageWidth() - 1 && y > image.getImageHeight() - 1) {
                return new PixelDouble(image.getPixel(x - 1, y - 1));
            } else if (x > image.getImageWidth() - 1)
                return new PixelDouble(image.getPixel(x - 1, y));
            else if (x < 0) {
                return new PixelDouble(image.getPixel(x + 1, y));
            } else if (y > image.getImageHeight() - 1)
                return new PixelDouble(image.getPixel(x, y - 1));
            else
                return new PixelDouble(image.getPixel(x, y + 1));

        }
    }


}
