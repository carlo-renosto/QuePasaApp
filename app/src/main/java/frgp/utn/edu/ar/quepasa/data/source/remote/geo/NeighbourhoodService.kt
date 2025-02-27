package frgp.utn.edu.ar.quepasa.data.source.remote.geo

import frgp.utn.edu.ar.quepasa.data.model.geo.Neighbourhood
import frgp.utn.edu.ar.quepasa.utils.pagination.Page
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NeighbourhoodService {
    @POST("neighbourhoods")
    suspend fun createNeighbourhood(@Body neighbourhood: Neighbourhood): Response<Neighbourhood>

    @GET("neighbourhoods")
    suspend fun getNeighbourhoods(
        @Query("activeOnly") activeOnly: Boolean,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<Page<Neighbourhood>>

    @GET("neighbourhoods/{id}")
    suspend fun getNeighbourhoodById(@Path("id") id: Long): Response<Neighbourhood>

    @GET("neighbourhoods/search")
    suspend fun getNeighbourhoodsByName(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("city") city: Int = -1
    ): Response<Page<Neighbourhood>>

    @PUT("neighbourhoods/{id}")
    suspend fun updateNeighbourhood(@Path("id") id: Long, @Body updatedNeighbourhood: Neighbourhood): Response<Neighbourhood>

    @DELETE("neighbourhoods/{id}")
    suspend fun deleteNeighbourhood(@Path("id") id: Long): Response<Void>
}