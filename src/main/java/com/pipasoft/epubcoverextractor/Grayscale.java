package com.pipasoft.epubcoverextractor;

import java.awt.image.BufferedImage;
import java.io.IOException;

//borrowed from https://dyclassroom.com/image-processing-project/how-to-convert-a-color-image-into-grayscale-image-in-java
public class Grayscale{
  public static BufferedImage convert(BufferedImage img) throws IOException{

	BufferedImage grayImg = img; 
			
    //get image width and height
    int width = grayImg.getWidth();
    int height = grayImg.getHeight();

    //convert to grayscale
    for(int y = 0; y < height; y++){
      for(int x = 0; x < width; x++){
        int p = grayImg.getRGB(x,y);

        int a = (p>>24)&0xff;
        int r = (p>>16)&0xff;
        int g = (p>>8)&0xff;
        int b = p&0xff;

        //calculate average
        int avg = (r+g+b)/3;

        //replace RGB value with avg
        p = (a<<24) | (avg<<16) | (avg<<8) | avg;

        grayImg.setRGB(x, y, p);
      }
    }
    return grayImg;
  }
}