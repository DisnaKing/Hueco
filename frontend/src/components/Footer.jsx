import { Link } from 'react-router'
import business from '@/business.config'
import texts from '@/texts/es'
import { navLinks } from '@/components/navLinks'

export default function Footer() {
  return (
    <footer className="border-t border-line bg-card">
      <div className="mx-auto flex max-w-5xl flex-col gap-4 px-4 py-8 text-sm text-muted md:flex-row md:items-center md:justify-between">
        <p>
          <span className="font-display font-semibold text-ink">{business.nombre}</span> · ©{' '}
          {new Date().getFullYear()} {texts.footer.derechos}
        </p>
        <nav aria-label={texts.nav.menu} className="flex flex-wrap gap-x-6 gap-y-2">
          {navLinks.map((link) => (
            <Link key={link.to} to={link.to} className="hover:text-primary">
              {link.label}
            </Link>
          ))}
        </nav>
      </div>
    </footer>
  )
}
