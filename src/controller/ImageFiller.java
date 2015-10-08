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

	private ImageX currentImage;
	private int currentImageWidth;
    private int currentImageHeight;
    private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	
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
                            floodFill(ptTransformed.x, ptTransformed.y, clickedPixel.toColor(), fillColor.toColor());
                        } catch (AWTException e1) {
                            e1.printStackTrace();
                        }

                    } else {

                        try {
                            boundaryFill(ptTransformed.x, ptTransformed.y, borderColor.toColor(), fillColor.toColor());
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
	
	public void floodFill(int x, int y, Color interiorColor,
            Color newColor) throws AWTException
	{
		if (currentImage.getPixelInt(x, y) != interiorColor.getRGB()) return;

		currentImage.setPixel(x, y, newColor.getRGB());

	    floodFill(x - 1, y, interiorColor, newColor);
	    floodFill(x + 1, y, interiorColor, newColor);
	    floodFill(x, y - 1, interiorColor, newColor);
	    floodFill(x, y + 1, interiorColor, newColor);

		
	}
	
	private float[] interpolHSB()
	{
		float[] floatValueHSB = new float[3];
		
		floatValueHSB[0] = this.hueThreshold / 180;
		floatValueHSB[1] = this.saturationThreshold / 255;
		floatValueHSB[2] = this.valueThreshold / 255;
		
		return floatValueHSB;
	}
	
	private void boundaryFill(int x, int y, Color boundaryColor,
            Color newColor) throws AWTException
	{
	    float[] tabInterpolHSB = interpolHSB();
        //if (currentImage.getPixelInt(x, y) != boundaryColor.getRGB() && currentImage.getPixelInt(x, y) != newColor.getRGB()) 
		if (currentImage.getPixelInt(x, y) >= Color.HSBtoRGB(tabInterpolHSB[0]-(float)0.05, tabInterpolHSB[1]-(float)0.05, tabInterpolHSB[2]-(float)0.05) && currentImage.getPixelInt(x, y) <= Color.HSBtoRGB(tabInterpolHSB[0]+(float)0.05, tabInterpolHSB[1]+(float)0.05, tabInterpolHSB[2]+(float)0.05) && currentImage.getPixelInt(x, y) != newColor.getRGB()) 
        {
        	currentImage.setPixel(x, y, newColor.getRGB());
            //g.setColor(fillColor);
            //g.drawLine(x, y, x, y);
            
        	
        	boundaryFill(x + 1, y, boundaryColor, newColor);
        	boundaryFill(x - 1, y, boundaryColor, newColor);
        	boundaryFill(x, y + 1, boundaryColor, newColor);
        	boundaryFill(x, y - 1, boundaryColor, newColor);

        }
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
