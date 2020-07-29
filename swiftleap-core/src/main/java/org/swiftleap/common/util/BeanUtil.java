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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.swiftleap.common.types.EnumValueType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Copy bean properties.
 * <p>
 * Created by ruans on 2016/06/29.
 */
public class BeanUtil {
    private static final Logger LOG = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * Copy bean properties.
     * <p>
     * WARNING: any common properties will be copied; but only the properties in some clazz should
     * be considered as copyable. That is why this method is called copyUnsafe. In the future there
     * will be a similar method but safe.
     * <p>
     *
     * @param dest   destination
     * @param source source
     * @param <T>    The common super class.
     * @param <A>    The dest.
     * @param <B>    The source.
     * @return the dest.
     */
    public static <T, A extends T, B extends T> A copyUnsafe(A dest, B source) {
        try {
            assign(dest, describe(source));
            return dest;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed with: " + e.getMessage(), e);
        }
    }

    public static <T, A extends T, B extends T> A copyUnsafe(A dest, B source, String... ignoredProperties) {
        try {
            assign(dest, describe(source), ArrayUtil.asSet(ignoredProperties));
            return dest;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed with: " + e.getMessage(), e);
        }
    }

    /**
     * Create a copy of a bean.
     * <p>
     * If the source has a copy constructor the copy constructor will be used instead.
     * This allows you to override the behaviour of this method by implementing a copy constructor.
     * </p>
     *
     * @param source the source.
     * @param <T>    source type.
     * @return a copy of source.
     */
    public static <T> T copy(T source) {
        if (source == null) return null;
        try {
            Class<?> clazz = source.getClass();
            try {
                //If we have a copy constructor use that instead.
                Constructor<?> c = clazz.getConstructor(clazz);
                return (T) c.newInstance(source);
            } catch (NoSuchMethodException ex) {
                //Nothing fall back to copyProperties.
            }

            T dest = (T) clazz.newInstance();
            BeanUtils.copyProperties(dest, source);
            return dest;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed with: " + e.getMessage(), e);
        }
    }

    public static String getPropertyName(Method m) {
        if (m == null) {
            return null;
        }
        String method = m.getName();
        if (method.startsWith("is") && method.length() > 2) {
            method = "" + Character.toLowerCase(method.charAt(2)) + method.substring(3);
        } else if (method.startsWith("get") && method.length() > 3) {
            method = "" + Character.toLowerCase(method.charAt(3)) + method.substring(4);
        } else if (method.startsWith("set") && method.length() > 3) {
            method = "" + Character.toLowerCase(method.charAt(3)) + method.substring(4);
        }
        return method;
    }

    public static boolean methodNameEquals(String method, String propertyName) {
        if (method == null || propertyName == null) {
            return false;
        }
        if (method.startsWith("is")) {
            method = method.substring(2);
        } else if (method.startsWith("get")) {
            method = method.substring(3);
        } else if (method.startsWith("set")) {
            method = method.substring(3);
        }
        int len = method.length();
        if (len != propertyName.length()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (i == 0) {
                if (Character.toLowerCase(method.charAt(i)) != Character.toLowerCase(propertyName.charAt(i))) {
                    return false;
                }
                continue;
            }

            if (method.charAt(i) != propertyName.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean methodNameEquals(Method m, String propertyName) {
        return m != null && methodNameEquals(m.getName(), propertyName);
    }

    private static void getReadMethodsPrv(Map<String, Method> map, Class<?> clazz) {
        for (Method m : clazz.getMethods()) {
            if ((m.getName().startsWith("get") || m.getName().startsWith("is")) && m.getParameterTypes().length < 1) {
                map.put(getPropertyName(m), m);
            }
        }
        Class<?> c = clazz.getSuperclass();
        if (c != null) {
            getReadMethodsPrv(map, c);
        }
    }

    public static void getReadMethods(Collection<Method> col, Class<?> clazz) {
        Map<String, Method> map = new HashMap<>();
        getReadMethodsPrv(map, clazz);
        col.addAll(map.values());
    }

    public static Map<String, Method> getReadMethods(Class<?> clazz) {
        Map<String, Method> map = new HashMap<>();
        getReadMethodsPrv(map, clazz);
        return map;
    }

    public static Map<String, Object> describe(final Object bean)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return describe(bean, null);

    }

    public static Map<String, Object> describe(final Object bean, Set<String> ignoreProps)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (bean == null) {
            return new HashMap<>();
        }
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(bean.getClass());
        Map<String, Object> map = new HashMap<>();
        for (PropertyDescriptor pd : pds) {
            Method m = pd.getReadMethod();
            if (m == null) {
                continue;
            }
            if (ignoreProps != null && ignoreProps.contains(pd.getName())) {
                continue;
            }
            map.put(pd.getName(), m.invoke(bean));
        }
        return map;
    }

    public static void assign(Object bean, final Map<String, Object> props)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        assign(bean, props, null);
    }

    public static void assign(Object bean, final Map<String, Object> props, Set<String> ignoreProps)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        if (bean == null) {
            return;
        }
        for (String k : props.keySet()) {
            if (ignoreProps != null && ignoreProps.contains(k)) {
                continue;
            }
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(bean.getClass(), k);
            if (pd != null) {
                Method m = pd.getWriteMethod();
                if (m != null) {
                    //NOTE do not be silent, throw an exception if the object cant be casted.
                    //TODO we need to report the failed method and say something like:
                    //Assigning method x failed, mapping type x to type y failed. It possible that the wrong
                    //fields/values are being mapped.
                    try {
                        Class<?> p = m.getParameterTypes()[0];
                        Object v = props.get(k);
                        if (v == null && p.isPrimitive()) {
                            LOG.info(String.format("Trying to assign null to a primitive: '%s' on '%s'",
                                    k, bean.getClass().getName()));
                            continue;
                        }
                        m.invoke(bean, safeCast(p, v));
                    } catch (IllegalAccessException
                            | IllegalArgumentException
                            | InstantiationException
                            | InvocationTargetException ex) {
                        LOG.error(String.format("Unable to set property: '%s' on '%s'",
                                k, bean.getClass().getName()));
                        throw ex;
                    }
                }
            }
        }
    }

    private static Object safeCast(final Class<?> c, Object o)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (o == null) {
            return null;
        }

        if (c == o.getClass()) {
            return o;
        }

        if (c.isAssignableFrom(o.getClass())) {
            return o;
        }

        Constructor<?> ctor = null;
        try {
            ctor = c.getConstructor(o.getClass());
        } catch (NoSuchMethodException ex) {
            //Do not throw
        }
        if (ctor != null) {
            return ctor.newInstance(o);
        }

        //If we got here its not of the same type, check for enum value type.
        if (o instanceof EnumValueType) {
            return safeCast(c, ((EnumValueType) o).getValue());
        }

        return o;
    }
}
