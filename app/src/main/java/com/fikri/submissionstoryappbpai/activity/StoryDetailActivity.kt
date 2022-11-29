package com.fikri.submissionstoryappbpai.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.ActivityStoryDetailBinding
import com.fikri.submissionstoryappbpai.other_class.withDateFormat
import com.fikri.submissionstoryappbpai.view_model.StoryDetailViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory
import java.text.DateFormat


class StoryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    private lateinit var binding: ActivityStoryDetailBinding

    private lateinit var viewModel: StoryDetailViewModel

    private lateinit var story: Story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(this)
            )[StoryDetailViewModel::class.java]

        binding.svMainScrollView.scrollY = viewModel.currentScreenScrollOffsetY

        if (intent.getParcelableExtra<Story>(EXTRA_STORY) != null) {
            story = intent.getParcelableExtra<Story>(EXTRA_STORY) as Story

            Glide.with(this@StoryDetailActivity)
                .load(story.photoUrl)
                .error(R.drawable.default_image)
                .into(binding.ivDetailPhoto)

            binding.apply {
                tvDate.text = resources.getString(
                    R.string.upload_date,
                    story.createdAt.withDateFormat(DateFormat.FULL)
                )
                tvDate.contentDescription = resources.getString(
                    R.string.uploaded_on,
                    story.createdAt.withDateFormat(DateFormat.FULL)
                )
                tvDetailName.text = story.name
                tvDetailName.contentDescription =
                    resources.getString(R.string.uploaded_by, story.name)
                tvDetailDescription.text = story.description
                story.address?.let {
                    tvAddress.visibility = View.VISIBLE
                    tvAddress.text = it
                }
            }
        } else {
            Glide.with(this@StoryDetailActivity)
                .load(R.drawable.default_image)
                .into(binding.ivDetailPhoto)

            binding.apply {
                tvDate.text =
                    resources.getString(R.string.upload_date, resources.getString(R.string.unknown))
                tvDetailName.text = resources.getString(R.string.unknown)
                tvDetailDescription.text = resources.getString(R.string.unknown)
            }
        }
    }

    private fun setupAction() {
        if (viewModel.firstAppeared) {
            viewModel.firstAppeared = false
            playAnimation()
        }
        binding.apply {
            svMainScrollView.viewTreeObserver.addOnScrollChangedListener {
                var scrollY = svMainScrollView.scrollY
                if (scrollY < 0) scrollY = 0
                viewModel.currentScreenScrollOffsetY = scrollY
            }
        }
    }

    private fun playAnimation() {
        binding.apply {
            tvDate.alpha = 0f
            tvDetailDescription.alpha = 0f
            tvAddress.alpha = 0f
            rlNameLabel.translationX = Resources.getSystem().displayMetrics.widthPixels.toFloat()
            cvNameWrapper.translationX = Resources.getSystem().displayMetrics.widthPixels.toFloat()
            ivPencil.translationX =
                -1 * (Resources.getSystem().displayMetrics.widthPixels.toFloat() -
                        (ivPencil.layoutParams.width.toFloat() +
                                llContentWrapper.paddingStart.toFloat() +
                                llContentWrapper.paddingEnd.toFloat() +
                                llDividerWrapper.paddingStart.toFloat() +
                                llDividerWrapper.paddingEnd.toFloat()))
            vwDivider.scaleX = 0f
            vwDivider.translationX =
                -1 * ((Resources.getSystem().displayMetrics.widthPixels.toFloat() / 2) -
                        (llContentWrapper.paddingStart.toFloat() +
                                llContentWrapper.paddingEnd.toFloat()))
        }

        val descriptionText =
            ObjectAnimator.ofFloat(binding.tvDetailDescription, View.ALPHA, 1f).setDuration(500)
        val addressText = ObjectAnimator.ofFloat(binding.tvAddress, View.ALPHA, 1f).setDuration(500)
        val nameAndDivider = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tvDate, View.ALPHA, 1f).setDuration(1000),
                ObjectAnimator.ofFloat(binding.rlNameLabel, View.TRANSLATION_X, 0f)
                    .setDuration(1000),
                ObjectAnimator.ofFloat(binding.cvNameWrapper, View.TRANSLATION_X, 0f)
                    .setDuration(1000),
                ObjectAnimator.ofFloat(binding.ivPencil, View.TRANSLATION_X, 0f).setDuration(1500),
                ObjectAnimator.ofFloat(binding.vwDivider, View.TRANSLATION_X, 0f).setDuration(1500),
                ObjectAnimator.ofFloat(binding.vwDivider, View.SCALE_X, 1f).setDuration(1500),
            )
        }

        AnimatorSet().apply {
            playSequentially(nameAndDivider, descriptionText, addressText)
            start()
        }
    }
}