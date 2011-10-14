package testenough;

import java.util.HashSet;
import java.util.Set;

public class Configuration {
    private Set<String> includePacakges = new HashSet<String>();

    public Configuration(String config) {
        parseConfig(config);
    }

    private void parseConfig(String config) {
        String[] lines = config.split("\n");
        for (String line : lines) {
            handlePackageInclude(line);
        }
    }

    private void handlePackageInclude(String line) {
        if (line.trim().startsWith("include:")) {
            String packages = line.substring("include:".length());
            for (String individualPackage : packages.split(",")) {
                includePacakges.add(individualPackage.trim());
            }
        }
    }

    public boolean shouldWeave(String className) {
        className = convertToNormalName(className);
        for (String includePacakge : includePacakges) {
            if (className.startsWith(includePacakge)) return true;
        }
        return false;
    }

    private String convertToNormalName(String className) {
        return className.replaceAll("/", ".");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        if (includePacakges != null ? !includePacakges.equals(that.includePacakges) : that.includePacakges != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return includePacakges != null ? includePacakges.hashCode() : 0;
    }
}
