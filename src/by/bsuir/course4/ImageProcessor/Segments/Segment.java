package by.bsuir.course4.ImageProcessor.Segments;

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

public class Segment {
	
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
	
	private int numOfClusters = 1;
	
	public Segment(int numOfSegments) {
		numOfClusters = numOfSegments;
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
			 int blueComponent,
			 int greenComponent,
			 int redComponent
			) {
		if(blueComponent >255) {
			blueComponent=255;
		}
		if(greenComponent >255) {
			greenComponent=255;
		}
		if(redComponent >255) {
			redComponent=255;
		}
		if(blueComponent < 0){
			blueComponent=0;
		}
		if(greenComponent < 0){
			greenComponent=0;
		}
		if(redComponent < 0){
			redComponent=0;
		}
		
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
			secondImage = readImage(path[0]);
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
			width = firstImage.getWidth();
			height = firstImage.getHeight();
			
			Cluster[] clusters = new Cluster[numOfClusters];
			Cluster cluster;
			int indexCluster=0;
			int pixel;
			
			//set cluster centers
			
			int index = (int)(firstImagePixels.length / numOfClusters);
			
			for(int i = index-1; i < firstImagePixels.length; i += index){
				cluster = new Cluster(i, firstImagePixels.length);
				setRGBToClusterCenter(cluster);				
				
				clusters[indexCluster] = cluster;
				indexCluster++;				
			}
			
			////////////////////////////////////////////////////////////////////////
			int red=0,green=0,blue=0;
			int diff=-1;
			
			///null pixels in clusters
			
			//pixels connect to clusters
			for(int i = 0; i < firstImagePixels.length; ++i){
				red = getRedComponent(firstImagePixels[i]);
				green = getGreenComponent(firstImagePixels[i]);
				blue = getBlueComponent(firstImagePixels[i]);
				indexCluster=-1;
				diff=-1;
				
				for(int j = 0; j < numOfClusters; ++j){
					if((diff > (int)(Math.abs(red-clusters[j].getRed())+ Math.abs(green-clusters[j].getGreen())+Math.abs(blue-clusters[j].getBlue()))) || diff==-1){
						diff =  (int)(Math.abs(red-clusters[j].getRed())+ Math.abs(green-clusters[j].getGreen())+Math.abs(blue-clusters[j].getBlue()));
						indexCluster = j;
					}
				}
				
				clusters[indexCluster].addPixel(firstImagePixels[i]);
			}
			//recount cluster center
			for(int j = 0; j < numOfClusters; ++j){
				for(int i = 0; i < clusters[j].pixelsLength(); ++i){
					pixel = clusters[j].getPixel(i);
					red += getRedComponent(pixel);
					green += getGreenComponent(pixel);
					blue += getBlueComponent(pixel);					
				}
				red = clusters[j].pixelsLength()>0? Math.round(red/clusters[j].pixelsLength()):0;
				green = clusters[j].pixelsLength()>0? Math.round(green/clusters[j].pixelsLength()):0;
				blue = clusters[j].pixelsLength()>0? Math.round(blue/clusters[j].pixelsLength()):0;
				clusters[j].newCenter(composePixel(blue, green, red));
				clusters[j].clearPixels();
				
				setRGBToClusterCenter(clusters[j]);	
			}
			//////////////////////////////////////////////////////////////////////////////////////
			
			for(int i = 0; i < secondImagePixels.length; ++i){
				red = getRedComponent(firstImagePixels[i]);
				green = getGreenComponent(firstImagePixels[i]);
				blue = getBlueComponent(firstImagePixels[i]);
				indexCluster=-1;
				diff=-1;
				for(int j = 0; j < numOfClusters; ++j){
					if((diff > (int)(Math.abs(red-clusters[j].getRed())+ Math.abs(green-clusters[j].getGreen())+Math.abs(blue-clusters[j].getBlue()))) || diff==-1){
						diff =  (int)(Math.abs(red-clusters[j].getRed())+ Math.abs(green-clusters[j].getGreen())+Math.abs(blue-clusters[j].getBlue()));
						indexCluster = j;
					}
				}
				secondImagePixels[i] = clusters[indexCluster].getCurrCenter();
				}

			
			
			File resultFile = new File(ROOT_PATH + "\\" + "SegmentedPicture.png");
			ImageIO.write(secondImage, "png", resultFile);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	private void setRGBToClusterCenter(Cluster cluster){
		cluster.setRed(getRedComponent(cluster.getCurrCenter()));
		cluster.setBlue(getBlueComponent(cluster.getCurrCenter()));
		cluster.setGreen(getGreenComponent(cluster.getCurrCenter()));
	}

}
