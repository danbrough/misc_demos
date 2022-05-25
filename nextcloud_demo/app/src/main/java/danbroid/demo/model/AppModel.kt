package danbroid.demo.model

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nextcloud.common.NextcloudClient
import com.nextcloud.common.NextcloudUriProvider
import com.owncloud.android.lib.common.OwnCloudBasicCredentials
import com.owncloud.android.lib.common.OwnCloudClient
import com.owncloud.android.lib.common.OwnCloudClientFactory
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory
import com.owncloud.android.lib.resources.files.CreateFolderRemoteOperation
import com.owncloud.android.lib.resources.files.ReadFolderRemoteOperation
import com.owncloud.android.lib.resources.status.GetCapabilitiesRemoteOperation
import kotlinx.coroutines.*

class AppModel(context: Application) : AndroidViewModel(context) {

  companion object {
    val log = danbroid.logging.getLog(AppModel::class)
  }


  init {
    log.warn("created appmodel: $this")
  }

  private val client: NextcloudClient by lazy {
    NextcloudClient(Uri.parse("https://home.danbrough.org/nextcloud/"),
      "dan",
      "Dyragtb1saes",
      context)
  }

  private val oldClient: OwnCloudClient by lazy {
    OwnCloudClientFactory.createOwnCloudClient("https://home.danbrough.org/nextcloud/".toUri(),
      context,
      true).also {
      it.credentials = OwnCloudBasicCredentials("dan", "Dyragtb1saes")
    }
  }


  fun test() {
    viewModelScope.launch(Dispatchers.IO) {
      log.debug("test worked in $this")
      log.info("client: $client")
/*      ReadFolderRemoteOperation("/").execute(oldClient).also {
        log.warn("RESULT: $it")
      }*/

      GetCapabilitiesRemoteOperation().execute(client).also {
        log.info("RESULT: $it")

      }

      ReadFolderRemoteOperation("/stuff/").execute(oldClient).also {
        log.warn("RESULT: $it")
        if (it.isSuccess)
          log.warn("DATA: ${it.resultData}")
      }
    }


    /*client.execute(ReadFolderRemoteOperation("/")).also {
      log.info("RESULT: $it")
    }
    client.execute(CreateFolderRemoteOperation("/stuff", true)).also {
      log.warn("result: $it")
    }

    client.execute(GetCapabilitiesRemoteOperation()).also {
      log.info("result: $it")
    }*/


    //client.setCreditials(OwnCloudCredentialsFactory.newBasicCredentials("dan","poiqwe098123"))

    /* val createOperation = CreateRemoteFolderOperation(newFolderPath, false)
     createOperation.execute(mClient, this, mHandler)*/
  }

}