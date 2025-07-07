package com.example.studyshare

import com.example.studyshare.DataClasses.Categoria
import com.example.studyshare.DataClasses.Comentario
import com.example.studyshare.DataClasses.Discussao
import com.example.studyshare.DataClasses.Estatistica
import com.example.studyshare.DataClasses.MaterialDidatico
import com.example.studyshare.DataClasses.MensagemDiscussao
import com.example.studyshare.DataClasses.ParticipanteSessao
import com.example.studyshare.DataClasses.SessaoEstudo
import com.example.studyshare.DataClasses.SyncOffline
import com.example.studyshare.DataClasses.Utilizador
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(
    val p_username: String,
    val p_password: String
)

data class LoginResponse(
    val id: Int,
    val username: String,
    val email: String
)


interface ApiService {

    // UTILIZADORES

    @GET("utilizadores")
    suspend fun getUtilizadores(): List<Utilizador>

    @GET("utilizadores")
    suspend fun getUtilizadorById(@Query("id") idFilter: String): List<Utilizador>

    @POST("utilizadores")
    suspend fun createUtilizador(@Body utilizador: Utilizador): Response<Unit>

    @DELETE("utilizadores")
    suspend fun deleteUtilizador(@Query("id") id: String): Response<Unit>

    @GET("utilizadores")
    suspend fun verificarUsernameExistente(@Query("username") username: String, @Query("select") select: String = "id"): List<Utilizador>

    @GET("utilizadores")
    suspend fun verificarEmailExistente(@Query("email") email: String, @Query("select") select: String = "id"): List<Utilizador>

    @POST("rpc/login_utilizador")
    suspend fun loginUtilizador(@Body loginRequest: LoginRequest): List<LoginResponse>


    @PATCH("utilizadores")
    @Headers("Prefer: return=representation")
    suspend fun updateUtilizadorParcial(
        @Query("id") id: String = "",   // <- Aqui o valor precisa ser "eq.{id}"
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): List<Utilizador>

    // MATERIAIS DIDATICOS

    @GET("materiaisdidaticos")
    suspend fun getMateriaisDidaticos(): List<MaterialDidatico>

    @GET("materiaisdidaticos")
    suspend fun getMaterialDidaticoById(@Query("id") id: String): List<MaterialDidatico>

    @Headers("Prefer: return=representation")
    @POST("materiaisdidaticos")
    suspend fun createMaterialDidatico(@Body material: MaterialDidatico): List<MaterialDidatico>

    @PUT("materiaisdidaticos")
    suspend fun updateMaterialDidatico(@Query("id") id: String, @Body material: MaterialDidatico): List<MaterialDidatico>

    @DELETE("materiaisdidaticos")
    suspend fun deleteMaterialDidatico(@Query("id") id: String): Response<Unit>

    @GET("materiaisdidaticos")
    suspend fun getMateriaisByAutor(@Query("autor_id") autor_id: String ): List<MaterialDidatico>

    // CATEGORIAS

    @GET("categorias")
    suspend fun getCategorias(): List<Categoria>

    @GET("categorias")
    suspend fun getCategoriaById(@Query("id") id: String): List<Categoria>

    @Headers("Prefer: return=representation")
    @POST("categorias")
    suspend fun createCategoria(@Body categoria: Categoria): List<Categoria>

    @PUT("categorias")
    suspend fun updateCategoria(@Query("id") id: String, @Body categoria: Categoria): List<Categoria>

    @DELETE("categorias")
    suspend fun deleteCategoria(@Query("id") id: String): Response<Unit>

    // COMENTARIOS

    @GET("comentarios")
    suspend fun getComentarios(): List<Comentario>

    @GET("comentarios")
    suspend fun getComentarioById(@Query("id") id: String): List<Comentario>

    @Headers("Prefer: return=representation")
    @POST("comentarios")
    suspend fun createComentario(@Body comentario: Comentario): List<Comentario>

    @PUT("comentarios")
    suspend fun updateComentario(@Query("id") id: String, @Body comentario: Comentario): List<Comentario>

    @DELETE("comentarios")
    suspend fun deleteComentario(@Query("id") id: String): Response<Unit>


    // SYNC OFFLINE

    @GET("syncoffline")
    suspend fun getSyncsOffline(): List<SyncOffline>

    @GET("syncoffline")
    suspend fun getSyncOfflineById(@Query("id") id: String): List<SyncOffline>

    @Headers("Prefer: return=representation")
    @POST("syncoffline")
    suspend fun createSyncOffline(@Body syncOffline: SyncOffline): List<SyncOffline>

    @DELETE("syncoffline")
    suspend fun deleteSyncOffline(@Query("id") id: String): Response<Unit>


    // ESTATISTICAS

    @GET("estatisticas")
    suspend fun getEstatisticas(): List<Estatistica>

    @GET("estatisticas")
    suspend fun getEstatisticaById(@Query("utilizador_id") utilizadorId: String): List<Estatistica>

    @PUT("estatisticas/{userId}")
    suspend fun updateEstatistica(
        @Path("userId") userId: Int,
        @Body estatistica: Estatistica
    ): Response<Unit>

    @POST("estatisticas")
    suspend fun createEstatistica(
        @Body estatistica: Estatistica
    ): Estatistica

    // SESSOES ESTUDO

    @GET("sessoesestudo")
    suspend fun getSessoesEstudo(): List<SessaoEstudo>

    @GET("sessoesestudo")
    suspend fun getSessaoEstudoById(@Query("id") id: String): List<SessaoEstudo>

    @Headers("Prefer: return=representation")
    @POST("sessoesestudo")
    suspend fun createSessaoEstudo(@Body sessaoEstudo: SessaoEstudo): List<SessaoEstudo>

    @PUT("sessoesestudo")
    suspend fun updateSessaoEstudo(@Query("id") id: String, @Body sessaoEstudo: SessaoEstudo): List<SessaoEstudo>

    @DELETE("sessoesestudo")
    suspend fun deleteSessaoEstudo(@Query("id") id: String): Response<Unit>

    @GET("sessoesestudo")
    suspend fun getSessoesEstudoByCriador(
        @Query("criador_id") filtro: String
    ): List<SessaoEstudo>


    // PARTICIPANTES SESSAO

    @GET("participantessessao")
    suspend fun getParticipantesSessao(): List<ParticipanteSessao>

    @GET("participantessessao")
    suspend fun getParticipanteSessaoById(@Query("id") id: String): List<ParticipanteSessao>

    @Headers("Prefer: return=representation")
    @POST("participantessessao")
    suspend fun createParticipanteSessao(@Body participanteSessao: ParticipanteSessao): List<ParticipanteSessao>

    @DELETE("participantessessao")
    suspend fun deleteParticipanteSessao(@Query("id") id: String): Response<Unit>


    // DISCUSSOES

    @GET("discussoes")
    suspend fun getDiscussoes(): List<Discussao>

    @GET("discussoes")
    suspend fun getDiscussaoById(@Query("id") id: String): List<Discussao>

    @Headers("Prefer: return=representation")
    @POST("discussoes")
    suspend fun createDiscussao(@Body discussao: Discussao): List<Discussao>

    @PUT("discussoes")
    suspend fun updateDiscussao(@Query("id") id: String, @Body discussao: Discussao): List<Discussao>

    @DELETE("discussoes")
    suspend fun deleteDiscussao(@Query("id") id: String): Response<Unit>

    @GET("discussoes")
    suspend fun getDiscussoesByCriador(@Query("criador_id") criadorId: String): List<Discussao>


    // MENSAGENS DISCUSSAO

    @GET("mensagensdiscussao")
    suspend fun getMensagensDiscussao(): List<MensagemDiscussao>

    @GET("mensagensdiscussao")
    suspend fun getMensagemDiscussaoById(@Query("id") id: String): List<MensagemDiscussao>

    @Headers("Prefer: return=representation")
    @POST("mensagensdiscussao")
    suspend fun createMensagemDiscussao(@Body mensagemDiscussao: MensagemDiscussao): List<MensagemDiscussao>

    @PUT("mensagensdiscussao")
    suspend fun updateMensagemDiscussao(@Query("id") id: String, @Body mensagemDiscussao: MensagemDiscussao): List<MensagemDiscussao>

    @DELETE("mensagensdiscussao")
    suspend fun deleteMensagemDiscussao(@Query("id") id: String): Response<Unit>
}