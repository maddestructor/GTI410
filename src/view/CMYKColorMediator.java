package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;




public class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {

	//Couleur CMJN
	int cyan;
	int magenta;
	int jaune;
	int noir;

	//Couleurs RVB
	int rouge;
	int vert;
	int bleu;

	//Sliders
	ColorSlider cyanCS;
	ColorSlider magentaCS;
	ColorSlider yellowCS;
	ColorSlider blackCS;

	//Sliders images
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage blackImage;

	//Size of those images
	int imagesWidth;
	int imagesHeight;

	//Colors results
	ColorDialogResult result;

	//Constantes pour pas se mélanger dans les arrays
	final int NOIR = 3;
	final int CYAN = 0;
	final int MAGENTA = 1;
	final int JAUNE = 2;

	final int ROUGE = 0;
	final int VERT = 1;
	final int BLEU = 2;


	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		this.rouge = result.getPixel().getRed();
		this.vert = result.getPixel().getGreen();
		this.bleu = result.getPixel().getBlue();

		int[] cmykArray = rgbToCMYK(rouge, vert, bleu);

		this.noir = cmykArray[NOIR];
		this.cyan = cmykArray[CYAN];
		this.magenta = cmykArray[MAGENTA];
		this.jaune = cmykArray[JAUNE];


		this.result = result;
		result.addObserver(this);

		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeCyanImage(cyan, magenta, jaune, noir);
		computeMagentaImage(cyan, magenta, jaune, noir);
		computeYellowImage(cyan, magenta, jaune, noir);
		computeBlackImage(cyan, magenta, jaune, noir);
	}


	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateCyan = false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
		boolean updateBlack = false;
		if (s == cyanCS && v != cyan) {
			cyan = v;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == magentaCS && v != magenta) {
			magenta = v;
			updateCyan = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == yellowCS && v != jaune) {
			jaune = v;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == blackCS && v != noir) {
			noir = v;
			updateCyan = true;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (updateCyan) {
			computeCyanImage(cyan, magenta, jaune, noir);
		}
		if (updateMagenta) {
			computeMagentaImage(cyan, magenta, jaune, noir);
		}
		if (updateYellow) {
			computeYellowImage(cyan, magenta, jaune, noir);
		}
		if (updateBlack) {
			computeBlackImage(cyan, magenta, jaune, noir);
		}

		int[] rgbArray = cmykToRGB(cyan, magenta, jaune, noir);

		Pixel pixel = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		result.setPixel(pixel);
	}

	/**
	 * Le code de cette méthode est dérivé de l'algorithme de cette page HTML:
	 * @source https://forums.adobe.com/thread/428899
	 */
	private int[] cmykToRGB(int _cyan, int _magenta, int _jaune, int _noir){
		int[] _rgbArray = new int[3];
		
		float cyan = (float) (_cyan / 255.0);
		float magenta = (float) (_magenta / 255.0);
		float jaune = (float) (_jaune / 255.0);
		float noir = (float) (_noir / 255.0);
		
		cyan = ( cyan * ( 1 - noir ) + noir );
		magenta = ( magenta * ( 1 - noir ) + noir );
		jaune = ( jaune * ( 1 - noir ) + noir );
		
		_rgbArray[ROUGE] = Math.round(( 1 - cyan ) * 255);
		_rgbArray[VERT] = Math.round(( 1 - magenta ) * 255);
		_rgbArray[BLEU] = Math.round(( 1 - jaune ) * 255);

		return _rgbArray;
       
	}

	/**
	 * Le code de cette méthode est dérivé de l'algorithme de cette page HTML:
	 * @source https://forums.adobe.com/thread/428899
	 */
	private int[] rgbToCMYK(int _rouge, int _vert, int _bleu){
		int[] _cmykArray = new int[4];
		

		float _cyan = (float) (1.0 - ( _rouge / 255.0 ));
		float _magenta = (float) (1.0 - ( _vert / 255.0 ));
		float _jaune = (float) (1.0 - ( _bleu / 255.0 ));
		float _noir = (float) 1.0;

		if ( _cyan < _noir ){
			_noir = _cyan;
		}
		if ( _magenta < _noir ){
			_noir = _magenta;
		}
		if ( _jaune < _noir ){
			_noir = _jaune;
		}
		
		_cyan = ( _cyan - _noir ) / ( 1 - _noir );
		_magenta = ( _magenta - _noir ) / ( 1 - _noir );
		_jaune = ( _jaune - _noir ) / ( 1 - _noir );
		
		
		_cmykArray[NOIR] = Math.round(255 * _noir);
		_cmykArray[CYAN] = Math.round(255 * _cyan);
		_cmykArray[MAGENTA] = Math.round(255 * _magenta);
		_cmykArray[JAUNE] = Math.round(255 * _jaune);


		return _cmykArray;
	}


	public void computeCyanImage(int _cyan, int _magenta, int _jaune, int _noir) {
//		int _rouge = cmykToRGB(_cyan, _magenta, _jaune, _noir)[ROUGE];
//		int _vert = cmykToRGB(_cyan, _magenta, _jaune, _noir)[VERT];
//		int _bleu = cmykToRGB(_cyan, _magenta, _jaune, _noir)[BLEU];
//		Pixel p = new Pixel(_rouge, _vert, _bleu, 255); 
		int[] rgbArray = cmykToRGB(_cyan, _magenta, _jaune, _noir);
		Pixel p = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed((int)(255 - _noir - ((double) i / (double) imagesWidth * (255 - _noir))));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
	}

	public void computeMagentaImage(int _cyan, int _magenta, int _jaune, int _noir) {
//		int _rouge = cmykToRGB(_cyan, _magenta, _jaune, _noir)[ROUGE];
//		int _vert = cmykToRGB(_cyan, _magenta, _jaune, _noir)[VERT];
//		int _bleu = cmykToRGB(_cyan, _magenta, _jaune, _noir)[BLEU];
//		Pixel p = new Pixel(_rouge, _vert, _bleu, 255); 
		int[] rgbArray = cmykToRGB(_cyan, _magenta, _jaune, _noir);
		Pixel p = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		for (int i = 0; i<imagesWidth; ++i) {
			p.setGreen((int)(255 - _noir - ((double) i / (double) imagesWidth * (255 - _noir))));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}

	public void computeYellowImage(int _cyan, int _magenta, int _jaune, int _noir) { 
//		int _rouge = cmykToRGB(_cyan, _magenta, _jaune, _noir)[ROUGE];
//		int _vert = cmykToRGB(_cyan, _magenta, _jaune, _noir)[VERT];
//		int _bleu = cmykToRGB(_cyan, _magenta, _jaune, _noir)[BLEU];
//		Pixel p = new Pixel(_rouge, _vert, _bleu, 255);
		int[] rgbArray = cmykToRGB(_cyan, _magenta, _jaune, _noir);
		Pixel p = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		for (int i = 0; i<imagesWidth; ++i) {
			p.setBlue((int)(255 - _noir - ((double) i / (double) imagesWidth * (255 - _noir))));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}

	public void computeBlackImage(int _cyan, int _magenta, int _jaune, int _noir) { 
		int _blackReference;
//		int _rouge = cmykToRGB(_cyan, _magenta, _jaune, _noir)[ROUGE];
//		int _vert = cmykToRGB(_cyan, _magenta, _jaune, _noir)[VERT];
//		int _bleu = cmykToRGB(_cyan, _magenta, _jaune, _noir)[BLEU];
//		Pixel p = new Pixel(_rouge, _vert, _bleu, 255); 
		int[] rgbArray = cmykToRGB(_cyan, _magenta, _jaune, _noir);
		Pixel p = new Pixel(rgbArray[ROUGE], rgbArray[VERT], rgbArray[BLEU], 255);
		for (int i = 0; i<imagesWidth; ++i) {

			_blackReference = (int)Math.round((((double)i / (double)imagesWidth)* 255.0));

			int[] rgbArrayLoop = cmykToRGB(_cyan, _magenta, _jaune, _blackReference);

			p.setRed(rgbArrayLoop[ROUGE]);
			p.setGreen(rgbArrayLoop[VERT]);
			p.setBlue(rgbArrayLoop[BLEU]); 

			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				blackImage.setRGB(i, j, rgb);
			}
		}
		if (blackCS != null) {
			blackCS.update(blackImage);
		}
	}

	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getBlackImage() {
		return blackImage;
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		blackCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public int getYellow() {
		return jaune;
	}

	/**
	 * @return
	 */
	public int getMagenta() {
		return magenta;
	}

	/**
	 * @return
	 */
	public int getCyan() {
		return cyan;
	}

	/**
	 * @return
	 */
	public int getBlack() {
		return noir;
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		int[] rgbArray = cmykToRGB(this.cyan, this.magenta, this.jaune, this.noir);
		Pixel currentColor = new Pixel(rgbArray[ROUGE], rgbArray[BLEU], rgbArray[VERT], 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;

		rouge = result.getPixel().getRed();
		vert = result.getPixel().getGreen();
		bleu = result.getPixel().getBlue();

		int[] cmykArray = rgbToCMYK(this.rouge, this.vert, this.bleu);

		cyanCS.setValue(cmykArray[CYAN]);
		magentaCS.setValue(cmykArray[MAGENTA]);
		yellowCS.setValue(cmykArray[JAUNE]);
		blackCS.setValue(cmykArray[NOIR]);

		cyan = cmykArray[CYAN];
		magenta = cmykArray[MAGENTA];
		jaune = cmykArray[JAUNE];
		noir = cmykArray[NOIR];

		computeCyanImage(cyan, magenta, jaune, noir);
		computeMagentaImage(cyan, magenta, jaune, noir);
		computeYellowImage(cyan, magenta, jaune, noir);
		computeBlackImage(cyan, magenta, jaune, noir);

		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}
}
