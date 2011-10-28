package testenough.weaver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import testenough.Configuration;
import testenough.fortest.ClassThatWillBeWeaved;
import testenough.fortest.TestTracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class BCWeaverTest {

    private File createdClass;
    private ClassLoader loader;
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = mock(Configuration.class);
    }

    @Test
    public void shouldNotWeaveIfNotConfiguredToBeWeaved() throws IllegalClassFormatException {
        String className = "foo/bar/HelloWorld";
        when(configuration.shouldWeave(className)).thenReturn(false);

        BCWeaver bcWeaver = new BCWeaver(configuration);
        assertThat(bcWeaver.weave(className, loader, new byte[0]), is(nullValue()));
    }

    @Test
    @Ignore("Still figuring out how to test this. Ignoring this for now.")
    public void testWeaveTheGivenCallInTheMethod() throws Exception {
        byte[] bytes = bytecodeFor(ClassThatWillBeWeaved.class);

        BCWeaver bcWeaver = new BCWeaver(null);
        byte[] weaved = bcWeaver.weave(null, loader, bytes);
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
