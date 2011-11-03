package testenough.scm;

import com.thoughtworks.studios.javaexec.CommandExecutor;
import com.thoughtworks.studios.javaexec.LineHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitRepository {

    private File path;

    public GitRepository() {
        this(null);
    }

    public GitRepository(String path) {
        this.path = path != null ? new File(path) : null;
    }

    public List<ModifiedFile> changesIn(String revision) {
        final boolean[] isFirstLine = {true};
        final List<ModifiedFile> files = new ArrayList<ModifiedFile>();
        new CommandExecutor(Arrays.asList("git", "diff-tree", "--name-status", "--root", "-r", revision), path).run(new LineHandler() {
            public void handleLine(String s) {
                if (isFirstLine[0]) {
                    isFirstLine[0] = false;
                    return;
                }
                String[] actionAndPath = s.split("\\s+");
                files.add(new ModifiedFile(actionAndPath[1], toAction(actionAndPath[0])));
            }

            private ModifiedFile.ModificationAction toAction(String actionString) {
                if ("A".equals(actionString)) return ModifiedFile.ModificationAction.ADD;
                if ("M".equals(actionString)) return ModifiedFile.ModificationAction.MODIFY;
                if ("R".equals(actionString)) return ModifiedFile.ModificationAction.RENAME;
                if ("D".equals(actionString)) return ModifiedFile.ModificationAction.DELETE;
                return ModifiedFile.ModificationAction.MODIFY;
            }
        });
        return files;
    }
}
