package com.example.ping_proof

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.ping_proof.PingProofEntryScreen.UserDetails
import com.solana.publickey.SolanaPublicKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PreferenceManger {
    val runningEnv: Environment = Environment.PROD
    private const val PREFS_NAME = "PingProofPrefs"
    private const val KEY_VALIDATOR_ID = "KEY_VALIDATOR_ID"
    private const val KEY_VALIDATOR_WALLET_ADDRESS = "KEY_VALIDATOR_WALLET_ADDRESS"
    //For know just store the validating value in mobile later fetch those details whether user is validating or not from backend
    private const val KEY_IS_VALIDATING = "KEY_IS_VALIDATING"
    private const val KEY_NNUMBER_OF_VALIDATIONS = "KEY_NNUMBER_OF_VALIDATIONS"

    private lateinit var prefs: SharedPreferences
    private val _validatorDetails = MutableStateFlow(UserDetails("", null, false))
    val validatorDetails: StateFlow<UserDetails> get() = _validatorDetails

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Set initial value
        _validatorDetails.value = getUserDetails()

        prefs.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_VALIDATOR_ID || key == KEY_VALIDATOR_WALLET_ADDRESS || key == KEY_IS_VALIDATING || key == KEY_NNUMBER_OF_VALIDATIONS) {
                _validatorDetails.value = getUserDetails()
            }
        }
    }

    fun getUserDetails(): UserDetails {
        val userId = prefs.getString(KEY_VALIDATOR_ID, "") ?: ""
        val walletAddressStr = prefs.getString(KEY_VALIDATOR_WALLET_ADDRESS, "") ?: ""
        val isValidating = prefs.getBoolean(KEY_IS_VALIDATING, false)
        val numberOfValidations = prefs.getInt(KEY_NNUMBER_OF_VALIDATIONS, 0)
        val pubKey = try {
            if (walletAddressStr.isNotBlank()) SolanaPublicKey.from(walletAddressStr) else null
        } catch (e: Exception) {
            Log.e("PreferenceManager", "Invalid pubkey: $e")
            null
        }
        return UserDetails(userId = userId, pubKey = pubKey, isValidating = isValidating, totalValidations = numberOfValidations)
    }

    fun setWalletAddress(address: String) {
        prefs.edit().putString(KEY_VALIDATOR_WALLET_ADDRESS, address).apply()
        try {
            _validatorDetails.value = _validatorDetails.value.copy(pubKey = SolanaPublicKey.from(address))
        } catch (e: Exception) {
            Log.e("PreferenceManager", "Invalid pubkey on setWalletAddress: $e")
        }
    }

    fun setUserID(id: String) {
        prefs.edit().putString(KEY_VALIDATOR_ID, id).apply()
        _validatorDetails.value = _validatorDetails.value.copy(userId = id)
    }

    fun setIsValidating(status: Boolean) {
        prefs.edit().putBoolean(KEY_IS_VALIDATING, status).apply()
        _validatorDetails.value = _validatorDetails.value.copy(isValidating = status)
    }

    fun setNumberOfValidations(count: Int) {
        prefs.edit().putInt(KEY_NNUMBER_OF_VALIDATIONS, count).apply()
        _validatorDetails.value = _validatorDetails.value.copy(totalValidations = count)
    }

    fun getWalletAddress(): SolanaPublicKey? {
        val publicKeyString = prefs.getString(KEY_VALIDATOR_WALLET_ADDRESS, "").toString()
        if (publicKeyString.isEmpty()) return null
        return SolanaPublicKey.from(publicKeyString)
    }

    fun getUserID(): String {
        return prefs.getString(KEY_VALIDATOR_ID, "").toString()
    }

    fun getIsValidating(): Boolean {
        return prefs.getBoolean(KEY_IS_VALIDATING, false)
    }

    fun getNumberOfValidations(): Int {
        return prefs.getInt(KEY_NNUMBER_OF_VALIDATIONS, 0)
    }
}