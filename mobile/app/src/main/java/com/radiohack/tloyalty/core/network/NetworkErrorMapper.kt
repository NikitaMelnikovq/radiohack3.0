package com.radiohack.tloyalty.core.network

import com.google.gson.JsonParseException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

object NetworkErrorMapper {
    fun map(throwable: Throwable): String {
        return when (throwable) {
            is ConnectException,
            is UnknownHostException -> "Не удалось подключиться к backend. Проверьте, что FastAPI запущен и baseUrl указан верно. Для Android Emulator используйте http://10.0.2.2:8000 вместо 127.0.0.1."
            is SocketTimeoutException -> "Backend не ответил вовремя. Проверьте, что сервер запущен и сеть доступна."
            is HttpException -> when (throwable.code()) {
                404 -> "Данные для выбранного пользователя не найдены."
                in 500..599 -> "Backend временно недоступен. Повторите попытку после запуска FastAPI."
                else -> "Ошибка backend: HTTP ${throwable.code()}."
            }
            is JsonParseException -> "Backend вернул неожиданный формат данных. Проверьте актуальность API контракта."
            else -> throwable.message ?: "Неизвестная ошибка сети."
        }
    }
}
