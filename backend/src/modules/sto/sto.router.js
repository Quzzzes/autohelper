const { Router }     = require('express')
const controller     = require('./sto.controller')
const authMiddleware = require('../../middleware/auth.middleware')

const router = Router()

router.get('/',        controller.getStoList)   // список СТО (без авторизации)
router.get('/:id',     controller.getStoById)   // карточка СТО

router.use(authMiddleware)
router.post('/:id/reviews', controller.addReview)  // отзыв — только авторизованным

module.exports = router
