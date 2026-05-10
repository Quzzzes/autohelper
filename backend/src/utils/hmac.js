const crypto = require('crypto')

// Подпись запросов к Express-pay.by алгоритмом HMAC-SHA1
// Выполняется ТОЛЬКО на сервере — секрет никогда не передаётся в приложение
function generateSignature(params, secretWord) {
  const sortedValues = Object.keys(params)
    .sort()
    .map(key => params[key])
    .join(';')

  return crypto
    .createHmac('sha1', secretWord)
    .update(sortedValues)
    .digest('hex')
    .toUpperCase()
}

module.exports = { generateSignature }
