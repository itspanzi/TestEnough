package testenough;

import org.junit.Test;

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
}
