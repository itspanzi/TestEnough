package testenough.counter;

import org.apache.commons.io.FileUtils;
import testenough.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Track {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook()));
    }

    static Runnable shutdownHook() {
        return new Runnable() {
            public void run() {
                File file = new File(configuration.trackingInfoFilePath());
                try {
                    FileUtils.writeStringToFile(file, trackingInformation.trackingInfoToPersist());
                } catch (IOException e) {
                    throw new RuntimeException("Could not persist tracking information to file: " + file);
                }
            }
        };
    }

    private static TrackingInformation trackingInformation = new TrackingInformation();
    private static final int INDEX_OF_CALLER = 2;
    private static Configuration configuration;

    public static void trackCurrentThread() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String actualFrameName = methodAsString(stackTrace[INDEX_OF_CALLER]);
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().matches(testClassNamePattern())) {
                trackingInformation.trackTest(actualFrameName, stackTraceElement);
            }
        }
    }

    private static String testClassNamePattern() {
        return configuration.testClassNamePattern();
    }

    public static String methodAsString(String fqn, String method) {
        return fqn + ":" + method;
    }

    private static String methodAsString(StackTraceElement frame) {
        return methodAsString(frame.getClassName(), frame.getMethodName());
    }

    public static void setConfiguration(Configuration configuration) {
        Track.configuration = configuration;
    }

    public static Set<String> testsFor(String methodAsString) {
        return trackingInformation.get(methodAsString);
    }

    public static void reset() {
        trackingInformation.clear();
    }

    public static String trackingInfoToPersist() {
        return trackingInformation.trackingInfoToPersist();
    }

    public static void loadOldTrackingInfo() {
        trackingInformation.loadFrom(new File(configuration.trackingInfoFilePath()));
    }
}
