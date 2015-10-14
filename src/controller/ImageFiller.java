package controller;

import model.ImageX;
import model.Pixel;
import model.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

public class ImageFiller extends AbstractTransformer {

    private final int HUE = 0;
    private final int SATURATION = 1;
    private final int VALUE = 2;
    private ImageX currentImage;
	private int currentImageWidth;
    private int currentImageHeight;
    private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
    private int hueThreshold = 0;
    private int saturationThreshold = 0;
    private int valueThreshold = 0;

    @Override
	public int getID() {
        return ID_FLOODER;
    }

	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;
				currentImageWidth = currentImage.getImageWidth();
                currentImageHeight = currentImage.getImageHeight();

				Point pt = e.getPoint();
				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}
				ptTransformed.translate(-currentImage.getPosition().x, -currentImage.getPosition().y);
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {

					currentImage.beginPixelUpdate();

                    if (floodFill) {

                        Pixel clickedPixel = currentImage.getPixel(ptTransformed.x, ptTransformed.y);

                        try {
                            floodFill(ptTransformed.x, ptTransformed.y, fillColor);
                        } catch (AWTException e1) {
                            e1.printStackTrace();
                        }

                    } else {

                        try {
                            boundaryFill(ptTransformed.x, ptTransformed.y, borderColor, fillColor);
                        } catch (AWTException e1) {
                            e1.printStackTrace();
                        }

                    }
                    currentImage.endPixelUpdate();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Horizontal line fill with specified color
	 */
	private void horizontalLineFill(Point ptClicked) {
		Stack stack = new Stack();
		stack.push(ptClicked);
		while (!stack.empty()) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth() &&
				!currentImage.getPixel(current.x, current.y).equals(fillColor)) {
				currentImage.setPixel(current.x, current.y, fillColor);
				
				// Next points to fill.
				Point nextLeft = new Point(current.x-1, current.y);
				Point nextRight = new Point(current.x+1, current.y);
				stack.push(nextLeft);
				stack.push(nextRight);
			}
		}
		
	}

    private void floodFill(int initialX, int initialY,
                          Pixel fillColorPixel) throws AWTException {

        System.out.println("Processing flood fill");

        Pixel interiorColorPixel = currentImage.getPixel(initialX, initialY);

        Stack<Point> recursiveStack = new Stack();

        recursiveStack.push(new Point(initialX, initialY));

        while (!recursiveStack.empty()) {
            Point currentPoint = recursiveStack.pop();

            if (isInTheImage(currentPoint)) {
                Pixel currentPixel = currentImage.getPixel(currentPoint.x, currentPoint.y);

                if (currentPixel.equals(interiorColorPixel) && !currentPixel.equals(fillColorPixel)) {

                    currentImage.setPixel(currentPoint.x, currentPoint.y, fillColorPixel);

//					4-WAY
                    Point topNeighbor = new Point(currentPoint.x, currentPoint.y + 1);
                    Point rightNeighbor = new Point(currentPoint.x + 1, currentPoint.y);
                    Point bottomNeighbor = new Point(currentPoint.x, currentPoint.y - 1);
                    Point leftNeighbor = new Point(currentPoint.x - 1, currentPoint.y);

                    recursiveStack.push(topNeighbor);
                    recursiveStack.push(rightNeighbor);
                    recursiveStack.push(bottomNeighbor);
                    recursiveStack.push(leftNeighbor);

//					8-WAY
//					Point topLeftNeighbor = new Point(currentPoint.x - 1, currentPoint.y + 1);
//					Point topRightNeighbor = new Point(currentPoint.x + 1, currentPoint.y + 1);
//					Point bottomRightNeighbor = new Point(currentPoint.x + 1, currentPoint.y - 1);
//					Point bottomLeftNeighbor = new Point(currentPoint.x - 1, currentPoint.y - 1);

//					recursiveStack.push(topLeftNeighbor);
//					recursiveStack.push(topRightNeighbor);
//					recursiveStack.push(bottomRightNeighbor);
//					recursiveStack.push(bottomLeftNeighbor);
                }

            }

        }
    }


    private boolean isInTheImage(Point point) {
        return point.x >= 0 && point.x < currentImageWidth && point.y >= 0 && point.y < currentImageHeight;
    }


    private void boundaryFill(int initialX, int initialY, Pixel boundaryColorPixel,
                              Pixel fillColorPixel) throws AWTException {

        System.out.println("Processing boundary fill");

        Stack<Point> recursiveStack = new Stack();

        recursiveStack.push(new Point(initialX, initialY));

        while (!recursiveStack.empty()) {
            Point currentPoint = recursiveStack.pop();

            if (isInTheImage(currentPoint)) {
                Pixel currentPixel = currentImage.getPixel(currentPoint.x, currentPoint.y);

                if (!isWithinBoundaryColorLimits(currentPixel, boundaryColorPixel) && !currentPixel.equals(fillColorPixel)) {

                    currentImage.setPixel(currentPoint.x, currentPoint.y, fillColorPixel);

//					4-WAY
                    Point topNeighbor = new Point(currentPoint.x, currentPoint.y + 1);
                    Point rightNeighbor = new Point(currentPoint.x + 1, currentPoint.y);
                    Point bottomNeighbor = new Point(currentPoint.x, currentPoint.y - 1);
                    Point leftNeighbor = new Point(currentPoint.x - 1, currentPoint.y);

                    recursiveStack.push(topNeighbor);
                    recursiveStack.push(rightNeighbor);
                    recursiveStack.push(bottomNeighbor);
                    recursiveStack.push(leftNeighbor);

//					8-WAY
//					Point topLeftNeighbor = new Point(currentPoint.x - 1, currentPoint.y + 1);
//					Point topRightNeighbor = new Point(currentPoint.x + 1, currentPoint.y + 1);
//					Point bottomRightNeighbor = new Point(currentPoint.x + 1, currentPoint.y - 1);
//					Point bottomLeftNeighbor = new Point(currentPoint.x - 1, currentPoint.y - 1);

//					recursiveStack.push(topLeftNeighbor);
//					recursiveStack.push(topRightNeighbor);
//					recursiveStack.push(bottomRightNeighbor);
//					recursiveStack.push(bottomLeftNeighbor);
                }

            }

        }


//        if (currentImage.getPixelInt(initialX, initialY) != boundaryColorPixel.getRGB() && currentImage.getPixelInt(initialX, initialY) != fillColorPixel.getRGB())
//		if (currentImage.getPixelInt(x, y) >= Color.HSBtoRGB(tabInterpolHSB[0]-(float)0.05, tabInterpolHSB[1]-(float)0.05, tabInterpolHSB[2]-(float)0.05) && currentImage.getPixelInt(x, y) <= Color.HSBtoRGB(tabInterpolHSB[0]+(float)0.05, tabInterpolHSB[1]+(float)0.05, tabInterpolHSB[2]+(float)0.05) && currentImage.getPixelInt(x, y) != newColor.getRGB())

    }

    private boolean isWithinBoundaryColorLimits(Pixel pixelToConsider, Pixel boundaryPixel) {

        float[] pixelHSBValues = Color.RGBtoHSB(pixelToConsider.getRed(), pixelToConsider.getGreen(), pixelToConsider.getBlue(), null);
        float[] boundaryPixelHSBValues = Color.RGBtoHSB(boundaryPixel.getRed(), boundaryPixel.getGreen(), boundaryPixel.getBlue(), null);


        int hueDifference = Math.round(Math.abs(pixelHSBValues[HUE] - boundaryPixelHSBValues[HUE]) * 180);
        int saturationDifference = Math.round(Math.abs(pixelHSBValues[SATURATION] - boundaryPixelHSBValues[SATURATION]) * 255);
        int valueDifference = Math.round(Math.abs(pixelHSBValues[VALUE] - boundaryPixelHSBValues[VALUE]) * 255);

        //This is now working
        return hueDifference <= hueThreshold
                && saturationDifference <= saturationThreshold
                && valueDifference <= valueThreshold;

    }

    private float[] thresholdHSB() {
        float[] floatValueHSB = new float[3];

        floatValueHSB[HUE] = (float) this.hueThreshold / 360;
        floatValueHSB[SATURATION] = (float) this.saturationThreshold / 255;
        floatValueHSB[VALUE] = (float) this.valueThreshold / 255;

        return floatValueHSB;
    }

    /**
	 * @return
	 */
	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
		System.out.println("new border color");
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean b) {
		floodFill = b;
		if (floodFill) 
		{
			System.out.println("now doing Flood Fill");
		} 
		else 
		{
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {

        return hueThreshold;
    }

    /**
     * @param i
     */
    public void setHueThreshold(int i) {
        hueThreshold = i;
        System.out.println("new Hue Threshold " + i);
    }

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
    }

    /**
     * @return
     */
    public int getValueThreshold() {

        return valueThreshold;
    }

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
	}
	
}
