import texts from '@/texts/es'

// Provisional: destino del avatar hasta que exista el login
export default function LoginPage() {
  return (
    <section className="mx-auto max-w-2xl px-4 py-10">
      <h1 className="font-display text-3xl">{texts.login.titulo}</h1>
      <p className="mt-4 text-muted">{texts.login.proximamente}</p>
    </section>
  )
}
