package by.bsuir.course4.ImageProcessor.imageCreate;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

public class Stereogram  {
	public static final String ROOT_PATH = "D:\\Work\\Image\\Stereogram";
	
	public static final String DEPTH_MAP_PATH = "\\DepthMap\\";
	
	public static final String PATTERN_PATH = "\\Pattern\\";
	
	public static final String STEREOGRAM_PATH = "\\Stereogram\\";
	
	public static final String EXTENSION = ".png";

	public static final byte FIRST_BYTE_OFFSET = 0;

	public static final byte SECOND_BYTE_OFFSET = 8;

	public static final byte THIRD_BYTE_OFFSET = 16;

	public static final int FIRST_BYTE_MASK = 0xFF << FIRST_BYTE_OFFSET;

	public static final int SECOND_BYTE_MASK = 0xFF << SECOND_BYTE_OFFSET;

	public static final int THIRD_BYTE_MASK = 0xFF << THIRD_BYTE_OFFSET;

	private BufferedImage depthMapImage;

	private BufferedImage patternImage;
	
	private BufferedImage resultImage;

	private int [] depthMapImagePixels;

	private int [] patternImagePixels;
	
	private int [] resultImagePixels;

	private int width;

	private int height;
	
	private int patt_width;
	
	private int patt_height;
	
	private int patt_step;
	
	public static final byte FOURTH_BYTE_OFFSET = 24;
	
	public static final int FOURTH_BYTE_MASK = 0xFF << 24;
	
	private int btw_eyes = 60;

	//for pattern image
	public Stereogram(int input_btw_eyes) {
		
		btw_eyes = input_btw_eyes;
		
	}
	//for random image
	public Stereogram(int input_btw_eyes, int input_patt_step) {
		
		btw_eyes = input_btw_eyes;
		patt_step = input_patt_step;
		
	}
	
	
	private int getBlueComponent(final int pixel) {
		return (pixel & FIRST_BYTE_MASK)
				>> FIRST_BYTE_OFFSET;
	}

	private int getGreenComponent(final int pixel) {
		return (pixel & SECOND_BYTE_MASK)
				>> SECOND_BYTE_OFFSET;
	}

	private int getRedComponent(final int pixel) {
		return (pixel & THIRD_BYTE_MASK)
				>> THIRD_BYTE_OFFSET;
	}
	

	private int composePixel(
			final int blueComponent,
			final int greenComponent,
			final int redComponent
			) {
		return 	(blueComponent & FIRST_BYTE_MASK)
					<< FIRST_BYTE_OFFSET
				|
				(greenComponent & FIRST_BYTE_MASK)
					<< SECOND_BYTE_OFFSET
				|
				(redComponent & FIRST_BYTE_MASK)
					<< THIRD_BYTE_OFFSET
				;
	}

	private int[] getImagePixels(BufferedImage leftImage) {
		return ((DataBufferInt) (leftImage.getRaster()
				.getDataBuffer())).getData();
	}

	private BufferedImage readImage(final int path, String location)
			throws IOException, URISyntaxException {
		return read(
				new File(ROOT_PATH + location + path + EXTENSION));
	}

	private BufferedImage read(File input) throws IOException {
        ImageInputStream stream = ImageIO.createImageInputStream(input);
        if (stream == null) {
            throw new IIOException("Can't create an ImageInputStream!");
        }
        BufferedImage bi = read(stream);
        if (bi == null) {
            stream.close();
        }
        return bi;
    }

	private BufferedImage read(ImageInputStream stream)
	        throws IOException {
			Iterator iter = ImageIO.getImageReaders(stream);
	        if (!iter.hasNext()) {
	            return null;
	        }
	        ImageReader reader = (ImageReader)iter.next();
	        ImageReadParam param = reader.getDefaultReadParam();
	        param.setDestinationType(ImageTypeSpecifier
	        		.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB));
	        reader.setInput(stream, true, true);
	        BufferedImage bi;
	        try {
	            bi = reader.read(0, param);
	        } finally {
	            reader.dispose();
	            stream.close();
	        }
	        return bi;
	    }
	
	private int[] fillStereogramWithBasicColors(int[] pattern, int pattern_width, int pattern_height){
		for ( int i = 0; i < height; i++){
			for( int j= 0; j < width; j++){
				resultImagePixels[j+i*width] = pattern[j-pattern_width*((int)j/pattern_width)+ (i-pattern_height*((int)i/pattern_height))*pattern_width];
			}
		}
		return resultImagePixels;
	}
	
	private void patternImageHandler(BufferedImage patternImage){
		
		patternImagePixels = getImagePixels(patternImage);
			if(depthMapImage.getWidth()/patternImage.getWidth() >= 10){
				patt_height = patternImage.getHeight();
				patt_width = patternImage.getWidth();
				resultImagePixels = fillStereogramWithBasicColors(patternImagePixels,patt_width,patt_height);	
					}
			else{
				System.out.println("Pattern image is too small! You will not see stereogram");
			}

		
		patt_step=patternImage.getWidth();
	}
	
	private int[] generateMatrixRndColors(){
		//
		//	Distance between eyes is 60 pixels (btw_eyes)
		//	Pattern step (patt_step) should be less than distance between eyes
		//

		Random randomGenerator = new Random();

		// Fill image with random colors 
		
		int [] rnd_color = new int[patt_step*patt_step];
		for ( int i = 0; i < patt_step; i++){
			for( int j= 0; j < patt_step; j++){
				int red = (int)(randomGenerator.nextInt(256));
				int blue = (int)(randomGenerator.nextInt(256));
				int green = (int)(randomGenerator.nextInt(256));
			
				rnd_color[i*patt_step + j] = composePixel(blue, green, red);
			}
		}
		return rnd_color;
	}

	//-----------------------------------------------------------------------------------------------
	public void process(final int ... path){
		try {
		
		depthMapImage = readImage(path[0],DEPTH_MAP_PATH);
		depthMapImagePixels = getImagePixels(depthMapImage);
		
		height = depthMapImage.getHeight();
		width = depthMapImage.getWidth();
		
		resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		resultImagePixels = getImagePixels(resultImage);
		
		if(path.length >= 2) {
			patternImage = readImage(path[1], PATTERN_PATH);
			patternImageHandler(patternImage);
		}
		else{
		
			int[] rnd_color = generateMatrixRndColors();
				
			// fulfill result image with random pixels 		
			resultImagePixels = fillStereogramWithBasicColors(rnd_color, patt_step, patt_step);
		}
		
		//for each stripe 
		for( int h = 0; h < (int)width/patt_step; h++){			   
			   for ( int i = 0; i < height; i++){
				   for( int j= 0; j < patt_step; j++){					   
					      
					       int argbnew = depthMapImagePixels[j+h*patt_step+ i*width];
					       
					       int rgb = getGreenComponent(argbnew);
					       
					       int shift = (int)((h-1)*patt_step-rgb*((patt_step-btw_eyes)/255.0));

					       if ( 
					    		   h != 0 
					    		   && 
					    		   j+shift < width 
					    		   && 
					    		   j+shift > 0 
					    	)
					       {
					    	 resultImagePixels[j+(int)(h*patt_step) + i*width] = resultImagePixels[j+shift + width*i];					    	   
					       }
					       
					       }
		    	  
			   }

			}
		File resultFile = new File(ROOT_PATH + STEREOGRAM_PATH + "Stereogram" + EXTENSION);
			ImageIO.write(resultImage, "png", resultFile);	
		}catch(IOException e){
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
}
}
