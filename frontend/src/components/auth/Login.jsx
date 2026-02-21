import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import './Login.css';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        console.log('Login attempt with:', { username });
        
        try {
            const response = await login({ username, password });
            console.log('Login response:', response);
            
            if (response && response.token) {
                console.log('Login successful, token received');
                
                // Get redirect path from localStorage or default to dashboard
                const redirectPath = localStorage.getItem('redirectPath') || '/dashboard';
                localStorage.removeItem('redirectPath');
                console.log('Redirecting to:', redirectPath);
                
                // Wait a moment to ensure AuthContext has time to update
                setTimeout(() => {
                    navigate(redirectPath, { replace: true });
                }, 100);
            } else {
                console.error('Invalid login response:', response);
                setError('Login failed. Please check your credentials.');
            }
        } catch (err) {
            console.error('Login error:', {
                status: err.response?.status,
                statusText: err.response?.statusText,
                data: err.response?.data,
                message: err.message
            });
            
            let errorMessage = 'Login failed';
            if (err.response?.data?.message) {
                errorMessage = err.response.data.message;
            } else if (err.response?.data?.errorMessage) {
                errorMessage = err.response.data.errorMessage;
            } else if (err.response?.status === 401) {
                errorMessage = 'Invalid username or password';
            } else if (err.response?.status === 400) {
                errorMessage = 'Invalid request';
            } else if (err.message) {
                errorMessage = err.message;
            }
            
            setError(errorMessage);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <h1 className="auth-title">Login</h1>
                {error && <div className="error-message">{error}</div>}
                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            className="form-input"
                            placeholder="Enter your username"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="form-input"
                            placeholder="Enter your password"
                        />
                    </div>
                    <button type="submit" className="auth-button">Login</button>
                    <div className="auth-footer">
                        <p>If you don't have an account, <a href="/register">register here</a></p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;
