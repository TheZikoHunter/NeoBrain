import React from 'react';
import MainLayout from "../components/Layouts/MainLayout.jsx";
import LoginForm from "../Auth/LoginForm.jsx";

function LoginPage(props) {
    return (
        <MainLayout>
            <LoginForm />
        </MainLayout>
    );
}

export default LoginPage;