import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import './Login.css';

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { register } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        
        try {
            const response = await register({
                username,
                password,
                email
            });
            
            if (!response || !response.token) {
                throw new Error('Invalid response from server');
            }
            
            localStorage.setItem('token', response.token);
            localStorage.setItem('username', response.username);
            
            window.location.href = '/';
            return null;
        } catch (err) {
            console.error('Registration error details:', {
                status: err.response?.status,
                statusText: err.response?.statusText,
                data: err.response?.data,
                message: err.message,
                request: err.request?.url,
                config: err.config
            });
            
            let errorMessage = 'An error occurred during registration';
            if (err.response?.data?.message) {
                errorMessage = err.response.data.message;
            } else if (err.response?.statusText) {
                errorMessage = err.response.statusText;
            } else if (err.message) {
                errorMessage = err.message;
            } else {
                errorMessage = 'Unable to connect to server. Please check if the backend is running.';
            }
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <h1 className="auth-title">Register</h1>
                {error && (
                    <div className="error-message">
                        <p>{error}</p>
                    </div>
                )}
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
                            placeholder="Choose a username"
                            disabled={loading}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="form-input"
                            placeholder="Enter your email"
                            disabled={loading}
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
                            placeholder="Choose a password"
                            disabled={loading}
                        />
                    </div>
                    <button 
                        type="submit" 
                        className="auth-button"
                        disabled={loading}
                    >
                        {loading ? 'Registering...' : 'Register'}
                    </button>
                    <div className="auth-footer">
                        <p>If you already have an account, <a href="/login">login here</a></p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Register;
