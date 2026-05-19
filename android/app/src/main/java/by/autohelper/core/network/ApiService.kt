package by.autohelper.core.network

import retrofit2.http.*

interface ApiService {

    // ─── Auth ──────────────────────────────────────────────────
    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): ApiResponse<AuthTokens>

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): ApiResponse<AuthTokens>

    // ─── Гараж ─────────────────────────────────────────────────
    @GET("api/cars")
    suspend fun getCars(): ApiResponse<List<Car>>

    @POST("api/cars")
    suspend fun addCar(@Body request: AddCarRequest): ApiResponse<Car>

    @DELETE("api/cars/{id}")
    suspend fun deleteCar(@Path("id") carId: String): ApiResponse<Any>

    // ─── Штрафы ────────────────────────────────────────────────
    @GET("api/fines/car/{carId}")
    suspend fun getFines(@Path("carId") carId: String): ApiResponse<List<Fine>>

    // ─── Напоминания ───────────────────────────────────────────
    @GET("api/reminders/car/{carId}")
    suspend fun getReminders(@Path("carId") carId: String): ApiResponse<List<Reminder>>

    @POST("api/reminders")
    suspend fun createReminder(@Body request: CreateReminderRequest): ApiResponse<Reminder>

    @DELETE("api/reminders/{id}")
    suspend fun deleteReminder(@Path("id") reminderId: String): ApiResponse<Any>

    // ─── Расходы ───────────────────────────────────────────────
    @GET("api/expenses/car/{carId}")
    suspend fun getExpenses(@Path("carId") carId: String): ApiResponse<List<Expense>>

    @POST("api/expenses")
    suspend fun addExpense(@Body request: AddExpenseRequest): ApiResponse<Expense>

    // ─── СТО ───────────────────────────────────────────────────
    @GET("api/sto")
    suspend fun getStoList(@Query("city") city: String? = null): ApiResponse<List<Sto>>

    // ─── Auth доп. ─────────────────────────────────────────────
    @POST("api/auth/logout")
    suspend fun logout(): ApiResponse<Any>
}
