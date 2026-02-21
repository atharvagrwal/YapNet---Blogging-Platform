import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../../contexts/AuthContext';
import PostItem from './PostItem';
import './PostList.css';

const PostList = () => {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { token } = useAuth();

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                setLoading(true);
                setError(null);
                const response = await axios.get('/api/posts', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setPosts(response.data);
            } catch (err) {
                setError('Failed to load posts.');
            } finally {
                setLoading(false);
            }
        };
        if (token) {
            fetchPosts();
        }
    }, [token]);

    if (loading) {
        return <div className="post-list-page"><div className="post-list-header"><h2>All Posts</h2></div><div className="post-list-content"><p>Loading...</p></div></div>;
    }
    if (error) {
        return <div className="post-list-page"><div className="post-list-header"><h2>All Posts</h2></div><div className="post-list-content"><p>{error}</p></div></div>;
    }
    return (
        <div className="post-list-page">
            <div className="post-list-header">
                <h2>All Posts</h2>
            </div>
            <div className="post-list-content">
                {posts.length === 0 ? (
                    <p>No posts found.</p>
                ) : (
                    posts.map(post => (
                        <div className="post-list-item" key={post.id}>
                            <h3 className="post-title">{post.title || post.content?.split("\n")[0]}</h3>
                            <div className="post-content">{post.content}</div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default PostList;
