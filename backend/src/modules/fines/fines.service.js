const { findAll, findOne, insert, update } = require('../../config/database')

function getFinesByCar(userId, carId) {
  return findAll('fines', f => f.car_id === carId)
    .sort((a, b) => new Date(b.fine_date) - new Date(a.fine_date))
}

function getFineById(userId, fineId) {
  const fine = findOne('fines', f => f.id === fineId)
  if (!fine) {
    const err = new Error('Штраф не найден')
    err.status = 404; err.code = 'NOT_FOUND'; throw err
  }
  return fine
}

function saveFines(carId, finesFromMvd) {
  for (const fine of finesFromMvd) {
    const exists = findOne('fines', f => f.resolution_no === fine.resolutionNo)
    if (!exists) insert('fines', { car_id: carId, ...fine, status: 'unpaid' })
  }
}

module.exports = { getFinesByCar, getFineById, saveFines }
