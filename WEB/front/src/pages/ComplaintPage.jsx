import React from 'react';
import MainLayout from "../components/Layouts/MainLayout.jsx";
import ReclamationForm from "../components/Complaint/ReclamationForm.jsx";

function ComplaintPage(props) {
    return (
        <MainLayout>
            <ReclamationForm />
        </MainLayout>
    );
}

export default ComplaintPage;