package com.pipasoft.epubcoverextractor;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import net.sf.jazzlib.ZipFile;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class Utils {
	
	/**
	 * Extracts the cover from the epub and writes it to destinationDir as
	 * epubname.extension, so if the epub is MyFavoriteBook.epub and the cover 
	 * image is a png, it will write
	 * MyFavoriteBook.png
	 * 
	 * if it can't get the cover, it returns null
	 * 
	 * @param epubFile
	 * @param destinationDir
	 * @return path to the cover or null
	 * @throws IOException
	 */
	public static String extractCover(File epubFile, String destinationDir) throws IOException {
		EpubReader reader = new EpubReader();
		ZipFile zf = new ZipFile(epubFile);
		Book book = reader.readEpub(zf);
		Resource coverResource = book.getCoverImage();
		if (coverResource == null) {
			System.err.println("ERROR: Couldn't parse cover for "+epubFile.getAbsolutePath());
			return null;
		}
		String coverHref = coverResource.getHref();
		
		
		String opfHref = book.getOpfResource().getHref();
		
		//get epubname
		String epubname = zf.getName();		
		String[] split = epubname.split("/");
		epubname = split[split.length-1];
		epubname = epubname.replace(".epub", "");

		//get extension	
		String extension = FilenameUtils.getExtension(coverHref);
		if (extension.equals("jpeg")) {
			extension = "jpg";
		}
		
		String coverImageFullHref = getCoverPath(opfHref, coverHref);
		
		String extractedCoverPath = destinationDir+"/"+epubname+"."+extension;
		extractFile(epubFile.toPath(), coverImageFullHref, Path.of(extractedCoverPath));
		
		System.out.println("wrote cover: "+extractedCoverPath);		
		return extractedCoverPath;		
	}	

	/**
	 * gets full path to cover
	 * 
	 * @param opfHref the path to the opf
	 * @param coverHrefthe path to the cover
	 * @return teh full path
	 */
	private static String getCoverPath(String opfHref, String coverHref) {		
		if (opfHref.contains("/")) {
			String opfParent = new File(opfHref).getParent();			
			return opfParent+"/"+coverHref;
		} else {
			return coverHref;
		}
	}
	
	/**
	 * Extracts a file from a zip (epubs are zips) and writes it to outputFile
	 * 
	 * @param zipFile
	 * @param fileName
	 * @param outputFile
	 * @throws IOException
	 */
	private static void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException {
		try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null)) {
			Path fileToExtract = fileSystem.getPath(fileName);
			Files.copy(fileToExtract, outputFile, StandardCopyOption.REPLACE_EXISTING);
		}
	}	
	
	/**
	 * returns an array of File objects of the epubs in the path
	 * 
	 * @param path the path to examine
	 * @return a File[]
	 */
	public static File[] listEpubs(String path) {
		try {
            File f = new File(path);

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    // We want to find only .c files
                    return name.endsWith(".epub");
                }
            };
            File[] fileList = f.listFiles(filter);
            Arrays.sort(fileList);
            return fileList;

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }		
		return null;
	}
		
	/**
	 * if the input String ends in File.separator, it removes it
	 * 
	 * @param input the String to examine
	 * @return the String with the trailing File.separataor removed
	 */
	public static String removeTrailingSeparator(String input) {
		String returnValue = input;
    	if (returnValue.endsWith(File.separator)) {
    		returnValue = returnValue.substring(0, returnValue.length() - 1);
    	}    
    	return returnValue;
	}

	/**
	 * manipulates the image (resize/gray)
	 * 
	 * @param cover the path to the image
	 * @param resize true to resize it
	 * @param convertToGrayscale true to convert to grayscale
	 * @throws IOException
	 */
	public static void modifyImage(String cover, boolean resize, boolean convertToGrayscale) throws IOException {
		//load image
	    BufferedImage img = null;
	    File coverFile = new File(cover);

	    //read image
	    try{      
	      img = ImageIO.read(coverFile);
	    }catch(IOException e){
	      System.err.println(e);
	      return;
	    }
	    if (img != null) {	    	
	    	if (resize) {
	    		img = resizeImage(img, Application.width,  Application.height);	    					
	    	}
	    	if (convertToGrayscale) {
	    		img = Grayscale.convert(img);
	    	}
	    }	     
	    writeImage(img, coverFile);			    
	}
	
	/**
	 * writes a BufferdImage to disk
	 * 
	 * @param img BufferImage to write
	 * @param ImagePath path to write image to
	 */
	private static void writeImage(BufferedImage img, File ImagePath) {
	    try{
	        String extension = FilenameUtils.getExtension(ImagePath.getName());
	        ImageIO.write(img, extension, ImagePath);
	      }catch(IOException e){
	        System.out.println(e);
	      }		
	}
	
	/**
	 * resizes a BufferedImage
	 * 
	 * @param originalImage the original bufferedImage
	 * @param targetWidth the width to resize to
	 * @param targetHeight the height to resize to
	 * @return the resized BufferedImage
	 * @throws IOException
	 */
	private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
	    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
	    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
	    return outputImage;
	}

	/**
	 * given a size like 600x800, splits it into the 2 pieces and returns a Point object
	 * 
	 * @param dimensions
	 * @return
	 */
	public static Point parseDimensions(String dimensions) {
		dimensions = dimensions.toLowerCase();
		Point point = null;
		boolean hasError = false;
		String[] split = dimensions.split("x");
		if (split.length == 2) {
			try {
				int width = Integer.parseInt(split[0]);
				int height = Integer.parseInt(split[1]);
				point = new Point(width, height);				
			} catch (NumberFormatException nfe) {
				hasError = true;
			}
		} else {
			hasError = true;
		}
		if (hasError) {
			System.err.println("ERROR: invalid dimensions: "+dimensions);
			System.err.println("       format is widthXheight, e.g. 600x800");
		}
		return point;
	}	
}
