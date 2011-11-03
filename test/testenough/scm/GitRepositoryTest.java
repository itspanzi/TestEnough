package testenough.scm;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GitRepositoryTest {

    @Test
    public void testShouldReturnAListOfModifiedFilesInAGivenRevision() throws Exception {
        List<ModifiedFile> modifiedFiles = new GitRepository(".").changesIn("5a408511d18bb57553289c8c1886c10a23cd17ae");
        assertThat(modifiedFiles.size(), is(2));
        assertThat(modifiedFiles.get(0), is(new ModifiedFile("build.xml", ModifiedFile.ModificationAction.MODIFY)));
        assertThat(modifiedFiles.get(1), is(new ModifiedFile("release/README", ModifiedFile.ModificationAction.ADD)));
    }
}
