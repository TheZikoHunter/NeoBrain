import React from 'react';
import MainLayout from "../components/Layouts/MainLayout.jsx";
import BannerSection from "../components/HeroSection/BannerSection.jsx";
import Category from "../components/HeroSection/Category.jsx";
import NewArrivals from "../components/HeroSection/NewArrivals.jsx";

function MainPage(props) {
    return (
        <MainLayout>
            <BannerSection />
            <Category/>
            <NewArrivals/>
        </MainLayout>
    );
}

export default MainPage;