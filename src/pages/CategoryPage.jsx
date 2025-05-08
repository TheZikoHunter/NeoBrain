import React from 'react';
import MainLayout from "../components/Layouts/MainLayout.jsx";
import Categories from "../components/HeroSection/Categories.jsx";

function CategoryPage(props) {
    return (
        <MainLayout>
            <Categories />
        </MainLayout>
    );
}

export default CategoryPage;