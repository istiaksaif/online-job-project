package com.simplemobiletools.gallery.bayzid.interfaces

import com.simplemobiletools.gallery.bayzid.models.Directory
import java.io.File

interface DirectoryOperationsListener {
    fun refreshItems()

    fun deleteFolders(folders: ArrayList<File>)

    fun recheckPinnedFolders()

    fun updateDirectories(directories: ArrayList<Directory>)
}
