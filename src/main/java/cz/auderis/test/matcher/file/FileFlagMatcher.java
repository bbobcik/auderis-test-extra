package cz.auderis.test.matcher.file;

import cz.auderis.test.support.NaturalDescriptionJoiner;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class FileFlagMatcher extends TypeSafeMatcher<File> {

    private final Set<FileFlags> requiredFlags;
    private final Set<FileFlags> forbiddenFlags;

    public FileFlagMatcher(Set<FileFlags> requiredFlags, Set<FileFlags> forbiddenFlags) {
        super(File.class);
        boolean checkIntersection = true;
        if ((null != requiredFlags) && !requiredFlags.isEmpty()) {
            this.requiredFlags = EnumSet.copyOf(requiredFlags);
        } else {
            this.requiredFlags = EnumSet.noneOf(FileFlags.class);
            checkIntersection = false;
        }
        if ((null != forbiddenFlags) && !forbiddenFlags.isEmpty()) {
            this.forbiddenFlags = EnumSet.copyOf(forbiddenFlags);
        } else {
            this.forbiddenFlags = EnumSet.noneOf(FileFlags.class);
            checkIntersection = false;
        }
        if (checkIntersection) {
            final EnumSet<FileFlags> intersection = EnumSet.copyOf(requiredFlags);
            intersection.retainAll(forbiddenFlags);
            if (!intersection.isEmpty()) {
                throw new IllegalArgumentException("Flags specified both as required and forbidden: " + intersection);
            }
        }
    }


    @Override
    protected boolean matchesSafely(File file) {
        for (FileFlags flag : requiredFlags) {
            if (!hasFlag(file, flag)) {
                return false;
            }
        }
        for (FileFlags flag : forbiddenFlags) {
            if (hasFlag(file, flag)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("file");
        final NaturalDescriptionJoiner joiner = new NaturalDescriptionJoiner(" that ", ", ", " and ", null);
        for (FileFlags flag : FileFlags.values()) {
            if (requiredFlags.contains(flag)) {
                joiner.add(flag.getDescription(true));
            } else if (forbiddenFlags.contains(flag)) {
                joiner.add(flag.getDescription(false));
            }
        }
        joiner.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(File file, Description mismatchDescription) {
        mismatchDescription.appendText("file ");
        mismatchDescription.appendValue(file);
        final NaturalDescriptionJoiner joiner = new NaturalDescriptionJoiner(" ",", ", " and ", null);
        for (FileFlags flag : FileFlags.values()) {
            if (requiredFlags.contains(flag) && !hasFlag(file, flag)) {
                joiner.add(flag.getDescription(false));
            } else if (forbiddenFlags.contains(flag) && hasFlag(file, flag)) {
                joiner.add(flag.getDescription(true));
            }
        }
        joiner.describeTo(mismatchDescription);
    }

    private static boolean hasFlag(File f, FileFlags flag) {
        final boolean result;
        switch (flag) {
            case EXISTS:
                result = f.exists();
                break;
            case READABLE:
                result = f.canRead();
                break;
            case WRITABLE:
                result = f.canWrite();
                break;
            case EXECUTABLE:
                result = f.canExecute();
                break;
            case DIRECTORY:
                result = f.isDirectory();
                break;
            case NORMAL_FILE:
                result = f.isFile();
                break;
            case HIDDEN:
                result = f.isHidden();
                break;
            default:
                throw new AssertionError();
        }
        return result;
    }

}
