package testenough.weaver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import testenough.fortest.ClassThatWillBeWeaved;
import testenough.fortest.TestTracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertThat;

public class BCWeaverTest {

    private File createdClass;
    private ClassLoader loader;

    @Before
    public void setUp() throws Exception {
        loader = Thread.currentThread().getContextClassLoader();
        String name = className(this.getClass());
        URL resource = loader.getResource(name);
        createdClass = new File(new File(resource.getFile()).getParentFile(), ClassThatWillBeWeaved.class.getSimpleName() + ".class");
        TestTracker.counter = 0;
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(createdClass);
    }

    @Test
    @Ignore("Still figuring out how to test this. Ignoring this for now.")
    public void testWeaveTheGivenCallInTheMethod() throws Exception {
        byte[] bytes = bytecodeFor(ClassThatWillBeWeaved.class);

        BCWeaver bcWeaver = new BCWeaver();
        byte[] weaved = bcWeaver.weave(loader, bytes);
        FileUtils.writeByteArrayToFile(createdClass, weaved);

        Class<?> createdClass = loader.loadClass("testenough.weaver.ClassThatWillBeWeaved");
        createdClass.getMethod("method1").invoke(null);
        assertThat(TestTracker.counter, Is.is(1));
    }

    private byte[] bytecodeFor(Class<ClassThatWillBeWeaved> klass) throws IOException {
        InputStream is = loader.getResourceAsStream(className(klass));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(is, out);
        return out.toByteArray();
    }

    private <T> String className(Class<T> klass) {
        String className = klass.getCanonicalName();
        return className.replaceAll("\\.", "/") + ".class";
    }
}
