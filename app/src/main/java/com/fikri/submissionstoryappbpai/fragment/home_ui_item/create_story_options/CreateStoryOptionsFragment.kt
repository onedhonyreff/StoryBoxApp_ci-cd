package com.fikri.submissionstoryappbpai.fragment.home_ui_item.create_story_options

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fikri.submissionstoryappbpai.databinding.FragmentCreateStoryOptionsBinding

class CreateStoryOptionsFragment : Fragment() {

    companion object {
        const val CREATE_BASIC_STORY = "create_basic_story"
        const val CREATE_GEOLOCATION_STORY = "create_geolocation_story"
    }

    private var binding: FragmentCreateStoryOptionsBinding? = null

    private lateinit var ctx: Context
    private lateinit var createStoryOptionsListener: CreateStoryOptionsListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateStoryOptionsBinding.inflate(inflater, container, false)
        return binding?.root as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
    }

    private fun setupAction() {
        binding?.apply {
            cvCreateStory.setOnClickListener {
                createStoryOptionsListener.onCreateStoryOptionsClicked(CREATE_BASIC_STORY)
            }

            cvCreateGeolocationStory.setOnClickListener {
                createStoryOptionsListener.onCreateStoryOptionsClicked(CREATE_GEOLOCATION_STORY)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        createStoryOptionsListener = ctx as CreateStoryOptionsListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    interface CreateStoryOptionsListener {
        fun onCreateStoryOptionsClicked(options: String)
    }
}