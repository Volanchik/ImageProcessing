package by.bsuir.course4.ImageProcessor.Rotation;

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

public class Scaling {
	
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

	private int resultWidth;

	private int resultHeight;

	private int [] resultingImagePixels;
	
	public static final byte FOURTH_BYTE_OFFSET = 24;
	
	public static final int FOURTH_BYTE_MASK = 0xFF << 24;
	
	private float scale;

	private int resultingWidth;

	private int resultingHeight;

	
	public Scaling(float input) {
		scale = input;
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
	
	private int getAlphaComponent(final int pixel) {
		return (pixel & FOURTH_BYTE_MASK)
				>> FOURTH_BYTE_OFFSET;
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
	
	private int composePixel(
			final int blueComponent,
			final int greenComponent,
			final int redComponent,
			final int alphaComponent
			) {
		return 	composePixel(blueComponent, greenComponent, redComponent)
				|
				(alphaComponent & FOURTH_BYTE_MASK)
					<< FOURTH_BYTE_OFFSET
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
			
		int rI;
		int rJ;
		float xDiff;
		float yDiff;
		int pixelA;
		int pixelB;
		int pixelC;
		int pixelD;
		resultingWidth = (int) (width * scale);
		resultingHeight = (int) (height * scale);
		firstImage = new BufferedImage(
				resultingWidth,
				resultingHeight,
				firstImage.getType());
		resultingImagePixels = getImagePixels(firstImage);
		for (int i = 0; i < resultingHeight; ++i) {
			for (int j = 0; j < resultingWidth; ++j) {
				rI = (int) (i / scale);
				rJ = (int) (j / scale);
				yDiff = i /scale - rI;
				xDiff = j /scale - rJ;
				pixelA = getPixelValue(rI, rJ);
				pixelB = getPixelValue(rI, rJ + 1);
				pixelC = getPixelValue(rI + 1, rJ);
				pixelD = getPixelValue(rI + 1, rJ + 1);
				setResultPixelValue(
						i,
						j,
						calculateResultingValue(
								pixelA,
								pixelB,
								pixelC,
								pixelD,
								xDiff,
								yDiff));
			}
		}
		File resultFile = new File(ROOT_PATH + "\\" + "Scaling.png");
		ImageIO.write(firstImage, "png", resultFile);
		
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private int calculateResultingValue(int pixelA, int pixelB, int pixelC,
			int pixelD, float xDiff, float yDiff) {
		int blueComponent = calculateComponentValue(
				getBlueComponent(pixelA),
				getBlueComponent(pixelB),
				getBlueComponent(pixelC),
				getBlueComponent(pixelD),
				xDiff,
				yDiff
				);
		int greenComponent = calculateComponentValue(
				getGreenComponent(pixelA),
				getGreenComponent(pixelB),
				getGreenComponent(pixelC),
				getGreenComponent(pixelD),
				xDiff,
				yDiff
				);
		int redComponent = calculateComponentValue(
				getRedComponent(pixelA),
				getRedComponent(pixelB),
				getRedComponent(pixelC),
				getRedComponent(pixelD),
				xDiff,
				yDiff
				);
		return composePixel(blueComponent, greenComponent, redComponent);
	}

	private int calculateComponentValue(
			final int componentA,
			final int componentB,
			final int componentC,
			final int componentD,
			final float h,
			final float w
			) {
		return (int) (
				componentA * (1 - w) * (1 - h)
				+ componentB * w * (1 - h)
				+ componentC * h * (1 - w)
				+ componentD * w * h
				);
	}

	protected void setResultPixelValue(final int i, final int j, final int value) {
		resultingImagePixels[i*resultingWidth + j] = value;
	}


}
