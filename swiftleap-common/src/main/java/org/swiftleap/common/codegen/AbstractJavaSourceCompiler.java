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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swiftleap.common.service.SystemErrorException;
import org.swiftleap.common.util.ArrayUtil;
import org.swiftleap.common.util.IOUtil;
import org.swiftleap.common.util.StringUtil;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ruans on 2018/01/09.
 */
public abstract class AbstractJavaSourceCompiler {
    static final Logger log = LoggerFactory.getLogger(AbstractJavaSourceCompiler.class);
    protected List<String> classPaths = new ArrayList<>();
    protected ClassLoader classLoader;

    protected AbstractJavaSourceCompiler(String scratchDir, ClassLoader classLoader, Class<?>... classPath) throws IOException {
        for(Class<?> c : classPath) {
            classPaths.add(c.getProtectionDomain().getCodeSource().getLocation().getPath());
        }
        this.classLoader = classLoader;
        String[] solved = extractJars(solveClassPath(classLoader), scratchDir);
        classPaths.addAll(Arrays.asList(solved));
    }

    public void compileCode(final String classPathOutputDir, final String className, final String code) throws Exception {
        compileCode(classPathOutputDir, new JavaSourceObject(className, code));
    }


    public URL compileCode(final String classPathOutputDir, final JavaSourceObject jsfs) throws Exception {
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        if (jc == null)
            throw new Exception("Compiler unavailable. Program must be run with full JDK.");

        Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(jsfs);

        String[] classPath = ArrayUtil.concat(classPaths.toArray(new String[0]), classPathOutputDir);

        String classPathStr = StringUtil.join(File.pathSeparator, classPath);
        if (!classPathStr.contains("swiftleap-common")) {
            throw new SystemErrorException("Unable to solve swiftleap-common in classpath. This happens when the class loader is not an URLClassLoaders");
        }

        List<String> options = new ArrayList<>();
        options.add("-proc:none");
        options.add("-d");
        options.add(classPathOutputDir);
        options.add("-classpath");
        options.add(classPathStr);

        log.debug("Using compiler options: " + StringUtil.join(" ", options));


        StringWriter output = new StringWriter();
        boolean success = jc
                .getTask(output, null, null, options, null, fileObjects)
                .call();
        if (success) {
            log.debug(output.toString());
            log.debug("Class has been successfully compiled");
        } else {
            throw new Exception("Compilation failed :" + toPrettyError(output));
        }

        File file = Paths.get(classPathOutputDir, (jsfs.getName().replace(".", File.separator)) + ".class").toFile();
        return file.toURI().toURL();
    }

    private static String toPrettyError(StringWriter output) {
        String res = output.toString();
        String[] lines = res.split("\\r?\\n");
        StringBuilder ret = new StringBuilder();
        for (String line : lines) {
            if (line.contains("javac processor")
                    || line.contains("JavacProcessingEnvironment")
                    || line.endsWith(" more")
                    || line.startsWith("Note:")
                    || line.endsWith(" warning")
                    || line.endsWith(" warnings")
                    || line.endsWith(" error")
                    || line.endsWith(" errors")
                    || line.trim().startsWith("at "))
                continue;
            ret.append(line).append('\n');
        }
        return ret.toString();
    }

    /**
     * Walk up the classloader chain to find all the jars.
     *
     * @param classLoader
     * @return
     */
    private static URL[] solveClassPath(ClassLoader classLoader) {
        URL[] urls = new URL[]{};
        if (classLoader == null)
            return urls;
        if (classLoader instanceof URLClassLoader) {
            urls = ((URLClassLoader) classLoader).getURLs();
        }
        return ArrayUtil.concat(urls, solveClassPath(classLoader.getParent()));
    }

    /**
     * Compiler Broken, it does not allow for jar nesting :(. Jars within Jars.
     *
     * @param urls
     */
    private static String[] extractJars(URL[] urls, String scratchDir) throws IOException {
        List<String> newUrls = new ArrayList<>();

        for (URL url : urls) {
            String path = url.getPath();
            path = path.replace("!/", "");
            String fileName = path.substring(path.lastIndexOf('/') + 1);

            if (!url.getProtocol().contains("jar")
                    || !fileName.contains("swiftleap-")
                    || !fileName.contains(".jar")) {
                newUrls.add(url.toString());
                continue;
            }

            File outputFile = new File(scratchDir + File.separator + fileName);

            newUrls.add(outputFile.getAbsolutePath());

            if (outputFile.exists()) {
                continue;
            }

            IOUtil.writeFileFully(outputFile, url.openStream());
        }
        return newUrls.toArray(new String[newUrls.size()]);
    }
}
