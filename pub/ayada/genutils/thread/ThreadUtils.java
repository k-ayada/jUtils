package pub.ayada.genutils.thread;

import java.lang.management.ManagementFactory;

public class ThreadUtils {
	
	public static long getCPUTime(Thread th){
		return ManagementFactory.getThreadMXBean().getThreadCpuTime(th.getId());
	}
	
	public static long getCurrentThreadCPUTime(){
		return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
	}
	
	public static long getCurrentThreadUserTime(){
		return ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
	}
	
	public static long getUserTime(Thread th){		
		ManagementFactory.getThreadMXBean().getThreadCpuTime(th.getId());
		return ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
	}
		
}
