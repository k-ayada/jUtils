package pub.ayada.genutils.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import pub.ayada.dataStructures.chararray.CharArr;

public class DirectByteBuffFileReader {

	private String FilePath;
	private RandomAccessFile RAFileObj;
	private FileChannel FileChnlObj;
	private boolean EndOfFile = false;
	private long FileLength = 0L;
	private long CurrentPointer = 0L;
	private int BuferSize = 0;
	private ByteBuffer byteBuffer;
	private final byte CR = (byte) '\n';

	public DirectByteBuffFileReader(){}
	/**
	 * <b>Constructor</b> - Creates a file handler for read-only purposes.
	 * <BR>
	 * The buffer size is calculated as 10% of the available free java heap
	 * memory at the time of this object initialization.
	 * <br>
	 * After the EOF is reached, the file handling resources are closed including the buffer).
	 *
	 * @parm String The system-dependent filename
	 *
	 * @throws Exception
	 */
	public DirectByteBuffFileReader(String filePath) throws Exception {
		try {
			this.FilePath = filePath;
			this.RAFileObj = new RandomAccessFile(this.FilePath, "r");
			this.FileChnlObj = this.RAFileObj.getChannel();
			this.FileLength = (new File(filePath)).length();
			this.BuferSize = (int) (Runtime.getRuntime().freeMemory() /10 );
			this.BuferSize = this.BuferSize - (this.BuferSize % 1024);
			this.byteBuffer = ByteBuffer.allocateDirect(this.BuferSize);
		} catch (Exception e) {
			ShowParentException(e);
			closeResource();
			throw new FileReadException(
					"Failed to initialize the FileReader Object");
		}
	}

	/**
	 * <b>Constructor</b> - Creates a file handler for read-only purposes.
	 *
	 * @parm String The system-dependent filename
	 * @parm int The buffer size to use while reading the file.
	 *
	 * @throws Exception
	 */
	public DirectByteBuffFileReader(String filePath, int bufferSize) throws Exception {
		try {
			this.BuferSize = bufferSize;
			this.FilePath = filePath;
			this.RAFileObj = new RandomAccessFile(this.FilePath, "r");
			this.FileChnlObj = this.RAFileObj.getChannel();
			this.FileLength = (new File(filePath)).length();
			this.byteBuffer = ByteBuffer.allocateDirect(this.BuferSize);
		} catch (Exception e) {
			ShowParentException(e);
			closeResource();
			throw new FileReadException(
					"Failed to initialize the FileReader Object");
		}
	}

	/**
	 * Returns the Current position until we have read form the file.
	 *
	 * @parm
	 *
	 * @return long Value of the current position.
	 */
	public long getCurrentPointer() {
		return this.CurrentPointer;
	}

	/**
	 * Returns the length of the file.
	 *
	 * @parm
	 *
	 * @return long Length of the file
	 *
	 */
	public long getFileLength() {
		return this.FileLength;
	}

	/**
	 * Returns current Status of file read.
	 *
	 * @parm
	 *
	 * @return boolean <code>true</code> - End Of File Reached.
	 *         <code>false</code> - Not yet reached End Of File
	 *
	 */
	public boolean isEndOfFile() {
		return this.EndOfFile;
	}

	/**
	 * Reads the chunk of file from the previous position. <br>
	 * Converts the ByteBuffer into CharArr and returns it.
	 *
	 * @parm
	 *
	 * @return CharArr Next chunk of the file
	 * @throws Exception
	 *
	 */
	public CharArr ReadFileChunkAsCharArr() throws Exception {
		if (isEndOfFile())
			return null;
		LoadNextChunk();
		CharArr arr = new CharArr(this.byteBuffer);		
		if (isEndOfFile()) {
			closeResource();
		} else {
			this.byteBuffer.clear();
		}					
		return arr;
	}

	/**
	 * Reads the chunk of file from the previous position. <br>
	 * Converts the ByteBuffer into CharArr and returns it.
	 *
	 * @parm
	 *
	 * @return CharArr Next chunk of the file
	 * @throws Exception
	 *
	 */
	public CharArr ReadFileChunkAsCharArr(String CharSet) throws Exception {
		if (isEndOfFile())
			return null;
		LoadNextChunk();
		CharArr arr = new CharArr(this.byteBuffer,CharSet);
		if (isEndOfFile()) {
			closeResource();
		} else {
			this.byteBuffer.clear();
		}	
		return arr;
	}
	/**
	 * Reads the chunk of file from the previous position and returns ByteBuffer
	 * read.
	 *
	 * @parm
	 *
	 * @return ByteBuffer Next chunk of the file
	 * @throws Exception
	 *
	 */
	public ByteBuffer ReadFileChunk() throws Exception {
		if (isEndOfFile())
			return null;
		LoadNextChunk();
		return this.byteBuffer;
	}

	/**
	 * Loads the Next file chunk into memory and the position pointer is
	 * updated to the new positions.
	 * <br>
	 * If EOF reached, the flag is set and the resources are closed.
	 *
	 * @parm
	 *
	 * @return
	 * @throws Exception
	 *
	 */
	public void LoadNextChunk() throws Exception {
		try {
			CheckNResetEndOfFile();
			this.FileChnlObj.read(this.byteBuffer, this.CurrentPointer);

			this.byteBuffer.rewind();
			int i = this.byteBuffer.limit() - 1;
			for (; i > 0; i--)
				if (this.byteBuffer.get(i) == this.CR)
					break;
			this.CurrentPointer += (long)(i+1);
			this.byteBuffer.position(i+1);
			this.byteBuffer.flip();
		}
		catch (Exception e) {
			StringBuilder msg = new StringBuilder ( "Failed to Read entire Data in the input file : '"
					+ this.FilePath + "'.\n"
					+ "Process Stopped after Reading bytes : "
					+ this.CurrentPointer + ".\n"
					+ "Process tried to read " + this.byteBuffer.capacity()
					+ " Bytes");
			ShowParentException(e);
			closeResource();
			throw new FileReadException(msg.toString());
		}
		// If not read anything, and still have not reached EOF throw the
		// exception.
		if (this.byteBuffer.limit() <= 0 && !isEndOfFile()) {
			StringBuilder msg = new StringBuilder ("Failed to Read entire Data in the input file : '"
							+ this.FilePath + "'.\n"
							+ "Process Stopped after Reading bytes : "
							+ this.CurrentPointer + ".\n"
							+ "Process tried to read " + this.byteBuffer.capacity()
							+ " Bytes");
			closeResource();
			throw new FileReadException(msg.toString());
		}
	}

	/*
	 * If the current position + buffer size for read goes beyond the file
	 * length, reduce the buffer size and set the EOF flag (it is safe to assume
	 * that the routine that called this will read the file :)
	 */
	private void CheckNResetEndOfFile() {
		if ((this.CurrentPointer + (long) this.BuferSize) > this.FileLength) {
			this.EndOfFile = true;
			this.BuferSize = (int) (this.FileLength - this.CurrentPointer);
		}
	}

	/*
	 * Close the resources.
	 */
	private void closeResource() throws Exception{
		try {
			this.byteBuffer.clear();
			this.byteBuffer = null;
		}
		catch(Exception e) {
			ShowParentException(e);
			throw new FileReadException(
					  "Failed to close the Resources after Reading the input file : '"
					  + this.FilePath + "'.\n");
		}
		try {
			if (this.FileChnlObj != null) 
				this.FileChnlObj.close();

		}
		catch(Exception e) {
			ShowParentException(e);
			throw new FileReadException(
					  "Failed to close the Resources after Reading the input file : '"
					  + this.FilePath + "'.\n");
		}
		try {
			if(this.RAFileObj != null) {
				this.RAFileObj.close();
			}
				
		}
		catch(Exception e) {
			ShowParentException(e);
			throw new FileReadException(
					  "Failed to close the Resources after Reading the input file : '"
					  + this.FilePath + "'.\n");
		}				
	}


	private void ShowParentException(Exception e) {
		System.err.println(e.getStackTrace()[0].toString());
		e.printStackTrace(System.err);
	}

	@SuppressWarnings("unused")
	private String disp(byte[] BufArr) throws Exception {

		if (BufArr == null)
			return "(null)";

		StringBuilder sb = new StringBuilder();
	  
		for (int i = 0; i < BufArr.length; i++)
			sb.append((char) (BufArr[i] & 0xFF));
		return sb.toString();
	}

	public void cleanNclose() {
		if (this.byteBuffer != null) {
			this.byteBuffer.clear();
			this.byteBuffer = null;
		}
	}



	public static void main(String arg[]) throws Exception {

		DirectByteBuffFileReader fr  = new DirectByteBuffFileReader("C:\\fast\\aLargeFile1.csv");
        while (!fr.isEndOfFile()) fr.ReadFileChunk();
	}

}
