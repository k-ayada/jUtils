package pub.ayada.genutils.jvm;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class StackTrace {
	public static String getErrorMessage(Throwable t)
	  {
	    if (t == null) {
	      return "";
	    }
	    if ((t instanceof InvocationTargetException))
	    {
	      InvocationTargetException ex = (InvocationTargetException)t;
	      t = ex.getTargetException();
	    }
	    String errMsg = (t instanceof RuntimeException) ? t.getMessage() : t.toString();
	    if ((errMsg == null) || (errMsg.length() == 0) || ("null".equals(errMsg))) {
	      errMsg = t.getClass().getName() + " at " + t.getStackTrace()[0].toString();
	    }
	    return errMsg;
	  }
	  public static String getStackTrace(Exception e)
	  {
	    if (e == null) {
	      return "";
	    }
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintWriter printWriter = new PrintWriter(baos);
	    e.printStackTrace(printWriter);
	    printWriter.flush();
	    String stackTrace = new String(baos.toByteArray());
	    printWriter.close();
	    return stackTrace;
	  }	
}
