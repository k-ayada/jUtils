package pub.ayada.genutils.string;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.StringTokenizer;


public class StringUtils {

	public static String[] splitByLength(String string, int len) {
		if (string == null || len <= 0)
			return null;

		if (string.length() < len) {
			String[] arr = new String[1];
			arr[0] = string;
			return arr;
		}

		int chunks = string.length() / len
				+ ((string.length() % len > 0) ? 1 : 0);
		String[] arr = new String[chunks];
		for (int i = 0, j = 0, l = string.length(); i < l; i += len, j++)

			// int xl = ((len > l-i) ? l-i : len)
			arr[j] = string.substring(i, (i + ((len > l - i) ? l - i : len)));
		return arr;
	}

	public static ArrayList<String> splitByLength(String string, int len,
			boolean splitAtNewLine, char Justify) {
		if (string == null || len <= 0)
			return (ArrayList<String>) null;

		String fmt = "";
		 if (Justify == 'l')
			  fmt = "%-"+  len + "s";
		 else 
			 fmt = "%"+  len + "s";		 
		 
		if (string.length() <= len) {
			String[] splits = string.split("\n");
			ArrayList<String> arr = new ArrayList<String>(splits.length);
			for (int i = 0; i < splits.length; i++)
				arr.add(String.format(fmt, splits[i]));

			return arr;
		}

		int chunks = string.length() / len
				+ ((string.length() % len > 0) ? 1 : 0);
		ArrayList<String> arr = new ArrayList<String>(chunks);
		char[] carr = string.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < carr.length; i++) {
			if (sb.length() == len) {
				arr.add(sb.toString());
				sb = new StringBuilder();
				sb.append(carr[i]);
			} else if (carr[i] == '\n' && splitAtNewLine) {
				arr.add(String.format(fmt, sb.toString().trim()));
				sb = new StringBuilder();
			} else
				sb.append(carr[i]);
		}
		arr.add(String.format(fmt, sb.toString().trim()));
		return  arr;
	}

	public static String[] splitByLength(String string, int len, int SplitCount) {
		if (string == null || len <= 0)
			return null;

		if (string.length() <= len)
			return new String[] { string };

		int chunks = string.length() / len
				+ ((string.length() % len > 0) ? 1 : 0);
		chunks = chunks > SplitCount ? SplitCount : chunks;
		String[] arr = new String[chunks];
		int i = 0, j = 0;
		for (int l = string.length(); i < l && j < SplitCount - 1; i += len, j++) {
			if ((i + len) > l)
				break;
			else
				arr[j] = string.substring(i, i + len);
		}
		arr[j] = string.substring(i);
		return arr;
	}

	public static <T> String arrJoin(T[] stringArr) {
		return joinArr(stringArr, "");
	}

	public static <T> String joinArr(T[] stringArr, String delm) {

		if (stringArr.length == 0)
			return "";

		if (stringArr.length == 1)
			return String.valueOf(stringArr[0]);

		StringBuilder sb = new StringBuilder();

		for (T str : stringArr)
			sb.append(String.valueOf(str)).append(delm);

		sb.setLength(sb.length() - (delm.length()));

		return sb.toString();
	}

	public static String repeat(String str, int count) {
		StringBuilder sb = new StringBuilder(str.length() * count);
		for (int i = 0; i < count; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	public static String repeat(char c, int count) {
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static ArrayList<String> splitByLengthWithWordWrap(String text, int len) {
		StringTokenizer st = new StringTokenizer(text);
		int SpaceLeft = len;
		int SpaceWidth = 1;

		StringBuilder sb = new StringBuilder(len);
		ArrayList<String> wrapList = new ArrayList<String>();

		while (st.hasMoreTokens()) {
			String nxtWord = st.nextToken();

			while (nxtWord.length() > len) {
				sb = new StringBuilder(len);
				String[] arr = splitByLength(nxtWord, len, 2);
				int i = 0;
				for (; i < arr.length - 1; i++) {
					wrapList.add(arr[i]);
				}
				nxtWord = arr[i];
				SpaceLeft = len - nxtWord.length();
			}

			if ((nxtWord.length() + SpaceWidth) > SpaceLeft) {
				wrapList.add(sb.toString());
				sb = new StringBuilder(len);
				sb.append(nxtWord + " ");
				SpaceLeft = len - (nxtWord.length() + SpaceWidth);
			} else {
				sb.append(nxtWord + " ");
				SpaceLeft -= (nxtWord.length() + SpaceWidth);
			}
		}
		wrapList.add(sb.toString());
		return wrapList;
		// for (String s : wrapList) System.out.println(s);
	}
	public static String Trim(String argInStr)
	{
		StringBuilder RegEx = new StringBuilder(argInStr);
		int i=0;
	 // Replace tab with space
	    i=0; while ((i = RegEx.indexOf("\t",i)) >= 0) { RegEx.replace(i, i+1,"") ;}
     // Replace LineFeed with null
	    i=0; while ((i = RegEx.indexOf("\r",i)) >= 0) { RegEx.replace(i, i+1,"") ;}
     // Replace CarriageReturn with space
	    i=0; while ((i = RegEx.indexOf("\n",i)) >= 0) { RegEx.replace(i, i+1," ");}
	 // Replace double spaces with space
	    i=0; while ((i = RegEx.indexOf("  ",i)) >= 0) { RegEx.replace(i, i+2," ");}

    	return RegEx.toString().trim();
	}

	public static StringBuilder clearStringBuilder(StringBuilder inStr) {
		return inStr.delete(0, inStr.length());
	}
	public static String RemoveAllWhitSpaceChars(String arglnStr) {
		char[] in = arglnStr.toCharArray();
		char[] out = new char[arglnStr.length()];
		int j = -1;
		for (int i = 0; i < in.length; i++)
			if (!Character.isWhitespace(in[i]))
				out[++j] = in[i];
		return new String(out).trim();
	}

	public static String RemoveWhitSpaceChars(String argInStr,
			char EnclosureChar, char EscapeChar) {
		
		char[] in = argInStr.toCharArray();
		char[] out = new char[argInStr.length()];
		int j = -1;

		boolean inEnclosure = false, ignore = false;
		for (int i = 0; i < in.length; i++) {
			// Found Enclosure char
			if (in[i] == EnclosureChar) {
				inEnclosure = !inEnclosure;
			} else if (in[i] == EscapeChar && inEnclosure
					   && in[i + 1] == EnclosureChar) {
				ignore = true;
			} else if ((Character.isWhitespace(in[i]) && !inEnclosure)) {
				ignore = true;
			}
			if (!ignore) 
				out[++j] = in[i];
			ignore = false;
		}	
		return new String(out).trim();
	}
	
	public static String paddL(String inStr, int totLen, char paddChar) {
		if (inStr.length() >= totLen) 
			return inStr;
		int miss = totLen - inStr.length();
		StringBuilder sb = new StringBuilder(totLen);
		for (int i = 0 ; i < miss ; i++) {
			sb.append(paddChar);
		}
		sb.append(inStr);
		return sb.toString();		
	}
	public static String paddR(String inStr, int totLen, char paddChar) {
		if (inStr.length() >= totLen) 
			return inStr;
		StringBuilder sb = new StringBuilder(totLen);
		sb.append(inStr);
		for (int i = sb.length() ; i < totLen ; i++) {
			sb.append(paddChar);
		}		
		return sb.toString();		
	}
	
	
	public static String asFormattedStr(String format, String eol, Object... objects) {
		Formatter f = new Formatter();
		String s = f.format(format + "%s", objects, eol).toString();
		f.close();
		return s;
	}

	public static String asFormattedStr(Formatter f, String format, String eol, Object... objects) {
		String s = f.format(format + "%s", objects, eol).toString();
		return s;
	}

	public static String asFormattedStr(String format, Object... objects) throws Exception {
		Formatter f = new Formatter();
		try {
			String s = f.format(format, objects).toString();
			return s;
		}catch (Exception e) {
			 throw new Exception("Failed to format the values " + Arrays.toString(objects) + " using format:" + format,e);
		} finally {		f.close();}
	}

	public static String asFormattedStr(Formatter f, String format, Object... objects) {
		String s = f.format(format, objects).toString();
		return s;
	}
	
    /**
     * Encodes the text into safe XML by replacing < > and & with XML tokens
     *
     * @param text  the text
     * @return the encoded text
     */
    public static String xmlEncode(String text) {
        if (text == null) {
            return "";
        }
        // must replace amp first, so we don't replace &lt; to amp later
        text = text.replaceAll("&", "&amp;");
        text = text.replaceAll("\"", "&quot;");
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        return text;
    }
    
    public static String humanReadable(long number)
    {
      long absNumber = Math.abs(number);
      double result = number;
      String suffix = "";
      if (absNumber < 1024L) {
        return String.valueOf(number);
      }
      if (absNumber < 1048576L)
      {
        result = number / 1024.0D;
        suffix = "k";
      }
      else if (absNumber < 1073741824L)
      {
        result = number / 1048576.0D;
        suffix = "m";
      }
      else
      {
        result = number / 1073741824.0D;
        suffix = "g";
      }
      return new DecimalFormat("0.0").format(result) + suffix;
    }
    
}
