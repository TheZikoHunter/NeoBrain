// authUtils.js - Frontend utility functions for authentication

const API_BASE_URL = 'http://localhost:8080';

// Login function
export const loginUser = async (email, password, rememberMe = false) => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include', // Important for cookies
            body: JSON.stringify({
                email,
                password,
                rememberMe
            }),
        });

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else {
            return { success: false, error: data.message || 'Login failed' };
        }
    } catch (error) {
        return { success: false, error: 'Network error. Please try again.' };
    }
};

// Logout function
export const logoutUser = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/logout`, {
            method: 'POST',
            credentials: 'include',
        });

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else {
            return { success: false, error: data.message || 'Logout failed' };
        }
    } catch (error) {
        return { success: false, error: 'Network error. Please try again.' };
    }
};

// Get current user function
export const getCurrentUser = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
            method: 'GET',
            credentials: 'include',
        });

        const data = await response.json();

        if (response.ok) {
            return { success: true, data };
        } else {
            return { success: false, error: data.message || 'Failed to get user info' };
        }
    } catch (error) {
        return { success: false, error: 'Network error. Please try again.' };
    }
};

// Check if user is authenticated
export const isAuthenticated = async () => {
    const result = await getCurrentUser();
    return result.success;
};