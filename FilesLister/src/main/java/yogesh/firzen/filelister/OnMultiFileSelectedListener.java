package yogesh.firzen.filelister;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by root on 4/9/17.
 */

public interface OnMultiFileSelectedListener {
    void onFilesSelected(ArrayList<File> files);
}
