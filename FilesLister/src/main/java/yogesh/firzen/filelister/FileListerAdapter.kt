package yogesh.firzen.filelister

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import yogesh.firzen.mukkiasevaigal.F
import yogesh.firzen.mukkiasevaigal.M
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by root on 9/7/17.
 */

internal class FileListerAdapter : RecyclerView.Adapter<FileListerAdapter.FileListHolder> {

    public var onFileSelectionChangedListener: OnFileSelectionChangedListener? = null
    private var data: MutableList<File> = LinkedList()
    var customExtension: String = ""

    //private File parent = Environment.getExternalStorageDirectory();
    //parent = defaultDir;
    var defaultDir = Environment.getExternalStorageDirectory()
    var selected = defaultDir
        private set
    var fileFilter: FileListerDialog.FILE_FILTER = FileListerDialog.FILE_FILTER.ALL_FILES
    private var context: Context? = null
    private var listerView: FilesListerView? = null
    private var unreadableDir: Boolean = false

    private val physicalPaths: Array<String>
        get() = arrayOf("/storage/sdcard0", "/storage/sdcard1", "/storage/extsdcard", "/storage/sdcard0/external_sdcard", "/mnt/extsdcard", "/mnt/sdcard/external_sd", "/mnt/external_sd", "/mnt/media_rw/sdcard1", "/removable/microsd", "/mnt/emmc", "/storage/external_SD", "/storage/ext_sd", "/storage/removable/sdcard1", "/data/sdext", "/data/sdext2", "/data/sdext3", "/data/sdext4", "/sdcard1", "/sdcard2", "/storage/microsd", "/data/user")


    constructor(defaultDir: File, view: FilesListerView) {
        this.defaultDir = defaultDir
        selected = defaultDir
        this.context = view.context
        listerView = view
    }

    constructor(view: FilesListerView) {
        //parent = defaultDir;
        this.context = view.context
        listerView = view
    }

    fun start() {
        fileLister(defaultDir)
    }

    private fun fileLister(dir: File) {
        val fs = LinkedList<File>()
        if (dir.absolutePath == "/"
                || dir.absolutePath == "/storage"
                || dir.absolutePath == "/storage/emulated"
                || dir.absolutePath == "/mnt") {
            unreadableDir = true
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                val vols = context!!.getExternalFilesDirs(null)
                if (vols != null && vols.isNotEmpty()) {
                    for (file in vols) {
                        if (file != null) {
                            var path = file.absolutePath
                            path = path.replace("/Android/data/([a-zA-Z_][.\\w]*)/files".toRegex(), "")
                            fs.add(File(path))
                        }
                    }
                } else {
                    fs.add(Environment.getExternalStorageDirectory())
                }
            } else {
                var s = System.getenv("EXTERNAL_STORAGE")
                if (!TextUtils.isEmpty(s))
                    fs.add(File(s!!))
                else {
                    val paths = physicalPaths
                    for (path in paths) {
                        val f = File(path)
                        if (f.exists())
                            fs.add(f)
                    }
                }
                s = System.getenv("SECONDARY_STORAGE")
                if (s != null && !TextUtils.isEmpty(s)) {
                    val rawSecondaryStorages = s.split(File.pathSeparator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (path in rawSecondaryStorages) {
                        val f = File(path)
                        if (f.exists())
                            fs.add(f)
                    }
                }
            }
        } else {
            unreadableDir = false
            val files = dir.listFiles()
            files?.forEach { file ->
                if (fileFilter == FileListerDialog.FILE_FILTER.ALL_FILES) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.IMAGE_ONLY && F.isImage(file)) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.VIDEO_ONLY && F.isVideo(file)) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.AUDIO_ONLY && F.isAudio(file)) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.ALL_MEDIA && F.isMedia(file)) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.DOCUMENT_ONLY && F.isDocument(file)) {
                    fs.add(file);
                } else if (fileFilter == FileListerDialog.FILE_FILTER.SPREADSHEET_ONLY && F.isSpreadsheet(file)) {
                    fs.add(file);
                } else if (fileFilter == FileListerDialog.FILE_FILTER.PRESENTATION_ONLY && F.isPresentation(file)) {
                    fs.add(file);
                } else if (fileFilter == FileListerDialog.FILE_FILTER.ALL_DOCUMENTS && F.isAnyDocumentType(file)) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.COMPRESSED_ONLY && F.isCompressed(file)) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.COMPRESSED_ONLY && F.isCompressed(file)) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.APK_ONLY && F.isAPK(file)) {
                    fs.add(file)
                } else if (fileFilter == FileListerDialog.FILE_FILTER.CUSTOM_EXTENSION && customExtension.contains(F.getExtension(file), true)) {
                    fs.add(file)
                } else if (fileFilter != FileListerDialog.FILE_FILTER.ALL_FILES && file.isDirectory) {
                    fs.add(file)
                }
            }
        }
        data = LinkedList(fs)
        data.sortWith(Comparator { f1, f2 ->
            if (f1.isDirectory && f2.isDirectory)
                f1.name.compareTo(f2.name, ignoreCase = true)
            else if (f1.isDirectory && !f2.isDirectory)
                -1
            else if (!f1.isDirectory && f2.isDirectory)
                1
            else if (!f1.isDirectory && !f2.isDirectory)
                f1.name.compareTo(f2.name, ignoreCase = true)
            else
                0
        })
        selected = dir
        if (dir.absolutePath != "/") {
            dirUp()
        }
        notifyDataSetChanged()
        listerView!!.scrollToPosition(0)
    }

    private fun dirUp() {
        if (!unreadableDir) {
            data.add(0, selected.parentFile!!)
            data.add(1, File(""))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListHolder {

        val v = View.inflate(context, R.layout.item_file_lister, null);

        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        v.setLayoutParams(lp)
        return FileListHolder(v)
    }

    fun formatSize(v: Long): String? {
        if (v < 1024) return "$v B"
        val z = (63 - java.lang.Long.numberOfLeadingZeros(v)) / 10
        return String.format("%.1f %sB", v.toDouble() / (1L shl z * 10), " KMGTPE"[z])
    }
    override fun onBindViewHolder(holder: FileListHolder, position: Int) {
        val f = data[position]

        holder.name.text = f.name

        if (unreadableDir) {
            if (position == 0) {
                holder.name.text = "${f.name} (Internal)"
            } else {
                holder.name.text = "${f.name} (External)"
            }
        }
        if (position == 0 && !unreadableDir) {
            holder.icon.setImageResource(R.drawable.ic_subdirectory_up_black)
            holder.fileInfoLayout.visibility = View.GONE
        } else {

            if(f.isFile || f.isDirectory){

                holder.fileInfoLayout.visibility = View.VISIBLE

                val c = Calendar.getInstance()
                c.timeInMillis = f.lastModified()
                val dateText = SimpleDateFormat(if (is24HourFormat(context)) "MM/dd/yyyy HH:mm" else "dd MMMM, yyyy hh:mm a").format(c.time).toLowerCase()
                holder.lastModified.text = dateText

                if(f.isFile){
                    holder.size.text = formatSize(f.length())
                }else{

                    holder.size.text = context?.getString(R.string.items)+" "+f.listFiles().size
                }

            }else{
                holder.fileInfoLayout.visibility = View.GONE
            }

            when {
                f.path == "" -> {
                    holder.icon.setImageResource(R.drawable.ic_create_new_folder_black)
                    holder.name.setText(R.string.create_a_new_directory)
                }


                f.isDirectory -> holder.icon.setImageResource(R.drawable.ic_folder_black)
                F.isImage(f) -> holder.icon.setImageResource(R.drawable.ic_photo_black)
                F.isVideo(f) -> holder.icon.setImageResource(R.drawable.ic_videocam_black)
                F.isAudio(f) -> holder.icon.setImageResource(R.drawable.ic_audiotrack_black)
                F.isAPK(f) -> holder.icon.setImageResource(R.drawable.ic_android_black)
                F.isCompressed(f) -> holder.icon.setImageResource(R.drawable.ic_archive_black)
                F.isDocument(f) -> holder.icon.setImageResource(R.drawable.ic_document_black)
                else -> holder.icon.setImageResource(R.drawable.ic_insert_drive_file_black)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun goToDefault() {
        fileLister(defaultDir)
    }

    private var is24HourFormat: Boolean? = null
    fun is24HourFormat(context: Context?): Boolean {
        if (is24HourFormat == null) is24HourFormat = DateFormat.is24HourFormat(context)
        return is24HourFormat as Boolean
    }


    internal inner class FileListHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var name: TextView = itemView.findViewById(R.id.name)
        var size: TextView = itemView.findViewById(R.id.size)
        var lastModified: TextView = itemView.findViewById(R.id.last_modified)
        var fileInfoLayout: View = itemView.findViewById(R.id.file_info_layout)
        var icon: ImageView = itemView.findViewById(R.id.icon)

        init {
            itemView.findViewById<View>(R.id.layout).setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (data[adapterPosition].path == "") {
                val view = View.inflate(context, R.layout.dialog_create_folder, null)
                val editText = view.findViewById<AppCompatEditText>(R.id.edittext)
                val builder = AlertDialog.Builder(context!!)
                        .setView(view)
                        .setTitle(R.string.enter_the_folder_name)
                        .setPositiveButton(R.string.create) { dialog, which -> }
                val dialog = builder.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val name = editText.text!!.toString()
                    if (TextUtils.isEmpty(name)) {
                        M.T(context!!, R.string.please_enter_a_valid_folder_name)
                    } else {
                        val file = File(selected, name)
                        if (file.exists()) {
                            M.T(context!!, R.string.this_folder_already_exists)
                        } else {
                            dialog.dismiss()
                            file.mkdirs()
                            fileLister(file)
                        }
                    }
                }
            } else {
                val f = data[adapterPosition]
                selected = f
                onFileSelectionChangedListener?.onFileSelected(f,f.absolutePath);
                M.L("From FileLister", f.absolutePath)
                if (f.isDirectory) {
                    fileLister(f)
                } else {
                }
            }
        }
    }
}