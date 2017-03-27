package pub.ayada.genutils.file;

public class PraseException extends Exception {
	private static final long serialVersionUID = 1L;
    public PraseException (String argMsg) 
       { 
         super("\n"+argMsg);
         super.printStackTrace();
         System.exit(1);
       } 
    public String getMessage()
       {
         return super.getMessage();
       }
}
