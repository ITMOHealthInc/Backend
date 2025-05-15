package org.example.models

data class UpdateGenderRequest(val gender: String) 
data class UpdateWeightRequest(val weight: Float) 
data class UpdateHeightRequest(val height: Float) 
data class UpdateWaistRequest(val waist: Float) 
data class UpdateHipsRequest(val hips: Float) 
data class UpdateChestRequest(val chest: Float) 
data class UpdateArmsRequest(val arms: Float) 
data class UpdateBodyFatRequest(val bodyFat: Float) 
data class UpdateMuscleMassRequest(val muscleMass: Float) 
data class UpdateBloodGlucoseRequest(val bloodGlucose: Float) 
data class UpdateBloodPressureSystolicRequest(val systolic: Int) 
data class UpdateBloodPressureDiastolicRequest(val diastolic: Int) 

data class ErrorResponse(val message: String)
data class MeasurementsResponse(
    val gender: String,
    val weight: Float,
    val height: Float,
    val waist: Float,
    val hips: Float,
    val chest: Float,
    val arms: Float,
    val bodyFat: Float,
    val muscleMass: Float,
    val bloodGlucose: Float,
    val bloodPressureSystolic: Int,
    val bloodPressureDiastolic: Int,
    val measuredAt: String
)

