const { Router }     = require('express')
const controller     = require('./expenses.controller')
const authMiddleware = require('../../middleware/auth.middleware')

const router = Router()
router.use(authMiddleware)

router.get('/car/:carId',  controller.getExpensesByCar)
router.post('/',           controller.addExpense)
router.delete('/:id',      controller.deleteExpense)

module.exports = router
