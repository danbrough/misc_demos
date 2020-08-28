package danbroid.demo.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*

/**
 * Define a Service that returns an [android.os.IBinder] for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 */
class SyncService : Service() {
  /*
   * Instantiate the sync adapter object.
   */
  override fun onCreate() {
    log.info("onCreate() ${Date()}")
    /*
     * Create the sync adapter as a singleton.
     * Set the sync adapter as syncable
     * Disallow parallel syncs
     */
    synchronized(sSyncAdapterLock) {
      sSyncAdapter = sSyncAdapter ?: DBSyncAdapter(applicationContext, true)
    }
  }

  /**
   * Return an object that allows the system to invoke
   * the sync adapter.
   *
   */
  override fun onBind(intent: Intent): IBinder {
    log.info("onBind() intent:$intent")
    /*
     * Get the object that allows external processes
     * to call onPerformSync(). The object is created
     * in the base class code when the SyncAdapter
     * constructors call super()
     *
     * We should never be in a position where this is called before
     * onCreate() so the exception should never be thrown
     */
    return sSyncAdapter?.syncAdapterBinder ?: throw IllegalStateException()
  }

  override fun onDestroy() {
    log.info("onDestroy()")
    super.onDestroy()
  }

  companion object {
    // Storage for an instance of the sync adapter
    private var sSyncAdapter: DBSyncAdapter? = null

    // Object to use as a thread-safe lock
    private val sSyncAdapterLock = Any()
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(SyncService::class.java)
