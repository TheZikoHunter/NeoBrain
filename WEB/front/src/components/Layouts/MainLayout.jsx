import Navigation from "../Navigation/Navigation.jsx";
import Footer from "../HeroSection/Footer.jsx";


export default function MainLayout({ children }) {
    return (
        <>
            <Navigation />
            {children}
            <Footer />
        </>
    )
}