import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { postsAPI } from '../../services/api';
import PostItem from './PostItem';
import './MyPosts.css';

const MyPosts = () => {
  const { user } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadMyPosts();
  }, []);

  const loadMyPosts = async () => {
    try {
      setLoading(true);
      const response = await postsAPI.getMyPosts();
      setPosts(response.data);
    } catch (err) {
      setError('Failed to load your posts');
      console.error('Error loading my posts:', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePostDeleted = (postId) => {
    setPosts(posts.filter(post => post.id !== postId));
  };

  const handlePostUpdated = (updatedPost) => {
    setPosts(posts.map(post => 
      post.id === updatedPost.id ? updatedPost : post
    ));
  };

  const handleLikeToggle = (postId, isLiked) => {
    setPosts(posts.map(post => 
      post.id === postId 
        ? { 
            ...post, 
            likedByCurrentUser: isLiked,
            likes: isLiked ? post.likes + 1 : post.likes - 1
          }
        : post
    ));
  };

  if (loading) {
    return (
      <div className="my-posts-container">
        <div className="loading">Loading your posts...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="my-posts-container">
        <div className="error">{error}</div>
      </div>
    );
  }

  return (
    <div className="my-posts-container">
      <div className="my-posts-header">
        <h2>My Posts</h2>
        <Link to="/posts/new" className="btn btn-primary">
          <i className="fas fa-plus"></i>
          Create New Post
        </Link>
      </div>

      {posts.length === 0 ? (
        <div className="no-posts">
          <div className="no-posts-content">
            <i className="fas fa-edit no-posts-icon"></i>
            <h3>No posts yet</h3>
            <p>Start sharing your thoughts with the world!</p>
            <Link to="/posts/new" className="btn btn-primary">
              Create Your First Post
            </Link>
          </div>
        </div>
      ) : (
        <div className="posts-grid">
          {posts.map((post) => (
            <PostItem
              key={post.id}
              post={post}
              onDelete={handlePostDeleted}
              onUpdate={handlePostUpdated}
              onLikeToggle={handleLikeToggle}
              showActions={true}
            />
          ))}
        </div>
      )}

      {posts.length > 0 && (
        <div className="posts-summary">
          <p>You have {posts.length} post{posts.length !== 1 ? 's' : ''}</p>
        </div>
      )}
    </div>
  );
};

export default MyPosts; 