package com.kingzcheung.xime.keyboard

/** Two fixed slots: symbols at the left edge, numbers immediately beside them. */
enum class KeyboardInputPage { TEXT, SYMBOLS, NUMBERS }

data class KeyboardModeTarget(val label: String, val action: String)

/**
 * 两个固定槽位：默认符号在最左、数字紧跟；[swapped]（设置 ›「交换符号和数字键」）时换过来，数字在最左、符号在右。
 * 符号页 / 数字页的返回键跟着那两格换边，保证「返回键盘」始终在另一个键的右边（与换位后的位置一致）。
 */
fun modeSlotTarget(page: KeyboardInputPage, slot: Int, textLabel: String, swapped: Boolean = false): KeyboardModeTarget {
    require(slot in 1..2)
    if (swapped) return when {
        slot == 1 && page == KeyboardInputPage.NUMBERS -> KeyboardModeTarget(textLabel, "abc")
        slot == 1 -> KeyboardModeTarget("123", "number")
        page == KeyboardInputPage.SYMBOLS -> KeyboardModeTarget(textLabel, "abc")
        else -> KeyboardModeTarget("!@#", "symbol")
    }
    return when {
        slot == 1 && page == KeyboardInputPage.SYMBOLS -> KeyboardModeTarget(textLabel, "abc")
        slot == 1 -> KeyboardModeTarget("!@#", "symbol")
        page == KeyboardInputPage.NUMBERS -> KeyboardModeTarget(textLabel, "abc")
        else -> KeyboardModeTarget("123", "number")
    }
}

/** Secondary pages never become the text destination, including nested tool overlays. */
fun KeyboardPage.textMainType(): MainType = when (this) {
    is KeyboardPage.Main -> type
    is KeyboardPage.Panel -> returnTo
    is KeyboardPage.Overlay -> behind.textMainType()
}

/** Tool overlays cover an input surface without replacing it or ending its input mode. */
fun KeyboardPage.underlyingPage(): KeyboardPage = when (this) {
    is KeyboardPage.Overlay -> behind.underlyingPage()
    else -> this
}

/** First page contains everyday punctuation; specialist categories remain available. */
fun commonSymbolsFor(textLabel: String): List<String> = when (textLabel) {
    "ABC" -> listOf(".", ",", "?", "!", "'", "\"", ":", ";", "-", "_", "/", "\\",
        "@", "#", "$", "%", "&", "*", "+", "=", "(", ")", "[", "]", "{", "}", "<", ">", "~", "`", "…", "°")
    "あいう" -> listOf("、", "。", "？", "！", "ー", "〜", "・", "…", "「", "」", "『", "』",
        "（", "）", "［", "］", "：", "；", "／", "＼", "@", "#", "%", "&", "+", "-", "×", "÷", "=", "♡", "♪", "☆")
    else -> listOf("，", "。", "？", "！", "、", "：", "；", "…", "“", "”", "‘", "’", "（", "）", "《", "》",
        "【", "】", "—", "～", "@", "#", "￥", "%", "&", "*", "+", "-", "=", "/", "\\", "·")
}
