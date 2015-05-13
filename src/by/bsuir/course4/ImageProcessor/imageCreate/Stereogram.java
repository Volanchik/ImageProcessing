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
	
	public static final String ROOT_PATH = "D:\\Work\\Image";

	public static final byte FIRST_BYTE_OFFSET = 0;

	public static final byte SECOND_BYTE_OFFSET = 8;

	public static final byte THIRD_BYTE_OFFSET = 16;

	public static final int FIRST_BYTE_MASK = 0xFF << FIRST_BYTE_OFFSET;

	public static final int SECOND_BYTE_MASK = 0xFF << SECOND_BYTE_OFFSET;

	public static final int THIRD_BYTE_MASK = 0xFF << THIRD_BYTE_OFFSET;

	private BufferedImage firstImage;

	private BufferedImage secondImage;

	private int [] firstImagePixels;

	private int [] secondImagePixels;

	private int width;

	private int height;
	
	public static final byte FOURTH_BYTE_OFFSET = 24;
	
	public static final int FOURTH_BYTE_MASK = 0xFF << 24;
		
	private int patt_step;
	
	private int btw_eyes = 60;

	
	public Stereogram(int input_height, int input_width, int input_patt_step) {
		
		height = input_height;
		width = input_width;
		patt_step = input_patt_step;
		
	}
	
	public int getBlueComponent(final int pixel) {
		return (pixel & FIRST_BYTE_MASK)
				>> FIRST_BYTE_OFFSET;
	}

	public int getGreenComponent(final int pixel) {
		return (pixel & SECOND_BYTE_MASK)
				>> SECOND_BYTE_OFFSET;
	}

	public int getRedComponent(final int pixel) {
		return (pixel & THIRD_BYTE_MASK)
				>> THIRD_BYTE_OFFSET;
	}
	

	public int composePixel(
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

	public int[] getImagePixels(BufferedImage leftImage) {
		return ((DataBufferInt) (leftImage.getRaster()
				.getDataBuffer())).getData();
	}

	public BufferedImage readImage(final String path)
			throws IOException, URISyntaxException {
		return read(
				new File(ROOT_PATH + "\\" + path));
	}

	public static BufferedImage read(File input) throws IOException {
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

	public static BufferedImage read(ImageInputStream stream)
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

	public int getPixelValue(int i, int j) {
		if(i>height-1) {
			i = height-1;
		}
		if(j>width-1) {
			j = width-1;
		}
		if(i<0){
			i=0;
		}
		if(j<0){
			j=0;
		}
		return firstImagePixels[i*width + j];
	}

	public void setPixelValue(final int i, final int j, final int value) {
		firstImagePixels[i*width + j] = value;
	}

	//-----------------------------------------------------------------------------------------------
	public void process(final String ... path){
		try {
		firstImage = readImage(path[0]);
		secondImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		firstImagePixels = getImagePixels(firstImage);
		secondImagePixels = getImagePixels(secondImage);
		if(path.length >= 2) {
			secondImage = readImage(path[1]);
			if (firstImage.getWidth() != secondImage.getWidth()
					|| firstImage.getHeight() != secondImage.getHeight()) {
				throw new IllegalArgumentException("Check the size of images.");
			}
			secondImagePixels = getImagePixels(secondImage);
		}
		
		//
		//	Distance between eyes is 60 pixels (btw_eyes)
		//	Pattern step (patt_step) should be less than distance between eyes
		//
		
		//if there is no input pattern
		Random randomGenerator = new Random();
		
		// Fill image with random colors 
		
		int [] rnd_color = new int[patt_step*height];
		for ( int i = 0; i < height; i++){
			for( int j= 0; j < patt_step; j++){
				int red = (int)(randomGenerator.nextInt(256));
				int blue = (int)(randomGenerator.nextInt(256));
				int green = (int)(randomGenerator.nextInt(256));
			
				rnd_color[i*patt_step + j] = composePixel(blue, green, red);
			}
		}
			
		// fulfill result image with random pixels 		
		for( int h = 0; h < (int)(width/patt_step); h++){			
		   for ( int i = 0; i < height; i++){
			   for( int j= 0; j < patt_step; j++){
				   secondImagePixels[i*width + (h*patt_step)+j] = rnd_color[i*patt_step + j];
			   }
		   }
		}
		
		//for each stripe 
		for( int h = 0; h < (int)(width/patt_step); h++){			   
			   for ( int i = 0; i < height; i++){
				   for( int j= 0; j < patt_step; j++){
					   
					      
					       int argbnew = firstImagePixels[j+h*patt_step+ i*width];
					       
					       int rgb = getGreenComponent(argbnew);

					       if ( 
					    		   h != 0 
					    		   && 
					    		   j+(int)((h-1)*patt_step-rgb*((patt_step-btw_eyes)/255.0))<width 
					    		   && 
					    		   j+(int)((h-1)*patt_step-rgb*((patt_step-btw_eyes)/255.0)) > 0  
					    	)
					          secondImagePixels[j+(int)(h*patt_step) + i*width] = secondImagePixels[j+(int)((h-1)*patt_step-rgb*((patt_step-btw_eyes)/255.0)) + width*i];
				   }
			    }
			}
		
		File resultFile = new File(ROOT_PATH + "\\" + "Stereogram.png");
		ImageIO.write(secondImage, "png", resultFile);
		}catch(IOException e){
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
}
}
