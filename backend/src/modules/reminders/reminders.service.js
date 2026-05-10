const { findAll, findOne, insert, update, remove } = require('../../config/database')

const REMINDER_TYPES = ['osgo', 'to', 'techosmotr', 'blue_card', 'oil', 'tires', 'vu']

function getRemindersByCar(carId) {
  return findAll('reminders', r => r.car_id === carId)
    .sort((a, b) => new Date(a.due_date) - new Date(b.due_date))
}

function createReminder(data) {
  if (!REMINDER_TYPES.includes(data.type)) {
    const err = new Error(`Неверный тип. Доступные: ${REMINDER_TYPES.join(', ')}`)
    err.status = 400; err.code = 'INVALID_TYPE'; throw err
  }
  return insert('reminders', data)
}

function updateReminder(reminderId, data) {
  return update('reminders', r => r.id === reminderId, data)
}

function deleteReminder(reminderId) {
  remove('reminders', r => r.id === reminderId)
}

module.exports = { getRemindersByCar, createReminder, updateReminder, deleteReminder }
