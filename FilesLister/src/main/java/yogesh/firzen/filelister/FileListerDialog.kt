package yogesh.firzen.filelister

import android.content.Context
import android.content.DialogInterface.*
import androidx.appcompat.app.AlertDialog
import java.io.File

/**
 * A File Lister Dialog
 */

class FileListerDialog {

    private var alertDialog: AlertDialog? = null

    private var filesListerView: FilesListerView? = null

    private var onFileSelectedListener: OnFileSelectedListener? = null

    /**
     * File Filter for the FileListerDialog
     */
    enum class FILE_FILTER {
        /**
         * List All Files
         */
        ALL_FILES,
        /**
         * List only directories
         */
        DIRECTORY_ONLY,
        /**
         * List Directory and Image files
         */
        IMAGE_ONLY,
        /**
         * List Directory and Video files
         */
        VIDEO_ONLY,
        /**
         * List Directory and Audio files
         */
        AUDIO_ONLY,
        /**
         * List Directory and Includes Audio, Video, Image files
         */
        ALL_MEDIA,
        /**
         * List Directory and Document files
         */
        DOCUMENT_ONLY,
        /**
         * List Directory and Spreadsheet files
         */
        SPREADSHEET_ONLY,
        /**
         * List Directory and Presentation files
         */
        PRESENTATION_ONLY,
        /**
         * List Directory and All Document files
         */
        ALL_DOCUMENTS,
        /**
         * List Directory and Compressed/Archived files
         */
        COMPRESSED_ONLY,
        /**
         * List Directory and APK files
         */
        APK_ONLY,
        /**
         * Custom File Extension
         */
        CUSTOM_EXTENSION
    }

    private constructor(context: Context) {
        //super(context);
        alertDialog = AlertDialog.Builder(context).create()
        init(context)
    }

    private constructor(context: Context, themeResId: Int) {
        //super(context, themeResId);
        alertDialog = AlertDialog.Builder(context, themeResId).create()
        init(context)
    }

    private fun init(context: Context) {
        filesListerView = FilesListerView(context)
        alertDialog!!.setView(filesListerView)
        alertDialog!!.setButton(BUTTON_POSITIVE, "Select") { dialogInterface, i ->
            dialogInterface.dismiss()
            if (onFileSelectedListener != null)
                onFileSelectedListener!!.onFileSelected(filesListerView!!.selected, filesListerView!!.selected.absolutePath)
        }
        alertDialog!!.setButton(BUTTON_NEUTRAL, "Default Dir") { dialogInterface, i ->
            //filesListerView.goToDefaultDir();
        }
        alertDialog!!.setButton(BUTTON_NEGATIVE, "Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
    }

    /**
     * Display the FileListerDialog
     */
    fun show() {
        //getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        when (filesListerView!!.fileFilter) {
            FILE_FILTER.DIRECTORY_ONLY -> alertDialog!!.setTitle("Select a directory")
            FILE_FILTER.VIDEO_ONLY -> alertDialog!!.setTitle("Select a Video file")
            FILE_FILTER.IMAGE_ONLY -> alertDialog!!.setTitle("Select an Image file")
            FILE_FILTER.AUDIO_ONLY -> alertDialog!!.setTitle("Select an Audio file")
            FILE_FILTER.ALL_FILES -> alertDialog!!.setTitle("Select a file")
        }
        filesListerView!!.start()
        alertDialog!!.show()
        alertDialog!!.getButton(BUTTON_NEUTRAL).setOnClickListener { filesListerView!!.goToDefaultDir() }
    }

    /**
     * Listener to know which file/directory is selected
     *
     * @param onFileSelectedListener Instance of the Listener
     */
    fun setOnFileSelectedListener(onFileSelectedListener: OnFileSelectedListener) {
        this.onFileSelectedListener = onFileSelectedListener
    }

    /**
     * Set the initial directory to show the list of files in that directory
     *
     * @param file File pointing to the directory
     */
    fun setDefaultDir(file: File) {
        filesListerView!!.setDefaultDir(file)
    }

    /**
     * Set the initial directory to show the list of files in that directory
     *
     * @param file String denoting to the directory
     */
    fun setDefaultDir(file: String) {
        filesListerView!!.setDefaultDir(file)
    }

    /**
     * Set the file filter for listing the files
     *
     * @param fileFilter One of the FILE_FILTER values
     */
    fun setFileFilter(fileFilter: FILE_FILTER) {
        filesListerView!!.fileFilter = fileFilter
    }

    fun setCustomFileExtension(extension: String) {

    }

    companion object {

        /**
         * Creates a default instance of FileListerDialog
         *
         * @param context Context of the App
         * @return Instance of FileListerDialog
         */
        fun createFileListerDialog(context: Context): FileListerDialog {
            return FileListerDialog(context)
        }

        /**
         * Creates an instance of FileListerDialog with the specified Theme
         *
         * @param context Context of the App
         * @param themeId Theme Id for the dialog
         * @return Instance of FileListerDialog
         */
        fun createFileListerDialog(context: Context, themeId: Int): FileListerDialog {
            return FileListerDialog(context, themeId)
        }
    }

}
