package com.catpuppyapp.sshkeyman.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream


object FsUtils {
    /**
     * internal and external storage path prefix
     */
    const val internalPathPrefix = "Internal:/"
    const val externalPathPrefix = "External:/"

    private val TAG = "FsUtils"
    //必须和 AndroidManifest.xml 里的 provider.android:authorities 的值一样
//    const val PROVIDER_AUTHORITY = "com.catpuppyapp.sshkeyman.play.pro.fileprovider"

    const val textMIME = "text/plain"
    const val appExportFolderName = "PuppyGitExport"
    const val appExportFolderNameUnderDocumentsDirShowToUser = "Documents/${appExportFolderName}"  //显示给用户看的路径

    /**
     * get authority for gen uri for file
     * note: the value must same as provider.android:authorities in AndroidManifest.xml
     */
    fun getAuthorityOfUri(context: Context):String {
        return AppModel.getAppPackageName(context) + ".provider"
    }

    fun getUriForFile(context: Context, file: File):Uri {
        val uri = FileProvider.getUriForFile(
            context,
            getAuthorityOfUri(context),
            file
        )

        MyLog.d(TAG, "#getUriForFile: uri='$uri'")

        return uri
    }


    fun recursiveExportFiles_Saf(contentResolver: ContentResolver, exportDir: DocumentFile, files: List<File>) {
        for(f in files) {
            if(f.isDirectory) {
                val subDir = exportDir.createDirectory(f.name)?:continue
                val subDirFiles = f.listFiles()?:continue
                recursiveExportFiles_Saf(contentResolver, subDir, subDirFiles.toList())
            }else {
                val targetFile = exportDir.createFile("*/*", f.name)?:continue
//                if(srcFile.exists()) {  //无需判断文件名是否已经存在，DocumentFile创建文件时会自动重命名
//
//                }
                val output = contentResolver.openOutputStream(targetFile.uri)?:continue
                f.inputStream().use { ins->
                    output.use { outs ->
                        ins.copyTo(outs)
                    }
                }
            }
        }

    }


    fun createTempFile(prefix:String, suffix:String=".tmp"):File{
        return File(AppModel.singleInstanceHolder.getOrCreateExternalCacheDir().canonicalPath, "$prefix-${generateRandomString()}$suffix")
    }

    fun copy(input:InputStream, output:OutputStream) {
        val inputStream = input.bufferedReader()
        val outputStream = output.bufferedWriter()

        inputStream.use { i ->
            outputStream.use { o->
                var b = i.read()
                while(b != -1) {
                    o.write(b)
                    b = i.read()
                }
            }
        }
    }
}
