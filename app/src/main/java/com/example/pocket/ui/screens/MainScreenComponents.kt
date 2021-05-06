package com.example.pocket.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.pocket.data.database.UrlEntity
import java.io.File
import java.io.FileInputStream

@Composable
fun UrlCard(modifier: Modifier = Modifier,urlItem:UrlEntity){
    Card(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()){
            Column(modifier = Modifier.fillMaxWidth(0.7f)) {
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
            }
            urlItem.imageAbsolutePath?.let{
                    val bitmap = BitmapFactory.decodeStream(FileInputStream(File(it)))
                    Image(
                        modifier=Modifier.fillMaxSize(),
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Thumbnail"
                    )
            }
        }
    }
}



