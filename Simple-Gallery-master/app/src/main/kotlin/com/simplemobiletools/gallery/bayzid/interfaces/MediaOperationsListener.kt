package com.simplemobiletools.gallery.bayzid.interfaces

import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.gallery.bayzid.models.ThumbnailItem

interface MediaOperationsListener {
    fun refreshItems()

    fun tryDeleteFiles(fileDirItems: ArrayList<FileDirItem>)

    fun selectedPaths(paths: ArrayList<String>)

    fun updateMediaGridDecoration(media: ArrayList<ThumbnailItem>)
}
