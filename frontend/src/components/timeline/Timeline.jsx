import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '@/contexts/AuthContext';
import PostItem from '../posts/PostItem';
import './Timeline.css';

const Timeline = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { token } = useAuth();

  useEffect(() => {
    const fetchTimeline = async () => {
      setLoading(true);
      setError('');
      try {
        const response = await axios.get('/api/posts', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        console.log('Fetched posts:', response.data); // Debug log
        setPosts(response.data);
      } catch (err) {
        let errorMsg = 'Failed to load timeline.';
        if (err.response && err.response.data && typeof err.response.data === 'string') {
          errorMsg += ' ' + err.response.data;
        } else if (err.message) {
          errorMsg += ' ' + err.message;
        }
        setError(errorMsg);
      } finally {
        setLoading(false);
      }
    };
    if (token) fetchTimeline();
  }, [token]);

  const handleDelete = (postId) => {
    setPosts(posts => posts.filter(post => post.id !== postId));
  };

  const handlePostUpdate = (updatedPost) => {
    setPosts(posts => posts.map(post => post.id === updatedPost.id ? updatedPost : post));
  };

  return (
    <div className="timeline-page">
      <div className="timeline-header">
        <h2>Timeline</h2>
        <p>Latest posts from everyone</p>
      </div>
      
      <div className="timeline-posts">
        {loading ? (
          <div className="loading">Loading...</div>
        ) : error ? (
          <div className="error">{error}</div>
        ) : posts.length === 0 ? (
          <div className="no-posts">
            <p>No posts yet. Be the first to share something!</p>
          </div>
        ) : (
          posts.map(post => (
            <PostItem
              key={post.id}
              post={post}
              onDelete={handleDelete}
              onUpdate={handlePostUpdate}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default Timeline; 