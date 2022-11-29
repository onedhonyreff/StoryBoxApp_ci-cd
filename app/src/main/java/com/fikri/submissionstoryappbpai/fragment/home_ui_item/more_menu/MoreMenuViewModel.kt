package com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu

import androidx.lifecycle.ViewModel
import com.fikri.submissionstoryappbpai.repository.MoreMenuRepository

class MoreMenuViewModel(private val moreMenuRepository: MoreMenuRepository) : ViewModel() {
    fun getUserName() = moreMenuRepository.getUserName()

    fun getActualLastLogin() = moreMenuRepository.getActualLastLogin()
}