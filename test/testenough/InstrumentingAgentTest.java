package testenough;

import org.junit.Test;
import testenough.weaver.BCWeaver;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

import static org.mockito.Mockito.*;

public class InstrumentingAgentTest {

    @Test
    public void shouldAddANewClassTransformer() throws IOException {
        Instrumentation instrumentation = mock(Instrumentation.class);
        InstrumentingAgent.agentMain(String.format("%s:resource/config.properties&%s:.", InstrumentingAgent.CONFIG_FILE_PATH, InstrumentingAgent.LIB_DIR), instrumentation);

        Configuration configuration = new Configuration(String.format("%s=tlb.foo", Configuration.POPULATE_INCLUDE_PACKAGES));
        verify(instrumentation).addTransformer(new CallTrackingTransformer(configuration, new BCWeaver()));

        verifyNoMoreInteractions(instrumentation);
    }
}
