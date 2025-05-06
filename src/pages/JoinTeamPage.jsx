import React from 'react';
import MainLayout from "../components/Layouts/MainLayout.jsx";
import JoinTeamForm from "../components/JoinTeam/JoinTeamForm.jsx";

function JoinTeamPage(props) {
    return (
        <MainLayout>
            <JoinTeamForm />
        </MainLayout>
    );
}

export default JoinTeamPage;