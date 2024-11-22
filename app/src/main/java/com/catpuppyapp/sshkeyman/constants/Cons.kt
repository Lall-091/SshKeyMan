package com.catpuppyapp.sshkeyman.constants

import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Cons {
    companion object {

        const val TempKeysDirName = "temp_keys"

//        @Deprecated("改用：StorageDirCons.DefaultStorageDir.allRepoParentsDir.name")  //20240527：禁用，sd相关
        const val homeDirName = "SshKeyManHome"
//        @Deprecated("改用：StorageDirCons.DefaultStorageDir.puppyGitDataDir.name")  //20240527：禁用，sd相关
        const val dataDirName = "SshKeyManData"

        const val defaultLogDirName = "Log"
        const val defaultSettingsDirName = "Settings"

        const val pressBackDoubleTimesInThisSecWillExit = 3;

            //废弃，直接由 /storage/emulated/0/Android/data/app包名/files目录作为内部存储，那样兼容旧版，更方便
            // 这个是在 files 目录下再建个目录作为顶级目录，确实更整洁，但问题在于不兼容旧版，所以废弃
//        const val defaultInternalStorageDirsParentDirName = "InternalStorageDir"  //20240527：禁用，sd相关

        //DateTimeFormatter是线程安全的
        val defaultDateTimeFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTimeFormatterCompact:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val dateTimeFormatter_yyyyMMdd:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val dateTimeFormatter_yyyyMMddHHmm:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")

//        const val defaultPageCount=50  //默认每页条目数

        //const的作用是定义编译时常量，可能会内联优化之类的
        const val selectedItem_Never = -1 //这个变量永远不会是这个值，这个值只是为了在页面里配合关联的状态变量实现永真判断的，目前20240411用在切换页面更新配置文件中记住的页面值上
        const val selectedItem_About = 6;
        const val selectedItem_Exit = 8;  //仅遵循旧代码规范而加的这个变量，实际上点这个直接退出，不会切换页面，也不会记住最后退出页面
        const val selectedItem_SshKeys = 9;

        const val errorCantGetExternalFilesDir = "Err: Can't get External files Dir"
        const val errorCantGetExternalCacheDir = "Err: Can't get External cache Dir"
        const val errorCantGetInnerDataDir = "Err: Can't get Inner data Dir"
        const val errorCantGetInnerCacheDir = "Err: Can't get Inner cache dir"
        const val errorCantGetInnerFilesDir = "Err: Can't get Inner files dir"

        //尝试创建文件夹来检测仓库名称是否合法，统一加这个前缀
        const val createDirTestNamePrefix = "test-create_"  //含义为测试创建

        const val nav_HomeScreen = "home"



        //db相关常量开始

        val dbUsedTimeZoneOffset: ZoneOffset = ZoneOffset.UTC
        //值大于等于50表示错误或者其他不正常的情况，例如仓库正在克隆，url类型无效，之类的，总之只要值大于等于50，就有问题
        const val dbCommonErrValStart = 50

        //一个无效的非空id。应用场景：有时候页面根据导航url中的id是否为空来决定是否启用某些功能，但url中如果传空字符串会出现"//"这样的字符串导致导航出错，可以用缓存key解决，但那样还要查缓存或者检查key是否有效，不如直接创建一个无效的导航id
        //为避免导航出错，这里不应该有 / 之类的字符
        const val dbInvalidNonEmptyId = "xyz"  //无效id的长度比默认生成的uuid短，且包含非hex字符，且不包含会导致url解析出错的字符，就够了，没必要弄很长

        //代表布尔值
        const val dbCommonFalse=0;
        const val dbCommonTrue=1;

        //base开头的字段每个表都有，所以就加到Common系列变量里了
        const val dbCommonBaseStatusOk=1  //default status
        const val dbCommonBaseStatusErr=dbCommonErrValStart+1  //51

//            注意，workStatus超过50就代表错误了




        //在stringResource中的占位符，格式："ph_随机字符串_编号"，其中ph是placeholder的缩写，编号填数字即可，需要几个占位符，就填几个字符串，一般一个就够了，所以最常用的是编号1，即 `placeholderPrefixForStrRes+1` 的值
        val placeholderPrefixForStrRes = "ph_a3f241dc_"
//        val placeholder1ForStringRes = placeholderPrefixForStrRes+1
//        val placeholder2ForStringRes = placeholderPrefixForStrRes+2
//        val placeholder3ForStringRes = placeholderPrefixForStrRes+3
//        val placeholder4ForStringRes = placeholderPrefixForStrRes+4
//        val placeholder5ForStringRes = placeholderPrefixForStrRes+5




        val sizeTB = 1000000000000
        val sizeTBHumanRead = "TB"
        val sizeGB = 1000000000
        val sizeGBHumanRead = "GB"
        val sizeMB = 1000000
        val sizeMBHumanRead = "MB"
        val sizeKB = 1000
        val sizeKBHumanRead = "KB"
        val sizeBHumanRead = "B"

    }
}
