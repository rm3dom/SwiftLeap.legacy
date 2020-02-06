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
package org.swiftleap.common.codegen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.BeanUtils;
import org.swiftleap.common.codegen.anotate.CGAlias;
import org.swiftleap.common.codegen.anotate.CGDefault;
import org.swiftleap.common.codegen.anotate.CGIgnore;
import org.swiftleap.common.codegen.anotate.CGType;
import org.swiftleap.common.service.ServiceException;

import java.beans.PropertyDescriptor;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/06/14.
 */
public abstract class CodeGenerator {
    Map<Class, ClassDef> definitions = new HashMap<>();

    private static CodeGenerator getGenerator(Language lang) {
        if (lang == Language.TYPESCRIPT)
            return new TypeScriptGenerator();
        else if (lang == Language.ELM)
            return new ElmGenerator();
        else if (lang == Language.KOTLIN)
            return new KotlinGenerator(lang);
        else if (lang == Language.KOTLINJS)
            return new KotlinGenerator(lang);
        else
            throw new ServiceException("Language no supported " + lang);
    }

    public static void generate(Language lang, PrintStream pw, Collection<Class<?>> classes) throws Exception {
        CodeGenerator generator = getGenerator(lang);

        classes = classes.stream().filter(c -> !isSimpleType(c)).collect(Collectors.toSet());

        generator.genTypes(pw, classes);
    }

    public static void generate(Language lang, String outDir, Collection<Class<?>> classes) throws Exception {
        CodeGenerator generator;
        if (lang == Language.TYPESCRIPT)
            generator = new TypeScriptGenerator();
        else if (lang == Language.ELM)
            generator = new ElmGenerator();
        else if (lang == Language.KOTLIN)
            generator = new KotlinGenerator(lang);
        else if (lang == Language.KOTLINJS)
            generator = new KotlinGenerator(lang);
        else
            throw new ServiceException("Language not supported " + lang);

        classes = classes.stream().filter(c -> !isSimpleType(c)).collect(Collectors.toSet());

        generator.genTypes(outDir, classes);
    }

    private static Class<?> getCollectionType(Type type) {
        ParameterizedType listType = (ParameterizedType) type;
        return (Class<?>) listType.getActualTypeArguments()[0];
    }

    private static boolean isArray(Class<?> type) {
        return type.isArray();
    }

    private static boolean isCollection(Class type) {
        return Iterable.class.isAssignableFrom(type) && !isMap(type);
    }

    private static boolean isMap(Class type) {
        return Map.class.isAssignableFrom(type);
    }

    private static boolean isNullable(Class<?> type) {
        return Object.class.isAssignableFrom(type);
    }

    private static boolean isEnumType(Class<?> type) {
        return Enum.class.isAssignableFrom(type);
    }

    private static boolean isSimpleType(Class<?> type) {
        if (isMap(type)) return true;
        if (type.isPrimitive()) return true;
        if (isEnumType(type)) return true;
        if (isNumber(type)) return true;
        if (type == Boolean.class) return true;
        if (type == String.class) return true;
        if (type == Void.class) return true;
        if (type == Object.class) return true;
        if (type == Date.class) return true;
        if (type == BigDecimal.class) return true;
        if (type == Byte.class) return true;
        return false;
    }

    static boolean isNumber(Class<?> type) {
        if (type == Integer.TYPE
                || type == Byte.TYPE
                || type == Float.TYPE
                || type == Short.TYPE
                || type == Double.TYPE
                || type == Long.TYPE)
            return true;
        return Number.class.isAssignableFrom(type);
    }

    public abstract void genTypes(PrintStream pw, Collection<Class<?>> classes) throws Exception;

    public abstract void genTypes(String outDir, Collection<Class<?>> classes) throws Exception;

    private <T extends Annotation> T getAnnotation(PropertyDescriptor pd, HashMap<String, Field> fields, Class<T> annonClass) {
        T t = pd.getReadMethod().getAnnotation(annonClass);
        if (t == null && fields.containsKey(pd.getName()))
            t = fields.get(pd.getName()).getAnnotation(annonClass);
        return t;
    }

    ClassDef parseClass(Class<?> clazz) {
        //Use this type instead
        if (clazz.isAnnotationPresent(CGType.class)) {
            CGType cgType = clazz.getAnnotation(CGType.class);
            clazz = cgType.value();
        }

        if (definitions.containsKey(clazz))
            return definitions.get(clazz);

        String className = clazz.getSimpleName();

        if (clazz.isAnnotationPresent(CGAlias.class)) {
            CGAlias cgAlias = clazz.getAnnotation(CGAlias.class);
            className = cgAlias.value();
        }

        ClassDef cdef = new ClassDef();
        cdef.type = clazz;
        cdef.name = className;

        definitions.put(clazz, cdef);

        //Map of fields, needed for attributes
        HashMap<String, Field> fields = new HashMap<>();
        Stream.of(clazz.getDeclaredFields()).forEach(f -> fields.put(f.getName(), f));

        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor pd : pds) {

            if (pd.getPropertyType() == Class.class)
                continue;

            if (Throwable.class.isAssignableFrom(pd.getPropertyType()))
                continue;

            if (pd.getReadMethod() == null
                    || getAnnotation(pd, fields, JsonIgnore.class) != null
                    || getAnnotation(pd, fields, CGIgnore.class) != null)
                continue;

            ClassDef pdCDef = null;
            Class<?> type = pd.getPropertyType();
            Class<?> realType = type;
            String name = pd.getName();
            String typeName;
            CGAlias cgAlias = getAnnotation(pd, fields, CGAlias.class);
            CGDefault cgDefault = getAnnotation(pd, fields, CGDefault.class);
            CGType cgType = getAnnotation(pd, fields, CGType.class);

            if (isCollection(type))
                realType = getCollectionType(pd.getReadMethod().getGenericReturnType());

            if (isArray(type))
                realType = type.getComponentType();

            if (realType.isAnnotationPresent(CGType.class))
                cgType = realType.getAnnotation(CGType.class);

            if (cgType != null)
                realType = cgType.value();

            typeName = realType.getSimpleName();

            if (realType.isAnnotationPresent(CGAlias.class)) {
                CGAlias typeAlias = realType.getAnnotation(CGAlias.class);
                typeName = typeAlias.value();
            }

            if (cgAlias != null)
                name = cgAlias.value();

            if (!isSimpleType(realType) && !isMap(type))
                pdCDef = parseClass(realType);

            PropertyDef pdef = new PropertyDef();
            pdef.simpleType = isSimpleType(realType);
            pdef.type = realType;
            pdef.nullable = isNullable(realType);
            pdef.classDef = pdCDef;
            pdef.name = name;
            pdef.defaultValue = cgDefault;
            pdef.enumType = isEnumType(realType);
            pdef.collection = isCollection(type) || isArray(type);
            pdef.map = isMap(type);
            pdef.typeName = typeName;
            cdef.props.put(name, pdef);
        }

        definitions.put(clazz, cdef);

        return cdef;
    }

    static class PropertyDef {
        private Class<?> type;
        private String name;
        private String typeName;
        private ClassDef classDef;
        private boolean simpleType;
        private boolean enumType;
        private CGDefault defaultValue;
        private boolean nullable;
        private boolean collection = false;
        private boolean map = false;

        public String getSafeName() {
            if (getName().equals("type"))
                return getName() + "_";
            return getName();
        }

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public ClassDef getClassDef() {
            return classDef;
        }

        public void setClassDef(ClassDef classDef) {
            this.classDef = classDef;
        }

        public boolean isSimpleType() {
            return simpleType;
        }

        public void setSimpleType(boolean simpleType) {
            this.simpleType = simpleType;
        }

        public boolean isEnumType() {
            return enumType;
        }

        public void setEnumType(boolean enumType) {
            this.enumType = enumType;
        }

        public CGDefault getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(CGDefault defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }

        public boolean isCollection() {
            return collection;
        }

        public void setCollection(boolean collection) {
            this.collection = collection;
        }

        public boolean isMap() {
            return map;
        }

        public void setMap(boolean map) {
            this.map = map;
        }
    }


    static class ClassDef {
        private Class type;
        private String name;
        private HashMap<String, PropertyDef> props = new HashMap<>();

        public Class getType() {
            return type;
        }

        public void setType(Class type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public HashMap<String, PropertyDef> getProps() {
            return props;
        }

        public void setProps(HashMap<String, PropertyDef> props) {
            this.props = props;
        }
    }
}
