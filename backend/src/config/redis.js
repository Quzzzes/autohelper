const { createClient } = require('redis')

let client

async function connectRedis() {
  client = createClient({ url: process.env.REDIS_URL })

  client.on('error', err => console.error('Redis ошибка:', err))

  await client.connect()
  console.log('Redis подключён')
}

// Получить значение из кэша
async function getCache(key) {
  const value = await client.get(key)
  return value ? JSON.parse(value) : null
}

// Сохранить значение в кэш с TTL в секундах
async function setCache(key, value, ttlSeconds) {
  await client.setEx(key, ttlSeconds, JSON.stringify(value))
}

// Удалить значение из кэша
async function deleteCache(key) {
  await client.del(key)
}

module.exports = { connectRedis, getCache, setCache, deleteCache }
