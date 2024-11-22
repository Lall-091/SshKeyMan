package com.catpuppyapp.sshkeyman.screen.content.homescreen.innerpage

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.catpuppyapp.sshkeyman.R
import com.catpuppyapp.sshkeyman.compose.ConfirmDialog2
import com.catpuppyapp.sshkeyman.compose.MyCheckBox
import com.catpuppyapp.sshkeyman.compose.MyLazyColumn
import com.catpuppyapp.sshkeyman.compose.ScrollableColumn
import com.catpuppyapp.sshkeyman.compose.SshKeyItem
import com.catpuppyapp.sshkeyman.data.AppContainer
import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import com.catpuppyapp.sshkeyman.style.MyStyleKt
import com.catpuppyapp.sshkeyman.theme.Theme
import com.catpuppyapp.sshkeyman.utils.ActivityUtil
import com.catpuppyapp.sshkeyman.utils.AppModel
import com.catpuppyapp.sshkeyman.utils.ComposeHelper
import com.catpuppyapp.sshkeyman.utils.Msg
import com.catpuppyapp.sshkeyman.utils.MyLog
import com.catpuppyapp.sshkeyman.utils.sshkey.SkmSshKeyUtil
import com.catpuppyapp.sshkeyman.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.sshkeyman.utils.doJobThenOffLoading
import com.catpuppyapp.sshkeyman.utils.sshkey.SkmKeyPairGenerator
import com.catpuppyapp.sshkeyman.utils.state.CustomStateListSaveable
import com.catpuppyapp.sshkeyman.utils.state.CustomStateSaveable
import com.catpuppyapp.sshkeyman.utils.state.mutableCustomStateListOf

private const val TAG = "SshKeyInnerPage"
private const val stateKeyTag = "SshKeyInnerPage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SshKeyInnerPage(
    showBottomSheet: MutableState<Boolean>,
    sheetState: SheetState,
    curRepo: CustomStateSaveable<SshKeyEntity>,
    curRepoIndex: MutableIntState,
    contentPadding: PaddingValues,
    listState: LazyListState,
    filterListState: LazyListState,
    openDrawer: () -> Unit,
    itemList: CustomStateListSaveable<SshKeyEntity>,
    needRefreshPage: MutableState<String>,
    showCreateDialog: MutableState<Boolean>,
    filterModeOnFlag: MutableState<Boolean>,
    filterKeyword: CustomStateSaveable<TextFieldValue>,
    closeFilter:()->Unit
) {
    val activityContext = AppModel.singleInstanceHolder.activityContext;
    val exitApp = AppModel.singleInstanceHolder.exitApp;
    val navController = AppModel.singleInstanceHolder.navController;
    val scope = rememberCoroutineScope()


    val clipboardManager = LocalClipboardManager.current


    val dbContainer = AppModel.singleInstanceHolder.dbContainer;
//    val repoDtoList = remember { mutableStateListOf<RepoEntity>() }

    val activity = ActivityUtil.getCurrentActivity()

    //back handler block start
    val isBackHandlerEnable = rememberSaveable { mutableStateOf(true)}
    val backHandlerOnBack = ComposeHelper.getDoubleClickBackHandler(context = activityContext, openDrawer = openDrawer, exitApp = exitApp)
    //注册BackHandler，拦截返回键，实现双击返回和返回上级目录
    BackHandler(enabled = isBackHandlerEnable.value, onBack = {
        if(filterModeOnFlag.value) {
            closeFilter()
        }else {
            backHandlerOnBack()
        }
    })
    //back handler block end

    val inDarkTheme = Theme.inDarkTheme

    val requireBlinkIdx = rememberSaveable{mutableIntStateOf(-1)}

    val isLoading = rememberSaveable { mutableStateOf(true)}
    val loadingText = rememberSaveable { mutableStateOf(activityContext.getString(R.string.loading))}
    val loadingOn = {text:String->
        loadingText.value = text

        // disable this feel better, else screen will blank then restore, feel sick
//        isLoading.value=true
    }
    val loadingOff = {
        isLoading.value=false
        loadingText.value = ""
    }

    val name = rememberSaveable { mutableStateOf("") }
    val comment = rememberSaveable { mutableStateOf("") }
    val passphrase = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val storePassphrase = rememberSaveable { mutableStateOf(true) }

    val algoList = SkmKeyPairGenerator.algoList
    val selectedAlgo = rememberSaveable { mutableIntStateOf(0) }

    val filterList = mutableCustomStateListOf( stateKeyTag, "filterList", listOf<SshKeyEntity>() )
    val enableFilterState = rememberSaveable { mutableStateOf(false)}

    val spacerHeight = remember {15.dp}
    if(showCreateDialog.value) {
        ConfirmDialog2(
            title = stringResource(R.string.create),
            requireShowTextCompose = true,
            textCompose = {
                ScrollableColumn {

                    TextField(
                        modifier = Modifier.fillMaxWidth(),

                        value = name.value,
                        onValueChange = {
                            name.value = it
                        },
                        singleLine = true,
                        label = {
                            Text(stringResource(R.string.name))
                        },
                    )

                    Spacer(Modifier.height(spacerHeight))

                    TextField(
                        modifier = Modifier.fillMaxWidth(),

                        value = comment.value,
                        onValueChange = {
                            comment.value = it
                        },
                        singleLine = true,
                        label = {
                            Text(stringResource(R.string.comment))
                        },
                        placeholder = {
                            Text(stringResource(R.string.eg_your_email_example))
                        }
                    )
                    Spacer(Modifier.height(spacerHeight))

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        value = passphrase.value,
                        onValueChange = {
                            passphrase.value=it
                        },
//                        label = stringResource(R.string.passphrase),
//                        placeholder = "",
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible.value) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            // Please provide localized description for accessibility services
                            val description =
                                if (passwordVisible.value) stringResource(R.string.hide) else stringResource(
                                    R.string.show
                                )

                            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                                // contentDescription is for accessibility
                                Icon(imageVector = image, contentDescription = description)
                            }
                        },
                        label = { Text(stringResource(R.string.passphrase)) }
                    )

                    Spacer(Modifier.height(spacerHeight))

                    MyCheckBox(stringResource(R.string.store_passphrase), storePassphrase)

                    Spacer(Modifier.height(spacerHeight))

                    for ((k, optext) in algoList.withIndex()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = MyStyleKt.RadioOptions.minHeight)

                                .selectable(
                                    selected = selectedAlgo.intValue == k,
                                    onClick = {
                                        //更新选择值
                                        selectedAlgo.intValue = k
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedAlgo.intValue == k,
                                onClick = null // null recommended for accessibility with screenreaders
                            )
                            Text(
                                text = optext,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }
                    }

                }
            },
            okBtnEnabled = name.value.isNotBlank() && selectedAlgo.intValue>=0 && selectedAlgo.intValue<algoList.size,
            onCancel = {showCreateDialog.value = false}
        ) {
            showCreateDialog.value = false
            doJobThenOffLoading {
                try {
                    //create entity
                    val algo = algoList[selectedAlgo.intValue]
                    val sshKeyEntity = SkmSshKeyUtil.createSshKeyEntity(
                        name = name.value,
                        algorithm = algo,
                        passphrase = passphrase.value,
                        comment = comment.value,
                    )

                    // clear pass if user choose dont store it
                    if(storePassphrase.value.not()) {
                        sshKeyEntity.passphrase = ""
                    }

                    // save to db
                    AppModel.singleInstanceHolder.dbContainer.sshKeyRepository.insert(sshKeyEntity)

                    // show success
                    Msg.requireShow(activityContext.getString(R.string.success))

                    // clean state
                    comment.value = ""
                    passphrase.value = ""
                    name.value = ""

                    // refresh page
                    changeStateTriggerRefreshPage(needRefreshPage)
                }catch (e:Exception) {
                    Msg.requireShowLongDuration(e.localizedMessage ?:"unknown err")
                    MyLog.e(TAG, "create ssh key pair err: ${e.stackTraceToString()}")
                }
            }

        }
    }





    if (!isLoading.value && itemList.value.isEmpty()) {  //无仓库，显示添加按钮
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())

                ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            //interactionSource和indication的作用是隐藏按下时的背景半透明那个按压效果，很难看，所以隐藏
            Column(modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                showCreateDialog.value=true
            },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row{
                    Icon(modifier = Modifier.size(50.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.create),
                        tint = MyStyleKt.IconColor.normal
                    )
                }
                Row {
                    Text(text = stringResource(id = R.string.create_a_key),
                        style = MyStyleKt.ClickableText.style,
                        color = MyStyleKt.ClickableText.color,
                        fontSize = MyStyleKt.TextSize.default
                    )
                }
            }

        }
    }



    if(!isLoading.value && itemList.value.isNotEmpty()) {  //有条目
        //根据关键字过滤条目
        val k = filterKeyword.value.text.lowercase()  //关键字
        val enableFilter = filterModeOnFlag.value && k.isNotEmpty()
        val filteredList = if(enableFilter){
            val tmpList = itemList.value.filter {
                it.name.lowercase().contains(k)
                        || it.comment.lowercase().contains(k)
                        || it.algo.lowercase().contains(k)
                        || it.note.lowercase().contains(k)
                        || it.id.lowercase().contains(k)
                        || it.getCreateTimeCached().lowercase().contains(k)
            }

            filterList.value.clear()
            filterList.value.addAll(tmpList)
            tmpList
        }else {
            itemList.value
        }

        val listState = if(enableFilter) filterListState else listState

        //更新是否启用filter
        enableFilterState.value = enableFilter


        MyLazyColumn(
            contentPadding = contentPadding,
            list = filteredList,
            listState = listState,
            requireForEachWithIndex = true,
            requirePaddingAtBottom = true
        ) {idx, element->

            SshKeyItem(
                showBottomSheet,
                curRepo,
                curRepoIndex,
                element,
                idx,
                requireBlinkIdx = requireBlinkIdx,
                needRefreshPage
            )

        }


    }


    //没换页面，但需要刷新页面，这时LaunchedEffect不会执行，就靠这个变量控制刷新页面了
//    if(needRefreshRepoPage.value) {
//        initRepoPage()
//        needRefreshRepoPage.value=false
//    }
    //compose创建时的副作用
    LaunchedEffect(needRefreshPage.value) {
        try {
            // 仓库页面检查仓库状态，对所有状态为notReadyNeedClone的仓库执行clone，卡片把所有状态为notReadyNeedClone的仓库都设置成不可操作，显示正在克隆loading信息
            doInit(
                dbContainer = dbContainer,
                repoDtoList = itemList,
                loadingOn = loadingOn,
                loadingOff = loadingOff,
                activityContext = activityContext,
            )

        } catch (cancel: Exception) {
//            println("LaunchedEffect: job cancelled")
        }
    }
}

private fun doInit(
    dbContainer: AppContainer,
    repoDtoList: CustomStateListSaveable<SshKeyEntity>,
    loadingOn:(String)->Unit,
    loadingOff:()->Unit,
    activityContext:Context,
){
    doJobThenOffLoading(loadingOn, loadingOff, activityContext.getString(R.string.loading)) {
        //执行仓库页面的初始化操作
        val repoRepository = dbContainer.sshKeyRepository
        //貌似如果用Flow，后续我更新数据库，不需要再次手动更新State数据就会自动刷新，也就是Flow会观测数据，如果改变，重新执行sql获取最新的数据，但最好还是手动更新，避免资源浪费
        val repoListFromDb = repoRepository.getAll();

        repoDtoList.value.clear()
        repoDtoList.value.addAll(repoListFromDb)
    }
}

