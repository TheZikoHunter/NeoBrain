// useAuth.js - Custom React hook for authentication
import { useState, useEffect, createContext, useContext } from 'react';

// Create Auth Context
const AuthContext = createContext();

// Auth Provider Component
export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const API_BASE_URL = 'http://localhost:8080';

    // Check authentication status on mount
    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
                method: 'GET',
                credentials: 'include',
            });

            if (response.ok) {
                const userData = await response.json();
                setUser(userData);
                setIsAuthenticated(true);
            } else {
                setUser(null);
                setIsAuthenticated(false);
            }
        } catch (error) {
            console.error('Auth check failed:', error);
            setUser(null);
            setIsAuthenticated(false);
        } finally {
            setLoading(false);
        }
    };

    const login = async (email, password, rememberMe = false) => {
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    email,
                    password,
                    rememberMe
                }),
            });

            const data = await response.json();

            if (response.ok) {
                setUser(data);
                setIsAuthenticated(true);
                return { success: true, data };
            } else {
                return { success: false, error: data.message || 'Login failed' };
            }
        } catch (error) {
            return { success: false, error: 'Network error. Please try again.' };
        }
    };

    const logout = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/logout`, {
                method: 'POST',
                credentials: 'include',
            });

            setUser(null);
            setIsAuthenticated(false);

            return { success: true };
        } catch (error) {
            return { success: false, error: 'Logout failed' };
        }
    };

    const value = {
        user,
        isAuthenticated,
        loading,
        login,
        logout,
        checkAuthStatus
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

// Custom hook to use auth context
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

// HOC for protected routes
export const withAuth = (WrappedComponent) => {
    return (props) => {
        const { isAuthenticated, loading } = useAuth();

        if (loading) {
            return (
                <div className="min-h-screen flex items-center justify-center">
                    <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-gray-900"></div>
                </div>
            );
        }

        if (!isAuthenticated) {
            window.location.href = '/login';
            return null;
        }

        return <WrappedComponent {...props} />;
    };
};