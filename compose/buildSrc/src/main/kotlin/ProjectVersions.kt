import org.gradle.api.JavaVersion
import java.util.*


object ProjectVersions {
  var SDK_VERSION = 30
  var MIN_SDK_VERSION = 23
  val JAVA_VERSION = JavaVersion.VERSION_1_8
  var BUILD_VERSION = 1
  var VERSION_OFFSET = 1
  var GROUP_ID = ""
  var KEYSTORE_PASSWORD = ""
  var VERSION_FORMAT = ""

  val VERSION_NAME: String
    get() = getVersionName()

  fun init(props: Properties) {
    SDK_VERSION = props.getProperty("sdkVersion", "29").toInt()
    MIN_SDK_VERSION = props.getProperty("minSdkVersion", "21").toInt()
    BUILD_VERSION = props.getProperty("buildVersion", "1").toInt()
    VERSION_OFFSET = props.getProperty("versionOffset", "1").toInt()
    VERSION_FORMAT = props.getProperty("versionFormat", "0.0.%d").trim()
    GROUP_ID = props.getProperty("groupID", "").trim()
    KEYSTORE_PASSWORD = props.getProperty("keystorePassword", "").trim()
  }

  fun getIncrementedVersionName() = getVersionName(BUILD_VERSION + 1)


  fun getVersionName(version: Int = BUILD_VERSION) =
      VERSION_FORMAT.format(version - VERSION_OFFSET).trim()
}
