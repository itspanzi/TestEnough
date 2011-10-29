package testenough.counter;

import org.junit.Test;
import testenough.fortest.SampleProductionCodeClass;

import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

public class TrackTest {

    @Test
    public void testShouldTrackThisTestAsTestingAGivenMethod() throws Exception {
        SampleProductionCodeClass obj = new SampleProductionCodeClass();
        obj.sampleMethod();

        assertThat(Track.testsFor(Track.methodAsString("testenough.fortest.SampleProductionCodeClass", "sampleMethod")),
                hasItem(getClass().getName() + ":testShouldTrackThisTestAsTestingAGivenMethod"));
    }

}
