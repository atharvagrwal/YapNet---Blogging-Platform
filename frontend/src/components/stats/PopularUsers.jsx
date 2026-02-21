import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { statsAPI, followAPI } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import './PopularUsers.css';

const PopularUsers = ({ limit = 10, compact = true, onStatsUpdate }) => {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [userLimit, setUserLimit] = useState(limit);

  useEffect(() => {
    loadPopularUsers();
  }, [userLimit]);

  const loadPopularUsers = async () => {
    try {
      setLoading(true);
      const response = await statsAPI.getMostPopularUsers(userLimit);
      // Filter out the current user from the list
      const filteredUsers = response.data.filter(userItem => userItem.id !== user?.id);
      setUsers(filteredUsers);
    } catch (err) {
      setError('Failed to load popular users');
      console.error('Error loading popular users:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFollowToggle = async (userId) => {
    // Don't allow following yourself
    if (userId === user?.id) {
      console.error('Cannot follow yourself');
      return;
    }

    setUsers(users => users.map(u => u.id === userId ? { ...u, followLoading: true } : u));
    try {
      await followAPI.toggleFollow(userId);
      setUsers(users => users.map(u => u.id === userId ? { ...u, isFollowed: !u.isFollowed, followLoading: false } : u));
      
      // Refresh stats after follow/unfollow
      if (onStatsUpdate) {
        onStatsUpdate();
      }
      
      // Reload popular users to get updated follower counts
      loadPopularUsers();
    } catch (err) {
      setUsers(users => users.map(u => u.id === userId ? { ...u, followLoading: false } : u));
      console.error('Error toggling follow:', err);
    }
  };

  if (loading) {
    return <div className="popular-users-container"><div className="loading">Loading...</div></div>;
  }
  if (error) {
    return <div className="popular-users-container"><div className="error">{error}</div></div>;
  }

  return (
    <div className={`popular-users-container${compact ? ' compact' : ''}`}>
      {!compact && (
        <div className="stats-header">
          <h2>Popular Users</h2>
          <p>Most followed and engaging users on YapNet</p>
        </div>
      )}
      {!compact && (
        <div className="controls">
          <div className="limit-control">
            <label htmlFor="limit">Show top:</label>
            <select
              id="limit"
              value={userLimit}
              onChange={(e) => setUserLimit(Number(e.target.value))}
              className="limit-select"
            >
              <option value={5}>5 users</option>
              <option value={10}>10 users</option>
              <option value={20}>20 users</option>
              <option value={50}>50 users</option>
            </select>
          </div>
          <button className="btn btn-primary" onClick={loadPopularUsers}>
            <i className="fas fa-sync-alt"></i> Refresh
          </button>
        </div>
      )}
      {users.length === 0 ? (
        <div className="no-users">
          <i className="fas fa-users no-users-icon"></i>
          <h3>No popular users found</h3>
          <p>Start following users to see who's trending!</p>
        </div>
      ) : (
        <div className="users-leaderboard">
          {users.slice(0, compact ? 5 : userLimit).map((userItem, index) => (
            <div key={userItem.id} className={`user-card rank-${index + 1}`}
              style={compact ? {padding: '0.5rem', marginBottom: '0.5rem', minHeight: 'unset'} : {}}>
              <div className="user-info" style={compact ? {gap: '0.5rem'} : {}}>
                <div className="user-avatar" style={compact ? {width: 36, height: 36, fontSize: '1rem'} : {}}>
                  <i className="fas fa-user"></i>
                </div>
                <div className="user-details">
                  <span className="username" style={compact ? {fontSize: '1rem'} : {}}>{userItem.username}</span>
                  <span className="stat-label" style={compact ? {fontSize: '0.8rem'} : {}}>Followers: {userItem.followerCount ?? 0}</span>
                </div>
              </div>
              <div className="user-actions" style={compact ? {gap: '0.25rem'} : {}}>
                <Link to={`/user/${userItem.username}`} className="btn btn-secondary btn-xs">Profile</Link>
                {user && user.id !== userItem.id && (
                  <button
                    className={`btn btn-xs ${userItem.isFollowed ? 'btn-unfollow' : 'btn-follow'}`}
                    onClick={() => handleFollowToggle(userItem.id)}
                    disabled={userItem.followLoading}
                  >
                    {userItem.followLoading ? (userItem.isFollowed ? 'Unfollowing...' : 'Following...') : (userItem.isFollowed ? 'Unfollow' : 'Follow')}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
      {!compact && (
        <div className="leaderboard-info">
          <h3>About the Leaderboard</h3>
          <p>This leaderboard shows the most popular users on YapNet based on their follower count, engagement, and overall activity. Users are ranked by their total followers and the engagement they receive on their content.</p>
          <p>Follow your favorite users to stay updated with their latest posts and contribute to the vibrant YapNet community!</p>
        </div>
      )}
    </div>
  );
};

export default PopularUsers; 