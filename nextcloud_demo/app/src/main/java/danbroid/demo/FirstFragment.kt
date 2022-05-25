package danbroid.demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import danbroid.demo.databinding.FragmentFirstBinding
import danbroid.demo.model.AppModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

  private var _binding: FragmentFirstBinding? = null

  private val appModel: AppModel by activityViewModels()

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {

    _binding = FragmentFirstBinding.inflate(inflater, container, false)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonFirst.setOnClickListener {
      findNavController().navigate(NavGraphConstants.Routes.SecondFragment)
    }

    binding.button1.setOnClickListener {
      log.info("clicked button 1: $appModel")
      appModel.test()

    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}