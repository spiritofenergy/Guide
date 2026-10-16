package com.kodex.guide.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.User
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.ui.theme.ButtonColorBlue

@Composable
fun DrawerHeader(
    user: User?,
    modifier: Modifier = Modifier
) {
    val userName = user?.userName?.takeIf { it.isNotBlank() }
    val email = user?.email.orEmpty()
    val role = user?.role ?: UserRole.ANONYMOUS

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(ButtonColorBlue.copy(alpha = 0.6f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Image(
            modifier = Modifier
                .size(90.dp)
                .padding(top = 15.dp),
            painter = painterResource(id = R.drawable.emblem),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.taman_peninsula),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        if (!userName.isNullOrEmpty()) {
            Text(
                text = userName,
                color = Color.White,
                fontSize = 16.sp
            )
        }
        if (email.isNotEmpty()) {
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        Text(
            text = "Тариф: ${getRoleDisplayName(role)}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

private fun getRoleDisplayName(role: UserRole): String {
    return when (role) {
        UserRole.ANONYMOUS -> "Гостевой"
        UserRole.USER -> "Пользователь"
        UserRole.BUSINESS -> "Бизнес"
        UserRole.PREMIUM -> "Премиум"
        UserRole.ADMIN -> "Администратор"
    }
}
