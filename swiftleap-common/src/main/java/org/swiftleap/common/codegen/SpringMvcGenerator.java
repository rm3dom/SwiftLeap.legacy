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

import org.reflections.Reflections;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swiftleap.common.codegen.anotate.CGIgnore;
import org.swiftleap.common.codegen.anotate.CGInclude;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/06/17.
 */
public class SpringMvcGenerator {
    private static Stream<Class<?>> getTypes(Method method) {
        Class<?> returnType = method.getReturnType();
        Class<?>[] paramTypes = method.getParameterTypes();
        if (returnType.isArray())
            returnType = returnType.getComponentType();

        ParameterizedType paramReturnType = null;
        try {
            paramReturnType = (ParameterizedType) method.getGenericReturnType();
            returnType = (Class<?>) paramReturnType.getActualTypeArguments()[0];
        } catch (Exception ex) {
        }

        return Stream.concat(Stream.of(returnType), Arrays.stream(paramTypes));
    }

    public static void generate(Language lang, String[] packagesToScan, String outputDir) throws Exception {
        Set<Class<?>> finalClasses = new HashSet<>();

        Stream.of(packagesToScan).forEach(packageToScan -> {
            final Reflections reflections = new Reflections(packageToScan);
            Set<Class<?>> classes = new HashSet<>();

            //MVC model
            classes.addAll(reflections.getTypesAnnotatedWith(Controller.class));
            classes.addAll(reflections.getTypesAnnotatedWith(RestController.class));

            classes = classes.stream()
                    .flatMap(c -> Arrays.stream(c.getMethods()))
                    .filter(m -> m.isAnnotationPresent(RequestMapping.class))
                    .filter(m -> !m.isAnnotationPresent(CGIgnore.class))
                    .flatMap(SpringMvcGenerator::getTypes)
                    .collect(Collectors.toSet());

            //CG Include
            classes.addAll(reflections.getTypesAnnotatedWith(CGInclude.class));

            classes = classes.stream()
                    .filter(t -> !t.isInterface()
                            && !t.isPrimitive()
                            && !t.isAnnotationPresent(CGIgnore.class)
                            && !Throwable.class.isAssignableFrom(t))
                    .collect(Collectors.toSet());

            finalClasses.addAll(classes);
        });

        finalClasses.add(Error.class);


        CodeGenerator.generate(lang, outputDir, finalClasses);
    }


}
