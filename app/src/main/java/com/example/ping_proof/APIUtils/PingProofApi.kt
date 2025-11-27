package com.example.ping_proof.APIUtils
import retrofit2.http.Body
import retrofit2.http.POST

interface PingProofApi {
    @POST("/api/v1/validator/signin")
    suspend fun registerWallet(@Body login: ValidatorLogin): RegisterAPIResponse

    @POST("/api/v1/validator/start-validating")
    suspend fun starValidating(@Body validatorId: StartValidation): StartValidatingresponse

    @POST("/api/v1/validator/get-count")
    suspend fun getCount(@Body validatorId: CountRequest): CountResponse

    @POST("/api/v1/validator/payment")
    suspend fun claimReward(@Body valdatorId: CountRequest): ClaimRewardResponse

    @POST("/api/v1/validator/list-payment")
    suspend fun getAllPayments(@Body valdatorId: CountRequest): List<AllPayments>
}