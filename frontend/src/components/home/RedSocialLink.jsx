import texts from '@/texts/es'
import { redIconos } from '@/components/home/redIconos'

const base =
  'inline-flex h-11 items-center justify-center rounded-full border border-line text-ink transition-colors hover:border-primary hover:text-primary'

// Logo de la red con su nombre como texto accesible; si no hay logo para el tipo, se muestra el nombre
export default function RedSocialLink({ red }) {
  const nombre = texts.contacto.redNombres[red.tipo] ?? red.tipo
  const icono = redIconos[red.tipo]

  if (!icono) {
    return (
      <a href={red.url} target="_blank" rel="noopener noreferrer" className={`${base} px-4 text-sm font-medium`}>
        {nombre}
      </a>
    )
  }

  return (
    <a
      href={red.url}
      target="_blank"
      rel="noopener noreferrer"
      aria-label={nombre}
      title={nombre}
      className={`${base} w-11`}
    >
      <svg viewBox="0 0 24 24" className="size-5 fill-current" aria-hidden="true" focusable="false">
        <path d={icono} />
      </svg>
    </a>
  )
}
