import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from "@/contexts/AuthContext";
import { useNavigate } from 'react-router-dom';
import './Post.css';

const PostForm = () => {
    const [content, setContent] = useState('');
    const [title, setTitle] = useState("");
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { token } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!title.trim()) {
            setError("Title is required");
            return;
        }
        if (title.length > 120) {
            setError("Title cannot exceed 120 characters");
            return;
        }
        if (!content.trim()) {
            setError("Content is required");
            return;
        }
        setError("");
        setLoading(true);
        try {
            console.log('Creating post with content:', content);
            console.log('Using token:', token);
            
            const response = await axios.post(
                '/api/posts',
                { title, content },
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            
            console.log('Post creation response:', response.data);
            
            if (response.data) {
                setTitle("");
                setContent("");
                navigate('/posts');
            } else {
                throw new Error('No response data received');
            }
        } catch (error) {
            console.error('Error creating post:', error);
            console.error('Error details:', {
                status: error.response?.status,
                statusText: error.response?.statusText,
                data: error.response?.data,
                message: error.message
            });
            
            let errorMessage = 'Failed to create post';
            if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.response?.data?.errorMessage) {
                errorMessage = error.response.data.errorMessage;
            } else if (error.message) {
                errorMessage = error.message;
            }
            
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="post-form-page">
            <div className="post-form-header">
                <h2>Create New Post</h2>
            </div>
            <div className="post-form-content">
                <form onSubmit={handleSubmit} className="post-form">
                    <div className="form-group">
                        <label htmlFor="title">Title</label>
                        <input
                            id="title"
                            type="text"
                            className="form-control"
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            maxLength={120}
                            placeholder="Enter a catchy title (max 120 chars)"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <textarea
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            placeholder="What's on your mind?"
                            className="post-textarea"
                            maxLength="500"
                            disabled={loading}
                        />
                        <div className="form-footer">
                            <span className="char-count">{500 - content.length} characters remaining</span>
                            <button type="submit" className="post-button" disabled={loading}>
                                {loading ? 'Creating...' : 'Create Post'}
                            </button>
                        </div>
                    </div>
                    {error && <div className="error-message">{error}</div>}
                </form>
            </div>
        </div>
    );
};

export default PostForm;
