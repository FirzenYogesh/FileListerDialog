package yogesh.firzen.filelister;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import yogesh.firzen.mukkiasevaigal.M;
import yogesh.firzen.mukkiasevaigal.S;

/**
 * Created by S.Yogesh on 14-02-2016.
 */
class FilesListerView extends RecyclerView {

    private FileListerAdapter adapter;

    FilesListerView(Context context) {
        super(context);
        init();
    }

    FilesListerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    FilesListerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new FileListerAdapter(this);
    }

    void start() {
        setAdapter(adapter);
        adapter.start();
    }

    void setDefaultDir(File file) {
        adapter.setDefaultDir(file);
    }

    void setDefaultDir(String path) {
        setDefaultDir(new File(path));
    }

    File getSelected() {
        return adapter.getSelected();
    }

    void goToDefaultDir() {
        adapter.goToDefault();
    }

    void setFileFilter(FileListerDialog.FILE_FILTER fileFilter) {
        adapter.setFileFilter(fileFilter);
    }

    FileListerDialog.FILE_FILTER getFileFilter() {
        return adapter.getFileFilter();
    }
}
