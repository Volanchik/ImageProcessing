package by.bsuir.course4.ImageProcessor.Filters;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

public class MedianFilter {
	
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
	
	private int matrixBound = 11;

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
	
	
	/*-------------------------------------------------------------------------------*/
	
	public void process(final String ... path) {
		
		try {
			firstImage = readImage(path[0]);
			firstImagePixels = getImagePixels(firstImage);
			if(path.length >= 2) {
				secondImage = readImage(path[1]);
				if (firstImage.getWidth() != secondImage.getWidth()
						|| firstImage.getHeight() != secondImage.getHeight()) {
					throw new IllegalArgumentException("Check the size of images.");
				}
				secondImagePixels = getImagePixels(secondImage);
			}
			width = firstImage.getWidth();
			height = firstImage.getHeight();
			
			int pixel = 0;
			
			int aggregationBlue = 0;
			int aggregationGreen = 0;
			int aggregationRed = 0;
			
			//filter
			for (int i = 0; i < height; ++i){
				for (int j = 0; j < width; ++j) {					
					pixel = getPixelValue(i, j);				
					
					
					for (int mi = -(matrixBound-1)/2; mi <= (matrixBound-1)/2; ++mi){
						for (int mj = -(matrixBound-1)/2; mj <= (matrixBound-1)/2; ++mj) {
							aggregationBlue += getBlueComponent(getPixelValue(i+mi, j+mj));
							aggregationGreen += getGreenComponent(getPixelValue(i+mi, j+mj));
							aggregationRed += getRedComponent(getPixelValue(i+mi, j+mj));					
							
						}
						}
					
					pixel = composePixel(aggregationBlue/(matrixBound*matrixBound), aggregationGreen/(matrixBound*matrixBound), aggregationRed/(matrixBound*matrixBound));
					
					aggregationBlue = 0;
					aggregationGreen = 0;
					aggregationRed = 0;
					firstImagePixels[i*width + j] = pixel;
					
				}
				}


			File resultFile = new File(ROOT_PATH + "\\" + "MedianFilter.png");
			ImageIO.write(firstImage, "png", resultFile);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
}
