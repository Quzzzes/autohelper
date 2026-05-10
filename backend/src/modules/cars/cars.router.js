const { Router }    = require('express')
const controller    = require('./cars.controller')
const authMiddleware= require('../../middleware/auth.middleware')

const router = Router()

// Все маршруты требуют авторизации
router.use(authMiddleware)

router.get('/',        controller.getCars)      // список авто пользователя
router.post('/',       controller.addCar)       // добавить авто
router.get('/:id',     controller.getCarById)   // карточка авто
router.put('/:id',     controller.updateCar)    // обновить авто
router.delete('/:id',  controller.deleteCar)    // удалить авто

module.exports = router
