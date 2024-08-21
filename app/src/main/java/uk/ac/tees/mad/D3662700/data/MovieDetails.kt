package uk.ac.tees.mad.D3662700.data


data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String,
    val release_date: String,
    val vote_average: Double,
    val vote_count: Int,
    val runtime: Int,
    val budget: Long,
    val revenue: Long,
    val status: String,
    val original_language: String,
    val genres: List<Genre>,
    val production_companies: List<ProductionCompany>,
    val homepage: String
) {
    data class Genre(
        val id: Int,
        val name: String
    )

    data class ProductionCompany(
        val id: Int,
        val name: String,
        val logo_path: String?,
        val origin_country: String
    )
}