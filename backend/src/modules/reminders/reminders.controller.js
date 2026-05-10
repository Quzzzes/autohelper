const remindersService = require('./reminders.service')
const { successResponse } = require('../../utils/response')

async function getRemindersByCar(req, res, next) {
  try {
    const reminders = await remindersService.getRemindersByCar(req.params.carId)
    return successResponse(res, reminders)
  } catch (err) { next(err) }
}

async function createReminder(req, res, next) {
  try {
    const reminder = await remindersService.createReminder(req.body)
    return successResponse(res, reminder, 201)
  } catch (err) { next(err) }
}

async function updateReminder(req, res, next) {
  try {
    const reminder = await remindersService.updateReminder(req.params.id, req.body)
    return successResponse(res, reminder)
  } catch (err) { next(err) }
}

async function deleteReminder(req, res, next) {
  try {
    await remindersService.deleteReminder(req.params.id)
    return successResponse(res, { message: 'Напоминание удалено' })
  } catch (err) { next(err) }
}

module.exports = { getRemindersByCar, createReminder, updateReminder, deleteReminder }
