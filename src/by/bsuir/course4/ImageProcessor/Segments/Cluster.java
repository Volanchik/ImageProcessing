package by.bsuir.course4.ImageProcessor.Segments;

public class Cluster {
	
	private int lastCenter;
	private int currCenter;
	private int red,green,blue;
	
	private int[] pixels;
	private int index;
	
	public Cluster(int center, int pixelsSize) {
		lastCenter = 0;
		currCenter = center;
		index = 0;
		pixels = new int[pixelsSize+1];
	}
	
	public boolean areCentersEqual(){
		return lastCenter==currCenter;
	}
	
	public void addPixel(int pixel){
		pixels[index] = pixel;
		index++;
	}
	
	public int getPixel(int i){
		return pixels[i];
	}
	
	public int pixelsLength(){
		return index;
	}

	public void newCenter(int pixel){
		lastCenter = currCenter;
		currCenter = pixel;
		red=0;green=0;blue=0;
	}
	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getLastCenter() {
		return lastCenter;
	}

	public void setLastCenter(int lastCenter) {
		this.lastCenter = lastCenter;
	}

	public int getCurrCenter() {
		return currCenter;
	}

	public void setCurrCenter(int currCenter) {
		this.currCenter = currCenter;
	}
	
	public void clearPixels(){
		int pixelsSize = pixels.length;
		pixels = new int[pixelsSize+1];
		index = 0;
	}
	
	
	
	

}
