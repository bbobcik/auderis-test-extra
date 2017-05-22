package cz.auderis.test.support.editor;

import java.beans.PropertyEditorManager;
import java.util.Arrays;
import java.util.List;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public final class AdditionalPropertyEditors {

    private static boolean registered = false;

    public static void register() {
        synchronized (PropertyEditorManager.class) {
            if (!registered) {
                final String currentPackage = AdditionalPropertyEditors.class.getPackage().getName();
                final List<String> origSearchPath = Arrays.asList(PropertyEditorManager.getEditorSearchPath());
                if (!origSearchPath.contains(currentPackage)) {
                    final String[] newSearchPath = origSearchPath.toArray(new String[origSearchPath.size() + 1]);
                    newSearchPath[origSearchPath.size()] = currentPackage;
                    PropertyEditorManager.setEditorSearchPath(newSearchPath);
                }
                registered = true;
            }
        }
    }

    private AdditionalPropertyEditors() {
        throw new AssertionError();
    }

}
