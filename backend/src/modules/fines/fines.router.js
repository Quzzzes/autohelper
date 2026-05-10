const { Router }     = require('express')
const controller     = require('./fines.controller')
const authMiddleware = require('../../middleware/auth.middleware')

const router = Router()
router.use(authMiddleware)

router.get('/car/:carId',  controller.getFinesByCar)   // список штрафов по авто
router.post('/sync/:carId',controller.syncFines)        // синхронизировать с МВД
router.get('/:id',         controller.getFineById)      // детали штрафа

module.exports = router
