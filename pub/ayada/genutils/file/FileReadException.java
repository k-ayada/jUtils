package pub.ayada.genutils.file;

public class FileReadException extends Exception {
	private static final long serialVersionUID = 1L;
    public FileReadException (String argMsg)  {
    	super(argMsg);
    }
  
    public FileReadException (Exception e, String argMsg)  {
    	super(argMsg);
    	System.err.println(e.getMessage());
        e.printStackTrace();
 
    } 
    public String getMessage() {
         return super.getMessage();
    }
}    
