package cz.auderis.test.matcher.file;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public enum FileFlags {

    EXISTS,

    READABLE,

    WRITABLE,

    EXECUTABLE,

    DIRECTORY,

    NORMAL_FILE,

    HIDDEN
    ;


    public String getDescription(boolean mode) {
        final FlagDesc flagDesc = DESCRIPTION.get(this);
        assert null != flagDesc;
        return mode ? flagDesc.positiveDesc : flagDesc.negativeDesc;
    }

    private static final Map<FileFlags, FlagDesc> DESCRIPTION;
    static {
        DESCRIPTION = new EnumMap<>(FileFlags.class);
        DESCRIPTION.put(EXISTS, new FlagDesc("exists", "does not exist"));
        DESCRIPTION.put(READABLE, new FlagDesc("is readable", "is not readable"));
        DESCRIPTION.put(WRITABLE, new FlagDesc("is writable", "is not writable"));
        DESCRIPTION.put(EXECUTABLE, new FlagDesc("is executable", "is not executable"));
        DESCRIPTION.put(DIRECTORY, new FlagDesc("is a directory", "is not a directory"));
        DESCRIPTION.put(NORMAL_FILE, new FlagDesc("is a normal file", "is not a normal file"));
        DESCRIPTION.put(HIDDEN, new FlagDesc("is hidden", "is not hidden"));
    }

    static final class FlagDesc {
        final String positiveDesc;
        final String negativeDesc;

        FlagDesc(String positiveDesc, String negativeDesc) {
            this.positiveDesc = positiveDesc;
            this.negativeDesc = negativeDesc;
        }
    }

}
