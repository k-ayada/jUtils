package pub.ayada.genutils.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;

import pub.ayada.dataStructures.chararray.CharArr;

public class DirectByteBuffFileWriter {

	private String FilePath;
	private String Eol;
	private Charset CharSet;
	
	private RandomAccessFile RAFileObj;
	private FileChannel FileChnlObj;
	private long FileLength = 0L;
	private FileLock fileLock = null;  
	public DirectByteBuffFileWriter() {
	}
    /**
	 * <b>Constructor</b> - Creates a file handler for write-only purposes.
	 * <BR>
	 * The buffer size is calculated as 10% of the available free java heap
	 * memory at the time of this object initialization.
	 * <br>
     * @param filePath
     * @param mode
     * @throws SecurityException
     * @throws IOException
     */
	public DirectByteBuffFileWriter(String filePath, String mode) throws SecurityException, IOException {
		this.FilePath = filePath;		
		this.CharSet =  Charset.defaultCharset();
		this.Eol = System.getProperty("line.separator");	
		constructor_generic(mode.toLowerCase());
		
	}
	/**
	 * <b>Constructor</b> - Creates a file handler for write-only purposes.
	 * @param filePath
	 * @param mode
	 * @param buferSize
	 * @param charSet
	 * @param eol
	 * @throws SecurityException
	 * @throws IOException
	 */
	public DirectByteBuffFileWriter(String filePath, String mode, String charSet, String eol) 
			throws SecurityException, IOException {
		this.FilePath = filePath;
		this.CharSet =  Charset.forName(charSet);
		this.Eol = eol;
		constructor_generic(mode.toLowerCase());
	}

	/** Generic portion of the constructor
	 * 
	 * @param mode
	 * @throws SecurityException
	 * @throws IOException
	 */
	private void constructor_generic(String mode) throws SecurityException, IOException {	
		this.FileLength = 0; 
		switch (mode.toLowerCase()) {
		case "create":			
			FileUtils.delFileIfExists(this.FilePath);
		case "append":
			 this.FileLength = (new File(this.FilePath)).length();;
		     break; 
		 default : throw new IllegalArgumentException("Invalid mode defined while opening the file :" + mode + "valid modes: c[create] | a[apend])");
		
		}
		this.RAFileObj = new RandomAccessFile(this.FilePath, "rw");
		this.FileChnlObj = this.RAFileObj.getChannel();			
		this.FileChnlObj.position(this.FileLength);		
	}	
	
	/**
	 * Tries to put a lock on the file. If successful, it will return true else false.
	 * @return LockStatus
	 * @throws IOException
	 */
	public boolean lockFile() throws IOException{
		try {
			this.fileLock = this.FileChnlObj.tryLock();		
		} catch (Exception e) {
			return false;
		}
		return (this.fileLock == null); 
	}
	/**
	 * Tries to unlock the file. If successful, it will return true else false.
	 * @return 
	 */
	public boolean unLockFile() {
		if(this.fileLock !=null && this.fileLock.isValid()) {
			try {
				this.fileLock.release();
			} catch (Exception e) {
				return false;
			}
		}	
		return true;
	}
	
	/**
	 * Writes the input String data to the File and moves the file pointer ahead.
	 * @param data
	 * @throws IOException
	 */
	public void write(String data) throws IOException{		
		StringBuilder sb = new StringBuilder(data);		
		byte[] b = sb.append(this.Eol).toString().getBytes(this.CharSet);
		ByteBuffer bb = ByteBuffer.wrap(b);	
		this.FileChnlObj.write(bb,this.FileLength);		
		this.FileLength +=(bb.capacity()) ;
		bb.clear();
	}
	/**
	 * Writes the input String data to the File and moves the file pointer ahead.
	 * @param data
	 * @throws IOException
	 */
	public void write(ByteBuffer buf) throws IOException{		
		this.FileChnlObj.write( buf,this.FileLength);		
		this.FileLength +=(buf.capacity()) ;
		buf.clear();
	}	
	
	/**
	 * Writes the input CharArr data to the File and moves the file pointer ahead.
	 * @param data
	 * @throws Exception
	 */
	public void write(CharArr data) throws Exception{
		
		data.appendArr(this.Eol.toCharArray());
		ByteBuffer bb = data.asBytBuffer(this.CharSet);
		this.FileChnlObj.write(bb,this.FileLength);
		this.FileLength +=bb.capacity() ;
		bb.clear();
	}		
	public void closeFile() throws IOException {
		try {
			unLockFile();
			this.RAFileObj.close();
		} catch (IOException e) {
		} finally {
			this.FileChnlObj = null;
		}
		try {
			this.RAFileObj.close();
		} catch (IOException e) {
		} finally {
			this.FileChnlObj = null;
		}
	}
	
	
}
