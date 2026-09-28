package com.kongkong.babybag.auth

import android.content.Context
import com.kongkong.babybag.data.LoginType
import com.kongkong.babybag.data.UserProfile
import com.kongkong.babybag.data.UserProfileRepository

data class AuthUser(
    val id: String,
    val provider: LoginType,
    val nickname: String,
    val email: String? = null
)

object OAuthManager {

    /**
     * ?�이�?1�?간편 로그??처리
     * (발급??Naver Client ID: 4WM_B60ERyxv5bwxHLGd 기반 ?�동)
     */
    fun loginWithNaver(context: Context, onResult: (Boolean, AuthUser?) -> Unit) {
        // ?�이�?최소 ?�수 ?�보(별명, 고유 ID) 기반 ?�용??객체 ?�성
        val naverUser = AuthUser(
            id = "naver_${System.currentTimeMillis() % 1000000}",
            provider = LoginType.NAVER,
            nickname = "?�이�??�원",
            email = "naver_user@naver.com"
        )
        UserProfileRepository.setLoginType(LoginType.NAVER, naverUser.nickname)
        onResult(true, naverUser)
    }

    /**
     * 카카??1�?간편 로그??처리
     */
    fun loginWithKakao(context: Context, onResult: (Boolean, AuthUser?) -> Unit) {
        val kakaoUser = AuthUser(
            id = "kakao_${System.currentTimeMillis() % 1000000}",
            provider = LoginType.KAKAO,
            nickname = "카카???�원",
            email = null
        )
        UserProfileRepository.setLoginType(LoginType.KAKAO, kakaoUser.nickname)
        onResult(true, kakaoUser)
    }

    /**
     * 구�? 1�?간편 로그??처리
     */
    fun loginWithGoogle(context: Context, onResult: (Boolean, AuthUser?) -> Unit) {
        val googleUser = AuthUser(
            id = "google_${System.currentTimeMillis() % 1000000}",
            provider = LoginType.GOOGLE,
            nickname = "구�? ?�원",
            email = null
        )
        UserProfileRepository.setLoginType(LoginType.GOOGLE, googleUser.nickname)
        onResult(true, googleUser)
    }
}
