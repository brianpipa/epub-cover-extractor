# How to use the epub cover extractor
### Notes
- This will only work on non-DRM epubs.
- The input and output directories must already exist.
- If the input or output paths have any spaces in them, you must put the whole thing in quotes.  
- I suggest putting the jar file in a separate/permanent directory (vs leaving it in your Downloads directory).
- you must have java installed and working. Google how to do that if you aren't sure.

### HOWTO
1. Download the latest jar file from https://github.com/brianpipa/epub-cover-extractor/releases and optionally put it in a more permanent location
2. open a command prompt (cmd for Windows) and cd to the directory the jar is in `cd PATH_TO_JAR_DIRECTORY`
3. once in the correct directory, type `java -jar epub-cover-extractor.jar` and you will see instructions like:
```
Missing required option: i
usage:
 -i,--input <arg>    epub dir
 -ng,--nogray        Don't convert images to grayscale
 -nr,--noresize      Don't resize images
 -o,--output <arg>   output image dir
 -u,--unmodified     Don't modify images
 ```
 The only thing you MUST give it is the input directory, the directory where the epubs are. If you do that and nothing else, it will resize and grayscale the covers and put them in the same directory the epubs are in.  
 Example:  `java -jar epub-cover-extractor.jar -i "C:\my epubs\" 
 This will process all epub files in "C:\my epubs\" and it will put the covers in "C:\my epubs\"
 
 Other options:  
 **-o** if you want the images to go to a different directory, specify it here. Again, if the directory path has any spaces in it, put the path in quotes.  
 Example:  `java -jar epub-cover-extractor.jar -i "C:\my epubs\" -o "C:\my epub screens"  
 This will process all epub files in "C:\my epubs\" and it will put the covers in "C:\my epub screens"  
 
 The other options should be self-explanatory (hopefully)
 
