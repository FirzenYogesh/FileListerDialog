package yogesh.firzen.filelister;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.io.File;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by S. Yogesh on 9/7/17.
 */

public class FileListerDialog {

    public enum FILE_FILTER {ALL_FILES, DIRECTORY_ONLY, IMAGE_ONLY, VIDEO_ONLY, AUDIO_ONLY}

    private AlertDialog alertDialog;

    private FilesListerView filesListerView;

    private OnFileSelectedListener onFileSelectedListener;

    private FileListerDialog(@NonNull Context context) {
        //super(context);
        alertDialog = new AlertDialog.Builder(context).create();
        init(context);
    }

    private FileListerDialog(@NonNull Context context, int themeResId) {
        //super(context, themeResId);
        alertDialog = new AlertDialog.Builder(context, themeResId).create();
        init(context);
    }

    public static FileListerDialog createFileListerDialog(@NonNull Context context) {
        return new FileListerDialog(context);
    }

    public static FileListerDialog createFileListerDialog(@NonNull Context context, int themeId) {
        return new FileListerDialog(context, themeId);
    }

    private void init(Context context) {
        filesListerView = new FilesListerView(context);
        alertDialog.setView(filesListerView);
        alertDialog.setButton(BUTTON_POSITIVE, "Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (onFileSelectedListener != null)
                    onFileSelectedListener.onFileSelected(filesListerView.getSelected(), filesListerView.getSelected().getAbsolutePath());
            }
        });
        alertDialog.setButton(BUTTON_NEUTRAL, "Default Dir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //filesListerView.goToDefaultDir();
            }
        });
        alertDialog.setButton(BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    public void show() {
        //getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        switch (filesListerView.getFileFilter()) {
            case DIRECTORY_ONLY:
                alertDialog.setTitle("Select a directory");
                break;
            case VIDEO_ONLY:
                alertDialog.setTitle("Select a Video file");
                break;
            case IMAGE_ONLY:
                alertDialog.setTitle("Select an Image file");
                break;
            case AUDIO_ONLY:
                alertDialog.setTitle("Select an Audio file");
                break;
            case ALL_FILES:
                alertDialog.setTitle("Select a file");
                break;
        }
        filesListerView.start();
        alertDialog.show();
        alertDialog.getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filesListerView.goToDefaultDir();
            }
        });
    }

    public void setOnFileSelectedListener(OnFileSelectedListener onFileSelectedListener) {
        this.onFileSelectedListener = onFileSelectedListener;
    }

    public void setDefaultDir(File file) {
        filesListerView.setDefaultDir(file);
    }

    public void setDefaultDir(String file) {
        filesListerView.setDefaultDir(file);
    }

    public void setFileFilter(FILE_FILTER fileFilter) {
        filesListerView.setFileFilter(fileFilter);
    }

}
