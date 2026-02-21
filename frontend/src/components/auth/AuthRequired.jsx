import React, { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import axios from 'axios';
import Dashboard from '../dashboard/Dashboard';
import './AuthRequired.css';

const AuthRequired = ({ children }) => {
    const { user } = useAuth();

    // If we have user data, show the children component
    if (user && user.username) {
        return (
            <div className="auth-container">
                {children}
            </div>
        );
    }

    // If not authenticated, redirect to login
    return <Navigate to="/login" replace />;
};

export default AuthRequired;
