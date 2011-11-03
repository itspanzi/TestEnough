package testenough.scm;

public class ModifiedFile {

    public static enum ModificationAction {
        ADD, DELETE, MODIFY, RENAME
    }
    private String filePath;
    private ModificationAction action;

    public ModifiedFile(String filePath, ModificationAction action) {
        this.filePath = filePath;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModifiedFile that = (ModifiedFile) o;

        if (action != that.action) return false;
        if (filePath != null ? !filePath.equals(that.filePath) : that.filePath != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = filePath != null ? filePath.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }
}
