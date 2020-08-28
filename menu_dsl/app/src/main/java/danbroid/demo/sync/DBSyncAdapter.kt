package danbroid.demo.sync

import android.accounts.Account
import android.content.*
import android.os.Bundle

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
class DBSyncAdapter @JvmOverloads constructor(
  context: Context,
  autoInitialize: Boolean,
  /**
   * Using a default argument along with @JvmOverloads
   * generates constructor for both method signatures to maintain compatibility
   * with Android 3.0 and later platform versions
   */
  allowParallelSyncs: Boolean = false,
  /*
   * If your app uses a content resolver, get an instance of it
   * from the incoming Context
   */
  val contentResolver: ContentResolver = context.contentResolver
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {

  init {
    log.error("DBSyncAdapter(): autoInitialize:${autoInitialize}")
  }

  override fun onUnsyncableAccount(): Boolean {
    log.warn("onUnsyncableAccount()")
    return super.onUnsyncableAccount()
  }

  override fun onSyncCanceled() {
    log.warn("onSyncCanceled()")
    super.onSyncCanceled()
  }


  override fun onPerformSync(
    account: Account?,
    extras: Bundle?,
    authority: String?,
    provider: ContentProviderClient?,
    syncResult: SyncResult?
  ) {
    log.warn("onPerformSync() account:$account extras:$extras authority:$authority")
  }

  override fun onSecurityException(
    account: Account?,
    extras: Bundle?,
    authority: String?,
    syncResult: SyncResult?
  ) {
    log.error("onSecurityException()")
    super.onSecurityException(account, extras, authority, syncResult)
  }

}

private val log = org.slf4j.LoggerFactory.getLogger(DBSyncAdapter::class.java)

