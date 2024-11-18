package com.catpuppyapp.sshkeyman.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScrollableColumn(modifier: Modifier=Modifier, content:@Composable ()->Unit) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        content()
    }
}
