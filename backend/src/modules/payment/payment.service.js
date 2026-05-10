const { queryOne, run } = require('../../config/database')

// В локальном режиме — заглушка для Express-pay.by
// При деплое на сервер подключается реальный API
async function createInvoiceForFine(fineId) {
  const fine = queryOne('SELECT * FROM fines WHERE id = ?', [fineId])
  if (!fine) {
    const err = new Error('Штраф не найден')
    err.status = 404
    err.code   = 'NOT_FOUND'
    throw err
  }

  // Заглушка: генерируем тестовый номер счёта
  const invoiceNo = Math.floor(Math.random() * 100000)
  run('UPDATE fines SET erip_invoice_no = ? WHERE id = ?', [invoiceNo, fineId])

  return {
    invoiceNo,
    qrCode: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==',
    note: 'Тестовый режим — реальная оплата недоступна',
  }
}

async function getInvoiceStatus(invoiceNo) {
  return { invoiceNo, status: 1, isPaid: false, note: 'Тестовый режим' }
}

async function processWebhook(data) {
  return true
}

module.exports = { createInvoiceForFine, getInvoiceStatus, processWebhook }
