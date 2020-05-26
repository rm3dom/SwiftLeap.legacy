package org.swiftleap.common.codegen;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class JavaSourceCompilerTest {

    class TestCompiler extends AbstractJavaSourceCompiler {

        protected TestCompiler(String scratchDir, ClassLoader classLoader, Class<?>... classPath) throws IOException {
            super(scratchDir, classLoader, classPath);
        }
    }

    @Test
    public void testCompile() throws Exception {
        String workingDir = Paths.get(System.getProperty("user.dir"), "target").toString();
        String outFile = "TestCompile.class";
        new TestCompiler(workingDir,
                Thread.currentThread().getContextClassLoader(),
                this.getClass()).compileCode(
                workingDir,
                "TestCompile.class",
                "class TestCompile{}\n");
        Assert.assertTrue(Paths.get(workingDir, outFile).toFile().exists());
    }
}