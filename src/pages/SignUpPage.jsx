import React from 'react';
import MainLayout from "../components/Layouts/MainLayout.jsx";
import SignUpForm from "../Auth/SignUpForm.jsx";

function SignUpPage(props) {
    return (
        <MainLayout>
            <SignUpForm />
        </MainLayout>
    );
}

export default SignUpPage;