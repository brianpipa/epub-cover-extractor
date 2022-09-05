package com.pipasoft.epubcoverextractor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

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
	 * 
	 * Extracts the cover from the epub and writes it to the same dir as the epub as
	 * epubname.extension, so if the epub is MyFavoriteBook.epub and the cover 
	 * image is a png, it will write MyFavoriteBook.png
	 * 
	 * if it can't get the cover, it returns null
	 */
	public static String extractCover(File epubFile) throws IOException {
		return extractCover(epubFile, epubFile.getParent());
	}

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
	public static void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException {
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
}
