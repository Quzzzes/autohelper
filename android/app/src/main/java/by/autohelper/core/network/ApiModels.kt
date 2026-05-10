package by.autohelper.core.network

// Универсальный ответ от API: { success: true, data: T }
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ApiError?,
)

data class ApiError(
    val code: String,
    val message: String,
)

// Auth
data class AuthRequest(val email: String, val password: String)
data class AuthTokens(val accessToken: String, val refreshToken: String)

// Авто
data class Car(
    val id: String,
    val plate: String,
    val make: String,
    val model: String,
    val year: Int,
    val vin: String?,
    val photo_url: String?,
)

data class AddCarRequest(
    val plate: String,
    val make: String,
    val model: String,
    val year: Int,
    val vin: String? = null,
)

// Штрафы
data class Fine(
    val id: String,
    val resolution_no: String?,
    val article: String?,
    val amount: Double,
    val fine_date: String?,
    val status: String,         // unpaid | paid | overdue
    val erip_invoice_no: Int?,
)

// Напоминания
data class Reminder(
    val id: String,
    val car_id: String,
    val type: String,           // osgo | to | techosmotr | blue_card | oil | tires | vu
    val due_date: String,
)

data class CreateReminderRequest(
    val car_id: String,
    val type: String,
    val due_date: String,
)

// Расходы
data class Expense(
    val id: String,
    val category: String,
    val amount: Double,
    val liters: Double?,
    val note: String?,
    val expense_date: String,
)

data class AddExpenseRequest(
    val car_id: String,
    val category: String,
    val amount: Double,
    val liters: Double? = null,
    val note: String? = null,
    val expense_date: String,
)

// СТО
data class Sto(
    val id: String,
    val name: String,
    val city: String?,
    val address: String?,
    val phone: String?,
    val services: Any?,         // массив или строка в зависимости от сервера
    val is_premium: Boolean,
    val rating: Double,
    val reviews_count: Int,
)
