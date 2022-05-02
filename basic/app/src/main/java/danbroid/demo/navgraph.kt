package danbroid.demo

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment

object NavGraphConstants {
  object Routes {
    const val FirstFragment = "first"
    const val SecondFragment = "second"
  }
}

fun NavController.createBasicNavGraph(context: Context): NavGraph =
  createGraph(startDestination = NavGraphConstants.Routes.FirstFragment) {

    fragment<FirstFragment>(NavGraphConstants.Routes.FirstFragment){
      label = context.getString(R.string.first_fragment_label)
    }

    fragment<SecondFragment>(NavGraphConstants.Routes.SecondFragment){
      label = context.getString(R.string.second_fragment_label)
    }
  }

/*
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/FirstFragment">

    <fragment
        android:id="@+id/FirstFragment"
        android:name="danbroid.demo.FirstFragment"
        android:label="@string/first_fragment_label"
        tools:layout="@layout/fragment_first">

        <action
            android:id="@+id/action_FirstFragment_to_SecondFragment"
            app:destination="@id/SecondFragment" />
    </fragment>
    <fragment
        android:id="@+id/SecondFragment"
        android:name="danbroid.demo.SecondFragment"
        android:label="@string/second_fragment_label"
        tools:layout="@layout/fragment_second">

        <action
            android:id="@+id/action_SecondFragment_to_FirstFragment"
            app:destination="@id/FirstFragment" />
    </fragment>
</navigation>
 */