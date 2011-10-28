package testenough;

import org.apache.commons.io.FileUtils;
import testenough.weaver.BCWeaver;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class InstrumentingAgent {

    static final String CONFIG_FILE_PATH = "configFilePath";
    static final String LIB_DIR = "lib";
    private static final String DEFAULT_CONFIG_PATH = "./config.properties";
    private static final String DEFAULT_LIB_DIR = "lib";

    public static void premain(String argument, Instrumentation instrumentation) throws IOException {
        Map<String, String> nameToValues = parseArguments(argument);
        addJarsFromLibToBootstrapperClassPath(instrumentation, nameToValues);
        Configuration configuration = new Configuration(configContents(nameToValues));
        instrumentation.addTransformer(new CallTrackingTransformer(configuration, new BCWeaver(configuration)));
    }

    private static void addJarsFromLibToBootstrapperClassPath(Instrumentation instrumentation, Map<String, String> nameToValues) throws IOException {
        String libDir = nameToValues.get(LIB_DIR);
        if (libDir == null) libDir = DEFAULT_LIB_DIR;
        for (File jar : allJarsInTheDir(libDir)) {
            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(jar));
        }
    }

    private static Collection<File> allJarsInTheDir(String libDir) {
        return FileUtils.listFiles(new File(libDir), new String[]{"jar"}, true);
    }

    private static String configContents(Map<String, String> nameToValues) throws IOException {
        String pathname = nameToValues.get(CONFIG_FILE_PATH);
        if (pathname == null) {
            pathname = DEFAULT_CONFIG_PATH;
        }
        File config = new File(pathname);
        return !config.exists() ? "" : FileUtils.readFileToString(config);
    }

    private static Map<String, String> parseArguments(String argument) {
        Map<String, String> nameToValues = new HashMap<String, String>();
        if (argument.trim().isEmpty()) return nameToValues;
        for (String arg : argument.split("&")) {
            if (!arg.contains(":")) {
                throw new IllegalArgumentException("Arguments are name value pairs. Name and value are separated using ':'. Multiple arguments are separated using '&'");
            }
            String[] nameAndValue = arg.split(":");
            nameToValues.put(nameAndValue[0], nameAndValue[1]);
        }
        return nameToValues;
    }
}
