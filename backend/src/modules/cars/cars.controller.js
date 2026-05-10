const carsService = require('./cars.service')
const { successResponse } = require('../../utils/response')

async function getCars(req, res, next) {
  try {
    const cars = await carsService.getCarsByUser(req.userId)
    return successResponse(res, cars)
  } catch (err) { next(err) }
}

async function addCar(req, res, next) {
  try {
    const car = await carsService.addCar(req.userId, req.body)
    return successResponse(res, car, 201)
  } catch (err) { next(err) }
}

async function getCarById(req, res, next) {
  try {
    const car = await carsService.getCarById(req.userId, req.params.id)
    return successResponse(res, car)
  } catch (err) { next(err) }
}

async function updateCar(req, res, next) {
  try {
    const car = await carsService.updateCar(req.userId, req.params.id, req.body)
    return successResponse(res, car)
  } catch (err) { next(err) }
}

async function deleteCar(req, res, next) {
  try {
    await carsService.deleteCar(req.userId, req.params.id)
    return successResponse(res, { message: 'Авто удалено' })
  } catch (err) { next(err) }
}

module.exports = { getCars, addCar, getCarById, updateCar, deleteCar }
