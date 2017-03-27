package pub.ayada.genutils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.stream.Stream;



public class FileUtils {

	public static BufferedWriter getBufferedWriter(String path) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(path);
		OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, "utf-8");
		return new BufferedWriter(out);
	}

	public static String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	public static boolean deleteDirWithFiles(File dir, int maxDepth) {
		File[] entries = dir.listFiles();
		if (entries == null)
			return false;
		Stream.of(entries).filter(File::isDirectory).forEach(f -> {
			if (maxDepth < 1) {
				throw new AssertionError("Contains directory " + f);
			} else {
				deleteDirWithFiles(f, maxDepth - 1);
			}
		});
		Stream.of(entries).forEach(f -> {
			try {
				Files.delete(f.toPath());
			} catch (IOException e) {
			}
		});
		if (dir.listFiles() == null)
			return dir.delete();
		return false;
	}

	public static ArrayList<File> getFiles(String InputFilePath) throws Exception {
		ArrayList<File> fileList = new ArrayList<File>();
		File dir = null;
		String filePattern = null;
		InputFilePath.trim();
		// Handle unix\URI style path
		if (InputFilePath.lastIndexOf('/') > 0) {
			dir = new File((InputFilePath.substring(0, InputFilePath.lastIndexOf('/') + 1) == "" ? "."
					: InputFilePath.substring(0, InputFilePath.lastIndexOf('/') + 1) + "\\"));
			filePattern = (InputFilePath.substring(InputFilePath.lastIndexOf('/') + 1));
		}
		// handle windows style path
		else if (InputFilePath.lastIndexOf('\\') > 0) {
			dir = new File(InputFilePath.substring(0, InputFilePath.lastIndexOf('\\') + 1));
			filePattern = (InputFilePath.substring(InputFilePath.lastIndexOf('\\') + 1));
		}
		// User has asked us to use current working directory
		else {
			dir = new File(".");
			filePattern = InputFilePath;
		}
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);

		if (!dir.isDirectory())
			throw new Exception(
					"Not a directory : \"" + dir.getAbsolutePath() + "\". Build from the pattern : " + InputFilePath);

		File[] InputfileObj = dir.listFiles();

		if (!dir.exists() || InputfileObj == null)
			return null;
		for (int i = 0; i < InputfileObj.length; i++)
			if (matcher.matches(Paths.get(InputfileObj[i].getName()))) {
				fileList.add(InputfileObj[i]);
			}

		return fileList;
	}

	public static File findFileOnClassPath(String name) {
		String classpath = System.getProperty("java.class.path");
		String pathSeparator = System.getProperty("path.separator");

		StringTokenizer tokenizer = new StringTokenizer(classpath, pathSeparator);
		while (tokenizer.hasMoreTokens()) {
			String pathElement = tokenizer.nextToken();

			File directoryOrJar = new File(pathElement);
			File absoluteDirectoryOrJar = directoryOrJar.getAbsoluteFile();
			if (absoluteDirectoryOrJar.isFile()) {
				File target = new File(absoluteDirectoryOrJar.getParent(), name);
				if (target.exists()) {
					return target;
				}
			} else {
				File target = new File(directoryOrJar, name);
				if (target.exists()) {
					return target;
				}
			}
		}
		return null;
	}
	
	public static boolean checkIfExists(String path) {		
		return new File(path).exists();
	}

	
	public static void delFileIfExists(String path) throws IOException {
		File f = new File(path); 
		if(f.exists()) {
			delFile(f);
		}
		
	}
	
	public static boolean delFile(File file) throws IOException {
		try {
			file.delete();	
			return true;
		} catch (Exception e) {
			throw  new IOException("Failed to delete the file:" + file.getAbsoluteFile(),e);
		}
	}
	public static void createEmpty(String paramFile) throws IOException {
		createEmpty( new File(paramFile));
	}
	public static void createEmpty(File paramFile) throws IOException {
		RandomAccessFile localRandomAccessFile = null;
		try {
			localRandomAccessFile = new RandomAccessFile(paramFile, "rws");
			localRandomAccessFile.setLength(0L);
			try {
				if (localRandomAccessFile != null)
					localRandomAccessFile.close();
			} catch (IOException localIOException1) {
				localIOException1.printStackTrace();
			}
		} finally {
			try {
				if (localRandomAccessFile != null)
					localRandomAccessFile.close();
			} catch (IOException localIOException2) {
				localIOException2.printStackTrace();
			}
		}
	}


}
