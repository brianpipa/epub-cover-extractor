package com.pipasoft.epubcoverextractor;

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
				if (cover != null) {
					if (convertToGrayscale) {
						Grayscale.convert(new File(cover));	
					}									
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

        Option unmodifiedImages = new Option("u", "unmodified", false, "Don't modify images");
        options.addOption(unmodifiedImages);
        
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
	}
	
}
