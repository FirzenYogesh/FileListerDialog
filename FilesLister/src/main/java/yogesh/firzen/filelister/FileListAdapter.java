package yogesh.firzen.filelister;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import yogesh.firzen.mukkiasevaigal.M;
import yogesh.firzen.mukkiasevaigal.S;

/**
 * Created by S.Yogesh on 9/7/17.
 */

class FileListerAdapter extends RecyclerView.Adapter<FileListerAdapter.FileListHolder> {

    private ArrayList<File> data = new ArrayList<>();
    //private File parent = Environment.getExternalStorageDirectory();
    private File defaultDir = Environment.getExternalStorageDirectory();
    private File selectedFile = null;
    private ArrayList<File> selectedFiles = new ArrayList<>();
    private FileListerDialog.FILE_FILTER fileFilter = FileListerDialog.FILE_FILTER.ALL_FILES;
    private Context context;
    private FilesListerView listerView;
    private boolean unreadableDir;
    private boolean showHiddenFilesAndFolders = false;
    private boolean enableMultiSelect = false;


    FileListerAdapter(File defaultDir, FilesListerView view) {
        this.defaultDir = defaultDir;
        selectedFile = null;
        //selectedFiles.add(defaultDir);
        this.context = view.getContext();
        listerView = view;
    }

    FileListerAdapter(FilesListerView view) {
        //parent = defaultDir;
        this.context = view.getContext();
        listerView = view;
    }

    void start() {
        fileLister(defaultDir);
    }

    void setDefaultDir(File dir) {
        defaultDir = dir;
        //parent = defaultDir;
    }

    File getDefaultDir() {
        return defaultDir;
    }

    FileListerDialog.FILE_FILTER getFileFilter() {
        return fileFilter;
    }

    void setFileFilter(FileListerDialog.FILE_FILTER fileFilter) {
        this.fileFilter = fileFilter;

    }

    void setShowHiddenFilesAndFolders(boolean show) {
        showHiddenFilesAndFolders = show;
    }

    void setEnableMultiSelect(boolean enable) {
        enableMultiSelect = enable;
    }

    private void fileLister(File dir) {
        ArrayList<File> fs = new ArrayList<>();
        if (dir.getAbsolutePath().equals("/")
                || dir.getAbsolutePath().equals("/storage")
                || dir.getAbsolutePath().equals("/storage/emulated")
                || dir.getAbsolutePath().equals("/mnt")) {
            unreadableDir = true;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                File[] vols = context.getExternalFilesDirs(null);
                if (vols != null && vols.length > 0) {
                    for (File file : vols) {
                        if (file != null) {
                            String path = file.getAbsolutePath();
                            path = path.replaceAll("/Android/data/([a-zA-Z_][.\\w]*)/files", "");
                            fs.add(new File(path));
                        }
                    }
                } else {
                    fs.add(Environment.getExternalStorageDirectory());
                }
            } else {
                String s = System.getenv("EXTERNAL_STORAGE");
                if (!TextUtils.isEmpty(s))
                    fs.add(new File(s));
                else {
                    String[] paths = getPhysicalPaths();
                    for (String path : paths) {
                        File f = new File(path);
                        if (f.exists())
                            fs.add(f);
                    }
                }
                s = System.getenv("SECONDARY_STORAGE");
                if (!TextUtils.isEmpty(s)) {
                    final String[] rawSecondaryStorages = s.split(File.pathSeparator);
                    for (String path : rawSecondaryStorages) {
                        File f = new File(path);
                        if (f.exists())
                            fs.add(f);
                    }
                }
            }
        } else {
            unreadableDir = false;
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    switch (getFileFilter()) {
                        case ALL_FILES:
                            return showHiddenFilesAndFolders || !file.getName().startsWith(".");
                        case AUDIO_ONLY:
                            if (showHiddenFilesAndFolders)
                                return (S.isAudio(file) || file.isDirectory());
                            else
                                return (S.isAudio(file) || file.isDirectory()) && !file.getName().startsWith(".");
                        case IMAGE_ONLY:
                            if (showHiddenFilesAndFolders)
                                return (S.isImage(file) || file.isDirectory());
                            else
                                return (S.isImage(file) || file.isDirectory()) && !file.getName().startsWith(".");
                        case VIDEO_ONLY:
                            if (showHiddenFilesAndFolders)
                                return (S.isVideo(file) || file.isDirectory());
                            else
                                return (S.isVideo(file) || file.isDirectory()) && !file.getName().startsWith(".");
                        case DIRECTORY_ONLY:
                            if (showHiddenFilesAndFolders)
                                return file.isDirectory();
                            else
                                return file.isDirectory() && !file.getName().startsWith(".");
                    }
                    return false;
                }
            });
            if (files != null) {
                fs = new ArrayList<>(Arrays.asList(files));
            }
        }
        M.L("From FileListAdapter", fs);
        data = new ArrayList<>(fs);
        Collections.sort(data, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if ((f1.isDirectory() && f2.isDirectory()))
                    return f1.getName().compareToIgnoreCase(f2.getName());
                else if (f1.isDirectory() && !f2.isDirectory())
                    return -1;
                else if (!f1.isDirectory() && f2.isDirectory())
                    return 1;
                else if (!f1.isDirectory() && !f2.isDirectory())
                    return f1.getName().compareToIgnoreCase(f2.getName());
                else return 0;
            }
        });
        selectedFile = dir;
        if (!dir.getAbsolutePath().equals("/")) {
            dirUp();
        }
        notifyDataSetChanged();
        listerView.scrollToPosition(0);
    }

    private void dirUp() {
        if (!unreadableDir) {
            data.add(0, selectedFile.getParentFile());
            data.add(1, null);
        }
        selectedFile = null;
    }

    @Override
    public FileListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileListHolder(View.inflate(getContext(), R.layout.item_file_lister, null));
    }

    @Override
    public void onBindViewHolder(FileListHolder holder, int position) {
        File f = data.get(position);
        if (f != null) {
            holder.name.setText(f.getName());
        } else if (!unreadableDir) {
            holder.name.setText("Create a new Folder here");
            holder.icon.setImageResource(R.drawable.ic_create_new_folder_black_48dp);
            holder.radioButton.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
        }
        if (unreadableDir) {
            if (f != null) {
                if (position == 0) {
                    holder.name.setText(f.getName() + " (Internal)");
                } else {
                    holder.name.setText(f.getName() + " (External)");
                }
            }
            holder.radioButton.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
        }
        if (position == 0 && f != null && !unreadableDir) {
            holder.icon.setImageResource(R.drawable.ic_subdirectory_up_black_48dp);
            holder.radioButton.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
        } else if (f != null) {
            if (f.isDirectory())
                holder.icon.setImageResource(R.drawable.ic_folder_black_48dp);
            else if (S.isImage(f))
                holder.icon.setImageResource(R.drawable.ic_photo_black_48dp);
            else if (S.isVideo(f))
                holder.icon.setImageResource(R.drawable.ic_videocam_black_48dp);
            else if (S.isAudio(f))
                holder.icon.setImageResource(R.drawable.ic_audiotrack_black_48dp);
            else
                holder.icon.setImageResource(R.drawable.ic_insert_drive_file_black_48dp);
            if (enableMultiSelect) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.radioButton.setVisibility(View.GONE);
            } else {
                holder.radioButton.setVisibility(View.VISIBLE);
                holder.checkBox.setVisibility(View.GONE);
            }
        }
        if (enableMultiSelect) {
            holder.checkBox.setChecked(selectedFiles.contains(f));
        } else {
            holder.radioButton.setChecked(f == selectedFile);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    File getSelected() {
        return selectedFile;
    }

    ArrayList<File> getSelectedFiles() {
        return selectedFiles;
    }

    void goToDefault() {
        fileLister(defaultDir);
    }

    class FileListHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        TextView name;
        ImageView icon;
        AppCompatCheckBox checkBox;
        AppCompatRadioButton radioButton;

        FileListHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.icon);
            checkBox = itemView.findViewById(R.id.checkbox);
            radioButton = itemView.findViewById(R.id.radio);
            itemView.findViewById(R.id.layout).setOnTouchListener(this);
        }

        void onDoubleTap() {
            File f = data.get(getPosition());
            //selectedFile = f;
            M.L("From FileLister", f.getAbsolutePath());
            if (f.isDirectory()) {
                fileLister(f);
                selectedFile = null;
                selectedFiles = new ArrayList<>();
            } else
                M.T(context, "This file is not a directory!");
        }

        void onSingleTapUp() {
            if (data.get(getPosition()) == null) {
                View view = View.inflate(getContext(), R.layout.dialog_create_folder, null);
                final AppCompatEditText editText = view.findViewById(R.id.edittext);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setView(view)
                        .setTitle("Enter the folder name")
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editText.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            M.T(getContext(), "Please enter a valid folder name");
                        } else {
                            File file = new File(selectedFile, name);
                            if (file.exists()) {
                                M.T(getContext(), "This folder already exists.\n Please provide another name for the folder");
                            } else {
                                dialog.dismiss();
                                file.mkdirs();
                                fileLister(file);
                            }
                        }
                    }
                });
            } else {
                File f = data.get(getPosition());
                if (unreadableDir) {
                    M.L("From FileLister", f.getAbsolutePath());
                    if (f.isDirectory()) {
                        fileLister(f);
                    }
                    selectedFile = null;
                    selectedFiles = new ArrayList<>();
                } else {
                    if (getPosition() == 0) {
                        M.L("From FileLister", f.getAbsolutePath());
                        if (f.isDirectory()) {
                            fileLister(f);
                        }
                        selectedFile = null;
                        selectedFiles = new ArrayList<>();
                    } else {
                        if (enableMultiSelect) {
                            if (selectedFiles.contains(f))
                                selectedFiles.remove(f);
                            else
                                selectedFiles.add(f);
                            notifyItemChanged(getPosition());
                        } else {
                            selectedFile = data.get(getPosition());
                            notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        private final int CLICK_THRESHOLD = S.dpToPx(5);
        private float sx, sy;
        private long lastTouchDown;
        private byte noClicks = 0;
        private boolean check = false;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (!check) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (noClicks == 2)
                            onDoubleTap();
                        else if (noClicks == 1)
                            onSingleTapUp();
                        noClicks = 0;
                        check = false;
                    }
                }, 300);
            }
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sx = motionEvent.getRawX();
                    sy = motionEvent.getRawY();
                    lastTouchDown = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    if (isAClick(sx, motionEvent.getRawX(), sy, motionEvent.getRawY(), lastTouchDown, System.currentTimeMillis())) {
                        noClicks++;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    noClicks = 0;
            }
            return true;
        }

        private boolean isAClick(float startX, float endX, float startY, float endY, long start, long end) {
            float differenceX = Math.abs(startX - endX);
            float differenceY = Math.abs(startY - endY);
            return (differenceX < 10 && differenceY < 10) && (end - start < 100);
        }
    }

    private static String[] getPhysicalPaths() {
        return new String[]{
                "/storage/sdcard0",
                "/storage/sdcard1",                 //Motorola Xoom
                "/storage/extsdcard",               //Samsung SGS3
                "/storage/sdcard0/external_sdcard", //User request
                "/mnt/extsdcard",
                "/mnt/sdcard/external_sd",          //Samsung galaxy family
                "/mnt/external_sd",
                "/mnt/media_rw/sdcard1",            //4.4.2 on CyanogenMod S3
                "/removable/microsd",               //Asus transformer prime
                "/mnt/emmc",
                "/storage/external_SD",             //LG
                "/storage/ext_sd",                  //HTC One Max
                "/storage/removable/sdcard1",       //Sony Xperia Z1
                "/data/sdext",
                "/data/sdext2",
                "/data/sdext3",
                "/data/sdext4",
                "/sdcard1",                         //Sony Xperia Z
                "/sdcard2",                         //HTC One M8s
                "/storage/microsd"                  //ASUS ZenFone 2
        };
    }

    private Context getContext() {
        return context;
    }
}