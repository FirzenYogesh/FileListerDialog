package yogesh.firzen.filelister;

import java.io.File;

public interface OnFileSelectionChangedListener {
    void onFileSelected(File file, String path);
}
