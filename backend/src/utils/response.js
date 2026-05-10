// Стандартный формат ответа API — используется во всех контроллерах

function successResponse(res, data, status = 200) {
  return res.status(status).json({ success: true, data })
}

function errorResponse(res, code, message, status = 400) {
  return res.status(status).json({
    success: false,
    error: { code, message },
  })
}

module.exports = { successResponse, errorResponse }
