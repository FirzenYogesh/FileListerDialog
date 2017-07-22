package yogesh.firzen.filelister;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import yogesh.firzen.mukkiasevaigal.M;
import yogesh.firzen.mukkiasevaigal.S;

/**
 * Created by root on 9/7/17.
 */

class FileListerAdapter extends RecyclerView.Adapter<FileListerAdapter.FileListHolder> {

    private List<File> data = new LinkedList<>();
    //private File parent = Environment.getExternalStorageDirectory();
    private File defaultDir = Environment.getExternalStorageDirectory();
    private File selectedFile = defaultDir;
    private FileListerDialog.FILE_FILTER fileFilter = FileListerDialog.FILE_FILTER.ALL_FILES;
    private Context context;
    private FilesListerView listerView;
    private boolean unreadableDir;


    FileListerAdapter(File defaultDir, FilesListerView view) {
        this.defaultDir = defaultDir;
        selectedFile = defaultDir;
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

    private void fileLister(File dir) {
        LinkedList<File> fs = new LinkedList<>();
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
                            return true;
                        case AUDIO_ONLY:
                            return S.isAudio(file) || file.isDirectory();
                        case IMAGE_ONLY:
                            return S.isImage(file) || file.isDirectory();
                        case VIDEO_ONLY:
                            return S.isVideo(file) || file.isDirectory();
                        case DIRECTORY_ONLY:
                            return file.isDirectory();
                    }
                    return false;
                }
            });
            if (files != null) {
                fs = new LinkedList<>(Arrays.asList(files));
            }
        }
        M.L("From FileListAdapter", fs);
        data = new LinkedList<>(fs);
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
        }
        if (unreadableDir) {
            if (f != null) {
                if (position == 0) {
                    holder.name.setText(f.getName() + " (Internal)");
                } else {
                    holder.name.setText(f.getName() + " (External)");
                }
            }
        }
        if (position == 0 && f != null && !unreadableDir) {
            holder.icon.setImageResource(R.drawable.ic_subdirectory_up_black_48dp);
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
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    File getSelected() {
        return selectedFile;
    }

    void goToDefault() {
        fileLister(defaultDir);
    }

    class FileListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView icon;

        FileListHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.icon);
            itemView.findViewById(R.id.layout).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
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
                selectedFile = f;
                M.L("From FileLister", f.getAbsolutePath());
                if (f.isDirectory()) {
                    fileLister(f);
                } else {
                }
            }
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