package com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.title

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.sshkeyman.play.pro.R

@Composable
fun AboutTitle() {
    Text(
        text = stringResource(id = R.string.about),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
