package view;

import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.ObserverIF;
import model.Pixel;

public class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider hueCS;
	ColorSlider saturationCS;
	ColorSlider valueCS;
	int hue;
	double saturation;
	double value;
	BufferedImage hueImage;
	BufferedImage saturationImage;
	BufferedImage valueImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	

	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight)
	{
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		this.result = result;
		this.setHSV();

		result.addObserver(this);
		
		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		saturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeHueImage(hue, saturation, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value);	
	}
	
	private void setHSV()
	{
		double max = 0;
        double min = 0;
        double c = 0;
        
        double red = (double)result.getPixel().getRed() / 255.0;
        double green = (double)result.getPixel().getGreen() / 255.0;
        double blue = (double)result.getPixel().getBlue() / 255.0;
        
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		if ((red >= green) && (red >= blue))
		{
			max = red;

		}
		else if ((green >= red) && (green >= blue))
		{
			max = green;
			
		}
		else if ((blue >= red) && (blue >= green))
		{
			max = blue;
		}
		
		if ((red <= green) && (red <= blue))
		{
			min = red;
		}
		else if ((green <= red) && (green <= blue))
		{
			min = green;
		}
		else if ((blue <= red) && (blue <= green))
		{
			min = blue;
		}
		
		c = max - min;
		
		if (c == 0)
		{
			this.hue = 0;
		}
	    else if (max == red)
		{
	    	int mod = (int) (((( (green - blue) / c ) % 6) + 6) % 6);
			this.hue = (int) Math.round( 60.0 * mod );
		}
		else if (max == green)
		{
			this.hue = (int) Math.round( 60.0 * ( ( (blue - red) / c ) + 2.0) );
		}
		else if (max == blue)
		{
			this.hue = (int) Math.round( 60.0 * ( ( (red - blue) / c ) + 4.0) );
		}
		
		this.value = max * 100;
		
		if (c == 0)
			this.saturation = 0;
		else
			this.saturation = (c / max) * 100;
		
		//System.out.println(result.getPixel().getRed() + "-" + result.getPixel().getGreen() + "-" + result.getPixel().getBlue() );
		//System.out.println(hue + "-" + saturation + "-" + value);
		
	}
	
	public void computeHueImage(int hue, double saturation, double value) { 
		
		double c = (saturation / 100.0) * (value / 100.0);
		int mod = (int) (((((double)hue / 60.0) % 2) + 2) % 2);
		double x = c * (1 - Math.abs(mod - 1));
		double m = (value / 100.0) - c;
		
		int red = 0;
		int green = 0;
		int blue = 0;
		
		if (hue >= 0 && hue < 60)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round(m * 255);
		}
		else if (hue >= 60 && hue < 120)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round(m * 255);
		}
		else if (hue >= 120 && hue < 180)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		else if (hue >= 180 && hue < 240)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 240 && hue < 300)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 300 && hue < 360)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		
		System.out.println(red + "-" + green + "-" + blue );
		System.out.println(hue + "-" + saturation + "-" + value);
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {

			p.setRed((int)(((double)i / (double)imagesWidth)*255)); 
			p.setGreen((int)(((double)i / (double)imagesWidth)*255)); 
			p.setBlue((int)(((double)i / (double)imagesWidth)*255)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (hueCS != null) {
			hueCS.update(hueImage);
		}
	}
	
	public void computeSaturationImage(int hue, double saturation, double value) {
		
		double c = (saturation / 100.0) * (value / 100.0);
		int mod = (int) (((((double)hue / 60.0) % 2) + 2) % 2);
		double x = c * (1 - Math.abs(mod - 1));
		double m = (value / 100.0) - c;
		
		int red = 0;
		int green = 0;
		int blue = 0;
		
		if (hue >= 0 && hue < 60)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round(m * 255);
			
		}
		else if (hue >= 60 && hue < 120)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round(m * 255);
		}
		else if (hue >= 120 && hue < 180)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		else if (hue >= 180 && hue < 240)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 240 && hue < 300)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 300 && hue < 360)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed((int)(((double)i / (double)imagesWidth)*255)); 
			p.setGreen((int)(((double)i / (double)imagesWidth)*255)); 
			p.setBlue((int)(((double)i / (double)imagesWidth)*255)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				saturationImage.setRGB(i, j, rgb);
			}
		}
		if (saturationCS != null) {
			saturationCS.update(saturationImage);
		}
	}
	
	public void computeValueImage(int hue, double saturation, double value) { 
		double c = (saturation / 100.0) * (value / 100.0);
		int mod = (int) (((((double)hue / 60.0) % 2) + 2) % 2);
		double x = c * (1 - Math.abs(mod - 1));
		double m = (value / 100.0) - c;
		
		int red = 0;
		int green = 0;
		int blue = 0;
		
		if (hue >= 0 && hue < 60)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round(m * 255);
			
		}
		else if (hue >= 60 && hue < 120)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round(m * 255);
		}
		else if (hue >= 120 && hue < 180)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		else if (hue >= 180 && hue < 240)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 240 && hue < 300)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 300 && hue < 360)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed((int)(((double)i / (double)imagesWidth)*255)); 
			p.setGreen((int)(((double)i / (double)imagesWidth)*255)); 
			p.setBlue((int)(((double)i / (double)imagesWidth)*255)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				valueImage.setRGB(i, j, rgb);
			}
		}
		if (valueCS != null) {
			valueCS.update(valueImage);
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage getValueImage() {
		return valueImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getSaturationImage() {
		return saturationImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getHueImage() {
		return hueImage;
	}

	/**
	 * @param slider
	 */
	public void setHueCS(ColorSlider slider) {
		hueCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setSaturationCS(ColorSlider slider) {
		saturationCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setValueCS(ColorSlider slider) {
		valueCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @return
	 */
	public double getSaturation() {
		return saturation;
	}

	/**
	 * @return
	 */
	public int getHue() {
		return hue;
	}
	
	@Override
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
				// is aready properly set, there is no need to recompute the images.
		double c = (saturation / 100.0) * (value / 100.0);
		int mod = (int) (((((double)hue / 60.0) % 2) + 2) % 2);
		double x = c * (1 - Math.abs(mod - 1));
		double m = (value / 100.0) - c;
		
		int red = 0;
		int green = 0;
		int blue = 0;
		
		if (hue >= 0 && hue < 60)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round(m * 255);
			
		}
		else if (hue >= 60 && hue < 120)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round(m * 255);
		}
		else if (hue >= 120 && hue < 180)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		else if (hue >= 180 && hue < 240)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 240 && hue < 300)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 300 && hue < 360)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		
		
				Pixel currentColor = new Pixel(red, green, blue, 255);
				if(currentColor.getARGB() == result.getPixel().getARGB()) return;
				
				
				this.setHSV();
				
				hueCS.setValue(hue);
				saturationCS.setValue((int) Math.round(saturation));
				valueCS.setValue((int) Math.round(value));
				computeHueImage(hue, saturation, value);
				computeSaturationImage(hue, saturation, value);
				computeValueImage(hue, saturation, value);
		
	}

	@Override
	public void update(ColorSlider s, int v) {
		boolean updateHue = false;
		boolean updateSaturation = false;
		boolean updateValue = false;
		

		
		if (s == hueCS && v != hue) {
			hue = v;
			updateSaturation = true;
			updateValue = true;
		}
		if (s == saturationCS && v != saturation) {
			saturation = v;
			updateHue = true;
			updateValue = true;
		}
		if (s == valueCS && v != value) {
			value = v;
			updateHue = true;
			updateSaturation = true;
		}
		if (updateHue) {
			computeHueImage(hue, saturation, value);
		}
		if (updateSaturation) {
			computeSaturationImage(hue, saturation, value);
		}
		if (updateValue) {
			computeValueImage(hue, saturation, value);
		}
		
		double c = (saturation / 100.0) * (value / 100.0);
		int mod = (int) (((((double)hue / 60.0) % 2) + 2) % 2);
		double x = c * (1 - Math.abs(mod - 1));
		double m = (value / 100.0) - c;
		
		int red = 0;
		int green = 0;
		int blue = 0;
		
		if (hue >= 0 && hue < 60)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round(m * 255);
			
		}
		else if (hue >= 60 && hue < 120)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round(m * 255);
		}
		else if (hue >= 120 && hue < 180)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((c + m) * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		else if (hue >= 180 && hue < 240)
		{
			red = (int) Math.round(m * 255);
			green = (int) Math.round((x + m) * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 240 && hue < 300)
		{
			red = (int) Math.round((x + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((c + m) * 255);
		}
		else if (hue >= 300 && hue < 360)
		{
			red = (int) Math.round((c + m) * 255);
			green = (int) Math.round(m * 255);
			blue = (int) Math.round((x + m) * 255);
		}
		
		Pixel pixel = new Pixel(red, green, blue, 255);
		result.setPixel(pixel);
		
	}

}
