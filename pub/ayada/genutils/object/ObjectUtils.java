package pub.ayada.genutils.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "serial","unchecked" })
public class ObjectUtils {
	static final Map<Class, Class> primMap = new LinkedHashMap<Class, Class>(9) {
		{
			put(boolean.class, Boolean.class);
			put(byte.class, Byte.class);
			put(char.class, Character.class);
			put(short.class, Short.class);
			put(int.class, Integer.class);
			put(float.class, Float.class);
			put(long.class, Long.class);
			put(double.class, Double.class);
			put(void.class, Void.class);
		}
	};
	/**
     * If the class is a primitive type, change it to the equivalent wrapper.
     *
     * @param eClass to check
     * @return the wrapper class if eClass is a primitive type, or the eClass if not.
     */
    public static Class primToWrapper(Class eClass) {
        Class clazz0 = primMap.get(eClass);
        if (clazz0 != null)
            eClass = clazz0;
        return eClass;
    }
    public static int sizeOf(Object o) throws IllegalArgumentException {
        if (o instanceof Collection)
            return ((Collection) o).size();
        if (o instanceof Map)
            return ((Map) o).size();
        if (o.getClass().isArray())
            return ((Object[])o).length;
        throw new UnsupportedOperationException();
    }
	public static Number toNumOf(Object o,Class eClass)
            throws NumberFormatException {
        if (o instanceof Number) {
            Number n = (Number) o;
            if (eClass == Double.class)
                return n.doubleValue();
            if (eClass == Long.class)
                return n.longValue();
            if (eClass == Integer.class)
                return n.intValue();
            if (eClass == Float.class)
                return n.floatValue();
            if (eClass == Short.class)
                return n.shortValue();
            if (eClass == Byte.class)
                return n.byteValue();
            if (eClass == BigDecimal.class)
                return n instanceof Long ? BigDecimal.valueOf(n.longValue()) : BigDecimal.valueOf(n.doubleValue());
            if (eClass == BigInteger.class)
                return new BigInteger(o.toString());
        } else {
            String s = o.toString();
            if (eClass == Double.class)
                return Double.parseDouble(s);
            if (eClass == Long.class)
                return Long.parseLong(s);
            if (eClass == Integer.class)
                return Integer.parseInt(s);
            if (eClass == Float.class)
                return Float.parseFloat(s);
            if (eClass == Short.class)
                return Short.parseShort(s);
            if (eClass == Byte.class)
                return Byte.parseByte(s);
            if (eClass == BigDecimal.class)
                return new BigDecimal(s);
            if (eClass == BigInteger.class)
                return new BigInteger(s);
        }
        throw new UnsupportedOperationException("Cannot convert " + o.getClass() + " to " + eClass);
    }

	public static Object getDeepCopy(Object in) throws CloneNotSupportedException {
		Object o = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(in);
			oos.flush();
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			baos.close();
			ObjectInputStream ois = new ObjectInputStream(bais);
			bais.close();
			o = ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			// nop
		}
		return o;
	}

	public static <T> T cast(Object o, String clazz) throws ClassNotFoundException {
		return (T) Class.forName(clazz).cast(o);
	}
	
}
