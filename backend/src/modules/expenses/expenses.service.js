const { findAll, insert, remove } = require('../../config/database')

const EXPENSE_CATEGORIES = ['fuel', 'repair', 'insurance', 'wash', 'parking', 'fine', 'techosmotr', 'other']

function getExpensesByCar(carId) {
  return findAll('expenses', e => e.car_id === carId)
    .sort((a, b) => new Date(b.expense_date) - new Date(a.expense_date))
}

function addExpense(data) {
  if (!EXPENSE_CATEGORIES.includes(data.category)) {
    const err = new Error(`Неверная категория. Доступные: ${EXPENSE_CATEGORIES.join(', ')}`)
    err.status = 400; err.code = 'INVALID_CATEGORY'; throw err
  }
  return insert('expenses', {
    expense_date: new Date().toISOString().slice(0, 10),
    ...data,
  })
}

function deleteExpense(expenseId) {
  remove('expenses', e => e.id === expenseId)
}

module.exports = { getExpensesByCar, addExpense, deleteExpense }
