package com.pipasoft.epubcoverextractor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

//grabbed from https://dyclassroom.com/image-processing-project/how-to-convert-a-color-image-into-grayscale-image-in-java
public class Grayscale{
  public static void convert(File colorFile)throws IOException{
    BufferedImage img = null;

    //read image
    try{      
      img = ImageIO.read(colorFile);
    }catch(IOException e){
      System.out.println(e);
    }

    //get image width and height
    int width = img.getWidth();
    int height = img.getHeight();

    //convert to grayscale
    for(int y = 0; y < height; y++){
      for(int x = 0; x < width; x++){
        int p = img.getRGB(x,y);

        int a = (p>>24)&0xff;
        int r = (p>>16)&0xff;
        int g = (p>>8)&0xff;
        int b = p&0xff;

        //calculate average
        int avg = (r+g+b)/3;

        //replace RGB value with avg
        p = (a<<24) | (avg<<16) | (avg<<8) | avg;

        img.setRGB(x, y, p);
      }
    }

    //write image
    try{
      File outfile = colorFile;
      String extension = FilenameUtils.getExtension(colorFile.getName());
      ImageIO.write(img, extension, outfile);
    }catch(IOException e){
      System.out.println(e);
    }
  }
}