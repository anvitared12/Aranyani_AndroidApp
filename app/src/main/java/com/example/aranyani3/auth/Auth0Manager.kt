package com.example.aranyani3.auth

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.remember
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.AuthenticationCallback
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.example.aranyani3.R
import com.example.aranyani3.viewmodel.ScanHistoryViewModel

class Auth0Manager(context: Context) {
    private val auth0 = Auth0(
        context.getString(R.string.auth0_client_id),
        context.getString(R.string.auth0_domain)
    )

    private val authenticationClient = AuthenticationAPIClient(auth0)

    private val credentialsManager = SecureCredentialsManager(
        context,
        authenticationClient,
        SharedPreferencesStorage(context, "auth0.credentials")
    )

    fun login(activity: Activity, onResult: (Result<Credentials>) -> Unit) {
        WebAuthProvider.login(auth0)
            .withScheme(activity.getString(R.string.auth0scheme))
            .withAudience("https://aranyani.api")
            .withScope("openid profile email offline_access")
            .start(activity, object : Callback<Credentials, com.auth0.android.authentication.AuthenticationException> {
                override fun onFailure(error: com.auth0.android.authentication.AuthenticationException) {
                    onResult(Result.failure(error))
                }

                override fun onSuccess(result: Credentials) {
                    try {
                        credentialsManager.saveCredentials(result)
                        onResult(Result.success(result))
                    } catch (error: CredentialsManagerException) {
                        onResult(Result.failure(error))
                    }
                }
            })
    }

    fun logout(activity: Activity, onResult: (Result<Unit>) -> Unit) {
        WebAuthProvider.logout(auth0)
            .withScheme(activity.getString(R.string.auth0scheme))
            .start(activity, object : Callback<Void?, com.auth0.android.authentication.AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    credentialsManager.clearCredentials()
                    onResult(Result.success(Unit))
                }

                override fun onFailure(error: com.auth0.android.authentication.AuthenticationException) {
                    onResult(Result.failure(error))
                }
            })
    }

    fun hasValidCredentials(onResult: (Boolean) -> Unit) {
        onResult(credentialsManager.hasValidCredentials())
    }

    fun getUserProfile(onResult: (Result<UserProfile>) -> Unit) {
        credentialsManager.getCredentials(
            object : Callback<Credentials, CredentialsManagerException> {
                override fun onFailure(error: CredentialsManagerException) {
                    onResult(Result.failure(error))
                }

                override fun onSuccess(result: Credentials) {
                    authenticationClient.userInfo(result.accessToken)
                        .start(object : AuthenticationCallback<UserProfile> {
                            override fun onFailure(error: com.auth0.android.authentication.AuthenticationException) {
                                onResult(Result.failure(error))
                            }

                            override fun onSuccess(result: UserProfile) {
                                onResult(Result.success(result))
                            }
                        })
                }
            }
        )
    }
    // Add this to your existing Auth0Manager.kt
    fun getAccessToken(onResult: (Result<String>) -> Unit) {
        credentialsManager.getCredentials(
            object : Callback<Credentials, CredentialsManagerException> {
                override fun onFailure(error: CredentialsManagerException) {
                    onResult(Result.failure(error))
                }
                override fun onSuccess(result: Credentials) {
                    onResult(Result.success(result.accessToken))
                }
            }
        )
    }
}
