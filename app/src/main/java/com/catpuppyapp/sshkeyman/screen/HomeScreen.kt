package com.catpuppyapp.sshkeyman.screen

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.catpuppyapp.sshkeyman.R
import com.catpuppyapp.sshkeyman.compose.FilterTextField
import com.catpuppyapp.sshkeyman.compose.LongPressAbleIconBtn
import com.catpuppyapp.sshkeyman.constants.Cons
import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import com.catpuppyapp.sshkeyman.screen.content.homescreen.innerpage.AboutInnerPage
import com.catpuppyapp.sshkeyman.screen.content.homescreen.innerpage.SshKeyInnerPage
import com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.actions.SshKeyPageActions
import com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.drawer.drawerContent
import com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.title.AboutTitle
import com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.title.SimpleTitle
import com.catpuppyapp.sshkeyman.screen.content.homescreen.scaffold.title.SshKeyTitle
import com.catpuppyapp.sshkeyman.style.MyStyleKt
import com.catpuppyapp.sshkeyman.utils.AppModel
import com.catpuppyapp.sshkeyman.utils.Msg
import com.catpuppyapp.sshkeyman.utils.MyLog
import com.catpuppyapp.sshkeyman.utils.changeStateTriggerRefreshPage
import com.catpuppyapp.sshkeyman.utils.state.mutableCustomStateListOf
import com.catpuppyapp.sshkeyman.utils.state.mutableCustomStateOf
import kotlinx.coroutines.launch


//for debug
private const val TAG = "HomeScreen"
private const val stateKeyTag = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
//    context: Context,
//    navController: NavController,
    drawerState: DrawerState,
//    scope: CoroutineScope,
//    scrollBehavior: TopAppBarScrollBehavior,
    currentHomeScreen: MutableIntState,
    sshkeyPageListState: LazyListState,
    sshkeyPageFilterListState: LazyListState,

//    filePageListState: LazyListState,
//    haptic: HapticFeedback,
) {



    val navController = AppModel.singleInstanceHolder.navController
    val scope = rememberCoroutineScope()
    val homeTopBarScrollBehavior = AppModel.singleInstanceHolder.homeTopBarScrollBehavior
    val activityContext = LocalContext.current  //这个能获取到


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = MyStyleKt.BottomSheet.skipPartiallyExpanded)
    val showBottomSheet = rememberSaveable { mutableStateOf(false)}
    val showCreateSshKeyDialog = rememberSaveable { mutableStateOf(false)}

    val sshKeyPageFilterModeOnFlag = rememberSaveable { mutableStateOf(false)}
    val sshKeyPageFilterKeyword =mutableCustomStateOf(keyTag = stateKeyTag, keyName = "sshKeyPageFilterKeyword"){
        TextFieldValue("")
    }
    val filterModeOff = {
        sshKeyPageFilterModeOnFlag.value = false
        sshKeyPageFilterKeyword.value = TextFieldValue("")
    }

    val filterModeOn = {
        sshKeyPageFilterKeyword.value = TextFieldValue("")
        sshKeyPageFilterModeOnFlag.value = true
    }
    //替换成我的cusntomstateSaver，然后把所有实现parcellzier的类都取消实现parcellzier，改成用我的saver
    val sshKeyPageCurItem = mutableCustomStateOf(keyTag = stateKeyTag, keyName = "sshKeyPageCurItem", initValue = SshKeyEntity(id=""))  //id=空，表示无效仓库
    //使用前检查，大于等于0才是有效索引
    val sshKeyPageCurItemIndex = remember { mutableIntStateOf(-1)}


    val sshKeyList = mutableCustomStateListOf(stateKeyTag, "sshKeyList") { listOf<SshKeyEntity>() }


    val needRefreshSshKeyPage = rememberSaveable { mutableStateOf("") }
    val drawTextList = listOf(
        stringResource(id = R.string.sshkeys),
        stringResource(id = R.string.about),
    )
    val drawIdList = listOf(
        Cons.selectedItem_SshKeys,
        Cons.selectedItem_About,
    )
    val drawIconList = listOf(
        Icons.Filled.Key,
        Icons.Filled.Info,
    )
    val refreshPageList = listOf(
        refreshSshKeyPage@{ changeStateTriggerRefreshPage(needRefreshSshKeyPage) },
        refreshAboutPage@{}, //About页面静态的，不需要刷新
    )

    val openDrawer = {  //打开侧栏(抽屉)
        scope.launch {
            drawerState.apply {
                if (isClosed) open()
            }
        }

        Unit
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                //侧栏菜单展开占屏幕宽度的比例
                //抽屉会过大或过小，然后闪烁一下变成目标宽度，会闪烁，不太好
//                modifier= if(drawerState.isOpen) Modifier.fillMaxWidth(.8F) else Modifier,
                //之前是250dp，显示不全广告，改成320了，正好能显示全
                modifier= Modifier
                    .fillMaxHeight()
                    .widthIn(max = 320.dp)
                    .verticalScroll(rememberScrollState())
                ,
                drawerShape = RectangleShape,
                content = drawerContent(
                    currentHomeScreen = currentHomeScreen,
                    scope = scope,
                    drawerState = drawerState,
                    drawerItemShape = RectangleShape,
                    drawTextList = drawTextList,
                    drawIdList = drawIdList,
                    drawIconList = drawIconList,
                    refreshPageList = refreshPageList,
                    showExit = true
                )
            )
        },
    ) {

        Scaffold(
            modifier = Modifier.nestedScroll(homeTopBarScrollBehavior.nestedScrollConnection)
            ,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        if(currentHomeScreen.intValue == Cons.selectedItem_SshKeys){
                            if(sshKeyPageFilterModeOnFlag.value) {
                                FilterTextField(
                                    sshKeyPageFilterKeyword,
                                )
                            }else {
                                SshKeyTitle(sshkeyPageListState, scope)
                            }
                        }else if (currentHomeScreen.intValue == Cons.selectedItem_About) {
                            AboutTitle()
                        } else {
                            SimpleTitle()
                        }
                    },
                    navigationIcon = {
                        if(sshKeyPageFilterModeOnFlag.value) {
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.close),
                                icon = Icons.Filled.Close,
                                iconContentDesc = stringResource(R.string.close),

                                ) {
                                filterModeOff()
                            }
                        }else {
                            LongPressAbleIconBtn(
                                tooltipText = stringResource(R.string.menu),
                                icon = Icons.Filled.Menu,
                                iconContentDesc = stringResource(R.string.menu),
                            ) {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }
                        }
                    },
                    actions = {
                        if(currentHomeScreen.intValue == Cons.selectedItem_SshKeys) {
                            if(sshKeyPageFilterModeOnFlag.value.not()) {
                                SshKeyPageActions(navController, sshKeyPageCurItem, needRefreshSshKeyPage,
                                    showCreateSshKeyDialog, filterModeOn
                                )
                            }
                        }
                    },
                    scrollBehavior = homeTopBarScrollBehavior,
                )
            }
        ) { contentPadding ->
            if(currentHomeScreen.intValue == Cons.selectedItem_SshKeys) {
//                changeStateTriggerRefreshPage(needRefreshRepoPage)
                SshKeyInnerPage(
                    showBottomSheet = showBottomSheet,
                    sheetState = sheetState,
                    curRepo = sshKeyPageCurItem,
                    curRepoIndex = sshKeyPageCurItemIndex,
                    contentPadding = contentPadding,
                    listState = sshkeyPageListState,
                    filterListState = sshkeyPageFilterListState,
                    openDrawer = openDrawer,
                    itemList = sshKeyList,
                    needRefreshPage = needRefreshSshKeyPage,
                    showCreateDialog = showCreateSshKeyDialog,
                    filterModeOnFlag = sshKeyPageFilterModeOnFlag,
                    filterKeyword = sshKeyPageFilterKeyword,
                    closeFilter = filterModeOff
                )

            }else if(currentHomeScreen.intValue == Cons.selectedItem_About) {
                //About页面是静态的，无需刷新
                AboutInnerPage(contentPadding, openDrawer = openDrawer)
            }
        }
    }

    //compose创建时的副作用
//    LaunchedEffect(currentPage.intValue) {
    LaunchedEffect(Unit) {
        //test
//        delay(30*1000)
//        throw RuntimeException("test save when exception")  // passed, it can save when exception threw, even in android 8, still worked like a charm
        //test

        try {
        } catch (e: Exception) {
            MyLog.e(TAG, "#LaunchedEffect err: "+e.stackTraceToString())
            Msg.requireShowLongDuration("init home err: "+e.localizedMessage)
        }
    }


    //compose被销毁时执行的副作用
    DisposableEffect(Unit) {
//        ("DisposableEffect: entered main")
        onDispose {
//            ("DisposableEffect: exited main")
        }
    }

}



