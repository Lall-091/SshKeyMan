package com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.title

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.sshkeyman.compose.RepoInfoDialog
import com.catpuppyapp.sshkeyman.compose.ScrollableRow
import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import com.catpuppyapp.sshkeyman.play.pro.R
import com.catpuppyapp.sshkeyman.style.MyStyleKt
import com.catpuppyapp.sshkeyman.utils.UIHelper
import com.catpuppyapp.sshkeyman.utils.state.CustomStateSaveable
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndexScreenTitle(
    curRepo: CustomStateSaveable<SshKeyEntity>,
    repoState: MutableIntState,
    scope: CoroutineScope,
    changeListPageItemListState: LazyListState
) {
    val haptic = LocalHapticFeedback.current
    val activityContext = LocalContext.current

    val showTitleInfoDialog = remember { mutableStateOf(false) }
    if(showTitleInfoDialog.value) {
        RepoInfoDialog(curRepo.value, showTitleInfoDialog)
    }

    val needShowRepoState = rememberSaveable { mutableStateOf(false)}
    val repoStateText = rememberSaveable { mutableStateOf("")}

    //设置仓库状态，主要是为了显示merge
    Libgit2Helper.setRepoStateText(repoState.intValue, needShowRepoState, repoStateText, activityContext)

    val getTitleColor = {
        UIHelper.getChangeListTitleColor(repoState.intValue)
    }

    Column(modifier = Modifier
        .widthIn(min = MyStyleKt.Title.clickableTitleMinWidth)
        .combinedClickable(
            onDoubleClick = { UIHelper.scrollToItem(scope, changeListPageItemListState, 0) },
            onLongClick = null
//            {  //长按显示仓库名
////                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//            }
        ) { // onClick
            showTitleInfoDialog.value = true
        }
        //外面的标题宽180.dp，这里的比外面的宽点，因为这个页面顶栏actions少
        .widthIn(max = 200.dp)
    ) {
        ScrollableRow {

            Text(
                text = curRepo.value.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp,
                color = getTitleColor()
            )
        }
        ScrollableRow  {
            //"[Index]|Merging" or "[Index]"
            Text(text = "["+stringResource(id = R.string.index)+"]" + (if(needShowRepoState.value) "|"+repoStateText.value else ""),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = MyStyleKt.Title.secondLineFontSize,
                color = getTitleColor()
            )
        }

    }
}
