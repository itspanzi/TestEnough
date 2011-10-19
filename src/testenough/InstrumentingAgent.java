package testenough;

import org.apache.commons.io.FileUtils;
import testenough.weaver.BCWeaver;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

public class InstrumentingAgent {

    static final String CONFIG_FILE_PATH = "configFilePath";
    static final String LIB_DIR = "lib";

    public static void agentMain(String argument, Instrumentation instrumentation) throws IOException {
        Map<String, String> nameToValues = parseArguments(argument);
        instrumentation.addTransformer(new CallTrackingTransformer(new Configuration(configContents(nameToValues)), new BCWeaver()));
    }

    private static String configContents(Map<String, String> nameToValues) throws IOException {
        return FileUtils.readFileToString(new File(nameToValues.get(CONFIG_FILE_PATH)));
    }

    private static Map<String, String> parseArguments(String argument) {
        Map<String, String> nameToValues = new HashMap<String, String>();
        for (String arg : argument.split("&")) {
            String[] nameAndValue = arg.split(":");
            nameToValues.put(nameAndValue[0], nameAndValue[1]);
        }
        return nameToValues;
    }
}
