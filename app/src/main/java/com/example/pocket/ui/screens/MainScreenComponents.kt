package com.example.pocket.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.pocket.data.database.UrlEntity
import java.io.File


@Composable
fun UrlCard(modifier: Modifier = Modifier,urlItem:UrlEntity){
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier=Modifier.padding(8.dp),
                text = urlItem.contentTitle,
                style = MaterialTheme.typography.h1
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = urlItem.host,
                style = MaterialTheme.typography.caption
            )
            urlItem.imageAbsolutePath?.let{
//                val bitmap = File(it)
//                    Glide.with(context)
//                        .load(File(it))
//                        .into(object :CustomTarget<Bitmap>(){
//                            override fun onResourceReady(
//                                resource: Bitmap,
//                                transition: Transition<in Bitmap>?
//                            ) {
//
//
//                            }
//
//                            override fun onLoadCleared(placeholder: Drawable?) {
//                                TODO("Not yet implemented")
//                            }
//                        })
//
//
//                Image(
//                    bitmap = imageResource(),contentDescription = ""
//                )
            }
           
        }
    }
}