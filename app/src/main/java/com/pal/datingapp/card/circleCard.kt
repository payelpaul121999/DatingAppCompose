package com.pal.datingapp.card

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RightAlignedCircles() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleWithIcon(iconRes = R.drawable.presence_video_online)
        Spacer(modifier = Modifier.width(8.dp))
        CircleWithIcon(iconRes = R.drawable.stat_notify_chat)
        Spacer(modifier = Modifier.width(8.dp))
        CircleWithIcon(iconRes = R.drawable.ic_dialog_info)
    }
}

@Composable
fun RightAlignedCirclesForBottom( onLeftClick: (() -> Unit)? = null,onCenterClick: (() -> Unit)? = null,onRightClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleWithIcon(iconRes = R.drawable.presence_video_online,40.dp,24.dp){
            onLeftClick?.invoke()
        }
        Spacer(modifier = Modifier.width(8.dp))
        CircleWithIcon(iconRes = R.drawable.ic_menu_view,50.dp,24.dp){
            onCenterClick?.invoke()
        }
        Spacer(modifier = Modifier.width(8.dp))
        CircleWithIcon(iconRes = R.drawable.ic_dialog_info,40.dp,24.dp){
            onRightClick?.invoke()
        }
    }
}

@Composable
fun CircleWithIcon(
    iconRes: Int,
    circleSize: Dp = 20.dp,
    circleMSize: Dp = 10.dp,
    onClick: (() -> Unit)? = null // optional click callback
) {
    Box(
        modifier = Modifier
            .size(circleSize)
            .clip(CircleShape)
            .background(Color(0xFFFF80AB)) // pink color
            .padding(2.dp) .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(circleMSize)
        )
    }
}
