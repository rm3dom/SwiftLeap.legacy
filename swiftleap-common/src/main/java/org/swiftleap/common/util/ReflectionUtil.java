/*
 * Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.swiftleap.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Additional reflection utilities.
 * Created by ruan on 2015/08/12.
 */
public class ReflectionUtil {
    public static Object getInstance(String className) {
        return getInstance(className, null);
    }

    public static Class getGenericType(Class clazz, int typeIndex) {
        Type st = clazz.getGenericSuperclass();
        if (st != null && (st instanceof ParameterizedType)) {
            ParameterizedType pt = (ParameterizedType) st;
            if (pt.getActualTypeArguments().length > typeIndex)
                return (Class) pt.getActualTypeArguments()[typeIndex];
        }

        Type[] sts = clazz.getGenericInterfaces();
        if (sts == null || sts.length < 1) return null;
        for (Type t : sts) {
            if (!(t instanceof ParameterizedType)) continue;
            ParameterizedType pt = (ParameterizedType) t;
            if (pt.getActualTypeArguments().length > typeIndex)
                return (Class) pt.getActualTypeArguments()[typeIndex];
        }
        return null;
    }

    public static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            return (ClassLoader) AccessController.doPrivileged(
                    (PrivilegedAction) () -> Thread.currentThread().getContextClassLoader());
        }
    }

    public static void setContextClassLoader(ClassLoader classLoader) {
        if (System.getSecurityManager() == null) {
            Thread.currentThread().setContextClassLoader(classLoader);
        } else {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                Thread.currentThread().setContextClassLoader(classLoader);
                return null;
            });
        }
    }

    public static Object getInstance(String className, ClassLoader classLoader) {
        //First try the classloader
        if (classLoader != null) {
            try {
                return classLoader.loadClass(className).newInstance();
            } catch (Exception ex) {
                //Nothing, try the next classloader
            }
        }
        //Try the current classloader
        try {
            return getContextClassLoader().loadClass(className).newInstance();
        } catch (Exception ex) {
            //Nothing, try the next classloader
        }
        //Not found try this classloader
        try {
            return ReflectionUtil.class.getClassLoader().loadClass(className).newInstance();
        } catch (Exception ex) {
            //finally we give up
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(Class<T> annonClass, Class<?> clazz, Method method, boolean recursive) {
        T t = method.getAnnotation(annonClass);
        if (t != null) return t;

        t = clazz.getAnnotation(annonClass);
        if (t != null) return t;

        if (recursive && clazz.getSuperclass() != null) {
            return getAnnotation(annonClass, clazz.getSuperclass(), true);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(Class<T> annonClass, Class<?> clazz, boolean recursive) {
        T t = clazz.getAnnotation(annonClass);
        if (t != null) return t;

        if (recursive && clazz.getSuperclass() != null) {
            return getAnnotation(annonClass, clazz.getSuperclass(), true);
        }

        return null;
    }
}
