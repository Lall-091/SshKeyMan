package com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.catpuppyapp.sshkeyman.compose.LongPressAbleIconBtn
import com.catpuppyapp.sshkeyman.play.pro.R


@Composable
fun SettingsActions(
    refreshPage: () -> Unit,
) {
    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.refresh),
        icon = Icons.Filled.Refresh,
        iconContentDesc = stringResource(id = R.string.refresh),
    ) {
        refreshPage()
    }

}

