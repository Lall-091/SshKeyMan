package com.catpuppyapp.sshkeyman.utils

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.catpuppyapp.sshkeyman.BuildConfig
import com.catpuppyapp.sshkeyman.constants.Cons
import com.catpuppyapp.sshkeyman.data.AppContainer
import com.catpuppyapp.sshkeyman.data.AppDataContainer
import java.io.File

private val TAG ="AppModel"

class AppModel {
    companion object {

        private val inited_1 = mutableStateOf(false)
        private val inited_2 = mutableStateOf(false)
//        private val inited_3 = mutableStateOf(false)

        val singleInstanceHolder:AppModel = AppModel()



        /**
         * 执行必须且无法显示界面的操作。
         * 中量级，应该不会阻塞很久
         */
        fun init_1(appModel: AppModel = singleInstanceHolder, activityContext:Context, realAppContext:Context, exitApp:()->Unit) {
            val funName = "init_1"

            // run once in app process life time

            if(inited_1.value.not()) {
                inited_1.value = true

                //set dbHolder ，如果以后使用依赖注入框架，这个需要修改
                appModel.dbContainer = AppDataContainer(realAppContext)

            }

            appModel.realAppContext = realAppContext

            // every time run after Activity destory and re create

            appModel.activityContext = activityContext;
//            appModel.mainActivity = mainActivity  //忘了这个干嘛的了，后来反正没用了，IDE提示什么Activity内存泄漏之类的，所以就注释了

            //设置app工作目录，如果获取不到目录，app无法工作，会在这抛出异常
            val externalFilesDir = getExternalFilesDirOrThrowException(activityContext)
            val externalCacheDir = getExternalCacheDirOrThrowException(activityContext)
            val innerDataDir = getInnerDataDirOrThrowException(activityContext)
            appModel.externalFilesDir = externalFilesDir
            appModel.externalCacheDir = externalCacheDir
            appModel.tempKeysDir = createDirIfNonexists(externalCacheDir, Cons.TempKeysDirName)
//            appModel.innerDataDir = innerDataDir

            // clear keys cache every time launch
            try {
                appModel.tempKeysDir.deleteRecursively()
                appModel.tempKeysDir.mkdirs()
            }catch (e:Exception) {
                MyLog.e(TAG, "#$funName: clear keys cache dir err: ${e.stackTraceToString()}")
            }


            // for avoid sshtools get NPE......., even this 'logging.properties' file doesn't exist yet
            System.setProperty("maverick.log.config", "${appModel.externalCacheDir.canonicalPath}/sshtools.logging.properties")

            //test start
//            println("isBouncyCastleRegistered: ${isBouncyCastleRegistered()}")
//            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
//            Security.addProvider(BouncyCastleProvider())

//            val sshKeyEntity = SshKeyUtil.createSshKeyEntity("test ed255", SshKeyUtil.ED25529, "test", "test")
//            println(sshKeyEntity)
            //test end


//            AppModel.singleInstanceHolder.logDir = createLogDirIfNonexists(externalCacheDir, Cons.defaultLogDirName);

            //20240527：禁用，sd相关 ，开始
//            appModel.internalStorageDirsParentDir = createDirIfNonexists(externalFilesDir, Cons.defaultInternalStorageDirsParentDirName)

            //设置repodir
//            appModel.allRepoParentDir = createDirIfNonexists(appModel.internalStorageDirsParentDir, StorageDirCons.DefaultStorageDir.repoStorage1.name)
//            StorageDirCons.DefaultStorageDir.repoStorage1.fullPath = appModel.allRepoParentDir.canonicalPath
//
//            //设置对用户可见的app工作目录
//            appModel.appDataUnderAllReposDir = createDirIfNonexists(appModel.internalStorageDirsParentDir, StorageDirCons.DefaultStorageDir.puppyGitDataDir.name)
//            StorageDirCons.DefaultStorageDir.puppyGitDataDir.fullPath = appModel.appDataUnderAllReposDir.canonicalPath
            //20240527：禁用，sd相关 ，结束


            //与sd相关代码互斥，开始
            //设置repodir
            appModel.homeDir = createDirIfNonexists(externalFilesDir, Cons.homeDirName)
            //test access external storage, passed
//            appModel.allRepoParentDir = createDirIfNonexists(File("/sdcard"), "puppygit-repos")



            //设置对用户可见的app工作目录
            appModel.dataDir = createDirIfNonexists(appModel.homeDir, Cons.dataDirName)
            //与sd相关代码互斥，结束



            // log dir，必须在初始化log前初始化这个变量
            appModel.logDir = createDirIfNonexists(appModel.dataDir, Cons.defaultLogDirName)

            //设置退出app的函数
            appModel.exitApp = exitApp


        }

        /**
         * 执行必须但已经可以显示界面的操作，所以这时候可以看到开发者设置的loading页面了，如果有的话。
         * 可重可轻，有可能阻塞很久
         */
        suspend fun init_2(appModel: AppModel = singleInstanceHolder) {
            val funName = "init_2"
            val applicationContext = appModel.realAppContext

            // one time task in one time app process life time
            if(inited_2.value.not()) {
                inited_2.value = true
                /*
                    init log
                 */
                //初始化日志
                //设置 日志保存时间和日志等级，(考虑：以后把这个改成从配置文件读取相关设置项的值，另外，用runBlocking可以实现阻塞调用suspend方法查db，但不推荐)
                //            MyLog.init(saveDays=3, logLevel='w', logDirPath=appModel.logDir.canonicalPath);
                MyLog.init(
                    logKeepDays=PrefMan.getInt(applicationContext, PrefMan.Key.logKeepDays, MyLog.defaultLogKeepDays),
                    logLevel=PrefMan.getChar(applicationContext, PrefMan.Key.logLevel, MyLog.defaultLogLevel),
                    logDirPath=appModel.logDir.canonicalPath
                )


                //执行会suspend的初始化操作
                //检查是否需要迁移密码
                try {
                    appModel.dbContainer.passEncryptRepository.migrateIfNeed(appModel.dbContainer.sshKeyRepository)
                }catch (e:Exception) {
                    MyLog.e(TAG, "#$funName migrate password err:"+e.stackTraceToString())
                    MyLog.w(TAG, "#$funName migrate password err, user's password may will be invalid :(")
                }



                doJobThenOffLoading {
                    try {
                        //删除过期日志文件
                        MyLog.delExpiredLogs()
                    }catch (e:Exception) {
                        MyLog.e(TAG, "#$funName del expired log files err:"+e.stackTraceToString())
                    }

                }

            }


        }

        /**
         * 执行组件相关变量的初始化操作
         * 轻量级，基本可以说不会阻塞
         */
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun init_3(appModel: AppModel = singleInstanceHolder){

            appModel.navController = rememberNavController()
//            appModel.coroutineScope = rememberCoroutineScope()

//            TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())  //上推隐藏，下拉出现，TopAppBarState 可放到外部以保存状态，如果需要的话
            //TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())  //常驻TopBar，固定显示，不会隐藏
//            appModel.homeTopBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            appModel.homeTopBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

            appModel.haptic = LocalHapticFeedback.current

        }

        fun getAppPackageName(context: Context):String {
            return context.packageName
        }

        fun getAppIcon(context: Context) :ImageBitmap{
            return context.packageManager.getApplicationIcon(getAppPackageName(context)).toBitmap().asImageBitmap()
        }

        fun getAppVersionCode():Int {
            return BuildConfig.VERSION_CODE
        }

        fun getAppVersionName():String {
            return BuildConfig.VERSION_NAME
        }

        //根据资源键名获取值而不是用 R.string.xxx 的id
        fun getStringByResKey(context: Context, resKey: String): String {
            val funName = "getStringByResKey"
            try {
                val res = context.resources
                val resType = "string"  //TODO 需要测试是否支持多语言
                return res.getString(res.getIdentifier(resKey, resType, getAppPackageName(context)))

            }catch (e:Exception) {
                //可能没对应资源之类的
                MyLog.e(TAG, "#$funName err: ${e.stackTraceToString()}")
                return ""
            }
        }

//        fun destroyer() {
//
//
//
//            inited_3.value = false
//            inited_2.value = false
//            inited_1.value = false
//        }


    }



    /**
     * long long ago, this is applicationContext get from Activity, but now, this maybe is baseContext of Activity,
     * baseContext bundled with Activity, save it's reference may cause memory leak;
     * applicationContext bundled with App (process maybe?), save it's reference more time is safe, but it can't get properly resources in some cases,
     * e.g. when call context.getString(), baseContext can get string resource with correct language, but applicationContext maybe can't,
     * that's why I save baseContext rather than applicationContext
     *
     * update this reference in Activity#onCreate can reduce risk of mem leak, but maybe still will make mem clean delay than usual
     *
     * now , actually this is Activity's Context, not the App
     */
    @Deprecated("use `LocalContext.current` instead, but this already many usages, so, keep it for now")
    lateinit var activityContext:Context

    /**
     * real the App context, not activity, this may not be get strings resource with expect language, but for show Toast or load raw resource stream, is fine
     */
    lateinit var realAppContext:Context
    //mainActivity
//    lateinit var mainActivity:Activity

    lateinit var dbContainer: AppContainer

//    @Deprecated("用 `LocalHapticFeedback.current` 替代")
    lateinit var haptic: HapticFeedback

//    @Deprecated("用 `rememberCoroutineScope()` 替代，remember的貌似会随页面创建，随页面释放")
//    lateinit var coroutineScope:CoroutineScope  //这个scope是全局的，生命周期几乎等于app的生命周期(？有待验证，不过因为是在根Compose创建的所以多半差不多是这样)，如果要执行和当前compose生命周期一致的任务，应该用 rememberCoroutineScope() 在对应compose重新获取一个scope

    lateinit var navController:NavHostController

    @OptIn(ExperimentalMaterial3Api::class)
    lateinit var homeTopBarScrollBehavior: TopAppBarScrollBehavior

    lateinit var homeDir: File  // this is internal storage, early version doesn't support clone repo to external path, so this name not indicate this path is internal path, but actually it is
    lateinit var exitApp: ()->Unit
    lateinit var externalFilesDir: File
    lateinit var externalCacheDir: File
    lateinit var tempKeysDir: File

    // app 的内部目录， /data/data/app包名 或者 /data/user/0/app包名，这俩目录好像其中一个是另一个的符号链接
//    lateinit var innerDataDir: File

    //内部StorageDir存储目录，所有类型为“内部”的StorageDir都存储在这个路径下，默认在用户空间 Android/data/xxxxxx包名/files/StorageDirs 路径。里面默认有之前的 allRepoParentDir 和 LogData 目录，且这两个目录不能删除
    //废弃，直接存到 Android/包名/files目录即， 不必再新建一层目录，存files没什么缺点，而且还能兼容旧版，何乐而不为？
    //    lateinit var internalStorageDirsParentDir:File  //20240527：禁用，sd相关

    //对用户可见的app工作目录，存储在allRepos目录下
    private lateinit var dataDir: File

    //20240505:这个变量实际上，半废弃了，只在初始化的时候用一下，然后把路径传给MyLog之后，MyLog就自己维护自己的logDir对象了，就不再使用这个变量了
    private lateinit var logDir: File

    // allRepoDir/PuppyGit-Data/Log
    fun getOrCreateLogDir():File {
        if(!logDir.exists()) {
            logDir.mkdirs()
        }
        return logDir
    }

    fun getOrCreateExternalCacheDir():File{
        if(externalCacheDir.exists().not()) {
            externalCacheDir.mkdirs()
        }

        return externalCacheDir
    }

    fun getOrCreateTempKeysDir():File{
        if(tempKeysDir.exists().not()) {
            tempKeysDir.mkdirs()
        }

        return tempKeysDir
    }

}
