const stoService = require('./sto.service')
const { successResponse } = require('../../utils/response')

async function getStoList(req, res, next) {
  try {
    const { city, service } = req.query
    const list = await stoService.getStoList({ city, service })
    return successResponse(res, list)
  } catch (err) { next(err) }
}

async function getStoById(req, res, next) {
  try {
    const sto = await stoService.getStoById(req.params.id)
    return successResponse(res, sto)
  } catch (err) { next(err) }
}

async function addReview(req, res, next) {
  try {
    const review = await stoService.addReview(req.userId, req.params.id, req.body)
    return successResponse(res, review, 201)
  } catch (err) { next(err) }
}

module.exports = { getStoList, getStoById, addReview }
