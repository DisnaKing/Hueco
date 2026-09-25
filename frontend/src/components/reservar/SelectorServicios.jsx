import texts from '@/texts/es'
import ServicioCard from '@/components/reservar/ServicioCard'

function Tarjetas({ servicios, seleccion, onToggle }) {
  return (
    <ul className="grid gap-3 md:grid-cols-2">
      {servicios.map((servicio) => (
        <li key={servicio.id}>
          <ServicioCard servicio={servicio} elegido={seleccion.has(servicio.id)} onToggle={onToggle} />
        </li>
      ))}
    </ul>
  )
}

// Lista plana: sin categorías, solo las tarjetas; con categorías, un bloque con título por cada una.
// grupos: [{ categoria, ancla, servicios }]
export default function SelectorServicios({ grupos, seleccion, onToggle }) {
  if (grupos.length === 1 && grupos[0].categoria === null) {
    return <Tarjetas servicios={grupos[0].servicios} seleccion={seleccion} onToggle={onToggle} />
  }

  return (
    <div className="flex flex-col gap-8">
      {grupos.map((grupo) => (
        <section key={grupo.ancla} id={grupo.ancla} aria-labelledby={`${grupo.ancla}-titulo`} className="scroll-mt-32">
          <h2
            id={`${grupo.ancla}-titulo`}
            tabIndex={-1}
            className="mb-3 font-display text-xl font-semibold outline-none"
          >
            {grupo.categoria ?? texts.reservar.otros}
          </h2>
          <Tarjetas servicios={grupo.servicios} seleccion={seleccion} onToggle={onToggle} />
        </section>
      ))}
    </div>
  )
}
