package danbroid.demo.ipfs.content


import android.widget.Toast
import danbroid.demo.ipfs.R
import danbroid.util.menu.MenuActionContext
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.menu
import danbroid.util.menu.rootMenu
import io.ipfs.kotlin.defaults.LocalIPFS
import ipfs.gomobile.android.IPFS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.net.URLEncoder
import java.util.*

const val URI_CONTENT_ROOT = "demo://content"

var _ipfs: IPFS? = null
private val log = LoggerFactory.getLogger("danbroid.demo.ipfs.content")

suspend fun MenuActionContext.runCommand(cmd: suspend (IPFS) -> Unit) =
  withContext(Dispatchers.IO) {
    log.info("runCommand() $cmd")
    val ipfs = _ipfs ?: IPFS(context).also {
      it.enableNamesysPubsub()
      it.enablePubsubExperiment()
      _ipfs = it
    }
    if (!ipfs.isStarted) {
      log.debug("starting ipfs..")
      ipfs.start()
      log.debug("ipfs started")
    }

    try {
      cmd.invoke(ipfs)
    } catch (err: Exception) {
      log.error(err.message, err)
      withContext(Dispatchers.Main) {
        Toast.makeText(context, err.message, Toast.LENGTH_SHORT).show()
      }
    }
  }


val rootContent: MenuItemBuilder by lazy {
  rootMenu<MenuItemBuilder> {
    id = URI_CONTENT_ROOT
    titleID = R.string.app_name

    menu {
      title = "Get ID"
      onClick = {
        runCommand { ipfs ->
          ipfs.newRequest("id").send().let { it.decodeToString() }.also {
            log.debug("result: $it")
          }
        }

        runCommand { ipfs ->
          ipfs.newRequest("version").send().let { it.decodeToString() }.also {
            log.debug("version: $it")
          }
        }
      }
    }
    menu {
      title = "Test 1"
      onClick = {
        withContext(Dispatchers.IO) {
          val api = LocalIPFS()
          log.debug("bandwidth: ${api.stats.bandWidth()}")

          api.pubSub.pub("poiqwe098123", "Hello from phone at ${Date()}").also {
            log.debug("pubsub : $it")
          }
        }
      }
    }

    menu {
      title = "Test 2"
      onClick = {
        withContext(Dispatchers.IO) {
          val api = LocalIPFS()
          log.debug("bandwidth: ${api.stats.bandWidth()}")

          api.pubSub.sub("poiqwe098123") {
            log.info("DATA: $it")
          }
        }
      }
    }

    menu {
      title = "Test 3 - TCP Pubsub"
      onClick = {
        withContext(Dispatchers.IO) {
          val api = LocalIPFS()
          api.pubSub.sub("poiqwe098123") {
            log.info("DATA: $it")
          }
        }
      }
    }
    menu {
      title = "Config 1"
      onClick = {
        runCommand { ipfs ->
          val config = ipfs.config.toString(1)
          File(
            context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS),
            "libipfs.so"
          ).setExecutable(true)

          File(
            context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS),
            "config.txt"
          )
            .writeText(config)
          log.debug("config: $config")
          val swarm = ipfs.config.getJSONObject("Swarm")
          log.info("swarm: ${swarm.toString(1)}")

        }
      }
    }
    menu {
      title = "Get"
      onClick = {
        runCommand { ipfs ->
          ipfs.newRequest("cat")
            .withArgument("/ipns/QmfBFfV72Rw9Vm9wWr2HFARydNWt4ap2xvU8j7Uw2xzzDR").send()
            .also {
              log.debug("received: ${it.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it.decodeToString(), Toast.LENGTH_SHORT)
                  .show()
              }
            }
        }
      }
    }

    menu {
      title = "Add"
      onClick = {
        runCommand { ipfs ->
          val msg = "Hello world at ${Date()}"
          val f = File(context.filesDir, "message.txt")
          f.writeText(msg)

          ipfs.newRequest("files/write").withArgument("/data/1.txt")
            .withOption("create", true)
            .withOption("parents", true)


//            .withHeader("Content-type", "text/plain")
//            .withHeader("Content-Disposition","form-data; name=\"file\"; filename=\"message.txt\"")

            .withHeader(
              "Content-Type",
              "multipart/form-data; boundary=-------BOUNDARY"
            )
            .withBody(
              """---------BOUNDARY
Content-Disposition: form-data; name="file"
Content-Type: text/plain

$msg

---------BOUNDARY--"""
            )

            .send().also {
              log.debug("received: ${it?.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it?.decodeToString(), Toast.LENGTH_SHORT)
                  .show()
              }
            }
        }
      }
    }

    var dataID: String? = null
    menu {
      title = "Stat"
      onClick = {
        runCommand { ipfs ->

          ipfs.newRequest("files/stat").withArgument("/data")
            .send().also {
              it ?: return@also
              val data = JSONObject(it.decodeToString())
              dataID = data.getString("Hash")
              log.debug("hash: $dataID")
              log.debug("received: ${it.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it.decodeToString(), Toast.LENGTH_SHORT)
                  .show()
              }
            }
        }
      }
    }

    menu {
      title = "Publish"
      onClick = {
        runCommand { ipfs ->

          dataID ?: return@runCommand
          ipfs.newRequest("name/publish").withArgument(dataID!!)
            .send().also {
              log.debug("received: ${it.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it.decodeToString(), Toast.LENGTH_SHORT)
                  .show()
              }
            }
        }
      }
    }
/*   "/dnsaddr/h1.danbrough.org/ipfs/QmfBFfV72Rw9Vm9wWr2HFARydNWt4ap2xvU8j7Uw2xzzDR",
                        "/dnsaddr/h1.danbrough.org/ipfs/QmSUVX7in38z9DkJUZJd8Ko9SCoktLW8YV91RqPDjEtVwT",
                        "/dnsaddr/h1.danbrough.org/ipfs/QmSaby9qHtxo2nSMrs7QrQmVEU5k2tLsFYzpVFKJZoxTaL"*/

    menu {
      title = "Connect"
      onClick = {

        runCommand { ipfs ->
          ipfs.newRequest("swarm/connect")
            .withArgument("/ip4/216.189.156.39/tcp/4001/p2p/QmfBFfV72Rw9Vm9wWr2HFARydNWt4ap2xvU8j7Uw2xzzDR")
            .send()?.also {
              log.debug("received: ${it.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it.decodeToString(), Toast.LENGTH_SHORT)
                  .show()
              }
            } ?: log.error("cmd returned null")
        }

        runCommand { ipfs ->
          ipfs.newRequest("swarm/connect")
            .withArgument("/ip4/192.168.1.2/tcp/4001/p2p/QmSUVX7in38z9DkJUZJd8Ko9SCoktLW8YV91RqPDjEtVwT")
            .send()?.also {
              log.debug("received: ${it.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it.decodeToString(), Toast.LENGTH_SHORT)
                  .show()
              }
            } ?: log.error("cmd returned null")
        }

      }
    }

    menu {
      title = "Swarm"
      onClick = {

        runCommand { ipfs ->
          ipfs.newRequest("swarm/addrs")
            .send()?.also {
              log.debug("received: ${it.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it.decodeToString(), Toast.LENGTH_SHORT)
                  .show()
              }
            } ?: log.error("cmd returned null")
        }
      }
    }

    menu {
      title = "Subscribe"
      onClick = {


        runCommand { ipfs ->
          ipfs.newRequest("pubsub/sub").withArgument("poiqwe098123")
            .send()?.also {

              log.debug("received: ${it.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it.decodeToString(), Toast.LENGTH_SHORT).show()
              }
            } ?: log.error("cmd returned null")
        }
      }
    }

    menu {
      title = "Publish"
      onClick = {

        runCommand { ipfs ->
          ipfs.newRequest("pubsub/pub").withArgument("poiqwe098123")
            .withArgument("Hello at ${Date()}!")
            .send()?.also {
              log.debug("received: ${it.decodeToString()}")
              withContext(Dispatchers.Main) {
                Toast.makeText(context, it.decodeToString(), Toast.LENGTH_SHORT)
                  .show()
              }
            } ?: log.error("cmd returned null")
        }
      }
    }

    menu {
      title = "Reconfigure"
      onClick = {

        runCommand { ipfs ->
          httpClient.newCall(
            Request.Builder().url("http://192.168.1.2/config.json").build()
          )
            .execute().body?.also {
              log.debug("got config")
              File(ipfs.repoAbsolutePath, "config").writeText(it.string())


              ipfs.restart()
            }
        }
      }
    }


    menu {
      title = "Queue Job"
      onClick = {


      }
    }
  }

}


private val httpClient = OkHttpClient().newBuilder().build()