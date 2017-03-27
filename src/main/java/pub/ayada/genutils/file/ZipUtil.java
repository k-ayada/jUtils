package pub.ayada.genutils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipUtil {

	@SuppressWarnings("unchecked")
	public void unzipArchive(File archive, File outputDir) throws ZipException, IOException {
		ZipFile zipfile = new ZipFile(archive);
		for (Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) zipfile.entries(); e.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			unzipEntry(zipfile, entry, outputDir);
		}
	}

	private void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {
		if (entry.isDirectory()) {
			createDir(new File(outputDir, entry.getName()));
			return;
		}
		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()) {
			createDir(outputFile.getParentFile());
		}
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		try {
			inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
			outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
			
			byte[] buf = new byte[4096];
			
			int n = 0;
		    while (-1 != (n = inputStream.read(buf)))
		    {
		    	outputStream.write(buf, 0, n);
		    }		

		} finally {
			try {
				 if(outputStream != null)
					 outputStream.close(); 				
			}catch (IOException ioe) {}
			try {
				 if(inputStream != null)
					 inputStream.close(); 				
			}catch (IOException ioe) {}
		}
	}

	private void createDir(File dir) {
		if (!dir.mkdirs()) {
			throw new RuntimeException("Can not create dir " + dir);
		}
	}
}
