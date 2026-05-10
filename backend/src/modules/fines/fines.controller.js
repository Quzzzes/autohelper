const finesService = require('./fines.service')
const { successResponse } = require('../../utils/response')

async function getFinesByCar(req, res, next) {
  try {
    const fines = await finesService.getFinesByCar(req.userId, req.params.carId)
    return successResponse(res, fines)
  } catch (err) { next(err) }
}

async function syncFines(req, res, next) {
  try {
    // Получить свежие штрафы с МВД и сохранить в БД
    const fines = await finesService.syncFinesFromMvd(req.userId, req.params.carId)
    return successResponse(res, fines)
  } catch (err) { next(err) }
}

async function getFineById(req, res, next) {
  try {
    const fine = await finesService.getFineById(req.userId, req.params.id)
    return successResponse(res, fine)
  } catch (err) { next(err) }
}

module.exports = { getFinesByCar, syncFines, getFineById }
