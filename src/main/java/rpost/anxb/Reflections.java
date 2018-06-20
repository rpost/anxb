package rpost.anxb;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Reflections {

    public static <T> T construct(Class<T> clazz, Class<?>[] argTypes, Object[] args) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(argTypes);
            assert constructor != null;
            constructor.setAccessible(true);
            return constructor.newInstance(args);

        } catch (NoSuchMethodException e) {
            throwRuntime(e);
        } catch (IllegalAccessException e) {
            throwRuntime(e);
        } catch (InvocationTargetException e) {
            throwRuntime(e);
        } catch (InstantiationException e) {
            throwRuntime(e);
        }
        throw new IllegalStateException("should not happen ;)");
    }

    public static <T> T invokeMethod(Object target, String methodName, Class[] argTypes, Object[] args) {
        try {
            return (T) MethodUtils.invokeMethod(target, true, methodName, args, argTypes);
        } catch (NoSuchMethodException e) {
            throwRuntime(e);
        } catch (IllegalAccessException e) {
            throwRuntime(e);
        } catch (InvocationTargetException e) {
            throwRuntime(e);
        }
        throw new IllegalStateException("should not happen ;)");
    }

    public static <T> T getFieldValue(Object target, String fieldName) {
        Field field = FieldUtils.getField(target.getClass(), fieldName, true);
        try {
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            throwRuntime(e);
        }
        throw new IllegalStateException("should not happen ;)");
    }

    private static void throwRuntime(Throwable e) {
        throw new RuntimeException(e);
    }
}
