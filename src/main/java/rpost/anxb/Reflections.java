package rpost.anxb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Reflections {

    public static <T> T construct(Class<T> clazz, Class<?>[] argTypes, Object[] args) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(argTypes);
            if (constructor == null) {
                throw new IllegalArgumentException(
                    "There is no constructor with argument types " + Arrays.toString(argTypes) + " in class " + clazz
                );
            }
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
        Method method = findMethod(target.getClass(), methodName, argTypes);
        if (method == null) {
            throw new IllegalArgumentException(
                "There is no method named " + methodName + " with argument types " + Arrays.toString(argTypes) +
                    " in class " + target.getClass()
            );
        }
        method.setAccessible(true);
        try {
            return (T) method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throwRuntime(e);
        } catch (InvocationTargetException e) {
            throwRuntime(e);
        }
        throw new IllegalStateException("should not happen ;)");
    }

    private static Method findMethod(Class clazz, String methodName, Class[] argTypes) {
        for (Class c = clazz; c != null; c = c.getSuperclass()) {
            for (Method method: c.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && Arrays.equals(method.getParameterTypes(), argTypes)) {
                    return method;
                }
            }
        }
        return null;
    }

    public static <T> T getFieldValue(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            throw new IllegalArgumentException("There is no field named " + fieldName + " in class " + target.getClass());
        }
        field.setAccessible(true);
        try {
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            throwRuntime(e);
        }
        throw new IllegalStateException("should not happen ;)");
    }

    private static Field findField(Class clazz, String fieldName) {
        for (Class c = clazz; c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    private static void throwRuntime(Throwable e) {
        throw new RuntimeException(e);
    }
}
