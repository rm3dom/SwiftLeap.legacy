package org.swiftleap.common.codegen;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public class JavaSourceCompilerTest {

    class TestCompiler extends AbstractJavaSourceCompiler {}

    @Test
    public void testCompile() throws Exception {
        String workingDir = Paths.get(System.getProperty("user.dir"), "target").toString();
        String outFile = "TestCompile.class";
        new TestCompiler().compileCode(
                System.getProperty("user.dir"),
                Thread.currentThread().getContextClassLoader(),
                workingDir,
                "TestCompile.class",
                "class TestCompile{}\n");
        Assert.assertTrue(Paths.get(workingDir, outFile).toFile().exists());
    }
}