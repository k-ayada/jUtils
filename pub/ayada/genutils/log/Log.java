package pub.ayada.genutils.log;


import pub.ayada.genutils.string.StringUtils;

public class Log {

	public static void print(Object... o) {
		System.out.println(getLine(o));
	}

	public static String getLine(Object... o) {
		StackTraceElement e = null;

		for (StackTraceElement e1 : Thread.currentThread().getStackTrace()) 
			if (!e1.getClassName().equals(Log.class) && !e1.getMethodName().equals("getStackTrace"))
				e = e1;

		String callSite = (e == null) 
				          ? "??"
				          : String.format("%s->%s.%s[%04d]" 
				        		         ,Thread.currentThread().getName()
				        		         ,e.getClassName()
						                 ,e.getMethodName()
						                 ,e.getLineNumber());
		
		StringBuilder b = new StringBuilder(callSite + "\t");
		for (int i = 0; i < o.length; i++) {
			b.append(String.valueOf(o[i])).append(" ");
		}
		return b.toString();
	}
	
	
	public static String getCallTrace(){
		
		
		StringBuilder b = new StringBuilder(Thread.currentThread().getName());
		b.append("->");
		StringBuilder t = new StringBuilder(StringUtils.repeat(' ',b.length()));
				
		StackTraceElement[] x = Thread.currentThread().getStackTrace();
		
		
		
		for (int i = 0 ; i < x.length ; i++ ) {
			
			if (!x[i].getClassName().equals(Log.class)  &&  
			    !x[i].getMethodName().equals("getStackTrace") &&
			    !x[i].getMethodName().equals("getCallTrace")) {
				b.append(x[i].toString());
			   if (i < x.length-1) b.append('\n').append(t);
			}
		}
/*		
		for (StackTraceElement e : Thread.currentThread().getStackTrace())
			if (!e.getClassName().equals(Log.class) && !e.getMethodName().equals("getCallTrace"))
				b.append(String.format("%s->%s.%s[%03d]", e.getClassName(),
						e.getMethodName(), e.getLineNumber()))
				 .append("\n\t"); 
*/
		return b.toString();
	}
}
