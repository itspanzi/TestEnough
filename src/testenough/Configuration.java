package testenough;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

public class Configuration {
    static final String POPULATE_INCLUDE_PACKAGES = "populateIncludePackages";
    private Properties properties = new Properties();
    private HashSet<String> packages = new HashSet<String>();

    public Configuration(String config) {
        parseConfig(config);
    }

    private void parseConfig(String config) {
        try {
            properties.load(new ByteArrayInputStream(config.getBytes()));
            populateIncludePackages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean shouldWeave(String className) {
        if (packages.isEmpty()) return true;
        className = convertToNormalName(className);
        for (String includePacakge : packages) {
            if (className.startsWith(includePacakge)) return true;
        }
        return false;
    }

    private void populateIncludePackages() {
        String includePackages = (String) properties.get(POPULATE_INCLUDE_PACKAGES);
        if (includePackages == null) return;
        for (String individualPackage : includePackages.split(",")) {
            packages.add(individualPackage.trim());
        }
    }

    private String convertToNormalName(String className) {
        return className.replaceAll("/", ".");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;
        return !(properties != null ? !properties.equals(that.properties) : that.properties != null);
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Configuration{properties=%s}", properties);
    }
}
