package testenough;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testenough.counter.Track;
import testenough.counter.TrackingInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestsPrunerTest {

    private File file;
    private TestsPruner testsPruner;

    @Before
    public void setUp() throws Exception {
        file = new File("org");
        file.mkdirs();
        FileUtils.write(new File(file, "tw/foo/First.class"), "simple");
        FileUtils.write(new File(file, "tw/bar/Second.class"), "simple");

        testsPruner = new TestsPruner();
        testsPruner.setProject(new Project());
        testsPruner.setDir(file);
        testsPruner.setIncludes("**/*");
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(file);
        Track.reset();
    }

    @Test
    public void shouldReturnAllTestsIfTrackingInformationIsNotSet() throws Exception {
        List<FileResource> resources = asList(testsPruner.iterator());
        assertThat(resources.size(), is(2));
    }

    @Test
    public void shouldReturnOnlyTestsThatTestTheCurrentModifications() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.repoLocation()).thenReturn(".");
        when(configuration.revisionToUse()).thenReturn("5a408511d18bb57553289c8c1886c10a23cd17ae");
        useTrackingInformation(Arrays.asList("release/README=>[tw.foo.First]", "build.xml=>[tw.foo.First]", "doesNotMatter=>[tw.bar.Second]"));

        testsPruner.setConfiguration(configuration);

        List<FileResource> resources = asList(testsPruner.iterator());

        assertThat(resources.size(), is(1));
        assertThat(resources.get(0).getName(), is("tw/foo/First.class"));
    }

    @Test
    public void shouldReturnTestsThatTheTrackerDoesNotKnowAbout() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.repoLocation()).thenReturn(".");
        when(configuration.revisionToUse()).thenReturn("5a408511d18bb57553289c8c1886c10a23cd17ae");
        useTrackingInformation(Arrays.asList("release/README=>[tw.foo.First]", "build.xml=>[tw.bar.Second]", "doesNotMatter=>[foo]"));

        FileUtils.write(new File(file, "tw/bar/NewTest.class"), "simple");

        testsPruner.setConfiguration(configuration);

        List<FileResource> resources = asList(testsPruner.iterator());

        assertThat(resources.size(), is(3));
    }

    private void useTrackingInformation(List<String> strings) {
        TrackingInformation trackingInformation = new TrackingInformation();
        trackingInformation.loadFrom(strings);
        Track.setTrackingInformation(trackingInformation);
    }

    private List<FileResource> asList(Iterator iterator) {
        List<FileResource> resources = new ArrayList<FileResource>();
        while (iterator.hasNext()) {
            resources.add((FileResource) iterator.next());
        }
        return resources;
    }
}
