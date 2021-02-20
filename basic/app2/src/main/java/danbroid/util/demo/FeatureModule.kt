package danbroid.util.demo

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

data class Thang(var count: Int = 0)

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureModule {

  @Provides
  @ActivityRetainedScoped
  fun thang(): Thang = Thang(0)
}

private val log = org.slf4j.LoggerFactory.getLogger(FeatureModule::class.java)
