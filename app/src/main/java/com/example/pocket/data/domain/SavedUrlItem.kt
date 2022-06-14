package com.example.pocket.data.domain

import com.example.pocket.data.database.UrlEntity
import java.net.URL

/**
 * A domain object equivalent to [UrlEntity].
 */
data class SavedUrlItem(
    val associatedUserId: String,
    val id: String,
    val url: URL,
    val title: String,
    val imageAbsolutePath: String? = null,
    val faviconAbsolutePath: String? = null
)

/**
 * A converter function used to convert an instance of [SavedUrlItem]
 * to an instance of [UrlEntity].
 */
fun SavedUrlItem.toUrlEntity() = UrlEntity(
    associatedUserId = associatedUserId,
    id = id.toInt(),
    url = url.toString(),
    contentTitle = title,
    imageAbsolutePath = imageAbsolutePath,
    faviconAbsolutePath = faviconAbsolutePath
)