package danbroid.demo.compose.ui


import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import danbroid.demo.compose.Actions
import danbroid.demo.compose.Destination
import danbroid.demo.compose.ui.utils.BackDispatcherAmbient
import danbroid.demo.compose.ui.utils.Navigator

@Composable
fun DemoApp(backDispatcher: OnBackPressedDispatcher) {
  @Suppress("RemoveExplicitTypeArguments")
  val navigator: Navigator<Destination> = rememberSavedInstanceState(
    saver = Navigator.saver<Destination>(backDispatcher)
  ) {
    Navigator(Destination.Onboarding, backDispatcher)
  }
  val actions = remember(navigator) { Actions(navigator) }

  Providers(BackDispatcherAmbient provides backDispatcher) {
    ProvideDisplayInsets {
      Crossfade(navigator.current) { destination ->
        when (destination) {
          Destination.Onboarding -> Destination.Onboarding(actions.onboardingComplete)
          Destination.Courses -> Destination.Courses(actions.selectCourse)
          is Destination.Course -> CourseDetails(
            destination.courseId,
            actions.selectCourse,
            actions.upPress
          )
        }
      }
    }
  }
}
