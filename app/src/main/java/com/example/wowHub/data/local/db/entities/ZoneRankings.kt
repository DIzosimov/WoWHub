package com.example.wowHub.data.local.db.entities

data class ZoneRankings(
    val bestPerformanceAverage: String?,
    val medianPerformanceAverage: String?,
    val difficulty: String?,
    val metric: String?,
    val partition: String?,
    val zone: String?,
    val allStars: List<AllStar>?,
    val rankings: List<Ranking>?
) {
    fun toSummaryString(): String {
        return buildString {
            append("Zone ID: $zone\n")
            append("Metric: ${metric?.uppercase() ?: "N/A"} | Difficulty: $difficulty\n")
            append("Best Avg: ${bestPerformanceAverage ?: "0.0"}\n")
            append("Median Avg: ${medianPerformanceAverage ?: "0.0"}\n")

            rankings?.filter { it.rankPercent != null }?.take(3)?.forEach { rank ->
                append("\n• ${rank.encounter?.name}: ${rank.rankPercent}%")
            }
        }
    }
}

data class AllStar(
    val spec: String?,
    val points: String?,
    val possiblePoints: String?,
    val rank: String?,
    val regionRank: String?,
    val serverRank: String?,
    val rankPercent: String?,
    val total: String?,
    val partition: String?
)

data class Ranking(
    val encounter: Encounter?,
    val rankPercent: String?,
    val medianPercent: String?,
    val lockedIn: Boolean?,
    val totalKills: String?,
    val fastestKill: String?,
    val bestAmount: String?,
    val allStars: AllStar?,
    val spec: String?,
    val bestSpec: String?
)

data class Encounter(
    val id: String?,
    val name: String?
)
