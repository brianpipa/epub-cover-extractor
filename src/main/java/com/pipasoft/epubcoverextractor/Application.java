package com.pipasoft.epubcoverextractor;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class Application {

	static String inputDir = ".";
	static String outputDir;
	static boolean convertToGrayscale = true;
	static boolean resize = true;
	static int width = 600;
	static int height = 800;
	
	/**
	 * main entry to the program
	 * 
	 * @param args commandline args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		parseOptions(args);
		
		if (!inputDir.equals(outputDir)) {
			Path outPath = Path.of(outputDir);
			if (!Files.isWritable(outPath)) {
				System.err.println("ERROR: output dir either doesn't exist or is not writable. Please fix, then try again");
				System.exit(1);
			}
		}
		
		File[] epubList = Utils.listEpubs(inputDir);		
		if (epubList != null) {
			System.out.println("Found epubs: "+epubList.length);
			System.out.println("-------------");
			for (File file : epubList) {
				String cover = Utils.extractCover(file, outputDir);
				if (cover != null && (resize || convertToGrayscale)) {
					Utils.modifyImage(cover, resize, convertToGrayscale);
				}
			}
		}

		System.out.println();
		System.out.println("Exited normally");
	}

	/**
	 * parses command line options
	 * 
	 * @param args commandline args
	 */
	private static void parseOptions(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "epub dir");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output image dir");
        output.setRequired(false);
        options.addOption(output);

        Option dims = new Option("s", "size", true, "dimensions to resize in widthXheight format. Defaults to 600X800 if not specified");
        dims.setRequired(false);
        options.addOption(dims);        
        
        Option unmodifiedImages = new Option("u", "unmodified", false, "Don't modify images");
        options.addOption(unmodifiedImages);
        
        Option noResize = new Option("nr", "noresize", false, "Don't resize images");
        options.addOption(noResize);
        
        Option noGray = new Option("ng", "nogray", false, "Don't convert images to grayscale");
        options.addOption(noGray);
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp(" ", options);
            System.exit(1);
        }

        if (cmd.hasOption("input")) {
        	inputDir =  Utils.removeTrailingSeparator(cmd.getOptionValue("input"));        	
        	outputDir = inputDir;        	
        }
        
        if (cmd.hasOption("output")) {
        	outputDir =  Utils.removeTrailingSeparator(cmd.getOptionValue("output"));
        }
        
        if (cmd.hasOption("unmodified")) {
        	convertToGrayscale = false;
        	resize = false;
        }
        if (cmd.hasOption("noresize")) {
        	resize = false;
        }
        if (cmd.hasOption("nogray")) {
        	convertToGrayscale = false;
        }
        if (cmd.hasOption("size")) {
        	Point size = Utils.parseDimensions(cmd.getOptionValue("size"));
        	if (size != null) {
        		width = (int) size.getX();
        		height = (int) size.getY();
        	} else {
        		System.err.println("Exiting with errors");
        		System.exit(1);
        	}
        }                
        
	}
	
}
