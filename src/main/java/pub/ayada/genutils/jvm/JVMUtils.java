package pub.ayada.genutils.jvm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;

public class JVMUtils {
	public static boolean is32BitJVM() {
		return System.getProperty("sun.arch.data.model").equals("32");
	}

	public static void logJVMInfo() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		if (runtime != null) {
			System.out.println("vmName=" + runtime.getVmName() + ", vmVendor="
					+ runtime.getVmVendor() + ", vmVersion="
					+ runtime.getVmVersion());

			System.out.println("vmInputArguments="
					+ runtime.getInputArguments());
		}
	}

	public static void showJMVProperties() {
		for (Object prop : System.getProperties().keySet())
			System.out.println(prop + "="
					+ System.getProperty((String) prop, ""));
	}

	@SuppressWarnings("rawtypes")
	public static long sizeOf(Class clazz) {
		long size = 0L;
		Object[] objects = new Object[100];
		try {
			clazz.newInstance();
			long startingMemoryUse = getUsedMemory();
			for (int i = 0; i < objects.length; i++) {
				objects[i] = clazz.newInstance();
			}
			long endingMemoryUse = getUsedMemory();
			float approxSize = (float) (endingMemoryUse - startingMemoryUse)
					/ objects.length;
			size = Math.round(approxSize);
		} catch (Exception e) {
			System.out.println("WARNING:couldn't instantiate" + clazz);
			e.printStackTrace();
		}
		return size;
	}

	public static long getUsedMemory() {
		gc();
		long totalMemory = Runtime.getRuntime().totalMemory();
		gc();
		long freeMemory = Runtime.getRuntime().freeMemory();
		long usedMemory = totalMemory - freeMemory;
		return usedMemory;
	}

	private static void gc() {
		try {
			System.gc();
			Thread.sleep(100L);
			System.runFinalization();
			Thread.sleep(100L);
			System.gc();
			Thread.sleep(100L);
			System.runFinalization();
			Thread.sleep(100L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	  public static long getDirectMemorySize()
	  {
	    RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
	    List<String> arguments = RuntimemxBean.getInputArguments();
	    long multiplier = 1L;
	    for (String s : arguments) {
	      if (s.contains("-XX:MaxDirectMemorySize="))
	      {
	        String memSize = s.toLowerCase().replace("-xx:maxdirectmemorysize=", "").trim();
	        if (memSize.contains("k")) {
	          multiplier = 1024L;
	        } else if (memSize.contains("m")) {
	          multiplier = 1048576L;
	        } else if (memSize.contains("g")) {
	          multiplier = 1073741824L;
	        }
	        memSize = memSize.replaceAll("[^\\d]", "");
	        
	        long retValue = Long.parseLong(memSize);
	        return retValue * multiplier;
	      }
	    }
	    return 0L;
	  }
	  
	  public static void destroyDirectByteBuffer(ByteBuffer toBeDestroyed)
	    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
	  {
	    if(!toBeDestroyed.isDirect())  {
	    	System.out.println("toBeDestroyed isn't direct!");
	    	return;
	    }
	    
	    Method cleanerMethod = toBeDestroyed.getClass().getMethod("cleaner", new Class[0]);
	    cleanerMethod.setAccessible(true);
	    Object cleaner = cleanerMethod.invoke(toBeDestroyed, new Object[0]);
	    Method cleanMethod = cleaner.getClass().getMethod("clean", new Class[0]);
	    cleanMethod.setAccessible(true);
	    cleanMethod.invoke(cleaner, new Object[0]);
	  }	
	
		public static Object getClone(Object in)
				throws CloneNotSupportedException {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(in);
				ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());
				ObjectInputStream ois = new ObjectInputStream(bais);
				return ois.readObject();
			} catch (IOException e) {
				return null;
			} catch (ClassNotFoundException e) {
				return null;
			}
		}	
	
	
	public static void main(String[] args) throws ClassNotFoundException {
		System.out.println(sizeOf(Class.forName(args[0])));
	}
}
