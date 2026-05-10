const { Router }     = require('express')
const controller     = require('./payment.controller')
const authMiddleware = require('../../middleware/auth.middleware')

const router = Router()
router.use(authMiddleware)

// POST /api/payment/invoice — выставить счёт на оплату штрафа
router.post('/invoice',           controller.createInvoice)

// GET /api/payment/invoice/:invoiceNo — проверить статус оплаты
router.get('/invoice/:invoiceNo', controller.getInvoiceStatus)

// POST /api/payment/webhook — webhook от Express-pay.by об успешной оплате
router.post('/webhook', controller.handlePaymentWebhook)

module.exports = router
