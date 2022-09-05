# epub-cover-extractor
Extracts covers from epubs. The original idea for this was to extract the covers so that they could be used in an ereader's scrensaver. By converting to grayscale and resizing properly, the images would be ready to use.

## Building
This project relies on https://github.com/psiegman/epublib and since that lib isn't currently in the main maven repo, you need to clone it and build (install) it separately. Once you have done that, just do the normal `mvn clean package` to build it.

## Running
Once you have built it, run it like so:  
    >java -jar target/epub-cover-extractor-1.0-jar-with-dependencies.jar   
    Missing required option: i    
    usage:    
    -i,--input <arg>    epub dir    
    -o,--output <arg>   output image dir    
    -u,--unmodified     Don't modify images  
  
You must specifiy the input directory. If you don't specify the output directory, the images will be wriitten in the input directory. The unmodified parameter will make it NOT change the images to grayscale and it won't resize them.
