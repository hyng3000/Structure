package com.improbable.structure.ui.reusables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.improbable.structure.R
import com.improbable.structure.ui.views.home_screen.HomeScreenConstants


@Composable
fun LargeSquareContentButton(
    onClick: () -> Unit,
    label: String?,
    color: Color,
    modifier: Modifier = Modifier,
    withBorder: Boolean = false,
    fontSize: Int = 16,
    isDeletable: Boolean = false,
    onDelete: () -> Unit = {}
) {
    val deletable by remember {
        mutableStateOf(isDeletable)
    }
    @Composable
    fun button(label: String, fontSize: Int, modifier: Modifier) =
        Button(
            shape = RoundedCornerShape(ReusableConstants.roundedCornerInDp),
            elevation = ButtonDefaults.elevation(
            defaultElevation = ReusableConstants.routineButtonElevation,
            pressedElevation = ReusableConstants.routineButtonElevation.div(2)),
            colors = ButtonDefaults.buttonColors(
            backgroundColor = color
            ),
            onClick = { if (!deletable) {onClick()} },
            modifier = modifier
                .height(130.dp)
                .widthIn(min = 130.dp)
                .padding(5.dp))
            {
                Column(modifier = Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                    if (isDeletable) {
                        IconButton(onClick = onDelete) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.back_button),
                                    tint = Color.Red,
                                    modifier = modifier
                                        .padding(horizontal = 10.dp)
                                        .weight(1f)
                                )
                            }
                        }
                    Text(text = label, textAlign = TextAlign.Center, fontSize = fontSize.sp)
                }
            }
        if (withBorder) {
            modifier.border(
                width = 2.dp,
                color = colorResource(id = R.color.struct_black),
                shape = RoundedCornerShape(ReusableConstants.roundedCornerInDp
                )
            )
        }
        if (label != null) {
            button(label = label, fontSize = fontSize, modifier = modifier)
        }
}

@Composable
fun TopSection(
    mainTitle: String,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .padding(0.dp, ReusableConstants.topSectionHorizontalPadding, 0.dp, 0.dp),
    secondaryTitle: String? = null,
    rightSideButton: @Composable() () -> Unit = {},
    ){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    )
    {
        IconButton(onClick = navigateUp) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button)
            )
        }
        TopSectionTitle(
            mainTitle = mainTitle,
            modifier = Modifier.padding(horizontal = ReusableConstants.topSectionHorizontalPadding), // ReusableConstants.topSectionHorizontalPadding, 0.dp,0.dp,0.dp
            secondaryTitle = secondaryTitle
        )
        rightSideButton()
    }
}

@Composable
fun AddButton(onClick: () -> Unit){
    AddButton(
        onClick = onClick ,
        modifier = Modifier.padding(
            0.dp,
            0.dp,
            ReusableConstants.topSectionHorizontalPadding,
            0.dp
        )
    )
}

@Composable
fun SaveButtonListItem(onClick: () -> Unit){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(ReusableConstants.roundedCornerInDp),
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            ){
            Text("Save", textAlign = TextAlign.Center)
    }
}

@Composable
fun AddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick ,
        shape = RoundedCornerShape(ReusableConstants.roundedCornerInDp),
        contentPadding = PaddingValues(ReusableConstants.standardPadding),
        modifier = modifier.aspectRatio(1f)
    ) {
        Text(text = "+", fontSize = ReusableConstants.titleTextSize)
    }
}

@Composable
fun TopSectionTitle(mainTitle: String, modifier: Modifier = Modifier, secondaryTitle: String? = null) {
    Column(modifier = modifier) {
        if (secondaryTitle != null) {
            Text(
                text = secondaryTitle
            )
        }
        Text(
            text = mainTitle,
            fontSize = ReusableConstants.titleTextSize
        )
    }
}

@Composable
fun RoundedRectangleFromBottomScreenSurface(modifier: Modifier = Modifier, topPadding: Dp = 10.dp, anyComposable: @Composable () -> Unit){
    Surface(
        shape = RoundedCornerShape(topStart = HomeScreenConstants.roundedCornerInDp, topEnd = HomeScreenConstants.roundedCornerInDp, bottomEnd = 0.dp, bottomStart = 0.dp), // RoundedCornerShape(HomeScreenConstants.roundedCornerInDp)
        color = colorResource(id = R.color.struct_black),
        elevation = HomeScreenConstants.elevation,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.99f)
            .padding(0.dp, topPadding, 0.dp, 0.dp)
        )
    {
        anyComposable()
    }
}

@Composable
fun LazyListButton(
    buttonLabel: String, onCLick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = colorResource(id =R.color.struct_white),
    withBorder: Boolean = false,
    fontSize: Int = 16
){
    var border: BorderStroke? = null
    if (withBorder) {
        border = BorderStroke(width = 2.dp, color = colorResource(id = R.color.struct_white))
    }
    Button(
        colors = ButtonDefaults.buttonColors(color),
        onClick = onCLick,
        shape = RoundedCornerShape(ReusableConstants.roundedCornerInDp),
        contentPadding = PaddingValues(0.dp),
        border = border,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(6f)
            .padding(
                horizontal = 20.dp,
                vertical = 10.dp
            )
    )
    {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Text(buttonLabel, fontSize = fontSize.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SaveButton(onClick: () -> Unit, modifier: Modifier){
    IconButton(onClick = { onClick() } ) {
        Icon(
            imageVector = Icons.Filled.Save,
            contentDescription = stringResource(R.string.back_button),
            modifier = modifier.padding(horizontal = 10.dp)
        )
    }
}

object ReusableConstants {
    val roundedCornerInDp: Dp = 20.dp
    val routineButtonElevation: Dp = 10.dp
    val standardPadding: Dp = 5.dp
    val titleTextSize = 30.sp
    val topSectionHorizontalPadding: Dp = 20.dp
}