import React, { useState, useEffect } from 'react';
import { statsAPI } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import './UserStats.css';

const UserStats = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadUserStats();
  }, []);

  // Periodic refresh every 10 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      loadUserStats();
    }, 10000);
    return () => clearInterval(interval);
  }, []);

  const loadUserStats = async () => {
    try {
      setLoading(true);
      const response = await statsAPI.getUserStats();
      setStats(response.data);
    } catch (err) {
      setError('Failed to load your statistics');
      console.error('Error loading user stats:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="user-stats-container">
        <div className="loading">Loading your statistics...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="user-stats-container">
        <div className="error">{error}</div>
      </div>
    );
  }

  if (!stats) {
    return (
      <div className="user-stats-container">
        <div className="error">No statistics available</div>
      </div>
    );
  }

  return (
    <div className="user-stats-container">
      <div className="stats-header">
        <h2>My Statistics</h2>
        <p>Your activity and engagement on YapNet</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">
            <i className="fas fa-edit"></i>
          </div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.postCount}</h3>
            <p className="stat-label">Posts</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <i className="fas fa-comment"></i>
          </div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.commentCount}</h3>
            <p className="stat-label">Comments</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <i className="fas fa-heart"></i>
          </div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.likeCount}</h3>
            <p className="stat-label">Likes Given</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <i className="fas fa-users"></i>
          </div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.followerCount}</h3>
            <p className="stat-label">Followers</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <i className="fas fa-user-plus"></i>
          </div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.followingCount}</h3>
            <p className="stat-label">Following</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <i className="fas fa-star"></i>
          </div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.totalLikes}</h3>
            <p className="stat-label">Total Likes Received</p>
          </div>
        </div>
      </div>

      <div className="stats-details">
        <div className="detail-section">
          <h3>Engagement Metrics</h3>
          <div className="metrics-grid">
            <div className="metric-item">
              <span className="metric-label">Average Likes per Post:</span>
              <span className="metric-value">{stats.avgLikesPerPost.toFixed(2)}</span>
            </div>
            <div className="metric-item">
              <span className="metric-label">Average Comments per Post:</span>
              <span className="metric-value">{stats.avgCommentsPerPost.toFixed(2)}</span>
            </div>
            <div className="metric-item">
              <span className="metric-label">Total Comments Received:</span>
              <span className="metric-value">{stats.totalComments}</span>
            </div>
          </div>
        </div>

        <div className="detail-section">
          <h3>Activity Summary</h3>
          <div className="activity-summary">
            <p>
              You have been active on YapNet with <strong>{stats.postCount}</strong> posts, 
              <strong> {stats.commentCount}</strong> comments, and <strong>{stats.likeCount}</strong> likes given.
            </p>
            <p>
              Your content has received <strong>{stats.totalLikes}</strong> total likes and 
              <strong> {stats.totalComments}</strong> total comments from the community.
            </p>
            <p>
              You have <strong>{stats.followerCount}</strong> followers and are following 
              <strong> {stats.followingCount}</strong> other users.
            </p>
          </div>
        </div>
      </div>

      <div className="stats-actions">
        <button 
          className="btn btn-primary"
          onClick={loadUserStats}
        >
          <i className="fas fa-sync-alt"></i>
          Refresh Statistics
        </button>
      </div>
    </div>
  );
};

export default UserStats; 