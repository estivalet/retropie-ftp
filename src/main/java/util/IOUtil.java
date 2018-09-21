/*
 * General Tools - Digital Howler Entertainment
 * Copyright (C) 2008 L.F.Estivalet <luizfernando_estivalet@yahoo.com>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *
 */

/*
 * Created on 03/08/2009 at 10:35:59 by 88758559000
 */
package util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author 88758559000
 * 
 */
public class IOUtil {

	private static final Logger LOGGER = Logger.getLogger(IOUtil.class.getName());

	/**
	 * Reads the stream fully, and returns a byte array of data.
	 * 
	 * @param stream
	 *            Stream to read.
	 * @return Byte array
	 */
	public static String readFully(final InputStream stream) {
		if (stream == null) {
			LOGGER.log(Level.WARNING, "Cannot read null input stream", new IOException("Cannot read null input stream"));
			return "";
		}

		final StringBuilder out = new StringBuilder();

		try {
			final char[] buffer = new char[0x10000];
			final Reader reader = new InputStreamReader(stream, "UTF-8");
			int read;
			do {
				read = reader.read(buffer, 0, buffer.length);
				if (read > 0) {
					out.append(buffer, 0, read);
				}
			} while (read >= 0);
		} catch (final UnsupportedEncodingException e) {
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		} catch (final IOException e) {
			LOGGER.log(Level.WARNING, "Could not read stream", e);
		}

		return out.toString();
	}

	public static boolean createDir(String dir) {
		return createDir(dir, false);
	}

	public static boolean createDir(String dir, boolean deleteContents) {
		File f = new File(dir);
		if (deleteContents) {
			deleteDir(f);
		}
		return f.mkdirs();
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static void write(String fullPath, String contents) {
		// Extract base path and file name.
		String basePath = fullPath.substring(0, fullPath.lastIndexOf("/"));
		String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
		write(basePath, fileName, contents);
	}

	public static void write(String basePath, String fileName, String contents) {
		BufferedWriter bw = null;
		try {
			createDir(basePath);
			File f = new File(basePath + "/" + fileName);
			f.createNewFile();
			bw = new BufferedWriter(new FileWriter(basePath + "/" + fileName));
			bw.write(contents);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * This function will copy files or directories from one location to another. note that the source and the destination must be mutually exclusive. This function can not be used to copy a directory to a sub directory of itself. The function will also have problems if the destination files already exist.
	 * 
	 * Got from http://www.dreamincode.net/code/snippet1443.htm
	 * 
	 * @param src
	 *            A File object that represents the source for the copy
	 * @param dest
	 *            A File object that represents the destination for the copy.
	 * @throws IOException
	 *             if unable to copy.
	 */
	public static void copyFiles(File src, File dest, boolean hiddenFiles) throws IOException {
		// Check to ensure that the source is valid...
		if (!src.exists()) {
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath() + ".");
		} else if (!src.canRead()) { // check to ensure we have rights to the
			// source...
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath() + ".");
		}
		// is this a directory copy?
		if (src.isDirectory()) {
			if (!src.isHidden() && !hiddenFiles) {
				if (!dest.exists()) { // does the destination already exist?
					// if not we need to make it exist if possible (note this is
					// mkdirs not mkdir)
					if (!dest.mkdirs()) {
						throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
					}
				}
				// get a listing of files...
				String list[] = src.list();
				// copy all the files in the list.
				for (int i = 0; i < list.length; i++) {
					File dest1 = new File(dest, list[i]);
					File src1 = new File(src, list[i]);
					copyFiles(src1, dest1, hiddenFiles);
				}
			}
		} else {
			// This was not a directory, so lets just copy the file
			FileInputStream fin = null;
			FileOutputStream fout = null;
			// Buffer 4K at a time (you can change this).
			byte[] buffer = new byte[4096];
			int bytesRead;
			try {
				// open the files for input and output
				fin = new FileInputStream(src);
				fout = new FileOutputStream(dest);
				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) { // Error copying file...
				IOException wrapper = new IOException("copyFiles: Unable to copy file: " + src.getAbsolutePath() + "to" + dest.getAbsolutePath() + ".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} finally { // Ensure that the files are closed (if they were
				// open).
				if (fin != null) {
					fin.close();
				}
				if (fout != null) {
					fout.close();
				}
			}
		}
	}

	public static void write(byte[] aInput, String aOutputFileName) {
		try {
			OutputStream output = null;
			try {
				output = new BufferedOutputStream(new FileOutputStream(aOutputFileName));
				output.write(aInput);
			} finally {
				output.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void write(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Fetch the entire contents of a text file, and return it in a String. This style of implementation does not throw Exceptions to the caller.
	 * 
	 * @param aFile
	 *            is a file which already exists and can be read.
	 */
	public static String getContents(File aFile) {
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line MINUS the newline. it returns null only for the END of the stream. it returns an empty String if two newlines appear in a row.
				 */
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return contents.toString();
	}

	public static ArrayList<String> getContentsAsArray(File aFile) {
		ArrayList<String> content = new ArrayList<String>();

		try {
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					content.add(line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return content;
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate until the BufferedReader return null which means there's no more data to read. Each line will appended to a StringBuilder and returned as String.
		 */
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to " + dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}

	public static void compareFolders(File src, File dest) throws Exception {
		List<String> fsrc = Arrays.asList(src.list());
		List<String> fdes = Arrays.asList(dest.list());

		for (String file : fsrc) {
			if (!fdes.contains(file)) {
				System.out.println(file);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		IOUtil.compareFolders(new File("C:\\attract-v2.2.0-1-win64\\menu-art (motion-plus)\\snap"), new File("C:\\attract-v2.2.0-1-win64\\menu-art (video 2)"));
	}
}
