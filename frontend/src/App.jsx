import { Route, Routes } from 'react-router'
import Layout from '@/components/Layout'
import HomePage from '@/pages/HomePage'
import ServiciosPage from '@/pages/reservar/ServiciosPage'
import HorarioPage from '@/pages/reservar/HorarioPage'
import LoginPage from '@/pages/LoginPage'
import NotFoundPage from '@/pages/NotFoundPage'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="reservar" element={<ServiciosPage />} />
        <Route path="reservar/horario" element={<HorarioPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
