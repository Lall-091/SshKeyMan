package com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.catpuppyapp.sshkeyman.R
import com.catpuppyapp.sshkeyman.compose.LongPressAbleIconBtn
import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import com.catpuppyapp.sshkeyman.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.sshkeyman.utils.state.CustomStateSaveable


@Composable
fun SshKeyPageActions(
    navController: NavHostController,
    curItem: CustomStateSaveable<SshKeyEntity>,
    needRefresh: MutableState<String>,
    showCreateSshKeyDialog:MutableState<Boolean>
) {
    /*  TODO 添加个设置按钮
     * 跳转到仓库全局设置页面，至少两个开关：
     * Auto Fetch                default:Off
     * Auto check Status         default:Off
     */


    val dropDownMenuExpendState = rememberSaveable { mutableStateOf(false)}

    val closeMenu = {dropDownMenuExpendState.value = false}


    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.refresh),
        icon = Icons.Filled.Refresh,
        iconContentDesc = stringResource(id = R.string.refresh),
    ) {
        changeStateTriggerRefreshPage(needRefresh)
    }

    LongPressAbleIconBtn(
        tooltipText = stringResource(R.string.create),
        icon = Icons.Filled.Add,
        iconContentDesc = stringResource(id = R.string.create),
    ) {
        showCreateSshKeyDialog.value = true
    }

}
