package testenough;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {

    @Test
    public void shouldReturnTrueIfAClassShouldBeInstrumented() {
        Configuration configuration = new Configuration("first_line\ninclude:com.foo, com.bar, com.tw.go\nlast_line");
        assertThat(configuration.shouldWeave("com/foo/Anything"), is(true));
        assertThat(configuration.shouldWeave("com/foo/something/else/Anything"), is(true));
        assertThat(configuration.shouldWeave("com/bar/Something"), is(true));
        assertThat(configuration.shouldWeave("com/tw/go/Another"), is(true));
        assertThat(configuration.shouldWeave("com/tw/DoesNotMatch"), is(false));
        assertThat(configuration.shouldWeave("org/tw/DoesNotMatch"), is(false));
    }
}
