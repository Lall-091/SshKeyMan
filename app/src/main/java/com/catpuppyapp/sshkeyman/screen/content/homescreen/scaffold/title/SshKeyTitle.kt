package com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.catpuppyapp.sshkeyman.R
import com.catpuppyapp.sshkeyman.utils.UIHelper
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SshKeyTitle(listState: LazyListState, scope:CoroutineScope) {
    Row (modifier = Modifier.combinedClickable(onDoubleClick = {
        UIHelper.scrollToItem(scope,listState,0)
    }) {

    }){
        Text(
            text = stringResource(id = R.string.sshkeys),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}
