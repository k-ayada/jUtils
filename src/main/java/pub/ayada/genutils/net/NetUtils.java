package pub.ayada.genutils.net;

import java.net.InetSocketAddress;

public class NetUtils {

	  public static final String VALID_PORT_REGEX = "[\\d]+";
	  public static final String HOSTNAME_PORT_SEPARATOR = ":";
	  
	  public static InetSocketAddress createInetSocketAddressFromHostAndPortStr(String hostAndPort)
	  {
	    return new InetSocketAddress(parseHostname(hostAndPort), parsePort(hostAndPort));
	  }
	  
	  public static String createHostAndPortStr(String hostname, int port)
	  {
	    return hostname + ":" + port;
	  }
	  
	  public static String parseHostname(String hostAndPort)
	  {
	    int colonIndex = hostAndPort.lastIndexOf(":");
	    if (colonIndex < 0) {
	      throw new IllegalArgumentException("Not a host:port pair: " + hostAndPort);
	    }
	    return hostAndPort.substring(0, colonIndex);
	  }
	  
	  public static int parsePort(String hostAndPort)
	  {
	    int colonIndex = hostAndPort.lastIndexOf(":");
	    if (colonIndex < 0) {
	      throw new IllegalArgumentException("Not a host:port pair: " + hostAndPort);
	    }
	    return Integer.parseInt(hostAndPort.substring(colonIndex + 1));
	  }	
	
	
}
