package com.example.ping_proof.APIUtils

data class ValidatorLogin(
    val address: String
)

data class StartValidation(
    val validator_id: String,
    val status: Boolean
)

data class CountRequest(
    val id: String,
)

data class RegisterAPIResponse(
    val success: Boolean,
    val message: String
)

data class StartValidatingresponse(
    val success: Boolean,
    val message: String
)

data class CountResponse(
    val count: Int
)

data class TaskResponse(
    val taskid: String,
    val validatorId: String,
    val url_tasks: List<URLTasks>
)

enum class StatusoFPing(val value: String) {
    UP("UP"),
    DOWN("DOWN");

    companion object {
        fun fromString(value: String): StatusoFPing? {
            return values().find { it.value.equals(value, ignoreCase = true) }
        }
    }
}

data class URLTasks(
    var status: StatusoFPing?,
    var latency: Double?,
    var validatedAt: Long?,
    val region: String,
    val endpoint: EndpointData,
    val url_task_id: String
)

data class EndpointData(
    val id: Int,
    val url: String,
)
