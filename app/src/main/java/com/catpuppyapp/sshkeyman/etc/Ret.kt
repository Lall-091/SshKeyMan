package com.catpuppyapp.sshkeyman.etc

class Ret<T> private constructor(initData:T){
    enum class RetCode {  //小于SuccessCode.default的全是失败代码
        success,
        err_doActForItemErr,
        err_invalidIdxForList,

    }


    var code = RetCode.success
    var msg = ""
    var data:T = initData
    var exception:Exception?=null

    fun hasError():Boolean {
        return code != RetCode.success
    }

    fun success():Boolean {
        return !hasError()
    }

    fun<O> copyWithNewData(newData:O? = null):Ret<O?> {
        return create(
            data = newData,
            msg = msg,
            code = code,
            exception = exception
        )
    }

    companion object {
        fun <T>createErrorDefaultDataNull(errMsg:String, data:T? =null, errCode:RetCode=RetCode.success, exception: Exception?=null):Ret<T?> {
            return create(data, errMsg, errCode, exception)
        }

        fun <T>createErrorDefaultDataNull(data:T? =null, errMsg:String, errCode:RetCode=RetCode.success, exception: Exception?=null):Ret<T?> {
            return create(data, errMsg, errCode, exception)
        }

        fun <T>createSuccessDefaultDataNull(data:T? =null, successMsg:String="", successCode:RetCode=RetCode.success):Ret<T?> {
            return create(data, successMsg, successCode, exception=null)
        }

        fun <T>createError(data:T, errMsg:String, errCode:RetCode=RetCode.success, exception: Exception?=null):Ret<T> {
            return create(data, errMsg, errCode, exception)
        }

        fun <T>createSuccess(data:T, successMsg:String="", successCode:RetCode=RetCode.success):Ret<T> {
            return create(data, successMsg, successCode, exception=null)
        }

        fun <T>create(data:T, msg:String, code:RetCode, exception: Exception?):Ret<T> {
            val r = Ret(data)
            r.data=data
            r.msg=msg
            r.code=code
            r.exception=exception
            return  r
        }

    }

}
