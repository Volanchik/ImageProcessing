package by.bsuir.course4.ImageProcessor;

import by.bsuir.course4.ImageProcessor.imageCreate.Stereogram;

public class EntryPoint {
	public static void main(String[] args) {
	
			String firstImage = "cherry-left.png";
			String secondImage = "cherry-right.png";
			String image1 = "ocean.png";
			String image2 = "fish.png";
			String image3 = "images.png";
			String image4 = "Woody.png";
			String image5 = "dot.png";
			String image6 = "logo.png";
<<<<<<< HEAD
			String image7 = "violine.png";
=======
			String image7 = "cats100100.png";
>>>>>>> 27a4b3c350f5c7cdcd3960fd4e414eb05a4d75a2
			
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
<<<<<<< HEAD
			Stereogram stereo = new Stereogram(689, 1100,100);
			stereo.process(image7);
=======
			Stereogram stereo = new Stereogram(70);
			stereo.process(image6,image7);


>>>>>>> 27a4b3c350f5c7cdcd3960fd4e414eb05a4d75a2
			
	}

}
