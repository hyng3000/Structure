package com.improbable.structure.ui.reusables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.improbable.structure.R
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import java.util.*

/**
 * SearchBarData implements the search function for a given list of objects based on the return
 * value(string) of the identity function parameter.
 * */
data class SearchBarData<T>(
    var searchableData: SnapshotStateList<T>,
    val identity: (T) -> String
) {
    var results = searchableData
        private set
    var searchTerm = mutableStateOf("")
        private set

    fun search(search: String){
        searchTerm.value = search

        results = if (search.isEmpty()) {
            searchableData
            } else {
                val result = mutableStateListOf<T>()
                for (data in searchableData) {
                    if (identity(data).contains(search.lowercase(Locale.getDefault()))) {
                        result.add(data)
                    }
                }
                result
            }
        }
    }


@Composable
fun SearchBar(
    textFieldValue: String,
    textFieldOnValueChange: (String) -> Unit,
    placeHolderText: String = "Search",
    modifier: Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
){
    Surface(
        modifier = modifier
            .height(70.dp)
            .fillMaxWidth(),
        color = colorResource(id = R.color.struct_white)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            TextField(
                placeholder =  { Text(placeHolderText, textAlign = TextAlign.Center) },
                value = textFieldValue,
                onValueChange = textFieldOnValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = TextFieldDefaults.textFieldColors(
                    placeholderColor = Color.Gray,
                    backgroundColor = Color.Transparent,
                    ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {focusManager.clearFocus()})
            )
        }
    }
}
