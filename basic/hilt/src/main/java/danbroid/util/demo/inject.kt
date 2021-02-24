package danbroid.util.demo

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Singleton


data class Thang(val msg: String)


@Module
@InstallIn(SingletonComponent::class)
object FooModule {

  @Provides
  @Singleton
  fun provideMessage(): Thang {
    return Thang("The message is ${Date()}")
  }

  // This can be deleted once FooActivity is migrated to use @AndroidEntryPoint
  fun inject(activity: MainActivity) {
    log.info("CALLED THIS")
  }


  // This can be deleted once FooActivity is migrated to use @AndroidEntryPoint
  fun inject(model: TestModel) {
    log.info("CALLED THIS TOO")
  }
}

private val log = LoggerFactory.getLogger(FooModule::class.java)