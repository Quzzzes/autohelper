const { validationResult } = require('express-validator')
const authService          = require('./auth.service')
const { successResponse, errorResponse } = require('../../utils/response')

async function register(req, res, next) {
  const errors = validationResult(req)
  if (!errors.isEmpty()) {
    return errorResponse(res, 'VALIDATION_ERROR', errors.array()[0].msg)
  }

  try {
    const { email, password } = req.body
    const tokens = await authService.register(email, password)
    return successResponse(res, tokens, 201)
  } catch (err) {
    next(err)
  }
}

async function login(req, res, next) {
  try {
    const { email, password } = req.body
    const tokens = await authService.login(email, password)
    return successResponse(res, tokens)
  } catch (err) {
    next(err)
  }
}

async function refresh(req, res, next) {
  try {
    const { refreshToken } = req.body
    const tokens = await authService.refresh(refreshToken)
    return successResponse(res, tokens)
  } catch (err) {
    next(err)
  }
}

async function logout(req, res, next) {
  try {
    const { refreshToken } = req.body
    await authService.logout(refreshToken)
    return successResponse(res, { message: 'Выход выполнен' })
  } catch (err) {
    next(err)
  }
}

module.exports = { register, login, refresh, logout }
