package com.bignerdranch.android.scores

import android.net.Uri

data class Link(
        var href: String
) {
    val gamePageUri: Uri
        get() {
            return Uri.parse(href)
                    .buildUpon()
                    .build()
        }
}
