package by.autohelper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary        = Color(0xFF1565C0),   // синий — цвет автомобильной темы
    onPrimary      = Color.White,
    secondary      = Color(0xFF388E3C),   // зелёный — оплачено / успех
    onSecondary    = Color.White,
    error          = Color(0xFFD32F2F),   // красный — штраф / ошибка
    background     = Color(0xFFF5F5F5),
    surface        = Color.White,
)

@Composable
fun AutoHelperTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content     = content,
    )
}
