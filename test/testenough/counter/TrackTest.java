package testenough.counter;

import org.junit.Before;
import org.junit.Test;
import testenough.Configuration;
import testenough.fortest.SampleProductionCodeClass;

import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

public class TrackTest {

    @Before
    public void setUp() throws Exception {
        Track.setConfiguration(new Configuration(""));
        Track.reset();
    }

    @Test
    public void testShouldTrackThisTestClassAsTestingAGivenMethod() throws Exception {
        SampleProductionCodeClass obj = new SampleProductionCodeClass();
        obj.sampleMethod();

        Set<String> tests = Track.testsFor(Track.methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod"));
        assertThat(tests.size(), is(1));
        assertThat(tests, hasItem(getClass().getName()));
    }

    @Test
    public void testShouldTrackThisTestClassAsTestingAGivenMethodEvenWhenThereAreMultipleTestCaseCalls() throws Exception {
        testMethodThatCallsProductionCode();

        Set<String> tests = Track.testsFor(Track.methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod"));
        assertThat(tests.size(), is(1));
        assertThat(tests, hasItem(getClass().getName()));
    }

    private void testMethodThatCallsProductionCode() {
        SampleProductionCodeClass obj = new SampleProductionCodeClass();
        obj.sampleMethod();
    }

    @Test
    public void testShouldUseTheConfiguredPatternToIdentifyTestClass() throws Exception {
        Track.setConfiguration(new Configuration(String.format("%s=.*NotPresent", Configuration.TEST_CLASS_PATTERN)));

        SampleProductionCodeClass obj = new SampleProductionCodeClass();
        obj.sampleMethod();

        assertThat(Track.testsFor(Track.methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod")), is(nullValue()));

        Track.setConfiguration(new Configuration(String.format("%s=.*CodeClass", Configuration.TEST_CLASS_PATTERN)));

        obj.sampleMethod();

        assertThat(Track.testsFor(Track.methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod")),
                hasItem(obj.getClass().getName()));
    }
}
