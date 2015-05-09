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


public class Rotation {

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
		
		private static final int TRANSPARENT_PIXEL = 0xFF << 24;

		private int angle;

		private int resultWidth;

		private int resultHeight;

		private int sourceCenterX;

		private int sourceCenterY;

		private int resultCenterX;

		private int resultCenterY;

		private float sinA;

		private float cosA;

		private float minusSinA;

		private float minusCosA;

		private int [] resultingImagePixels;
		
		public static final byte FOURTH_BYTE_OFFSET = 24;
		
		public static final int FOURTH_BYTE_MASK = 0xFF << 24;
		
		

		
		public Rotation(int input_angle) {
			angle = input_angle;
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
				
			double rad = Math.toRadians(angle % 360);
			
			sinA = (float) Math.sin(rad);
			cosA = (float) Math.cos(rad);
			
			minusSinA = - sinA;
			minusCosA = cosA;
			
			setResultingSize();
			int sourceX;
			int sourceY;
			int pixelA;
			int pixelB;
			int pixelC;
			int pixelD;
			float sourceXDiff;
			float sourceYDiff;
			for (int y = resultCenterY; y > resultCenterY - resultHeight; --y) {
				for (int x = -resultCenterX; x < -resultCenterX + resultWidth; ++x) {
					sourceX = (int) getX(x, y, minusSinA, minusCosA);
					sourceY = (int) getY(x, y, minusSinA, minusCosA);
					sourceXDiff = Math.abs(getX(x, y, minusSinA, minusCosA) - sourceX);
					sourceYDiff = Math.abs(getY(x, y, minusSinA, minusCosA) - sourceY);
					pixelA = getSourcePixelValueByCoordinates(sourceX, sourceY);
					pixelB = getSourcePixelValueByCoordinates(sourceX + 1, sourceY);
					pixelC = getSourcePixelValueByCoordinates(sourceX, sourceY - 1);
					pixelD = getSourcePixelValueByCoordinates(sourceX + 1, sourceY - 1);
					setResultPixelValueByCoordinates(
							x,
							y,
							calculateResultingValue(
									pixelA,
									pixelB,
									pixelC,
									pixelD,
									sourceXDiff,
									sourceYDiff));
				}
			}
			
			for (int i=0 ; i< Math.max(width, height)*Math.max(width, height)*4; i++){
				if(getAlphaComponent(resultingImagePixels[i])==0 
						& getBlueComponent(resultingImagePixels[i]) ==0
						& getRedComponent(resultingImagePixels[i]) ==0
						&getGreenComponent(resultingImagePixels[i])==0){
				resultingImagePixels[i] = composePixel(255, 255, 255,255);
						}
			}
			
			File resultFile = new File(ROOT_PATH + "\\" + "Rotated.png");
			ImageIO.write(firstImage, "png", resultFile);
			
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}



		private void setResultingSize() {
			sourceCenterX = width / 2;
			sourceCenterY = height / 2;
			resultWidth = (int) Math.max(width, height)*2;
			resultHeight = (int)Math.max(width, height)*2;
			firstImage = new BufferedImage(
					resultWidth,
					resultHeight,
					firstImage.getType());
			resultingImagePixels = getImagePixels(firstImage);
			resultCenterX = resultWidth / 2;
			resultCenterY = resultHeight / 2;
			
			for (int i=0 ; i< Math.max(width, height)*Math.max(width, height)*4; i++){
				resultingImagePixels[i] = composePixel(255, 255, 255,100);
			}
		}

		private int getSourcePixelValueByCoordinates(final int x, final int y) {
			if (x < -  sourceCenterX
					|| x > - sourceCenterX + width
					|| y < sourceCenterY - height
					|| y > sourceCenterY
					) {
				return TRANSPARENT_PIXEL;
			}
			return getSourcePixelValueByCoordinatesStrict(x, y);
		}

		private int getSourcePixelValueByCoordinatesStrict(int x, int y) {
			int j = x + sourceCenterX;
			int i = - ( y - sourceCenterY);
			if (i >= height - 1) {
				i = height - 1;
			}
			if (j >= width - 1) {
				j = width - 1;
			}
			return firstImagePixels[i*width + j];
		}

		private void setResultPixelValueByCoordinates(
				final int x, final int y, final int value) {
			resultingImagePixels[
			                     (- ( y - resultCenterY)) * resultWidth
			                     + (x + resultCenterX)] = value;
		}

		private float getX(
				final int sourceX,
				final int sourceY,
				final float sinA,
				final float cosA) {
			return sourceX * cosA + sourceY * sinA;
		}

		private float getY(
				final int sourceX,
				final int sourceY,
				final float sinA,
				final float cosA) {
			return sourceY * cosA - sourceX * sinA;
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
			int alphaComponent = calculateComponentValue(
					getAlphaComponent(pixelA),
					getAlphaComponent(pixelB),
					getAlphaComponent(pixelC),
					getAlphaComponent(pixelD),
					xDiff,
					yDiff
					);
			return composePixel(
					blueComponent,
					greenComponent,
					redComponent,
					alphaComponent);
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

		
	}
