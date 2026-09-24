// Datos de cada comercio que se fijan al compilar.
// El contenido editable (eslogan, horario, testimonios…) viene de GET /api/negocio.
export default {
  nombre: 'Peluquería Ejemplo',
  // Se usa en "Llamar para reservar" aunque el backend no responda
  telefono: '+34 600 000 000',
  logo: '/logo.svg',
  fotosSobreNosotros: [
    { src: '/sobre-nosotros/1.jpg', alt: 'Interior de la peluquería con los tocadores', width: 800, height: 600 },
    { src: '/sobre-nosotros/2.jpg', alt: 'Detalle de tijeras y peines sobre el mostrador', width: 800, height: 600 },
  ],
}
