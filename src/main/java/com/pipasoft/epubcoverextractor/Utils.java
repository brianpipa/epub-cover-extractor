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
	
	public static String extractCover(File file, String destinationDir) throws IOException {
		EpubReader reader = new EpubReader();
		ZipFile zf = new ZipFile(file);
		Book book = reader.readEpub(zf);
		Resource coverResource = book.getCoverImage();
		if (coverResource == null) {
			System.err.println("ERROR: Couldn't parse cover for "+file.getAbsolutePath());
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
		extractFile(file.toPath(), coverImageFullHref, Path.of(extractedCoverPath));
		
		System.out.println("wrote cover: "+extractedCoverPath);		
		return extractedCoverPath;		
	}
	
	public static String extractCover(File file) throws IOException {
		return extractCover(file, file.getParent());
	}

	private static String getCoverPath(String opfHref, String coverHref) {		
		if (opfHref.contains("/")) {
			String opfParent = new File(opfHref).getParent();			
			return opfParent+"/"+coverHref;
		} else {
			return coverHref;
		}
	}
	
	public static void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException {
		try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null)) {
			Path fileToExtract = fileSystem.getPath(fileName);
			Files.copy(fileToExtract, outputFile, StandardCopyOption.REPLACE_EXISTING);
		}
	}	
	
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
		
	public static String removeTrailingSeparator(String input) {
		String returnValue = input;
    	if (returnValue.endsWith(File.separator)) {
    		returnValue = returnValue.substring(0, returnValue.length() - 1);
    	}    
    	return returnValue;
	}
}
