package com.simplemobiletools.gallery.bayzid.dialogs

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.gallery.bayzid.R
import com.simplemobiletools.gallery.bayzid.activities.MainActivity
import com.simplemobiletools.gallery.bayzid.extensions.config
import com.simplemobiletools.gallery.bayzid.helpers.*
import kotlinx.android.synthetic.main.dialog_filter_media.view.*

class FilterMediaDialog(val activity: BaseSimpleActivity, val callback: (result: Int) -> Unit) {
    private var view = activity.layoutInflater.inflate(R.layout.dialog_filter_media, null)

    init {
        val filterMedia = activity.config.filterMedia

        view.apply {
            filter_media_images.isChecked = filterMedia and TYPE_IMAGES != 0
            filter_media_videos.isChecked = filterMedia and TYPE_VIDEOS != 0
            filter_media_gifs.isChecked = filterMedia and TYPE_GIFS != 0
            filter_media_raws.isChecked = filterMedia and TYPE_RAWS != 0
            filter_media_svgs.isChecked = filterMedia and TYPE_SVGS != 0
            filter_media_portraits.isChecked = filterMedia and TYPE_PORTRAITS != 0
        }



        AlertDialog.Builder(activity)
//            .show()

//            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
//            .setNegativeButton(R.string.cancel, null)
            .create().apply {
                activity.setupDialogStuff(view, this, R.string.filter_media)
            }
            .dismiss()
        dialogConfirmed()
    }

    private fun dialogConfirmed() {
//        Toast.makeText(activity,MainActivity.Global.a+" "+MainActivity.Global.b+" "+MainActivity.Global.c,Toast.LENGTH_LONG).show()
        var result = 0
        if (MainActivity.Global.a.equals("1"))
        {
//            Toast.makeText(activity,"TEST",Toast.LENGTH_LONG).show()
            if (true)
                result += TYPE_IMAGES
            if (true)
                result += TYPE_VIDEOS
            if (view.filter_media_gifs.isChecked)
                result += TYPE_GIFS
            if (view.filter_media_raws.isChecked)
                result += TYPE_RAWS
            if (view.filter_media_svgs.isChecked)
                result += TYPE_SVGS
            if (view.filter_media_portraits.isChecked)
                result += TYPE_PORTRAITS

            if (result == 0) {
                result = getDefaultFileFilter()
            }

            if (activity.config.filterMedia != result) {
                activity.config.filterMedia = result
                callback(result)
            }
        }


        else if (MainActivity.Global.b.equals("2"))
        {
//            Toast.makeText(activity,"TEST2",Toast.LENGTH_LONG).show()
            if (true)
                result += TYPE_IMAGES
            if (false)
                result += TYPE_VIDEOS
            if (view.filter_media_gifs.isChecked)
                result += TYPE_GIFS
            if (view.filter_media_raws.isChecked)
                result += TYPE_RAWS
            if (view.filter_media_svgs.isChecked)
                result += TYPE_SVGS
            if (view.filter_media_portraits.isChecked)
                result += TYPE_PORTRAITS

            if (result == 0) {
                result = getDefaultFileFilter()
            }

            if (activity.config.filterMedia != result) {
                activity.config.filterMedia = result
                callback(result)
            }
        }

        else if (MainActivity.Global.c.equals("3"))
        {
//            Toast.makeText(activity,"TEST3",Toast.LENGTH_LONG).show()
            if (false)
                result += TYPE_IMAGES
            if (true)
                result += TYPE_VIDEOS
            if (view.filter_media_gifs.isChecked)
                result += TYPE_GIFS
            if (view.filter_media_raws.isChecked)
                result += TYPE_RAWS
            if (view.filter_media_svgs.isChecked)
                result += TYPE_SVGS
            if (view.filter_media_portraits.isChecked)
                result += TYPE_PORTRAITS

            if (result == 0) {
                result = getDefaultFileFilter()
            }

            if (activity.config.filterMedia != result) {
                activity.config.filterMedia = result
                callback(result)
            }
            MainActivity.Global.a = "0"

            MainActivity.Global.b = "0"

            MainActivity.Global.c = "0"
        }

        else if (MainActivity.Global.a.equals("0") && MainActivity.Global.b.equals("0") && MainActivity.Global.c.equals("0"))
        {
//            Toast.makeText(activity,"TEST3",Toast.LENGTH_LONG).show()
            if (true)
                result += TYPE_IMAGES
            if (true)
                result += TYPE_VIDEOS
            if (view.filter_media_gifs.isChecked)
                result += TYPE_GIFS
            if (view.filter_media_raws.isChecked)
                result += TYPE_RAWS
            if (view.filter_media_svgs.isChecked)
                result += TYPE_SVGS
            if (view.filter_media_portraits.isChecked)
                result += TYPE_PORTRAITS

            if (result == 0) {
                result = getDefaultFileFilter()
            }

            if (activity.config.filterMedia != result) {
                activity.config.filterMedia = result
                callback(result)
            }
            MainActivity.Global.a = "0"

            MainActivity.Global.b = "0"

            MainActivity.Global.c = "0"
        }

    }
}
