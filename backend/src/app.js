require('dotenv').config({ path: '../.env.local' })

const express = require('express')
const cors    = require('cors')
const helmet  = require('helmet')
const morgan  = require('morgan')

const { connectDatabase }  = require('./config/database')
const errorMiddleware      = require('./middleware/error.middleware')

const authRouter      = require('./modules/auth/auth.router')
const carsRouter      = require('./modules/cars/cars.router')
const finesRouter     = require('./modules/fines/fines.router')
const remindersRouter = require('./modules/reminders/reminders.router')
const expensesRouter  = require('./modules/expenses/expenses.router')
const stoRouter       = require('./modules/sto/sto.router')
const paymentRouter   = require('./modules/payment/payment.router')

const app  = express()
const PORT = process.env.PORT || 3000

app.use(helmet({ contentSecurityPolicy: false }))
app.use(cors())
app.use(morgan('dev'))
app.use(express.json())
app.use(express.urlencoded({ extended: true }))
app.use(express.static(require('path').join(__dirname, '../public')))

app.use('/api/auth',      authRouter)
app.use('/api/cars',      carsRouter)
app.use('/api/fines',     finesRouter)
app.use('/api/reminders', remindersRouter)
app.use('/api/expenses',  expensesRouter)
app.use('/api/sto',       stoRouter)
app.use('/api/payment',   paymentRouter)

app.get('/health', (req, res) => {
  res.json({ status: 'ok', version: '1.0.0' })
})

app.use(errorMiddleware)

connectDatabase()

app.listen(PORT, () => {
  console.log(`Сервер запущен: http://localhost:${PORT}`)
  console.log(`Проверка:       http://localhost:${PORT}/health`)
})

module.exports = app
