// Ids de ?servicios=1,3: descarta lo que no sea un entero positivo y los repetidos
export function parseIds(valor) {
  const ids = []
  for (const parte of (valor ?? '').split(',')) {
    const texto = parte.trim()
    if (!/^\d+$/.test(texto)) continue
    const id = Number(texto)
    if (id > 0 && !ids.includes(id)) ids.push(id)
  }
  return ids
}

// Servicios activos elegidos, en el orden de la carta. Los ids que no existen o no están activos se ignoran.
export function serviciosElegidos(ids, servicios) {
  const elegidos = new Set(ids)
  return servicios.filter((s) => elegidos.has(s.id))
}

// Id de ancla para una categoría: "Tratamientos" → "cat-tratamientos"; sin categoría → "cat-otros"
export function anclaCategoria(categoria) {
  const slug = (categoria ?? 'otros')
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '')
  return `cat-${slug || 'otros'}`
}
