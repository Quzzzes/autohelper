const paymentService = require('./payment.service')
const { successResponse } = require('../../utils/response')

async function createInvoice(req, res, next) {
  try {
    const { fineId } = req.body
    const invoice = await paymentService.createInvoiceForFine(fineId)
    return successResponse(res, invoice, 201)
  } catch (err) { next(err) }
}

async function getInvoiceStatus(req, res, next) {
  try {
    const status = await paymentService.getInvoiceStatus(req.params.invoiceNo)
    return successResponse(res, status)
  } catch (err) { next(err) }
}

async function handlePaymentWebhook(req, res, next) {
  try {
    await paymentService.processWebhook(req.body)
    return res.status(200).send('OK')  // Express-pay ожидает 200 OK
  } catch (err) { next(err) }
}

module.exports = { createInvoice, getInvoiceStatus, handlePaymentWebhook }
