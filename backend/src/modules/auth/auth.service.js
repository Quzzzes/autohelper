const bcrypt = require('bcryptjs')
const jwt    = require('jsonwebtoken')
const { findOne, insert } = require('../../config/database')

const SALT_ROUNDS = 10
const JWT_SECRET  = process.env.JWT_SECRET || 'local_dev_secret'

async function register(email, password) {
  if (findOne('users', u => u.email === email)) {
    const err = new Error('Пользователь с таким email уже существует')
    err.status = 409; err.code = 'EMAIL_EXISTS'; throw err
  }
  const password_hash = await bcrypt.hash(password, SALT_ROUNDS)
  const user = insert('users', { email, password_hash, is_premium: false })
  return generateTokenPair(user.id)
}

async function login(email, password) {
  const user = findOne('users', u => u.email === email)
  if (!user) {
    const err = new Error('Неверный email или пароль')
    err.status = 401; err.code = 'INVALID_CREDENTIALS'; throw err
  }
  const ok = await bcrypt.compare(password, user.password_hash)
  if (!ok) {
    const err = new Error('Неверный email или пароль')
    err.status = 401; err.code = 'INVALID_CREDENTIALS'; throw err
  }
  return generateTokenPair(user.id)
}

function refresh(refreshToken) {
  try {
    const payload = jwt.verify(refreshToken, JWT_SECRET)
    return generateTokenPair(payload.userId)
  } catch {
    const err = new Error('Недействительный refresh токен')
    err.status = 401; err.code = 'TOKEN_INVALID'; throw err
  }
}

function logout() { return true }

function generateTokenPair(userId) {
  return {
    accessToken:  jwt.sign({ userId }, JWT_SECRET, { expiresIn: '15m' }),
    refreshToken: jwt.sign({ userId }, JWT_SECRET, { expiresIn: '30d' }),
  }
}

module.exports = { register, login, refresh, logout }
