package jaemisseo.man.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * ref: https://www.baeldung.com/java-object-primitive-type
 * ref: https://stackoverflow.com/questions/12361492/how-to-determine-the-primitive-type-of-a-primitive-variable
 */
public class PrimitiveTypeUtil {

    private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;
    static {
        WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>(16);
        WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        WRAPPER_TYPE_MAP.put(Character.class, char.class);
        WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        WRAPPER_TYPE_MAP.put(Double.class, double.class);
        WRAPPER_TYPE_MAP.put(Float.class, float.class);
        WRAPPER_TYPE_MAP.put(Long.class, long.class);
        WRAPPER_TYPE_MAP.put(Short.class, short.class);
        WRAPPER_TYPE_MAP.put(Void.class, void.class);
    }

    public static boolean isPrimitiveType(Object source) {
        return isPrimitiveType(source.getClass());
    }

    public static boolean isPrimitiveType(Class clazz) {
        return WRAPPER_TYPE_MAP.containsKey(clazz);
    }

    /**
     *  - String is not primitiveType.
     *  - Maybe ambiguous number you type can be BigDecimal.
     * @param source
     * @return
     */
    public static boolean isPrimitiveTypeOrStringOrBigDecimal(Object source) {
        return isPrimitiveTypeOrStringOrBigDecimal(source.getClass());
    }

    public static boolean isPrimitiveTypeOrStringOrBigDecimal(Class clazz) {
        return isPrimitiveType(clazz) || String.class.isAssignableFrom(clazz) || BigDecimal.class.isAssignableFrom(clazz);
    }

}
