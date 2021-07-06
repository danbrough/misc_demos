package danbroid.media.service

import androidx.media2.session.MediaController
import androidx.media2.session.SessionCommand

fun MediaController.playItem() {
  sendCustomCommand(SessionCommand(AudioService.COMMAND_PLAY_ITEM, null), null)
}