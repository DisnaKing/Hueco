import { Link } from 'react-router'
import texts from '@/texts/es'

export default function NotFoundPage() {
  return (
    <section className="mx-auto max-w-2xl px-4 py-16 text-center">
      <h1 className="font-display text-3xl">{texts.noEncontrado.titulo}</h1>
      <p className="mt-4 text-muted">{texts.noEncontrado.texto}</p>
      <Link
        to="/"
        className="mt-8 inline-flex h-11 items-center rounded-full bg-primary px-6 font-medium text-primary-contrast"
      >
        {texts.noEncontrado.volver}
      </Link>
    </section>
  )
}
