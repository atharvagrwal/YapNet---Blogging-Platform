import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { postsAPI, usersAPI } from '../../services/api';
import PostItem from './PostItem';
import './UserPosts.css';

const UserPosts = ({ userId }) => {
  const [posts, setPosts] = useState([]);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { token } = useAuth();

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await postsAPI.getUserPosts(userId);
        setPosts(response.data);
      } catch (error) {
        console.error('Error fetching user posts:', error);
      } finally {
        setLoading(false);
      }
    };
    if (token && userId) {
      fetchPosts();
    }
  }, [token, userId]);

  const fetchUserInfo = async () => {
    try {
      const response = await usersAPI.getUserByUsername(userId);
      setUser(response.data);
    } catch (err) {
      console.error('Error fetching user info:', err);
    }
  };

  const handlePostUpdate = (updatedPost) => {
    setPosts(posts.map(post => 
      post.id === updatedPost.id ? updatedPost : post
    ));
  };

  const handleDelete = (postId) => {
    setPosts(posts => posts.filter(post => post.id !== postId));
  };

  if (loading) {
    return (
      <div className="user-posts-container">
        <div className="loading">Loading user posts...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="user-posts-container">
        <div className="error">Error: {error}</div>
      </div>
    );
  }

  return (
    <div className="user-posts-container">
      <div className="user-posts-header">
        {user ? (
          <>
            <h2>{user.username}'s Posts</h2>
            <p>Posts by {user.username}</p>
          </>
        ) : (
          <>
            <h2>User Posts</h2>
            <p>Posts from this user</p>
          </>
        )}
      </div>
      
      <div className="user-posts-list">
        {posts.length === 0 ? (
          <div className="no-posts">
            <p>No posts found for this user.</p>
          </div>
        ) : (
          posts.map(post => (
            <PostItem
              key={post.id}
              post={post}
              onUpdate={handlePostUpdate}
              onDelete={handleDelete}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default UserPosts; 