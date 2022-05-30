package com.bikenance.features.strava.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id


data class StravaActivity(

    @BsonId
    val _id: Id<StravaActivity>? = null,

    @JsonProperty("id")
    var id: String? = null,
    @JsonProperty("resource_state")
    var resourceState: Int? = null,
    @JsonProperty("external_id")
    var externalId: String? = null,
    @JsonProperty("upload_id")
    var uploadId: String? = null,
    @JsonProperty("athlete")
    var athleteRef: AthleteRef? = AthleteRef(),
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("distance")
    var distance: Int? = null,
    @JsonProperty("moving_time")
    var movingTime: Int? = null,
    @JsonProperty("elapsed_time")
    var elapsedTime: Int? = null,
    @JsonProperty("total_elevation_gain")
    var totalElevationGain: Int? = null,
    @JsonProperty("type")
    var type: String? = null,
    @JsonProperty("start_date")
    var startDate: String? = null,
    @JsonProperty("start_date_local")
    var startDateLocal: String? = null,
    @JsonProperty("timezone")
    var timezone: String? = null,
    @JsonProperty("utc_offset")
    var utcOffset: Int? = null,
    @JsonProperty("start_latlng")
    var startLatlng: ArrayList<Double> = arrayListOf(),
    @JsonProperty("end_latlng")
    var endLatlng: ArrayList<Double> = arrayListOf(),
    @JsonProperty("achievement_count")
    var achievementCount: Int? = null,
    @JsonProperty("kudos_count")
    var kudosCount: Int? = null,
    @JsonProperty("comment_count")
    var commentCount: Int? = null,
    @JsonProperty("athlete_count")
    var athleteCount: Int? = null,
    @JsonProperty("photo_count")
    var photoCount: Int? = null,
    @JsonProperty("map")
    var map: Map? = Map(),
    @JsonProperty("trainer")
    var trainer: Boolean? = null,
    @JsonProperty("commute")
    var commute: Boolean? = null,
    @JsonProperty("manual")
    var manual: Boolean? = null,
    @JsonProperty("private")
    var private: Boolean? = null,
    @JsonProperty("flagged")
    var flagged: Boolean? = null,
    @JsonProperty("gear_id")
    var gearId: String? = null,
    @JsonProperty("from_accepted_tag")
    var fromAcceptedTag: Boolean? = null,
    @JsonProperty("average_speed")
    var averageSpeed: Double? = null,
    @JsonProperty("max_speed")
    var maxSpeed: Double? = null,
    @JsonProperty("average_cadence")
    var averageCadence: Double? = null,
    @JsonProperty("average_temp")
    var averageTemp: Int? = null,
    @JsonProperty("average_watts")
    var averageWatts: Double? = null,
    @JsonProperty("weighted_average_watts")
    var weightedAverageWatts: Int? = null,
    @JsonProperty("kilojoules")
    var kilojoules: Double? = null,
    @JsonProperty("device_watts")
    var deviceWatts: Boolean? = null,
    @JsonProperty("has_heartrate")
    var hasHeartrate: Boolean? = null,
    @JsonProperty("max_watts")
    var maxWatts: Int? = null,
    @JsonProperty("elev_high")
    var elevHigh: Double? = null,
    @JsonProperty("elev_low")
    var elevLow: Double? = null,
    @JsonProperty("pr_count")
    var prCount: Int? = null,
    @JsonProperty("total_photo_count")
    var totalPhotoCount: Int? = null,
    @JsonProperty("has_kudoed")
    var hasKudoed: Boolean? = null,
    @JsonProperty("workout_type")
    var workoutType: Int? = null,
    @JsonProperty("suffer_score")
    var sufferScore: String? = null,
    @JsonProperty("description")
    var description: String? = null,
    @JsonProperty("calories")
    var calories: Double? = null,
    @JsonProperty("segment_efforts")
    var segmentEfforts: ArrayList<SegmentEfforts> = arrayListOf(),
    @JsonProperty("splits_metric")
    var splitsMetric: ArrayList<SplitsMetric> = arrayListOf(),
    @JsonProperty("laps")
    var laps: ArrayList<Laps> = arrayListOf(),
    @JsonProperty("gear")
    var gear: Gear? = Gear(),
    @JsonProperty("partner_brand_tag")
    var partnerBrandTag: String? = null,
    @JsonProperty("highlighted_kudosers")
    var highlightedKudosers: ArrayList<HighlightedKudosers> = arrayListOf(),
    @JsonProperty("hide_from_home")
    var hideFromHome: Boolean? = null,
    @JsonProperty("device_name")
    var deviceName: String? = null,
    @JsonProperty("embed_token")
    var embedToken: String? = null,
    @JsonProperty("segment_leaderboard_opt_out")
    var segmentLeaderboardOptOut: Boolean? = null,
    @JsonProperty("leaderboard_opt_out")
    var leaderboardOptOut: Boolean? = null

)