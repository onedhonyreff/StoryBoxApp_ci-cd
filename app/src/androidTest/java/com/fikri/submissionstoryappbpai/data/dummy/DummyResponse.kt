package com.fikri.submissionstoryappbpai.data.dummy

import com.fikri.submissionstoryappbpai.data_model.*

object DummyResponse {
    fun generateLoginResponse(): LoginResponseModel {
        return LoginResponseModel(
            false,
            "success",
            LoginResult(
                "user-FgGwllRGOI2HqhAg",
                "Indry Puji Lestari",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUZnR3dsbFJHT0kySHFoQWciLCJpYXQiOjE2NjY5MjEwMzZ9.TPwBoGZLrFcVIbQWqTWNyvCkp--z4WfY_P7oGsEYlyw"
            )
        )
    }

    fun generateRegisterResponse(): RegisterResponseModel {
        return RegisterResponseModel(
            false,
            "User Created"
        )
    }

    fun generateUploadStoryResponse(): AddStoryResponseModel {
        return AddStoryResponseModel(
            false,
            "success"
        )
    }

    fun generateUploadStoryMapResponse(): AddStoryResponseModel {
        return AddStoryResponseModel(
            false,
            "success"
        )
    }

    fun generateStoryMapResponse(): AllStoryResponseModel {
        return AllStoryResponseModel(
            false,
            "Stories fetched successfully",
            arrayListOf(
                Story(
                    "story-FvU4u0Vp2S3PMsFg",
                    "Indry Puji Lestari",
                    "Lorem Ipsum",
                    "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                    "2022-01-08T06:34:18.598Z",
                    -6.342958179,
                    106.929503828,
                    null
                )
            )
        )
    }

    fun generateStoryListResponse(page: Int = 1, size: Int = 40): AllStoryResponseModel {
        val stories: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                "story-FvU4u0Vp2S3PMsFg $i",
                "Indry Puji Lestari $i",
                "Lorem Ipsum $i",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-01-08T06:34:18.598Z",
                -6.342958179 + i,
                106.929503828 + i,
                null
            )
            stories.add(story)
        }

        return AllStoryResponseModel(
            false,
            "Stories fetched successfully",
            stories.subList((page - 1) * size, (page - 1) * size + size)
        )
    }
}
