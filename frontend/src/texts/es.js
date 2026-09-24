// Todos los textos fijos de la interfaz
export default {
  nav: {
    inicio: 'Ir al inicio',
    servicios: 'Servicios',
    sobreNosotros: 'Sobre nosotros',
    contacto: 'Contacto',
    reservar: 'Reservar',
    abrirMenu: 'Abrir menú',
    cerrarMenu: 'Cerrar menú',
    menu: 'Menú',
    iniciarSesion: 'Iniciar sesión',
  },
  hero: {
    esloganPorDefecto: 'Reserva tu cita en un momento',
    tituloServicios: 'Elige tus servicios',
    ayudaServicios: 'Puedes elegir varios. Verás el tiempo y el precio total abajo.',
    seleccionado: 'Elegido',
  },
  estadoHoy: {
    abierto: (hora) => `Abierto hoy hasta las ${hora}`,
    abreHoy: (hora) => `Hoy abrimos a las ${hora}`,
    cerrado: 'Cerrado',
    proximaApertura: (dia, hora) => `Cerrado · Abrimos el ${dia} a las ${hora}`,
  },
  dias: {
    MONDAY: 'lunes',
    TUESDAY: 'martes',
    WEDNESDAY: 'miércoles',
    THURSDAY: 'jueves',
    FRIDAY: 'viernes',
    SATURDAY: 'sábado',
    SUNDAY: 'domingo',
  },
  barra: {
    servicios: (n) => (n === 1 ? '1 servicio' : `${n} servicios`),
    verHorarios: 'Ver horarios',
  },
  errores: {
    servicios: 'No podemos mostrar los servicios ahora mismo',
    llamar: 'Llamar para reservar',
  },
  sobreNosotros: {
    titulo: 'Sobre nosotros',
  },
  opiniones: {
    titulo: 'Lo que dicen nuestros clientes',
  },
  contacto: {
    titulo: 'Contacto',
    direccion: 'Dirección',
    comoLlegar: 'Cómo llegar',
    telefono: 'Teléfono',
    email: 'Email',
    horario: 'Horario',
    cerrado: 'Cerrado',
    hoy: 'hoy',
    redes: 'Síguenos',
    redNombres: {
      INSTAGRAM: 'Instagram',
      FACEBOOK: 'Facebook',
      TIKTOK: 'TikTok',
      WHATSAPP: 'WhatsApp',
      X: 'X',
      YOUTUBE: 'YouTube',
    },
  },
  footer: {
    derechos: 'Todos los derechos reservados.',
  },
  reservar: {
    titulo: 'Tu reserva',
    proximamente: 'Próximamente: elige fecha y hora',
    sinServicios: 'No has elegido ningún servicio.',
    total: 'Total',
    elegirServicios: 'Elegir servicios',
  },
  login: {
    titulo: 'Iniciar sesión',
    proximamente: 'Próximamente podrás entrar para ver y gestionar tus citas.',
  },
  noEncontrado: {
    titulo: 'Página no encontrada',
    texto: 'La página que buscas no existe o se ha movido.',
    volver: 'Volver al inicio',
  },
}
