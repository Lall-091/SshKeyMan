package com.catpuppyapp.sshkeyman

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.platform.createLifecycleAwareWindowRecomposer
import androidx.core.view.WindowCompat
import com.catpuppyapp.sshkeyman.compose.LoadingText
import com.catpuppyapp.sshkeyman.screen.AppScreenNavigator
import com.catpuppyapp.sshkeyman.theme.CatPuppyAppAndroidTheme
import com.catpuppyapp.sshkeyman.theme.Theme
import com.catpuppyapp.sshkeyman.utils.AppModel
import com.catpuppyapp.sshkeyman.utils.LanguageUtil
import com.catpuppyapp.sshkeyman.utils.MyLog
import com.catpuppyapp.sshkeyman.utils.PrefMan
import com.catpuppyapp.sshkeyman.utils.doJobThenOffLoading
import com.catpuppyapp.sshkeyman.utils.showToast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.Locale


private const val TAG = "MainActivity"


class MainActivity : ComponentActivity() {
    companion object {
        init {
            // must remove android default BC, then add ours
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
            Security.addProvider(BouncyCastleProvider())
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val funName = "onCreate"

        super.onCreate(savedInstanceState)

        // baseContext, life time with activity, can get properly resources, but save reference to static field will increase risk of memory leak
//        AppModel.init_1(activityContext = baseContext, realAppContext = applicationContext, exitApp = {finish()})
        AppModel.init_1(activityContext = this, realAppContext = applicationContext, exitApp = {finish()})

        //for make imePadding() work
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // for catch exception, block start。( refer: https://stackoverflow.com/questions/76061623/how-to-restart-looper-when-exception-throwed-in-jetpack-compose
        val recomposer = window.decorView.createLifecycleAwareWindowRecomposer(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                try {
                    //                throwable.printStackTrace();
                    MyLog.e(TAG, "#$funName err: "+throwable.stackTraceToString())

                    //出错提示下用户就行，经我测试，画面会冻结，但数据不会丢，问题不大
                    showToast(applicationContext, getString(R.string.err_restart_app_may_resolve), Toast.LENGTH_LONG)  //测试了下，能显示Toast

                    //不重新创建Activity的话，页面会freeze，按什么都没响应，不过系统导航键还是可用的
                    //重新创建不一定成功，有可能会返回桌面
//                    ActivityUtil.restartActivityByIntent(this)

                    // 不重建Activity，直接退出
                    finish()

                    // 如果想显示错误弹窗，参见文档 “下一步-20240120.txt” 中的："compose错误处理 compose出错弹窗实现 20240505"

                }catch (e:Exception) {
                    e.printStackTrace()  //再出异常，管不了了，随便吧，打印下就行
                }
            }, lifecycle)

        // set window use our recomposer
        window.decorView.compositionContext = recomposer
        // for catch exception, block end

//        // val settings = SettingsUtil.getSettingsSnapshot()

        val theme = PrefMan.getInt(applicationContext, PrefMan.Key.theme, Theme.defaultThemeValue)

        setContent {
            CatPuppyAppAndroidTheme(
                darkTheme = if(theme == Theme.auto) isSystemInDarkTheme() else (theme == Theme.dark)
            ) {
                MainCompose()
                //                Greeting(baseContext)
            }
        }

    }

    override fun attachBaseContext(newBase: Context) {
        try {
            // check language
            val languageCode = LanguageUtil.getLangCode(newBase)
            if(!LanguageUtil.isSupportedLanguage(languageCode)) {
                // here should run faster as possible, throw Exception is bad for running speed
//               // throw RuntimeException("found unsupported lang in config, will try auto detect language")

                // auto detected or unsupported language
                super.attachBaseContext(newBase)
                return
            }


            // found supported language
            super.attachBaseContext(createContextByLanguageCode(languageCode, newBase))
        }catch (e:Exception) {
            MyLog.e(TAG, "#attachBaseContext err: ${e.localizedMessage}")

            // auto detected or unsupported language
            super.attachBaseContext(newBase)
        }

    }

    private fun createContextByLanguageCode(languageCode: String, baseContext: Context): Context {
        // split language codes, e.g. split "zh-rCN" to "zh" and "CN"
        val (language, country) = LanguageUtil.splitLanguageCode(languageCode)
        val locale = if (country.isBlank()) Locale(language) else Locale(language, country)
        Locale.setDefault(locale)
        val config = baseContext.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return baseContext.createConfigurationContext(config)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//
////        AppModel.destroyer()
//    }
}


@Composable
fun MainCompose() {
    val stateKeyTag = "MainCompose"

    val funName = "MainCompose"
    val appContext = LocalContext.current
    val loadingText = rememberSaveable { mutableStateOf(appContext.getString(R.string.launching))}

    val sshCertRequestListenerChannel = remember { Channel<Int>() }
    val isInitDone = rememberSaveable { mutableStateOf(false) };




    //初始化完成显示app界面，否则显示loading界面
    if(isInitDone.value) {
        AppScreenNavigator()
    }else {
        //这个东西太阴间了，除非是真的需要确保阻止用户操作，例如保存文件，否则尽量别用这个
//        LoadingDialog(loadingText.value)
//        LoadingDialog()

        //TODO 把文字loading替换成有App Logo 的精美画面
        //这里用Scaffold是因为其会根据系统是否暗黑模式调整背景色，就不需要我自己判断了
        Scaffold { contentPadding ->
            LoadingText(contentPadding = contentPadding, text = loadingText.value)
        }
    }

    //compose创建时的副作用
    LaunchedEffect(Unit) {
//        println("LaunchedEffect传Unit只会执行一次，由于maincompose是app其他compose的根基，不会被反复创建销毁，所以maincompose里的launchedEffect只会执行一次，可以用来执行读取配置文件之类的初始化操作")
        try {
//        读取配置文件，初始化状态之类的操作，初始化时显示一个loading页面，完成后更新状态变量，接着加载app页面
            //初始化完成之后，设置变量，显示应用界面
            doJobThenOffLoading {
                isInitDone.value = false

                //test passed
//                assert(!MyLog.isInited)
                //test

                AppModel.init_2()

                //test passed
//                assert(MyLog.isInited)
                //test

                isInitDone.value = true

            }


        } catch (e: Exception) {
            MyLog.e(TAG, "#$funName err: "+e.stackTraceToString())
        }

        //test passed
//        delay(30*1000)
//        AppModel.singleInstanceHolder.exitApp()  //测试exitApp()，Editor未保存的数据是否会保存，结论：会
//        appContext.findActivity()?.recreate()  // 测试重建是否会保存数据，结论：会
//        throw RuntimeException("throw exception test")
        //test
    }

    //compose被销毁时执行的副作用
    DisposableEffect(Unit) {
//        ("DisposableEffect: entered main")
        onDispose {
//            ("DisposableEffect: exited main")
            sshCertRequestListenerChannel.close()
        }
    }

}
