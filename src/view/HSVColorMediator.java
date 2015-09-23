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
	int saturation;
	int value;
	BufferedImage hueImage;
	BufferedImage saturationImage;
	BufferedImage valueImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	//Couleurs RVB
	int rouge;
	int vert;
	int bleu;
	
	final int ROUGE = 0;
	final int VERT = 1;
	final int BLEU = 2;
	
	final int HUE = 0;
	final int SATURATION = 1;
	final int VALUE = 2;


	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight)
	{
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		this.rouge = result.getPixel().getRed();
		this.vert = result.getPixel().getGreen();
		this.bleu = result.getPixel().getBlue();
		
		int [] hsvArray = rgbToHSV(rouge, vert, bleu);
		
		this.hue = hsvArray[HUE];
		this.saturation = hsvArray[SATURATION];
		this.value = hsvArray[VALUE];

		this.result = result;
		result.addObserver(this);
		
		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		saturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		computeHueImage(hue, saturation, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value);	
	}
	
	
	private int[] rgbToHSV(int _rouge, int _vert, int _bleu)
	{
		float[] hsvArray = new float[3];

		float rouge = (float) ( _rouge / 255.0 );                     //RGB from 0 to 255
		float vert = (float) ( _vert / 255.0 );
		float bleu = (float) ( _bleu / 255.0 );

		float min = Math.min( rouge,Math.min( vert, bleu ));    //Min. value of RGB
		float max = Math.max( rouge,Math.max( vert, bleu ));  //Max. value of RGB
		float delta_max = max - min          ;   //Delta RGB value 

		hsvArray[VALUE] = max;

		if ( delta_max == 0 )                     //This is a gray, no chroma...
		{
			hsvArray[HUE] = 0;                     //HSV results from 0 to 1
			hsvArray[SATURATION] = 0;
		}
		else                                    //Chromatic data...
		{
			hsvArray[SATURATION] = delta_max / max;

			float delta_rouge = ( ( ( max - rouge ) / 6 ) + ( delta_max / 2 ) ) / delta_max;
			float delta_vert = ( ( ( max - vert ) / 6 ) + ( delta_max / 2 ) ) / delta_max;
			float delta_bleu = ( ( ( max - bleu ) / 6 ) + ( delta_max / 2 ) ) / delta_max;

			if      ( rouge == max ) hsvArray[HUE] = delta_bleu - delta_vert;
			else if ( vert == max ) hsvArray[HUE] = ( 1 / 3 ) + delta_rouge - delta_bleu;
			else if ( bleu == max ) hsvArray[HUE] = ( 2 / 3 ) + delta_vert - delta_rouge;

			if ( hsvArray[HUE] < 0 ) hsvArray[HUE] += 1;
			if ( hsvArray[HUE] > 1 ) hsvArray[HUE] -= 1;
		}
		
		int[] returnArray = new int[3];
		
		returnArray[HUE] = Math.round(hsvArray[HUE] * 255);
		returnArray[SATURATION] = Math.round(hsvArray[SATURATION] * 255);
		returnArray[VALUE] = Math.round(hsvArray[VALUE] * 255);
		
		return returnArray;
		
	}
	
	private int[] hsvToRGB(int _hue, int _saturation, int _value){
		
		int[] rgbArray = new int[3];
		
		float _rouge;
		float _vert;
		float _bleu;
		
		float hue = (float) ( _hue / 255.0 );                     //RGB from 0 to 255
		float saturation = (float) ( _saturation / 255.0 );
		float value = (float) ( _value / 255.0 );
		
		float var_h;
		float var_i;
		float var_1;
		float var_2;
		float var_3;

		if ( saturation == 0 )                       //hue from 0 to 1
		{
			rgbArray[ROUGE] = Math.round(value * 255);
			rgbArray[VERT] = Math.round(value * 255);
			rgbArray[BLEU] = Math.round(value * 255);

			return rgbArray;
		}
		else
		{
			var_h = hue * 6;
			if ( var_h == 6 ){

				var_h = 0   ;   //hue must be < 1

			}
			
			var_i = (float) Math.floor(var_h);    //Or ... var_i = floor( var_h )
			var_1 = value * ( 1 - saturation );
			var_2 = value * ( 1 - saturation * ( var_h - var_i ) );
			var_3 = value * ( 1 - saturation * ( 1 - ( var_h - var_i ) ) );

			if      ( var_i == 0 ) {
				_rouge = value; 
				_vert = var_3; 
				_bleu = var_1;
			}
			else if ( var_i == 1 ) {
				_rouge = var_2; 
				_vert = value; 
				_bleu = var_1; 
			}
			else if ( var_i == 2 ) {
				_rouge = var_1; 
				_vert = value; 
				_bleu = var_3; 
			}
			else if ( var_i == 3 ) { 
				_rouge = var_1; 
				_vert = var_2; 
				_bleu = value;    
			}
			else if ( var_i == 4 ) { 
				_rouge = var_3; 
				_vert = var_1; 
				_bleu = value; 
			}
			else                   { 
				_rouge = value; 
				_vert = var_1; 
				_bleu = var_2; 
			}

			rgbArray[ROUGE] = Math.round(_rouge * 255);                 //_rouge_vert_bleu results from 0 to 255
			rgbArray[VERT] = Math.round(_vert * 255);
			rgbArray[BLEU] = Math.round(_bleu * 255);
			
			return rgbArray;
			
			
		}
	}
	
	public void computeHueImage(int _hue, int _saturation, int _value) { 
		int[] rgbArray = hsvToRGB(_hue, _saturation, _value);
		Pixel p = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		
		int t;
		for (int i = 0; i<imagesWidth; ++i) {
			t = (int) Math.round(((double)i / (double)imagesWidth)*255.0);
			p.setRed(hsvToRGB(t,_saturation,_value)[ROUGE]);
			p.setGreen(hsvToRGB(t,_saturation,_value)[VERT]);
			p.setBlue(hsvToRGB(t,_saturation,_value)[BLEU]);
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (hueCS != null) {
			hueCS.update(hueImage);
		}
		
	}
	
	public void computeSaturationImage(int _hue, int _saturation, int _value) {
		int[] rgbArray = hsvToRGB(_hue, _saturation, _value);
		Pixel p = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		
		int t;
		for (int i = 0; i<imagesWidth; ++i) {
			t = (int) Math.round(((double)i / (double)imagesWidth)*255.0);
			p.setRed(hsvToRGB(_hue,t,_value)[ROUGE]);
			p.setGreen(hsvToRGB(_hue,t,_value)[VERT]);
			p.setBlue(hsvToRGB(_hue,t,_value)[BLEU]);
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				saturationImage.setRGB(i, j, rgb);
			}
		}
		if (saturationCS != null) {
			saturationCS.update(saturationImage);
		}
		
	}
	
	public void computeValueImage(int _hue, int _saturation, int _value) { 
		int[] rgbArray = hsvToRGB(_hue, _saturation, _value);
		Pixel p = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		
		int t;
		for (int i = 0; i<imagesWidth; ++i) {
			t = (int) Math.round(((double)i / (double)imagesWidth)*255.0);
			p.setRed(hsvToRGB(_hue,_saturation,t)[ROUGE]);
			p.setGreen(hsvToRGB(_hue,_saturation,t)[VERT]);
			p.setBlue(hsvToRGB(_hue,_saturation,t)[BLEU]);
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
	public float getValue() {
		return value;
	}

	/**
	 * @return
	 */
	public float getSaturation() {
		return saturation;
	}
	
	/**
	 * @return
	 */
	public float getHue() {
		return hue;
	}

	/**
	 * @return
	 */
	public int getSliderHue() {
		return Math.round(hue * 255);
	}
	
	public int getSliderValue() {
		return Math.round(value * 255);
	}

	/**
	 * @return
	 */
	public int getSliderSaturation() {
		return Math.round(saturation * 255);
	}


	
	@Override
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		int[] rgbArray = hsvToRGB(this.hue, this.saturation, this.value);
		Pixel currentColor = new Pixel(rgbArray[ROUGE], rgbArray[BLEU], rgbArray[VERT], 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;

		rouge = result.getPixel().getRed();
		vert = result.getPixel().getGreen();
		bleu = result.getPixel().getBlue();

		int[] hsvArray = rgbToHSV(this.rouge, this.vert, this.bleu);

		hueCS.setValue(Math.round(255 * hsvArray[HUE]));
		saturationCS.setValue(Math.round(255 * hsvArray[SATURATION]));
		valueCS.setValue(Math.round(255 * hsvArray[VALUE]));


		this.hue = hsvArray[HUE];
		this.saturation = hsvArray[SATURATION];
		this.value = hsvArray[VALUE];

		computeHueImage(hsvArray[HUE], hsvArray[SATURATION], hsvArray[VALUE]);
		computeSaturationImage(hsvArray[HUE], hsvArray[SATURATION], hsvArray[VALUE]);
		computeValueImage(hsvArray[HUE], hsvArray[SATURATION], hsvArray[VALUE]);

		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.

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

		int[] rgbArray = hsvToRGB(hue, saturation, value);

		Pixel pixel = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		result.setPixel(pixel);
		
	}

}
