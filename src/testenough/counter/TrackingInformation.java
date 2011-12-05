package testenough.counter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @understands method to tests mapping
*/
public class TrackingInformation {

    private static final Pattern VALUES_REGEX = Pattern.compile("\\[(.*)\\]");
    private Map<String, Set<String>> methodToTests = new HashMap<String, Set<String>>();

    public void trackTest(String actualFrameName, StackTraceElement stackTraceElement) {
        Set<String> tests = methodToTests.get(actualFrameName);
        if (tests == null) {
            tests = new TreeSet<String>();
            methodToTests.put(actualFrameName, tests);
        }
        tests.add(stackTraceElement.getClassName());
    }

    public String trackingInfoToPersist() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Set<String>> codeToTests : methodToTests.entrySet()) {
            builder.append(codeToTests.getKey()).append("=>").append("[");
            for (String test : codeToTests.getValue()) {
                builder.append(test).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("]\n");
        }
        return builder.toString();
    }

    public Set<String> get(String methodAsString) {
        return methodToTests.get(methodAsString);
    }

    public void clear() {
        methodToTests.clear();
    }

    public void loadFrom(File filePath) {
        try {
            loadFrom(FileUtils.readLines(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFrom(List<String> methodToTestsList) {
        for (String line : methodToTestsList) {
            String[] keyAndValues = line.split("=>");
            Matcher matcher = VALUES_REGEX.matcher(keyAndValues[1]);
            String[] values = null;
            if (matcher.matches()) {
                values = matcher.group(1).split(",");
            }
            methodToTests.put(keyAndValues[0], values(values));
        }
    }

    private TreeSet<String> values(String[] values) {
        TreeSet<String> vals = new TreeSet<String>();
        Collections.addAll(vals, values);
        return vals;
    }

    public boolean hasTest(String testClass) {
        Set<String> tests = new LinkedHashSet<String>();
        for (Set<String> value : methodToTests.values()) {
            tests.addAll(value);
        }
        return tests.contains(testClass);
    }
}
