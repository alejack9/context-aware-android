package it.unibo.giacche.contextaware.models;

data class GeoJSONProperties(
    val reqId: String,
    val reqNr: Int,
    val timeStamp: Long,
    val noiseLevel: Double,
    val alpha: Boolean,
    val alphaValue: Double,
    val cloaking: Boolean,
    val cloakingTimeout: Int,
    val cloakingK: Int,
    val cloakingSizeX: Double,
    val cloakingSizeY: Double,
    val dummyLocation: Boolean,
    val dummyUpdatesCount: Int,
    val dummyUpdatesRadiusMin: Double,
    val dummyUpdatesRadiusStep: Double,
    val gpsPerturbated: Boolean,
    val perturbatorDecimals: Int
)

fun GeoJSONProperties.toMap(): Map<String, Any> {
    return mapOf(
        "reqId" to this.reqId,
        "reqNr" to this.reqNr,
        "timeStamp" to this.timeStamp,
        "noiseLevel" to this.noiseLevel,
        "alpha" to this.alpha,
        "alphaValue" to this.alphaValue,
        "cloaking" to this.cloaking,
        "cloakingTimeout" to this.cloakingTimeout,
        "cloakingK" to this.cloakingK,
        "cloakingSizeX" to this.cloakingSizeX,
        "cloakingSizeY" to this.cloakingSizeY,
        "dummyLocation" to this.dummyLocation,
        "dummyUpdatesCount" to this.dummyUpdatesCount,
        "dummyUpdatesRadiusMin" to this.dummyUpdatesRadiusMin,
        "dummyUpdatesRadiusStep" to this.dummyUpdatesRadiusStep,
        "gpsPerturbated" to this.gpsPerturbated,
        "perturbatorDecimals" to this.perturbatorDecimals
    )
}