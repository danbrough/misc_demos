package danbroid.demo.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * A bound Service that instantiates the authenticator
 * when started.
 */
class AuthenticatorService : Service() {

  // Instance field that stores the authenticator object
  private lateinit var mAuthenticator: AccountAuthenticator

  override fun onCreate() {
    log.info("onCreate()")

    // Create a new authenticator object
    mAuthenticator = AccountAuthenticator(this)
  }

  /*
   * When the system binds to this Service to make the RPC call
   * return the authenticator's IBinder.
   */
  override fun onBind(intent: Intent?): IBinder = mAuthenticator.iBinder
}

private val log = org.slf4j.LoggerFactory.getLogger(AuthenticatorService::class.java)
