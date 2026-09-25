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
    reservarCita: 'Reservar cita',
    llamar: 'Llamar',
  },
  estadoHoy: {
    abierto: (hora) => `Abierto hoy hasta las ${hora}`,
    abreHoy: (hora) => `Hoy abrimos a las ${hora}`,
    cerrado: 'Cerrado',
    // dia es "lunes" o, si falta más de una semana, "5 de enero"
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
    continuar: 'Continuar',
  },
  errores: {
    servicios: 'No podemos mostrar los servicios ahora mismo',
    llamar: 'Llamar para reservar',
  },
  carta: {
    titulo: 'Servicios',
    ayuda: 'Toca un servicio para empezar tu reserva con él.',
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
    cierreUnDia: (fecha) => `Cerrado el ${fecha}`,
    cierreVariosDias: (desde, hasta) => `Cerrado del ${desde} al ${hasta}`,
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
    pasos: ['Servicios', 'Fecha y hora', 'Tus datos y confirmar'],
    paso: (n, total) => `Paso ${n} de ${total}`,
    tituloServicios: 'Elige tus servicios',
    ayudaServicios: 'Puedes elegir varios. Verás el tiempo y el precio total abajo.',
    categorias: 'Categorías',
    otros: 'Otros',
    tituloHorario: 'Elige fecha y hora',
    tusServicios: 'Tus servicios',
    total: 'Total',
    proximamente: 'Próximamente: elige fecha y hora',
    cambiarServicios: 'Cambiar servicios',
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
