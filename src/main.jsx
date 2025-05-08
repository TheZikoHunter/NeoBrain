import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from "react-router-dom"
import './index.css'
import App from './App.jsx'
import MainPage from "./pages/MainPage.jsx";
import JoinTeamPage from "./pages/JoinTeamPage.jsx";
import ComplaintPage from "./pages/ComplaintPage.jsx";
import ProductsPage from "./pages/ProductsPage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import SignUpPage from "./pages/SignUpPage.jsx";

/*createRoot(document.getElementById('root')).render(
  <StrictMode>
   <MainLayout>
     <BannerSection></BannerSection>
     <Category></Category>
     <NewArrivals></NewArrivals>
   </MainLayout>
  </StrictMode>,
)*/
createRoot(document.getElementById('root')).render(
    <StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/join-team" element={<JoinTeamPage />} />
                <Route path="/reclamation" element={<ComplaintPage />} />
                <Route path="/products" element={<ProductsPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignUpPage />} />
            </Routes>
        </BrowserRouter>
    </StrictMode>
)
