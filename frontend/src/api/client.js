// Lanza un error si la respuesta no es 2xx, para que los hooks lo traten igual que un fallo de red
export async function getJson(path, { signal } = {}) {
  const response = await fetch(path, { signal, headers: { Accept: 'application/json' } })
  if (!response.ok) {
    throw new Error(`GET ${path} respondió ${response.status}`)
  }
  return response.json()
}
