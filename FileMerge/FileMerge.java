import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;

class FileMerge {

	boolean addNewLinesSet = false;							// Set with -n flag
	boolean deleteOldFilesSet = false;						// Set with -d flag
	boolean needHelpSet = false;							// Set with -h flag

	boolean fileNameSet = false;							// Set with -f flag and args 
	String mergedFileName;

	boolean filesToMergeSet = false;						// Set with -m flag and args
	ArrayList<File> filesToMerge = new ArrayList<File>();

	ArrayList<String> userArgs = new ArrayList<String>();

	public static void main(String[] args) {
		
		try {
			FileMerge merger = new FileMerge(args);
		} catch (ArgumentException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println("Error - an error occurred with reading, writing, opening, or closing files!");
		}

	}

	FileMerge (String[] arguments) throws ArgumentException, IOException {

		for (String s : arguments) {
			userArgs.add(s);
		}

		if (userArgs.size() == 0) {
			throw new ArgumentException("Error - no arguments included! Type 'java FileMerge -h' for help.");
		}

		parseArguments();
		executeArguments();
	}

	void parseArguments() throws ArgumentException, IOException {
		
		while (userArgs.size() > 0) {
			switch (userArgs.get(0)) {
				case "-h":
					if (needHelpSet == true)
						throw new ArgumentException("Error - argument '-h' repeated!");
					needHelpSet = true;
					userArgs.clear();
					break;
				case "-n":
					if (addNewLinesSet == true)
						throw new ArgumentException("Error - argument '-n' is repeated!");
					addNewLinesSet = true;
					userArgs.remove(0);
					break;
				case "-d":
					if (deleteOldFilesSet == true)
						throw new ArgumentException("Error - argument '-d' is repeated!");
					deleteOldFilesSet = true;
					userArgs.remove(0);
					break;
				case "-f":
					if (fileNameSet == true)
						throw new ArgumentException("Error - argument '-f' is repeated!");
					parseFArg();
					break;
				case "-m":
					if (filesToMergeSet == true)
						throw new ArgumentException("Error - argument '-m' is repeated!");
					parseMArg();
					break;
				default:
					throw new ArgumentException("Error - invalid argument: " + userArgs.get(0));
			}
		}
	}

	void parseFArg() throws ArgumentException {

		fileNameSet = true;
		userArgs.remove(0);

		if (userArgs.size() == 0)
			throw new ArgumentException ("Error - no filename specified!");

		String potentialFileName = userArgs.get(0);
		userArgs.remove(0);

		if (potentialFileName.equals("-h") ||
			potentialFileName.equals("-n") ||
			potentialFileName.equals("-d") ||
			potentialFileName.equals("-f") ||
			potentialFileName.equals("-m")) {
				throw new ArgumentException("Error - invalid filename: " + potentialFileName);
		} else {
			mergedFileName = potentialFileName;
		}

		return;
	}

	void parseMArg() throws ArgumentException, IOException {

		filesToMergeSet = true;
		userArgs.remove(0);

		if (userArgs.size() == 0) {
			throw new ArgumentException("Error - no files specified to merge!");
		}

		for (boolean escape = false; escape != true;) {

			String fname = userArgs.get(0);

			if (fname.equals("-h") ||
				fname.equals("-n") ||
				fname.equals("-d") ||
				fname.equals("-f") ||
				fname .equals("-m")) {
					if (filesToMerge.size() == 0) {
						throw new ArgumentException("Error - no files specified to merge!");
					} else if (filesToMerge.size() > 0) {
						escape = true;
					}
			} else {
				File tempFile = new File(userArgs.get(0));

				if (!tempFile.exists()) {
					throw new ArgumentException("Error - specified file does not exist: " + userArgs.get(0));
				} else if (!tempFile.isFile()) {
					throw new ArgumentException("Error - specified file is not a file: " + userArgs.get(0));
				}

				filesToMerge.add(tempFile);
				userArgs.remove(0);
			}

			if (userArgs.size() == 0)
				escape = true;

		}

		return;
	}

	void executeArguments() throws ArgumentException, IOException {

		//System.out.println("Execution should go here!");
		if (needHelpSet == true) {
			displayHelpMessage();
		} else if (fileNameSet == false) {
			throw new ArgumentException("Error - no output file specified!");
		} else if (filesToMergeSet == false) {
			throw new ArgumentException("Error - no files to merge specified!");
		} else {
			executeNonHelpArguments();
		}

		return;
	}

	void displayHelpMessage() {

		System.out.println("FileMerge usage: java FileMerge (-h) (-d) (-n) [-f {output filename}] [-m {names of files to merge}]");
		System.out.println("FileMerge takes the contents of one or more files and merges them into a single output file.");
		System.out.println("'-f': Specifies the name of the output file. Not optional.");
		System.out.println("'-m': Specifies the filenames of the files to merge. More than one filename may be specified. Not optional.");
		System.out.println("'-n': If this flag is set, then newlines are inserted into the output between each input file. Optional.");
		System.out.println("'-d': If this flag is set, then after the files are merged, the old files are deleted. leaving only the new output file. Optional.");
		System.out.println("'-h': If this flag is set, then this help message is displayed and all other arguments are disregarded.");
		System.out.println("FileMerge - written by Kevin Kmetz. Use this program at your own risk - no warranty or guarantee of its operation is provided.");

		return;
	}

	void executeNonHelpArguments() throws IOException {

		File outputFile = new File(mergedFileName);
		outputFile.createNewFile();

		FileOutputStream outputStream = new FileOutputStream(outputFile);

		for (File f : filesToMerge) {
			FileInputStream tempInputStream = new FileInputStream(f);

			for (int data = tempInputStream.read(); data != -1; data = tempInputStream.read()) {
				outputStream.write(data);
			}

			if (addNewLinesSet == true) {
				outputStream.write(13);				// 13 is the decimal ASCII code for carriage return
				outputStream.write(10);				// 10 is the decimal ASCII code for newline
			}

			tempInputStream.close();
		}

		outputStream.close();

		if (deleteOldFilesSet == true) {
			for (int i = 0; i < filesToMerge.size(); i++) {
				filesToMerge.get(i).delete();
			}
		}

		return;
	}

	class ArgumentException extends Exception {

		private String msg;

		ArgumentException(String message) {
			msg = message;
		}

		public String toString() {
			return msg;
		}

	}

}