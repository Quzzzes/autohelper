const { findAll, findOne, insert, update } = require('../../config/database')

function getStoList({ city, service }) {
  let list = findAll('sto')
  if (city)    list = list.filter(s => s.city === city)
  if (service) list = list.filter(s => s.services.includes(service))
  return list.sort((a, b) => b.is_premium - a.is_premium || b.rating - a.rating)
}

function getStoById(stoId) {
  const sto = findOne('sto', s => s.id === stoId)
  if (!sto) {
    const err = new Error('СТО не найдено')
    err.status = 404; err.code = 'NOT_FOUND'; throw err
  }
  return sto
}

function addReview(userId, stoId, data) {
  const { rating, comment } = data
  if (rating < 1 || rating > 5) {
    const err = new Error('Оценка должна быть от 1 до 5')
    err.status = 400; err.code = 'INVALID_RATING'; throw err
  }

  const review = insert('sto_reviews', { user_id: userId, sto_id: stoId, rating, comment })

  const reviews = findAll('sto_reviews', r => r.sto_id === stoId)
  const avgRating = reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length
  update('sto', s => s.id === stoId, { rating: +avgRating.toFixed(2), reviews_count: reviews.length })

  return review
}

module.exports = { getStoList, getStoById, addReview }
