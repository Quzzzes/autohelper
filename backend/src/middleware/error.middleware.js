// Централизованный обработчик ошибок — подключается последним в app.js
function errorMiddleware(err, req, res, next) {
  const status  = err.status  || 500
  const message = err.message || 'Внутренняя ошибка сервера'

  // В dev-режиме показываем стек — в продакшене нет
  if (process.env.NODE_ENV === 'development') {
    console.error(err.stack)
  }

  res.status(status).json({
    success: false,
    error: {
      code:    err.code    || 'SERVER_ERROR',
      message: message,
    },
  })
}

module.exports = errorMiddleware
