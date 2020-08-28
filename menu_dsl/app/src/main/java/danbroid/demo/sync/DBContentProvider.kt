package danbroid.demo.sync

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class DBContentProvider : ContentProvider() {

  override fun insert(uri: Uri, values: ContentValues?): Uri? {
    TODO("Not yet implemented")
  }

  override fun query(
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?
  ): Cursor? {
    TODO("Not yet implemented")
  }

  override fun onCreate(): Boolean {
    log.info("onCreate()")
    return true
  }

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<out String>?
  ): Int {
    TODO("Not yet implemented")
  }

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
    TODO("Not yet implemented")
  }

  override fun getType(uri: Uri): String? {
    TODO("Not yet implemented")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(DBContentProvider::class.java)
