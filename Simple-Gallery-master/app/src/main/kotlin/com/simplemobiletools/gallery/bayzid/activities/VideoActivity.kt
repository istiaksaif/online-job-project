package com.simplemobiletools.gallery.bayzid.activities

import android.os.Bundle

class VideoActivity : PhotoVideoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        mIsVideo = true
        super.onCreate(savedInstanceState)
    }
}
