package testenough;

import org.junit.Before;
import org.junit.Test;
import testenough.weaver.BCWeaver;

import java.lang.instrument.IllegalClassFormatException;

import static org.mockito.Mockito.*;

public class CallTrackingTransformerTest {


    private Configuration configuration;
    private BCWeaver weaver;
    private CallTrackingTransformer transformer;
    private ClassLoader loader;
    private byte[] bytes;

    @Before
    public void setUp() throws Exception {
        configuration = mock(Configuration.class);
        weaver = mock(BCWeaver.class);
        transformer = new CallTrackingTransformer(configuration, weaver);
        loader = Thread.currentThread().getContextClassLoader();
        bytes = new byte[10];
    }

    @Test
    public void shouldWeaveIfConfiguredToBeWeaved() throws IllegalClassFormatException {
        when(configuration.shouldWeave("foo/bar/HelloWorld")).thenReturn(true);

        transformer.transform(loader, "foo/bar/HelloWorld", null, null, bytes);

        verify(weaver).weave(loader, bytes);
        verifyNoMoreInteractions(weaver);
    }

    @Test
    public void shouldNotWeaveIfNotConfiguredToBeWeaved() throws IllegalClassFormatException {
        when(configuration.shouldWeave("foo/bar/HelloWorld")).thenReturn(false);

        transformer.transform(loader, "foo/bar/HelloWorld", null, null, bytes);

        verifyNoMoreInteractions(weaver);
    }
}
