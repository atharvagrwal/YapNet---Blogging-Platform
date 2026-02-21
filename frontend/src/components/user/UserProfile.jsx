import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from "@/contexts/AuthContext";
import './UserProfile.css';
import { useParams } from 'react-router-dom';

const UserProfile = () => {
    const { user, token } = useAuth();
    const { username } = useParams();
    const [profile, setProfile] = useState({
        fullName: '',
        biography: '',
        profilePictureUrl: '',
        username: '',
        email: ''
    });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        if (username) {
            fetchOtherUserProfile(username);
        } else {
            fetchProfile();
        }
    }, [username]);

    const fetchProfile = async () => {
        try {
            const response = await axios.get('/api/users/profile', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setProfile({
                fullName: response.data.fullName || '',
                biography: response.data.biography || '',
                profilePictureUrl: response.data.profilePictureUrl || '',
                username: response.data.username || '',
                email: response.data.email || ''
            });
        } catch (error) {
            console.error('Error fetching profile:', error);
            setError('Failed to load profile');
        } finally {
            setLoading(false);
        }
    };

    const fetchOtherUserProfile = async (username) => {
        try {
            const response = await axios.get(`/api/users/username/${username}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setProfile({
                fullName: response.data.fullName || '',
                biography: response.data.biography || '',
                profilePictureUrl: response.data.profilePictureUrl || '',
                username: response.data.username || '',
                email: response.data.email || ''
            });
        } catch (error) {
            console.error('Error fetching user profile:', error);
            setError('Failed to load user profile');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setError('');
        setSuccess('');

        try {
            const response = await axios.put('/api/users/profile', profile, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            setSuccess('Profile updated successfully!');
            setProfile({
                fullName: response.data.fullName || '',
                biography: response.data.biography || '',
                profilePictureUrl: response.data.profilePictureUrl || ''
            });
        } catch (error) {
            console.error('Error updating profile:', error);
            setError('Failed to update profile');
        } finally {
            setSaving(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfile(prev => ({
            ...prev,
            [name]: value
        }));
    };

    if (loading) {
        return (
            <div className="profile-page">
                <div className="profile-header">
                    <h2>User Profile</h2>
                </div>
                <div className="profile-content">
                    <p>Loading...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-page">
            <div className="profile-header">
                <h2>User Profile</h2>
            </div>
            <div className="profile-content">
                <div className="profile-info">
                    <p className="username">Username: {profile.username || user?.username}</p>
                    <p className="email">Email: {profile.email || user?.email}</p>
                    {profile.biography && (
                      <p className="biography">Bio: {profile.biography}</p>
                    )}
                </div>
                {/* Only show edit form if viewing own profile */}
                {!username && (
                <form onSubmit={handleSubmit} className="profile-form">
                    <div className="form-group">
                        <label htmlFor="fullName">Full Name:</label>
                        <input
                            type="text"
                            id="fullName"
                            name="fullName"
                            value={profile.fullName}
                            onChange={handleChange}
                            placeholder="Enter your full name"
                            maxLength="100"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="biography">Biography:</label>
                        <textarea
                            id="biography"
                            name="biography"
                            value={profile.biography}
                            onChange={handleChange}
                            placeholder="Tell us about yourself..."
                            maxLength="500"
                            rows="4"
                        />
                        <span className="char-count">{500 - profile.biography.length} characters remaining</span>
                    </div>

                    <div className="form-group">
                        <label htmlFor="profilePictureUrl">Profile Picture URL:</label>
                        <input
                            type="url"
                            id="profilePictureUrl"
                            name="profilePictureUrl"
                            value={profile.profilePictureUrl}
                            onChange={handleChange}
                            placeholder="https://example.com/image.jpg"
                            maxLength="255"
                        />
                    </div>

                    {profile.profilePictureUrl && (
                        <div className="profile-picture-preview">
                            <label>Profile Picture Preview:</label>
                            <img 
                                src={profile.profilePictureUrl} 
                                alt="Profile preview" 
                                onError={(e) => {
                                    e.target.style.display = 'none';
                                    e.target.nextSibling.style.display = 'block';
                                }}
                            />
                            <span style={{ display: 'none', color: '#dc3545' }}>
                                Invalid image URL
                            </span>
                        </div>
                    )}

                    {error && <div className="error-message">{error}</div>}
                    {success && <div className="success-message">{success}</div>}

                    <button 
                        type="submit" 
                        className="save-button"
                        disabled={saving}
                    >
                        {saving ? 'Saving...' : 'Save Profile'}
                    </button>
                </form>
                )}
            </div>
        </div>
    );
};

export default UserProfile; 