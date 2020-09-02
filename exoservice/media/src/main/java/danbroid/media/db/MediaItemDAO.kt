package danbroid.media.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import danbroid.media.domain.MediaItem
import danbroid.media.domain.TABLE_MEDIA_ITEM


@Dao
interface MediaItemDAO {

  @Query("SELECT * FROM $TABLE_MEDIA_ITEM WHERE mediaID = :mediaID")
  fun get(mediaID: String): MediaItem?


  @Query("SELECT * FROM $TABLE_MEDIA_ITEM WHERE parentID = :parentID ORDER BY modified DESC")
  fun getChildrenAsLiveData(parentID: String): LiveData<List<MediaItem>>

  @Query("SELECT * FROM $TABLE_MEDIA_ITEM WHERE parentID = :parentID ORDER BY modified DESC")
  fun getChildren(parentID: String): List<MediaItem>

  @Query("DELETE FROM $TABLE_MEDIA_ITEM WHERE parentID = :parentID")
  fun deleteChildren(parentID: String): Int

  @Insert
  fun insert(children: List<MediaItem>)

  @Delete
  fun delete(mediaItem: MediaItem)

  @Insert
  fun insert(mediaItem: MediaItem)
}



