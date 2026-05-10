const { Router } = require('express')
const { body }   = require('express-validator')
const controller = require('./auth.controller')

const router = Router()

// POST /api/auth/register — регистрация нового пользователя
router.post('/register', [
  body('email').isEmail().withMessage('Некорректный email'),
  body('password').isLength({ min: 6 }).withMessage('Пароль минимум 6 символов'),
], controller.register)

// POST /api/auth/login — вход, возвращает access + refresh токены
router.post('/login', [
  body('email').isEmail(),
  body('password').notEmpty(),
], controller.login)

// POST /api/auth/refresh — обновить access токен по refresh токену
router.post('/refresh', controller.refresh)

// POST /api/auth/logout — инвалидировать refresh токен
router.post('/logout', controller.logout)

module.exports = router
