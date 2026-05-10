const jwt = require('jsonwebtoken')

const JWT_SECRET = process.env.JWT_SECRET || 'local_dev_secret'

function authMiddleware(req, res, next) {
  const authHeader = req.headers.authorization

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({
      success: false,
      error: { code: 'UNAUTHORIZED', message: 'Токен не предоставлен' },
    })
  }

  const token = authHeader.split(' ')[1]

  try {
    const payload = jwt.verify(token, JWT_SECRET)
    req.userId = payload.userId
    next()
  } catch {
    return res.status(401).json({
      success: false,
      error: { code: 'TOKEN_INVALID', message: 'Недействительный или истёкший токен' },
    })
  }
}

module.exports = authMiddleware
