package testenough.counter;

import java.util.*;

public class Track {
    private static Map<String, Set<String>> methodToTests = new HashMap<String, Set<String>>();

    public static void trackCurrentThread() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String actualFrameName = methodAsString(stackTrace[2]);
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().endsWith("Test")) {
                Set<String> tests = methodToTests.get(actualFrameName);
                if (tests == null) {
                    tests = new TreeSet<String>();
                    methodToTests.put(actualFrameName, tests);
                }
                tests.add(methodAsString(stackTraceElement));
            }
        }
    }

    public static Set<String> testsFor(String methodAsString) {
        return methodToTests.get(methodAsString);
    }

    public static String methodAsString(String fqn, String method) {
        return fqn + ":" + method;
    }

    private static String methodAsString(StackTraceElement frame) {
        return methodAsString(frame.getClassName(), frame.getMethodName());
    }
}
