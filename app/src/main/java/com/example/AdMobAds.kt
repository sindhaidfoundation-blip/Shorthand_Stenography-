package com.example

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

private const val BANNER_AD_UNIT_ID = "ca-app-pub-4568292017940942/8315885855"
private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4568292017940942/4516241640"

@Composable
fun AdMobBanner() {
    val context = LocalContext.current
    val widthDp = LocalConfiguration.current.screenWidthDp.coerceAtLeast(1)
    val adView = remember(context) { AdView(context) }

    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }

    LaunchedEffect(adView, widthDp) {
        adView.adUnitId = BANNER_AD_UNIT_ID
        adView.setAdSize(
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
        )
        adView.loadAd(AdRequest.Builder().build())
    }

    AndroidView(
        factory = { adView },
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    )
}

/**
 * Shows a test interstitial only after every fourth user-initiated tab change.
 * This avoids interrupting normal typing, dictation, or lesson activity.
 */
class InterstitialAdController(private val activity: Activity) {
    private var interstitialAd: InterstitialAd? = null
    private var tabSwitchCount = 0

    fun load() {
        if (interstitialAd != null) return

        InterstitialAd.load(
            activity,
            INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun onTabSelected() {
        tabSwitchCount += 1
        if (tabSwitchCount % 4 != 0) {
            if (interstitialAd == null) load()
            return
        }

        val ad = interstitialAd
        if (ad == null) {
            load()
            return
        }

        interstitialAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                load()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                load()
            }
        }
        ad.show(activity)
    }
}
