package com.example.pocket.data.domain

import com.example.pocket.data.database.UrlEntity
import java.net.URL

/**
 * A domain object equivalent to [UrlEntity].
 */
data class SavedUrlItem(
    val id: String,
    val url: URL,
    val title: String,
    val imageAbsolutePath: String? = null,
    val faviconAbsolutePath: String? = null
)