const { Router }     = require('express')
const controller     = require('./reminders.controller')
const authMiddleware = require('../../middleware/auth.middleware')

const router = Router()
router.use(authMiddleware)

router.get('/car/:carId', controller.getRemindersByCar)
router.post('/',          controller.createReminder)
router.put('/:id',        controller.updateReminder)
router.delete('/:id',     controller.deleteReminder)

module.exports = router
