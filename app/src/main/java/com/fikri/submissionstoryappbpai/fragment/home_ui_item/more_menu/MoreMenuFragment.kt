package com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.FragmentMoreMenuBinding
import com.fikri.submissionstoryappbpai.other_class.withDateFormat
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory
import java.text.DateFormat

class MoreMenuFragment : Fragment() {

    companion object {
        const val DISPLAY_OPTIONS = "display_option"
        const val APPLICATION_DETAILS_OPTIONS = "application_details_option"
    }

    private var binding: FragmentMoreMenuBinding? = null

    private lateinit var viewModel: MoreMenuViewModel
    private lateinit var ctx: Context
    private lateinit var settingFragListener: SettingFragListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreMenuBinding.inflate(inflater, container, false)
        return binding?.root as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireActivity())
        )[MoreMenuViewModel::class.java]

        binding?.llDisplayConfiguration?.setOnClickListener {
            settingFragListener.onOptionClicked(DISPLAY_OPTIONS)
        }

        binding?.llApplicationDetails?.setOnClickListener {
            settingFragListener.onOptionClicked(APPLICATION_DETAILS_OPTIONS)
        }

        binding?.btnLogout?.setOnClickListener {
            settingFragListener.onLogoutRequest()
        }

        viewModel.getUserName().observe(requireActivity()) { name ->
            binding?.tvUserName?.text = name
        }

        viewModel.getActualLastLogin().observe(requireActivity()) { stringDate ->
            val lastLogin =
                stringDate.withDateFormat(
                    type = DateFormat.FULL,
                    pattern = "yyyy-MM-dd HH:mm:ss.SSS"
                )
            binding?.tvLastLogin?.text = getString(R.string.login_since, lastLogin)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        settingFragListener = ctx as SettingFragListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    interface SettingFragListener {
        fun onOptionClicked(item: String)
        fun onLogoutRequest()
    }
}