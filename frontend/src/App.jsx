import { Route, Routes } from 'react-router'
import Layout from '@/components/Layout'
import HomePage from '@/pages/HomePage'
import ServiciosPage from '@/pages/reservar/ServiciosPage'
import HorarioPage from '@/pages/reservar/HorarioPage'
import DatosPage from '@/pages/reservar/DatosPage'
import LoginPage from '@/pages/LoginPage'
import AgendaPage from '@/pages/AgendaPage'
import NotFoundPage from '@/pages/NotFoundPage'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="reservar" element={<ServiciosPage />} />
        <Route path="reservar/horario" element={<HorarioPage />} />
        <Route path="reservar/datos" element={<DatosPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
      {/* Fuera del Layout: la agenda es del comercio y no lleva la navegación de la web pública */}
      <Route path="agenda" element={<AgendaPage />} />
    </Routes>
  )
}

export default App
