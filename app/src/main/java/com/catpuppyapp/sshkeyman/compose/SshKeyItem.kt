package com.catpuppyapp.sshkeyman.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catpuppyapp.sshkeyman.R
import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import com.catpuppyapp.sshkeyman.style.MyStyleKt
import com.catpuppyapp.sshkeyman.theme.Theme
import com.catpuppyapp.sshkeyman.utils.AppModel
import com.catpuppyapp.sshkeyman.utils.Msg
import com.catpuppyapp.sshkeyman.utils.MyLog
import com.catpuppyapp.sshkeyman.utils.UIHelper
import com.catpuppyapp.sshkeyman.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.sshkeyman.utils.copyAndShowCopied
import com.catpuppyapp.sshkeyman.utils.doJobThenOffLoading
import com.catpuppyapp.sshkeyman.utils.getFormatTimeFromSec
import com.catpuppyapp.sshkeyman.utils.state.CustomStateSaveable
import kotlinx.coroutines.delay

private val TAG = "SshKeyItem"

@Composable
fun SshKeyItem(
    showBottomSheet: MutableState<Boolean>,
    curItem: CustomStateSaveable<SshKeyEntity>,
    curItemIdx: MutableIntState,
    curItemDto: SshKeyEntity,
    curItemDtoIdx:Int,
    requireBlinkIdx:MutableIntState,
    needRefresh:MutableState<String>
) {
    val navController = AppModel.singleInstanceHolder.navController
    val haptic = AppModel.singleInstanceHolder.haptic
    val activityContext = AppModel.singleInstanceHolder.activityContext

    val inDarkTheme = Theme.inDarkTheme


    val cardColor = UIHelper.defaultCardColor()
    val highlightColor = if(inDarkTheme) Color(0xFF9D9C9C) else Color(0xFFFFFFFF)


    val clipboardManager = LocalClipboardManager.current

    val viewDialogText = rememberSaveable { mutableStateOf("") }
    val showViewDialog = rememberSaveable { mutableStateOf(false) }
    if(showViewDialog.value) {
        CopyableDialog(
            title = curItemDto.name,
            text = viewDialogText.value,
            onCancel = {
                showViewDialog.value=false
            }
        ) { //复制到剪贴板
            showViewDialog.value=false
            clipboardManager.setText(AnnotatedString(viewDialogText.value))
            Msg.requireShow(activityContext.getString(R.string.copied))
        }
    }

    val showDelDialog = rememberSaveable { mutableStateOf(false) }
    if(showDelDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.delete),
            requireShowTextCompose = true,
            textCompose = {
                Column {
                    Text(stringResource(R.string.will_delete_itemname, curItemDto.name))
                }
            },
            okTextColor = MyStyleKt.TextColor.danger(),
            onCancel = {showDelDialog.value =false}
        ) {
            showDelDialog.value =false
            doJobThenOffLoading {
                try {
                    AppModel.singleInstanceHolder.dbContainer.sshKeyRepository.delete(curItemDto)

                    Msg.requireShow(activityContext.getString(R.string.success))
                    changeStateTriggerRefreshPage(needRefresh)
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?:"unknown err")
                    MyLog.e(TAG, "delete ssh key '${curItemDto.name}' err: ${e.stackTraceToString()}")
                }
            }
        }
    }



    val setCurItem = {
        //设置当前仓库（如果不将repo先设置为无效值，可能会导致页面获取旧值，显示过时信息）
        curItem.value = SshKeyEntity()  // change state to a new value, if delete this line, may cause page not refresh after changed repo
        curItem.value = curItemDto  // update state to target value

        curItemIdx.intValue = curItemDtoIdx
    }

    Column (modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Card(
            //0.9f 占父元素宽度的百分之90
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(0.95F)

                //使卡片按下效果圆角，但和elevation冲突，算了，感觉elevation更有立体感比这个重要，所以禁用这个吧
//                .clip(CardDefaults.shape)  //使按下卡片的半透明效果符合卡片轮廓，不然卡片圆角，按下是尖角，丑陋

//                .combinedClickable(
//                    //只要仓库就绪就可启用长按菜单，不检查git仓库的state是否null，因为即使仓库为null，也需要长按菜单显示删除按钮，也不检查仓库是否出错，1是因为出错不会使用此组件而是另一个errcard，2是就算使用且可长按也仅显示删除和取消
//                    enabled = true,
//                    onClick = {},
//                    onLongClick = {
//                        //震动反馈
//                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//
//                        setCurItem()
//
//                        //显示底部菜单
//                        showBottomSheet.value = true
//                    },
//                )
//            .defaultMinSize(minHeight = 100.dp)

            ,
            colors = CardDefaults.cardColors(
                //如果是请求闪烁的索引，闪烁一下
                containerColor = if (requireBlinkIdx.intValue != -1 && requireBlinkIdx.intValue == curItemDtoIdx) {
                    //高亮2s后解除
                    doJobThenOffLoading {
                        delay(UIHelper.getHighlightingTimeInMills())  //解除高亮倒计时
                        requireBlinkIdx.intValue = -1  //解除高亮
                    }
                    highlightColor
                } else {
                    cardColor
                }

            ),
//        border = BorderStroke(1.dp, Color.Black),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.fillMaxWidth(.6F)) {
                    Text(
                        text = curItemDto.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = 10.dp,
                            top = 5.dp,
                            bottom = 0.dp,
                            end = 1.dp
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                }

                LongPressAbleIconBtn(
                    tooltipText = stringResource(R.string.delete),
                    icon = Icons.Filled.Delete,
                    iconContentDesc = stringResource(id = R.string.delete),
                    iconModifier = Modifier.size(20.dp)
                ) {
                    showDelDialog.value = true
                }

            }

            HorizontalDivider()

            Column(
                modifier = Modifier.padding(start = 10.dp, top = 4.dp, end = 10.dp, bottom = 10.dp)
            ) {


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.publickey) + ":")
                    Text(
                        text = stringResource(R.string.click_to_copy),

                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Light,
                        style = MyStyleKt.ClickableText.style,
                        color = MyStyleKt.ClickableText.color,
                        modifier = MyStyleKt.ClickableText.modifier.clickable {
                            copyAndShowCopied(curItemDto.publicKey, activityContext, clipboardManager)
                        },


                        )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.privatekey) + ":")
                    Text(
                        text = stringResource(R.string.click_to_copy),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Light,
                        style = MyStyleKt.ClickableText.style,
                        color = MyStyleKt.ClickableText.color,
                        modifier = MyStyleKt.ClickableText.modifier.clickable {
                            copyAndShowCopied(curItemDto.privateKey, activityContext, clipboardManager)
                        },


                        )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.passphrase) + ":")
                    Text(
                        text = stringResource(R.string.click_to_copy),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Light,
                        style = MyStyleKt.ClickableText.style,
                        color = MyStyleKt.ClickableText.color,
                        modifier = MyStyleKt.ClickableText.modifier.clickable {
                            copyAndShowCopied(curItemDto.passphrase, activityContext, clipboardManager)
                        }


                    )
                }


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.details) + ":")
                    Text(
                        text = stringResource(R.string.click_to_view),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Light,
                        style = MyStyleKt.ClickableText.style,
                        color = MyStyleKt.ClickableText.color,
                        modifier = MyStyleKt.ClickableText.modifier.clickable {

                            val sb = StringBuilder()
                            sb.appendLine("public key:")
                            sb.appendLine(curItemDto.publicKey).appendLine()
                            sb.appendLine("private key:")
                            sb.appendLine(curItemDto.privateKey).appendLine()
                            sb.appendLine("passphrase:")
                            sb.appendLine(curItemDto.passphrase).appendLine()

                            viewDialogText.value = sb.toString()

                            showViewDialog.value = true
                        },

                        )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.algo) + ":")
                    Text(
                        text = curItemDto.algo,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Light,
                        modifier = MyStyleKt.NormalText.modifier,

                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.create) + ":")
                    Text(
                        text = getFormatTimeFromSec(curItemDto.baseFields.baseUpdateTime),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Light,
                        modifier = MyStyleKt.NormalText.modifier,

                    )
                }

            }
        }
    }

}
