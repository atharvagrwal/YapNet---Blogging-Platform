import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import axios from 'axios';
import UserSearch from '../user/UserSearch';
import PostForm from '../posts/PostForm';
import './Dashboard.css';

const Dashboard = () => {
    const { user, token } = useAuth();
    const navigate = useNavigate();
    console.log('Dashboard rendered', { user, token });

    // Always render the dashboard
    return (
        <div className="dashboard">
            <div className="dashboard-content">
                <div className="welcome-section">
                    <h1>Welcome, {user.username || 'User'}!</h1>
                </div>

                <div className="dashboard-cards">
                    <div className="card">
                        <h3>Post Management</h3>
                        <div className="card-content">
                            <Link to="/posts" className="action-button" onClick={(e) => {
                                e.preventDefault();
                                console.log('View Posts clicked');
                                navigate('/posts');
                            }}>
                                <i className="fas fa-eye"></i> View Posts
                            </Link>
                            <Link to="/posts/new" className="action-button" onClick={(e) => {
                                e.preventDefault();
                                console.log('Create Post clicked');
                                navigate('/posts/new');
                            }}>
                                <i className="fas fa-plus"></i> Create Post
                            </Link>
                        </div>
                    </div>

                    <div className="card">
                        <h3>Find Users</h3>
                        <div className="card-content">
                            <UserSearch />
                        </div>
                    </div>

                    <div className="card">
                        <h3>Profile</h3>
                        <div className="card-content">
                            <Link to="/profile" className="action-button" onClick={(e) => {
                                e.preventDefault();
                                console.log('Profile clicked');
                                navigate('/profile');
                            }}>
                                <i className="fas fa-user"></i> Manage Profile
                            </Link>
                        </div>
                    </div>

                    {/* Placeholder card to fill right side if only one card is present */}
                    {/* <div className="card card-placeholder" style={{ visibility: 'hidden' }}></div> */}
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
