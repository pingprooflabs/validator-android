package com.example.ping_proof.APIUtils

import com.example.ping_proof.PreferenceManger
import com.example.ping_proof.getEnvironment
import com.solana.publickey.SolanaPublicKey
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val BASE_URL = getEnvironment().baseUrl
    val api: PingProofApi = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
        GsonConverterFactory.create()).build().create(PingProofApi::class.java)

    suspend fun registerWallet(pubKey: SolanaPublicKey): RegisterAPIResponse {
        return api.registerWallet(ValidatorLogin(address = pubKey.base58()))
    }

    suspend fun getCount(validatorId: String): CountResponse {
        return  api.getCount(CountRequest(id = validatorId))
    }

    suspend fun claimReward(validatorId: String): ClaimRewardResponse {
        return api.claimReward(CountRequest(id = validatorId))
    }

    suspend fun getAllTransactions(validatorId: String): List<AllPayments> {
        return api.getAllPayments(CountRequest(id = validatorId))
    }

    suspend fun startValidating(validatorId: String): StartValidatingresponse {
        return api.starValidating(StartValidation(validator_id = validatorId, status = true))
    }

    suspend fun stopValidating(validatorId: String): StartValidatingresponse {
        return api.starValidating(StartValidation(validator_id = validatorId, status = false))
    }

}