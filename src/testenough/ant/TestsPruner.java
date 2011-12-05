package testenough.ant;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import testenough.Configuration;
import testenough.counter.Track;
import testenough.scm.GitRepository;
import testenough.scm.ModifiedFile;

import java.util.*;

/**
 * @understands given a list of tests and SCM directory, prunes and returns a relevant list of tests
 */
public class TestsPruner extends FileSet {

    private static Configuration configuration;

    @Override
    public Iterator iterator() {
        if (configuration == null) return super.iterator();
        List<FileResource> list = new ArrayList<FileResource>();
        Iterator<FileResource> iterator = super.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return prune(list);
    }

    private Iterator<FileResource> prune(List<FileResource> list) {
        Set<FileResource> pruned = new LinkedHashSet<FileResource>();
        Set<String> allTests = new HashSet<String>();
        List<ModifiedFile> head = new GitRepository(configuration.repoLocation()).changesIn(configuration.revisionToUse());
        for (ModifiedFile modifiedFile : head) {
            Set<String> tests = Track.testsFor(modifiedFile.getFilePath());
            if (tests != null) {
                allTests.addAll(tests);
            }
        }
        for (FileResource resource : list) {
            if (allTests.contains(toClassName(resource))) {
                pruned.add(resource);
            }
            if (!Track.hasTest(toClassName(resource))) {
                pruned.add(resource);
            }
        }
        return pruned.iterator();
    }

    private String toClassName(FileResource resource) {
        return resource.getName().replace(".class", "").replaceAll("/", ".");
    }

    public void setConfiguration(Configuration configuration) {
        TestsPruner.configuration = configuration;
    }
}
