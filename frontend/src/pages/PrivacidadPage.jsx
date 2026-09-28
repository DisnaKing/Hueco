import texts from '@/texts/es'

// Texto de plantilla: cada comercio lo adapta en texts/es.js (issue #34)
export default function PrivacidadPage() {
  const { titulo, secciones } = texts.privacidad
  return (
    <article className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="font-display text-3xl font-semibold">{titulo}</h1>
      {secciones.map((s) => (
        <section key={s.titulo} className="mt-8">
          <h2 className="text-lg font-semibold">{s.titulo}</h2>
          <p className="mt-2 leading-relaxed text-ink/85">{s.texto}</p>
        </section>
      ))}
    </article>
  )
}
