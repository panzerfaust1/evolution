package xyz.ev0lve.evolution;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {
    public static <T> Method getMethod(Class<T> targetClass, String methodName, @Nullable Class<?> ...args) {
        try {
            return targetClass.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException ex) {
            Evolution.LOGGER.error("Could not get method %s in class %s".formatted(methodName, targetClass.getName()));
        }

        return null;
    }

    public static void patchMethod(Method method) {
        if (method == null) {
            Evolution.LOGGER.error("Could not patch method");
        } else {
            method.setAccessible(true);
            Evolution.LOGGER.info("Patched method %s!".formatted(method.getName()));
        }
    }

    public static <T> Object invoke(T targetObject, Method method, Object ...args) {
        try {
            return method.invoke(targetObject, args);
        } catch (IllegalAccessException ex) {
            Evolution.LOGGER.error("Could not invoke method %s (illegal access)".formatted(method.getName()));
            return null;
        } catch (InvocationTargetException ex) {
            Evolution.LOGGER.error("Could not invoke method %s (bad invocation target)".formatted(method.getName()));
            return null;
        }
    }
}
