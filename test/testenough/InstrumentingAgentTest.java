package testenough;

import org.junit.Test;
import testenough.weaver.BCWeaver;

import java.lang.instrument.Instrumentation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class InstrumentingAgentTest {

    @Test
    public void shouldAddANewClassTransformer() {
        Instrumentation instrumentation = mock(Instrumentation.class);
        InstrumentingAgent.agentMain("args", instrumentation);

        Configuration configuration = new Configuration("");
        verify(instrumentation).addTransformer(new CallTrackingTransformer(configuration, new BCWeaver()));

        verifyNoMoreInteractions(instrumentation);
    }
}
