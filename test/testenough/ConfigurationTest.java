package testenough;

import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {

    @Test
    public void shouldReturnTrueIfAClassShouldBeInstrumented() {
        Configuration configuration = new Configuration(String.format("%s:com.foo, com.bar, com.tw.go", Configuration.POPULATE_INCLUDE_PACKAGES));
        assertThat(configuration.shouldWeave("com/foo/Anything"), is(true));
        assertThat(configuration.shouldWeave("com/foo/something/else/Anything"), is(true));
        assertThat(configuration.shouldWeave("com/bar/Something"), is(true));
        assertThat(configuration.shouldWeave("com/tw/go/Another"), is(true));
        assertThat(configuration.shouldWeave("com/tw/DoesNotMatch"), is(false));
        assertThat(configuration.shouldWeave("org/tw/DoesNotMatch"), is(false));
    }

    @Test
    public void shouldNotBombWhenPackagesToWeaveIsNotSpecified() {
        Configuration configuration = new Configuration(String.format("foo=bar"));
        assertThat(configuration.shouldWeave("com/foo/Anything"), is(true));
        assertThat(configuration.shouldWeave("com/foo/something/else/Anything"), is(true));
        assertThat(configuration.shouldWeave("com/bar/Something"), is(true));
        assertThat(configuration.shouldWeave("com/tw/go/Another"), is(true));
        assertThat(configuration.shouldWeave("com/tw/DoesNotMatch"), is(true));
        assertThat(configuration.shouldWeave("org/tw/DoesNotMatch"), is(true));
    }

    @Test
    public void testReturnTheNameOfAClassInProduction() throws Exception {
        Configuration configuration = new Configuration(String.format("%s=com.sample.SimpleClass", Configuration.SAMPLE_PRODUCTION_CLASS));
        assertThat(configuration.sampleClassFromProd(), is("com.sample.SimpleClass"));
    }

    @Test
    public void testThrowExceptionWhenTheNameOfAClassInProductionIsNotMentioned() throws Exception {
        Configuration configuration = new Configuration(String.format("foo=bar"));
        try {
            configuration.sampleClassFromProd();
            fail("Should have failed because the sample production class name is not given");
        } catch (Exception e) {
            assertThat(e.getMessage(), is(String.format("Sample production class property is not specified. Please specify the '%s=com.org.SampleClass' in the configuration " +
                    "where 'com.org.SampleClass' is the Fully Qualified Name of one class from your application under test.This is used to figure out the class path of the " +
                    "production classes dynamically.", Configuration.SAMPLE_PRODUCTION_CLASS)));
        }
    }

    @Test
    public void testShouldGiveTheCodeToInserted() throws Exception {
        Configuration configuration = new Configuration(String.format("foo=bar"));
        assertThat(configuration.codeToBeInserted(), is("testenough.counter.Track.trackCurrentThread();"));
        String code = "System.out.println(\"Hello World\");";
        configuration = new Configuration(String.format("%s=%s", Configuration.CODE_TO_INSERT, code));
        assertThat(configuration.codeToBeInserted(), is(code));
    }

    @Test
    public void testShouldGiveTheFileToPersistTrackingInformationIn() throws Exception {
        Configuration configuration = new Configuration(String.format("%s=sample/tracking_info_file", Configuration.TRACKING_INFO_FILE_PATH));
        assertThat(configuration.trackingInfoFilePath(), is("sample/tracking_info_file"));
        configuration = new Configuration("");
        assertThat(configuration.trackingInfoFilePath(), is("out/te_tracking_info.txt"));
    }
}
