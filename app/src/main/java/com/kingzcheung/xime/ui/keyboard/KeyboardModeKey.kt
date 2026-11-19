package com.kingzcheung.xime.ui.keyboard

import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kingzcheung.xime.keyboard.KeyboardInputPage
import com.kingzcheung.xime.keyboard.modeSlotTarget

val LocalModeSlotWeight = staticCompositionLocalOf { 0.8f }
val LocalTextModeLabel = staticCompositionLocalOf { "中文" }

/** A mode key is deliberately a text key, never a language/globe key. */
@Composable
internal fun KeyboardModeKey(
    slot: Int,
    page: KeyboardInputPage = KeyboardInputPage.TEXT,
    onKeyPress: (String) -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onKeyPressDown: ((String) -> Unit)? = null,
    shadowEnabled: Boolean = true,
    shadowElevation: Dp = 1.dp,
    shadowShapeRadius: Dp = 8.dp,
    /** 26 键传入：符号键按住当 Ctrl，拖到字母上发 Ctrl+字母（用户 10-24）。 */
    ctrlTargets: ShiftSlideTargets? = null,
    onCtrlShortcut: ((Char) -> Unit)? = null,
) {
    // 设置「交换符号和数字键」：开关一变（modeKeySwapVersion）就重新读，键盘立刻换边。
    val context = androidx.compose.ui.platform.LocalContext.current
    val swapVersion by com.kingzcheung.xime.settings.SettingsPreferences.modeKeySwapVersion
        .collectAsState()
    val swapped = androidx.compose.runtime.remember(swapVersion) {
        com.kingzcheung.xime.settings.SettingsPreferences.isSymbolNumberSwapped(context)
    }
    val target = modeSlotTarget(page, slot, LocalTextModeLabel.current, swapped)
    androidx.compose.runtime.CompositionLocalProvider(LocalKeyboardPunctuation provides null) {
    if (ctrlTargets != null && onCtrlShortcut != null && target.action == "symbol") CtrlSymbolKeyButton(
        text = target.label,
        targets = ctrlTargets,
        holdMillis = LocalKeyboardInputPreferences.current.symbolKeyHoldMs,
        onOpenSymbols = { onKeyPress(target.action) },
        onShortcut = onCtrlShortcut,
        onPressDown = { onKeyPressDown?.invoke("mode_change") },
        backgroundColor = backgroundColor,
        textColor = textColor,
        modifier = modifier.testTag("mode-slot-$slot"),
        shadowEnabled = shadowEnabled,
        shadowElevation = shadowElevation,
        shadowShapeRadius = shadowShapeRadius,
    ) else KeyButton(
        text = target.label,
        fontSize = 16.sp,
        onClick = { onKeyPress(target.action) },
        onPress = { onKeyPressDown?.invoke("mode_change") },
        backgroundColor = backgroundColor,
        textColor = textColor,
        modifier = modifier.testTag("mode-slot-$slot").semantics { if (target.action == "abc") contentDescription = "返回键盘" },
        shadowEnabled = shadowEnabled,
        shadowElevation = shadowElevation,
        shadowShapeRadius = shadowShapeRadius,
    )
    }
}
