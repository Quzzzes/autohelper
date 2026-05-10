const fs   = require('fs')
const path = require('path')
const crypto = require('crypto')

// Хранилище в памяти + автосохранение в JSON-файл
// Ничего устанавливать не нужно — работает на чистом Node.js

const DATA_FILE = path.join(__dirname, '../../data/db.json')

const db = {
  users:       [],
  cars:        [],
  reminders:   [],
  fines:       [],
  expenses:    [],
  sto:         [],
  sto_reviews: [],
}

function connectDatabase() {
  fs.mkdirSync(path.dirname(DATA_FILE), { recursive: true })

  if (fs.existsSync(DATA_FILE)) {
    const saved = JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'))
    Object.assign(db, saved)
    console.log('База данных загружена из файла')
  } else {
    seedTestData()
    save()
    console.log('База данных создана с тестовыми данными')
  }
}

// Сохранить текущее состояние в файл
function save() {
  fs.writeFileSync(DATA_FILE, JSON.stringify(db, null, 2), 'utf8')
}

function generateId() {
  return crypto.randomBytes(16).toString('hex')
}

function seedTestData() {
  db.sto = [
    { id: generateId(), name: 'СТО Автосервис Минск',  city: 'Минск', address: 'ул. Тимирязева, 65',        phone: '+375 17 234-56-78', services: ['oil','tires','to'],       is_premium: true,  rating: 4.8, reviews_count: 12 },
    { id: generateId(), name: 'Шиномонтаж Экспресс',   city: 'Минск', address: 'пр. Независимости, 120',    phone: '+375 29 111-22-33', services: ['tires'],                  is_premium: false, rating: 4.2, reviews_count: 5  },
    { id: generateId(), name: 'СТО Автомастер',         city: 'Минск', address: 'ул. Кальварийская, 17',     phone: '+375 44 555-66-77', services: ['body','engine','oil'],    is_premium: false, rating: 4.5, reviews_count: 8  },
  ]
}

// ─── Универсальные методы для работы с коллекциями ───────────

function findAll(table, filterFn = null) {
  const rows = db[table] || []
  return filterFn ? rows.filter(filterFn) : rows
}

function findOne(table, filterFn) {
  return (db[table] || []).find(filterFn) || null
}

function insert(table, data) {
  const row = { id: generateId(), created_at: new Date().toISOString(), ...data }
  db[table].push(row)
  save()
  return row
}

function update(table, filterFn, updateData) {
  const index = db[table].findIndex(filterFn)
  if (index === -1) return null
  db[table][index] = { ...db[table][index], ...updateData }
  save()
  return db[table][index]
}

function remove(table, filterFn) {
  const before = db[table].length
  db[table] = db[table].filter(row => !filterFn(row))
  if (db[table].length !== before) save()
}

module.exports = { connectDatabase, findAll, findOne, insert, update, remove }
