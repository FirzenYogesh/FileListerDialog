package yogesh.firzen.filelister;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

/**
 * Created by S. Yogesh on 9/7/17.
 */

public class FileListerDialog extends AlertDialog {

    public enum FILE_FILTER {ALL_FILES, DIRECTORY_ONLY, IMAGE_ONLY, VIDEO_ONLY, AUDIO_ONLY}

    private FilesListerView filesListerView;

    private Context context;

    private OnFileSelectedListener onFileSelectedListener;

    private FileListerDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private FileListerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init(context);
    }

    public static FileListerDialog createFileListerDialog(@NonNull Context context) {
        return new FileListerDialog(context);
    }

    public static FileListerDialog createFileListerDialog(@NonNull Context context, int themeId) {
        return new FileListerDialog(context, themeId);
    }

    private void init(Context context) {
        filesListerView = new FilesListerView(getContext());
        setButton(BUTTON_POSITIVE, "Select", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (onFileSelectedListener != null)
                    onFileSelectedListener.onFileSelected(filesListerView.getSelected(), filesListerView.getSelected().getAbsolutePath());
            }
        });
        setButton(BUTTON_NEUTRAL, "Default Dir", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //filesListerView.goToDefaultDir();
            }
        });
        setButton(BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        setView(filesListerView);
    }

    @Override
    public void show() {
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        switch (filesListerView.getFileFilter()) {
            case DIRECTORY_ONLY:
                setTitle("Select a directory");
                break;
            case VIDEO_ONLY:
                setTitle("Select a Video file");
                break;
            case IMAGE_ONLY:
                setTitle("Select an Image file");
                break;
            case AUDIO_ONLY:
                setTitle("Select an Audio file");
                break;
            case ALL_FILES:
                break;
        }
        filesListerView.start();
        super.show();
        getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
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
