package com.example.studyshare

import com.example.studyshare.DataClasses.Avaliacao
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

    @GET("utilizadores/{id}")
    suspend fun getUtilizadorById(@Path("id") id: Int): Utilizador

    @POST("utilizadores")
    suspend fun createUtilizador(@Body utilizador: Utilizador): Response<Unit>

    @PUT("utilizadores/{id}")
    suspend fun updateUtilizador(@Path("id") id: Int, @Body utilizador: Utilizador): Utilizador

    @DELETE("utilizadores/{id}")
    suspend fun deleteUtilizador(@Path("id") id: Int)

    @GET("utilizadores")
    suspend fun verificarUsernameExistente(
        @Query("username") username: String,
        @Query("select") select: String = "id"
    ): List<Utilizador>

    @GET("utilizadores")
    suspend fun verificarEmailExistente(
        @Query("email") email: String,
        @Query("select") select: String = "id"
    ): List<Utilizador>

    @POST("rpc/login_utilizador")
    suspend fun loginUtilizador(
        @Body loginRequest: LoginRequest
    ): List<LoginResponse>

    // MATERIAIS DIDATICOS

    @GET("materiaisdidaticos")
    suspend fun getMateriaisDidaticos(): List<MaterialDidatico>

    @GET("materiaisdidaticos/{id}")
    suspend fun getMaterialDidaticoById(@Path("id") id: Int): MaterialDidatico

    @POST("materiaisdidaticos")
    suspend fun createMaterialDidatico(@Body material: MaterialDidatico): Response<MaterialDidatico>

    @PUT("materiaisdidaticos/{id}")
    suspend fun updateMaterialDidatico(@Path("id") id: Int, @Body material: MaterialDidatico): MaterialDidatico

    @DELETE("materiaisdidaticos/{id}")
    suspend fun deleteMaterialDidatico(@Path("id") id: Int)


    // CATEGORIAS

    @GET("categorias")
    suspend fun getCategorias(): List<Categoria>

    @GET("categorias/{id}")
    suspend fun getCategoriaById(@Path("id") id: Int): Categoria

    @Headers("Prefer: return=representation")
    @POST("categorias")
    suspend fun createCategoria(@Body categoria: Categoria): List<Categoria>

    @PUT("categorias/{id}")
    suspend fun updateCategoria(@Path("id") id: Int, @Body categoria: Categoria): Categoria

    @DELETE("categorias/{id}")
    suspend fun deleteCategoria(@Path("id") id: Int)


    // AVALIACOES

    @GET("avaliacoes")
    suspend fun getAvaliacoes(): List<Avaliacao>

    @GET("avaliacoes/{id}")
    suspend fun getAvaliacaoById(@Path("id") id: Int): Avaliacao

    @POST("avaliacoes")
    suspend fun createAvaliacao(@Body avaliacao: Avaliacao): Avaliacao

    @PUT("avaliacoes/{id}")
    suspend fun updateAvaliacao(@Path("id") id: Int, @Body avaliacao: Avaliacao): Avaliacao

    @DELETE("avaliacoes/{id}")
    suspend fun deleteAvaliacao(@Path("id") id: Int)


    // COMENTARIOS

    @GET("comentarios")
    suspend fun getComentarios(): List<Comentario>

    @GET("comentarios/{id}")
    suspend fun getComentarioById(@Path("id") id: Int): Comentario

    @POST("comentarios")
    suspend fun createComentario(@Body comentario: Comentario): Comentario

    @PUT("comentarios/{id}")
    suspend fun updateComentario(@Path("id") id: Int, @Body comentario: Comentario): Comentario

    @DELETE("comentarios/{id}")
    suspend fun deleteComentario(@Path("id") id: Int)


    // SYNC OFFLINE

    @GET("syncoffline")
    suspend fun getSyncsOffline(): List<SyncOffline>

    @GET("syncoffline/{id}")
    suspend fun getSyncOfflineById(@Path("id") id: Int): SyncOffline

    @POST("syncoffline")
    suspend fun createSyncOffline(@Body syncOffline: SyncOffline): SyncOffline

    @DELETE("syncoffline/{id}")
    suspend fun deleteSyncOffline(@Path("id") id: Int)


    // ESTATISTICAS

    @GET("estatisticas")
    suspend fun getEstatisticas(): List<Estatistica>

    @GET("estatisticas/{id}")
    suspend fun getEstatisticaById(@Path("id") id: Int): Estatistica

    @POST("estatisticas")
    suspend fun createEstatistica(@Body estatistica: Estatistica): Estatistica

    @DELETE("estatisticas/{id}")
    suspend fun deleteEstatistica(@Path("id") id: Int)


    // SESSOES ESTUDO

    @GET("sessoesestudo")
    suspend fun getSessoesEstudo(): List<SessaoEstudo>

    @GET("sessoesestudo/{id}")
    suspend fun getSessaoEstudoById(@Path("id") id: Int): SessaoEstudo

    @POST("sessoesestudo")
    suspend fun createSessaoEstudo(@Body sessaoEstudo: SessaoEstudo): SessaoEstudo

    @PUT("sessoesestudo/{id}")
    suspend fun updateSessaoEstudo(@Path("id") id: Int, @Body sessaoEstudo: SessaoEstudo): SessaoEstudo

    @DELETE("sessoesestudo/{id}")
    suspend fun deleteSessaoEstudo(@Path("id") id: Int)


    // PARTICIPANTES SESSAO

    @GET("participantessessao")
    suspend fun getParticipantesSessao(): List<ParticipanteSessao>

    @GET("participantessessao/{id}")
    suspend fun getParticipanteSessaoById(@Path("id") id: Int): ParticipanteSessao

    @POST("participantessessao")
    suspend fun createParticipanteSessao(@Body participanteSessao: ParticipanteSessao): ParticipanteSessao

    @DELETE("participantessessao/{id}")
    suspend fun deleteParticipanteSessao(@Path("id") id: Int)


    // DISCUSSOES

    @GET("discussoes")
    suspend fun getDiscussoes(): List<Discussao>

    @GET("discussoes/{id}")
    suspend fun getDiscussaoById(@Path("id") id: Int): Discussao

    @POST("discussoes")
    suspend fun createDiscussao(@Body discussao: Discussao): Discussao

    @PUT("discussoes/{id}")
    suspend fun updateDiscussao(@Path("id") id: Int, @Body discussao: Discussao): Discussao

    @DELETE("discussoes/{id}")
    suspend fun deleteDiscussao(@Path("id") id: Int)


    // MENSAGENS DISCUSSAO

    @GET("mensagensdiscussao")
    suspend fun getMensagensDiscussao(): List<MensagemDiscussao>

    @GET("mensagensdiscussao/{id}")
    suspend fun getMensagemDiscussaoById(@Path("id") id: Int): MensagemDiscussao

    @POST("mensagensdiscussao")
    suspend fun createMensagemDiscussao(@Body mensagemDiscussao: MensagemDiscussao): MensagemDiscussao

    @PUT("mensagensdiscussao/{id}")
    suspend fun updateMensagemDiscussao(@Path("id") id: Int, @Body mensagemDiscussao: MensagemDiscussao): MensagemDiscussao

    @DELETE("mensagensdiscussao/{id}")
    suspend fun deleteMensagemDiscussao(@Path("id") id: Int)
}