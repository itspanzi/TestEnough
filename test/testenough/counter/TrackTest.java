package testenough.counter;

import org.junit.Before;
import org.junit.Test;
import testenough.Configuration;
import testenough.fortest.FakeTestClass;
import testenough.fortest.SampleProductionCodeClass;

import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;
import static org.junit.internal.matchers.StringContains.containsString;
import static testenough.counter.Track.methodAsString;

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

        Set<String> tests = Track.testsFor(methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod"));
        assertThat(tests.size(), is(1));
        assertThat(tests, hasItem(getClass().getName()));
    }

    @Test
    public void testShouldTrackMultipleTestsForTheSameMethod() throws Exception {
        Track.setConfiguration(config(".*Test|.*TestClass"));
        FakeTestClass.callTracker();

        Set<String> tests = Track.testsFor(methodAsString(FakeTestClass.class.getName(), "callTracker"));
        assertThat(tests.size(), is(2));
        assertThat(tests, hasItem(getClass().getName()));
        assertThat(tests, hasItem(FakeTestClass.class.getName()));
    }

    @Test
    public void testShouldTrackThisTestClassAsTestingAGivenMethodEvenWhenThereAreMultipleTestCaseCalls() throws Exception {
        testMethodThatCallsProductionCode();

        Set<String> tests = Track.testsFor(methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod"));
        assertThat(tests.size(), is(1));
        assertThat(tests, hasItem(getClass().getName()));
    }

    private void testMethodThatCallsProductionCode() {
        SampleProductionCodeClass obj = new SampleProductionCodeClass();
        obj.sampleMethod();
    }

    @Test
    public void testShouldUseTheConfiguredPatternToIdentifyTestClass() throws Exception {
        Track.setConfiguration(config(".*NotPresent"));

        SampleProductionCodeClass obj = new SampleProductionCodeClass();
        obj.sampleMethod();

        assertThat(Track.testsFor(methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod")), is(nullValue()));

        Track.setConfiguration(new Configuration(String.format("%s=.*CodeClass", Configuration.TEST_CLASS_PATTERN)));

        obj.sampleMethod();

        assertThat(Track.testsFor(methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod")),
                hasItem(obj.getClass().getName()));
    }

    private Configuration config(String regex) {
        return new Configuration(String.format("%s=%s", Configuration.TEST_CLASS_PATTERN, regex));
    }

    @Test
    public void testShouldGiveTheTextThatItWillPersist() throws Exception {
        Track.setConfiguration(config(".*Test|.*TestClass"));
        SampleProductionCodeClass obj = new SampleProductionCodeClass();
        obj.sampleMethod();
        obj.anotherMethod();
        new SampleProductionCodeClass.AnInnerClass().innerMethod();
        FakeTestClass.callTracker();

        String data = Track.trackingInfoToPersist();

        assertThat(data, containsString(String.format("%s=>[%s]", methodAsString(SampleProductionCodeClass.class.getName(), "sampleMethod"), getClass().getName())));
        assertThat(data, containsString(String.format("%s=>[%s]", methodAsString(SampleProductionCodeClass.class.getName(), "anotherMethod"), getClass().getName())));
        assertThat(data, containsString(String.format("%s=>[%s]", methodAsString(SampleProductionCodeClass.AnInnerClass.class.getName(), "innerMethod"), getClass().getName())));
        assertThat(data, containsString(String.format("%s=>[%s,%s]", methodAsString(FakeTestClass.class.getName(), "callTracker"), getClass().getName(), FakeTestClass.class.getName())));
    }
}
