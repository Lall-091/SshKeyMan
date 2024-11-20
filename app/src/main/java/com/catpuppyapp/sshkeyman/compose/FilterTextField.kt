package com.catpuppyapp.sshkeyman.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.catpuppyapp.sshkeyman.R
import com.catpuppyapp.sshkeyman.utils.state.CustomStateSaveable

@Composable
fun FilterTextField(
    filterKeyWord: CustomStateSaveable<TextFieldValue>,
    placeholderText:String = stringResource(R.string.input_keyword),
    singleLine:Boolean = true,
    modifier: Modifier? = null,
    trailingIconTooltipText: String="",
    trailingIcon: ImageVector?=null,
    trailingIconColor: Color = Color.Unspecified,
    trailingIconDesc: String?=null,
    trailingIconOnClick:(()->Unit)?=null,
    onValueChange:(newValue:TextFieldValue)->Unit = { filterKeyWord.value = it },
) {
    val trailIcon:@Composable (() -> Unit)? = if(trailingIcon!=null){
        {
            LongPressAbleIconBtn(
                tooltipText = trailingIconTooltipText,
                icon = trailingIcon,
                iconContentDesc = trailingIconDesc,
                iconColor = trailingIconColor
            ) {
                if(trailingIconOnClick != null) {
                    trailingIconOnClick()
                }
            }

        }
    }else {
        null
    }
    OutlinedTextField(
        modifier = modifier ?: Modifier.fillMaxWidth(),
        value = filterKeyWord.value,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(placeholderText) },
        singleLine = singleLine,
        trailingIcon = trailIcon,
        // label = {Text(title)}

        //软键盘换行按钮替换成搜索图标且按搜索图标后执行搜索
//        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
//        keyboardActions = KeyboardActions(onSearch = {
//            doFilter(filterKeyWord.value.text)
//        })
    )
}
