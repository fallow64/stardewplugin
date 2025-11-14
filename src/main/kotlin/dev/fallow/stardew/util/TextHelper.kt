package dev.fallow.stardew.util

import com.marcusslover.plus.lib.color.Color
import com.marcusslover.plus.lib.sound.Note
import com.marcusslover.plus.lib.text.Text
import net.kyori.adventure.audience.Audience
import org.bukkit.Sound

enum class StardewColor(val plus: Color) {
    Green(Color.of("#95dab6")),
    Red(Color.of("#dc8580")),
    Blue(Color.of("#83b2d0")),
    DarkBlue(Color.of("#7f87b2")),
    LightGray(Color.of("#abadb8")),
}

enum class FeedbackType(val display: String, val color: Color, val sound: Note? = null) {
    Success(
        display = "SUCCESS",
        color = StardewColor.Green.plus,
        sound = Note.of(Sound.ENTITY_VILLAGER_YES, 1f, 1f, 0)
    ),
    Error(
        display = "ERROR",
        color = StardewColor.Red.plus,
        sound = Note.of(Sound.ENTITY_VILLAGER_NO, 1f, 1f, 1)
    ),
    Info(
        display = "INFO",
        color = StardewColor.Blue.plus,
        sound = Note.of(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
    ),
    Debug(
        display = "DEBUG",
        color = StardewColor.DarkBlue.plus
    )
}

fun Audience.sendFeedback(type: FeedbackType, msg: Text, enableSound: Boolean = true) {
    Text.of("${type.color.plus()}[${type.display}]&r ").append(msg).send(this)

    if (enableSound && type.sound != null) {
        type.sound.send(this)
    }
}

fun Audience.sendFeedback(type: FeedbackType, msg: String, enableSound: Boolean = true) {
    sendFeedback(type, Text.of(msg), enableSound)
}

fun Audience.sendText(text: Text) {
    text.send(this)
}
