package pub.ayada.genutils.jvm;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SysInfo {
	private static String localHostName = null;

	public static String getLocalHostName() {
		if (localHostName == null) {
			try {
				localHostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				localHostName = "localhost";
			}
		}
		return localHostName;
	}
}
