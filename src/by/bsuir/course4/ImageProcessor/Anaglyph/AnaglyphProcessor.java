package by.bsuir.course4.ImageProcessor.Anaglyph;

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

public class AnaglyphProcessor {
	
	public static final String ROOT_PATH = "D:\\Work\\Image";

	public static final byte FIRST_BYTE_OFFSET = 0;

	public static final byte SECOND_BYTE_OFFSET = 8;

	public static final byte THIRD_BYTE_OFFSET = 16;

	public static final int FIRST_BYTE_MASK = 0xFF;

	public static final int SECOND_BYTE_MASK = 0xFF << 8;

	public static final int THIRD_BYTE_MASK = 0xFF << 16;

	private BufferedImage firstImage;

	private BufferedImage secondImage;

	private int [] firstImagePixels;

	private int [] secondImagePixels;

	private int width;

	private int height;

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

	public int getPixelValue(final int i, final int j) {
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
			
			

			for(int i = 0; i < firstImagePixels.length; ++i){
				firstImagePixels[i] = calculateRGB(	secondImagePixels[i],
						firstImagePixels[i]);
			}			
			
			
			
			File resultFile = new File(ROOT_PATH + "\\" + "Anaglyph.png");
			ImageIO.write(firstImage, "png", resultFile);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		
	}

	private int calculateRGB(int leftBGR, int rightBGR) {
		return
				(rightBGR & FIRST_BYTE_MASK)
				|
				(rightBGR & SECOND_BYTE_MASK)
				|
				(leftBGR & THIRD_BYTE_MASK)
				;
	}


}
