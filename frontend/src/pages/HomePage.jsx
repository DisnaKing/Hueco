import business from '@/business.config'

export default function HomePage() {
  return (
    <section id="servicios" className="mx-auto max-w-5xl scroll-mt-16 px-4 py-10">
      <h1 className="font-display text-3xl">{business.nombre}</h1>
    </section>
  )
}
