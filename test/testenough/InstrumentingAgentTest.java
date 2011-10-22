package testenough;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import testenough.weaver.BCWeaver;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class InstrumentingAgentTest {


    private File config;
    private Instrumentation instrumentation;

    @Before
    public void setUp() throws Exception {
        config = new File("config.properties");
        FileUtils.write(config, configFileContents());
        FileUtils.forceDeleteOnExit(config);
        instrumentation = mock(Instrumentation.class);
    }

    @After
    public void tearDown() throws Exception {
        verify(instrumentation, atLeastOnce()).appendToBootstrapClassLoaderSearch(any(JarFile.class));
        verifyNoMoreInteractions(instrumentation);
        FileUtils.forceDelete(config);
    }

    @Test
    public void shouldAddANewClassTransformer() throws IOException {
        InstrumentingAgent.premain(String.format("%s:resource/config.properties&%s:.", InstrumentingAgent.CONFIG_FILE_PATH, InstrumentingAgent.LIB_DIR), instrumentation);

        Configuration configuration = new Configuration(configFileContents());
        verify(instrumentation).addTransformer(new CallTrackingTransformer(configuration, new BCWeaver()));

    }

    private String configFileContents() {
        return String.format("%s=tlb.foo", Configuration.POPULATE_INCLUDE_PACKAGES);
    }

    @Test
    public void shouldDefaultToConfigFileIfNotSpecified() throws IOException {
        InstrumentingAgent.premain(String.format("%s:.", InstrumentingAgent.LIB_DIR), instrumentation);

        Configuration configuration = new Configuration(configFileContents());
        verify(instrumentation).addTransformer(new CallTrackingTransformer(configuration, new BCWeaver()));
    }

    @Test
    public void shouldUseAnEmptyConfigurationWhenTheConfigFileIsNotFound() throws IOException {
        InstrumentingAgent.premain(String.format("%s:foo_bar&%s:.", InstrumentingAgent.CONFIG_FILE_PATH, InstrumentingAgent.LIB_DIR), instrumentation);

        Configuration configuration = new Configuration("");
        verify(instrumentation).addTransformer(new CallTrackingTransformer(configuration, new BCWeaver()));
    }

    @Test
    public void testShouldAddTheJarsInLibDirToBootstrapperClasspath() throws Exception {
        InstrumentingAgent.premain(String.format("%s:test_folder", InstrumentingAgent.LIB_DIR), instrumentation);
        Configuration configuration = new Configuration(configFileContents());
        verify(instrumentation).addTransformer(new CallTrackingTransformer(configuration, new BCWeaver()));
    }

    @Test
    public void shouldDefaultTheLibFolder() throws IOException {
        InstrumentingAgent.premain("", instrumentation);
        Configuration configuration = new Configuration(configFileContents());
        verify(instrumentation).addTransformer(new CallTrackingTransformer(configuration, new BCWeaver()));
    }
}
