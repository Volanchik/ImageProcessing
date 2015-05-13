package by.bsuir.course4.ImageProcessor;

import by.bsuir.course4.ImageProcessor.imageCreate.Stereogram;
import by.bsuir.course4.ImageProcessor.imageCreate.mailWindow;

public class EntryPoint {
	public static void main(String[] args) {
	
			String firstImage = "cherry-left.png";
			String secondImage = "cherry-right.png";
			String image1 = "ocean.png";
			String image2 = "fish.png";
			String image3 = "images.png";
			String image4 = "Woody.png";

			
	/*		AnaglyphProcessor anaglyph = new AnaglyphProcessor();			
			anaglyph.process(firstImage, secondImage);
			
			NewFilter newf = new NewFilter();
			newf.process(image1);
			
			MedianFilter medianf = new MedianFilter();
			medianf.process(image1);
			
			AdvancedFilter advf = new AdvancedFilter();
			advf.process(image1);
			
			
			ContrastFilter cntrf = new ContrastFilter();
			cntrf.process(image1);


			Segment segment = new Segment(5);
			segment.process(image4);
			
			
			
			Rotation rotation = new Rotation(10);
			rotation.process(image1);
			
			
			Scaling scaling = new Scaling((float) 0.31);
			scaling.process(image4);
			
			*/
			
			mailWindow window = new mailWindow();
			
			
	}

}
