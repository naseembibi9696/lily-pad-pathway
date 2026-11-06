package com.ripple.lilypath.data.sample

import android.media.ToneGenerator
import com.ripple.lilypath.domain.model.FrogFriend
import com.ripple.lilypath.domain.model.Season

object SampleData {

    val frogFriends: List<FrogFriend> = listOf(
        FrogFriend(Season.SPRING, "Pip", false),
        FrogFriend(Season.SUMMER, "Marsh", false),
        FrogFriend(Season.AUTUMN, "Ember", false),
        FrogFriend(Season.WINTER, "Frost", false)
    )

    val pentatonicTones: List<Int> = listOf(
        ToneGenerator.TONE_DTMF_1,
        ToneGenerator.TONE_DTMF_3,
        ToneGenerator.TONE_DTMF_5,
        ToneGenerator.TONE_DTMF_7,
        ToneGenerator.TONE_DTMF_9
    )

    val seasonOrder: List<Season> = listOf(
        Season.SPRING,
        Season.SUMMER,
        Season.AUTUMN,
        Season.WINTER
    )
}
