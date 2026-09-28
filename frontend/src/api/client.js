// Lanza un error si la respuesta no es 2xx, para que los hooks lo traten igual que un fallo de red
export async function getJson(path, { signal } = {}) {
  const response = await fetch(path, { signal, headers: { Accept: 'application/json' } })
  if (!response.ok) {
    const error = new Error(`GET ${path} respondió ${response.status}`)
    error.status = response.status
    throw error
  }
  return response.json()
}

// POST con JSON. No lanza en 4xx: quien llama decide qué hacer con cada status (400, 409, 429…)
export async function postJson(path, body) {
  const response = await fetch(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    body: JSON.stringify(body),
  })
  const texto = await response.text()
  let data = null
  try {
    data = texto ? JSON.parse(texto) : null
  } catch {
    data = null
  }
  return { ok: response.ok, status: response.status, data }
}
