package danbroid.media.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import danbroid.media.domain.MediaItem

const val MEDIA_DB_FILE = "media.db"

@Database(entities = arrayOf(MediaItem::class), version = 1)
abstract class MediaDB : RoomDatabase() {
  abstract fun mediaItemDAO(): MediaItemDAO


  @Transaction
  fun setChildren(parentID: String, children: List<MediaItem>) {
    val dao = mediaItemDAO()
    dao.deleteChildren(parentID)
    children.forEach {
      it.parentID = parentID
    }
    dao.insert(children)
  }


  companion object {
    @Volatile
    private var db: MediaDB? = null

    fun getInstance(context: Context) = db ?: synchronized(MediaDB::class.java) {
      db ?: Room.databaseBuilder(
        context.applicationContext,
        MediaDB::class.java, MEDIA_DB_FILE
      )/*.addMigrations(object : Migration(1, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
          log.warn("MIgrating from 4 -> 5 $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
          database.execSQL("ALTER TABLE `$TABLE_MEDIA_ITEM` ADD `modified` INTEGER NOT NULL DEFAULT 0")
        }

      })*/
        .fallbackToDestructiveMigration()
        .build().also {
          db = it
        }
    }

  }
}

val log = org.slf4j.LoggerFactory.getLogger(MediaDB::class.java)
