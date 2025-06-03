import React from 'react';
import MainLayout from "../components/Layouts/MainLayout.jsx";
import Products from "../components/HeroSection/products.jsx";

function ProductsPage(props) {
    return (
        <MainLayout>
            <Products />
        </MainLayout>
    );
}

export default ProductsPage;