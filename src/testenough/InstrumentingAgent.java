package testenough;

import testenough.weaver.BCWeaver;

import java.lang.instrument.Instrumentation;

public class InstrumentingAgent {

    public static void agentMain(String arguments, Instrumentation instrumentation) {
        instrumentation.addTransformer(new CallTrackingTransformer(new Configuration(""), new BCWeaver()));
    }
}
