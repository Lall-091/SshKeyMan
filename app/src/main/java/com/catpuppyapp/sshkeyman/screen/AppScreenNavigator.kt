package com.catpuppyapp.sshkeyman.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.catpuppyapp.sshkeyman.constants.Cons
import com.catpuppyapp.sshkeyman.utils.AppModel

@Composable
fun AppScreenNavigator() {
    //初始化compose相关变量
    AppModel.init_3()

    //上面初始化成功，这里才能获取到navController
    val navController =AppModel.singleInstanceHolder.navController

//    val startScreen = Cons.selectedItem_Repos
    //初始启动页面的子页面（Repos/Files之类的）
    val currentHomeScreen = rememberSaveable{ mutableIntStateOf(Cons.selectedItem_SshKeys) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

//    val scope = ComposeHelper.getCoroutineScope()

//    val homeTopBarScrollBehavior = AppModel.singleInstanceHolder.homeTopBarScrollBehavior
    val editorPageLastFilePath = rememberSaveable { mutableStateOf("")}

    val repoPageListState = rememberLazyListState()
//    val filePageListState = StateUtil.getRememberLazyListState()
    //网上拷贝的声称能记住滚动状态的lazyColumn listState，实际上，没有用，我记得默认的listState是能记住的，现在不能记住，可能是bug
//    val repoPageListState = rememberForeverLazyListState("repo")
//    val filePageListState = rememberForeverLazyListState("file")
    //好像lazyColumn用不了这个，只有Column能用
//    val reposPageScrollState = rememberScrollState()
//    val filesPageScrollState = rememberScrollState()

//    val haptic = LocalHapticFeedback.current


    //x 20240419 已实现) 改成记住上次退出屏幕从配置文件读取
    val navStartScreen = Cons.nav_HomeScreen;

    //TODO 添加一级页面的数据，编辑器打开的文件列表，都存到state里，并提供一个setter，
    // setter会更新内存里的数据和数据库里的数据，setter参考currentPage的setCurrentPage()，
    // 所有需要使用一级页面数据的页面都使用这里创建的state，并调用相同的setter更新数据，
    // 这样，数据更新后不需传递，其他compose也能拿到最新的数据，只有当依赖某信息显示页面且这
    // 个信息是一次性的时候才在路由里传数据，例如需要用户id来显示某用户的信息，这种情况，就在路由url里传id

    NavHost(navController = navController, startDestination = navStartScreen) {
        composable(Cons.nav_HomeScreen) {
            HomeScreen(drawerState, currentHomeScreen, repoPageListState, editorPageLastFilePath)
        }
        //注意：带返回箭头的二级菜单用 navController.navigateUp() api返回，这个和navipopup有点不同，如果在自己app里导航，两者一致，如果从外部app导航进本app，例如从其他app通过deeplink跳转到了本app，那么navigateUp会返回其他app，而popup则会尝试返回到操作系统为本app构建的虚拟栈中，按照这个理解，navigateUp期望返回进入本app的应用，而popup则期望返回本app内当前页面的上一页面（如果有的话）
        //如果不是通过外部app跳转进本程序，navigateUp不会退出本app，但popup会。
        //把popup理解成和返回键关联，naviup理解成和左上角返回箭头关联就行了。
        //简单来说，在左上角返回按钮那里用navigateUp()，其他情况想返回用popup就行了。
        /*
        参见：https://stackoverflow.com/questions/67245601/difference-between-navigateup-and-popbackstack
        * For starters: If you arrived at your current destination from within your app, they act exactly identical.

            ONLY if you arrived at the current destination with a deeplink from a different app, they will behave differently:

            navigateUp() will leave your app and return to the app that navigated to the deep link in your app.

            popBackStack() will attempt to go back one step in your backstack, and will not do anything if there is no backstack entry.
        * */

    }
}
