const { findAll, findOne, insert, update, remove } = require('../../config/database')

function getCarsByUser(userId) {
  return findAll('cars', c => c.user_id === userId)
}

function addCar(userId, carData) {
  const userCars = findAll('cars', c => c.user_id === userId)
  if (userCars.length >= 1) {
    const err = new Error('Бесплатный план — только 1 автомобиль. Оформите Premium.')
    err.status = 403; err.code = 'FREEMIUM_LIMIT'; throw err
  }
  return insert('cars', { user_id: userId, ...carData })
}

function getCarById(userId, carId) {
  const car = findOne('cars', c => c.id === carId && c.user_id === userId)
  if (!car) {
    const err = new Error('Автомобиль не найден')
    err.status = 404; err.code = 'NOT_FOUND'; throw err
  }
  return car
}

function updateCar(userId, carId, carData) {
  return update('cars', c => c.id === carId && c.user_id === userId, carData)
}

function deleteCar(userId, carId) {
  remove('cars', c => c.id === carId && c.user_id === userId)
}

module.exports = { getCarsByUser, addCar, getCarById, updateCar, deleteCar }
