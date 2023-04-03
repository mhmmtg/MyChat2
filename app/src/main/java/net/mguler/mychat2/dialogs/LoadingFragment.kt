package net.mguler.mychat2.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.mguler.mychat2.databinding.FragmentLoadingBinding

class LoadingFragment : DialogFragment() {
    private lateinit var binding: FragmentLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

}