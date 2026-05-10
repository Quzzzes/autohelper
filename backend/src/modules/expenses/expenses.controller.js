const expensesService = require('./expenses.service')
const { successResponse } = require('../../utils/response')

async function getExpensesByCar(req, res, next) {
  try {
    const expenses = await expensesService.getExpensesByCar(req.params.carId)
    return successResponse(res, expenses)
  } catch (err) { next(err) }
}

async function addExpense(req, res, next) {
  try {
    const expense = await expensesService.addExpense(req.body)
    return successResponse(res, expense, 201)
  } catch (err) { next(err) }
}

async function deleteExpense(req, res, next) {
  try {
    await expensesService.deleteExpense(req.params.id)
    return successResponse(res, { message: 'Расход удалён' })
  } catch (err) { next(err) }
}

module.exports = { getExpensesByCar, addExpense, deleteExpense }
