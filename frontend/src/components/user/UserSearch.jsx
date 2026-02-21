import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { usersAPI, followAPI } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import './UserSearch.css';

const UserSearch = () => {
  const { user } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');
  const [searchType, setSearchType] = useState('all');
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [followingStates, setFollowingStates] = useState({});

  useEffect(() => {
    if (searchQuery.trim()) {
      performSearch();
    } else {
      setUsers([]);
      setCurrentPage(0);
      setTotalPages(0);
      setTotalElements(0);
    }
  }, [searchQuery, searchType, currentPage]);

  const performSearch = async () => {
    try {
      setLoading(true);
      setError(null);

      let response;
      if (searchType === 'all') {
        response = await usersAPI.searchUsers(searchQuery, currentPage, 10);
        setUsers(response.data.content || response.data);
        setTotalPages(response.data.totalPages || 0);
        setTotalElements(response.data.totalElements || response.data.length);
      } else if (searchType === 'username') {
        response = await usersAPI.searchUsersByUsername(searchQuery);
        setUsers(response.data);
        setTotalPages(0);
        setTotalElements(response.data.length);
      } else if (searchType === 'email') {
        response = await usersAPI.searchUsersByEmail(searchQuery);
        setUsers(response.data);
        setTotalPages(0);
        setTotalElements(response.data.length);
      }
    } catch (err) {
      setError('Failed to search users');
      console.error('Error searching users:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFollowToggle = async (userId) => {
    setUsers(users => users.map(u => u.id === userId ? { ...u, followLoading: true } : u));
    try {
      await followAPI.toggleFollow(userId);
      setUsers(users => users.map(u => u.id === userId ? { ...u, isFollowed: !u.isFollowed, followLoading: false } : u));
    } catch (err) {
      setUsers(users => users.map(u => u.id === userId ? { ...u, followLoading: false } : u));
      console.error('Error toggling follow:', err);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setCurrentPage(0);
    performSearch();
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  return (
    <div className="user-search-container">
      <div className="search-header">
        <h2>Search Users</h2>
        <p>Find and connect with other users on YapNet</p>
      </div>

      <form onSubmit={handleSearch} className="search-form">
        <div className="search-input-group">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by username, email, or full name..."
            className="search-input"
            minLength="2"
          />
          
          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
            className="search-type-select"
          >
            <option value="all">All Fields</option>
            <option value="username">Username Only</option>
            <option value="email">Email Only</option>
          </select>
          
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Searching...' : 'Search'}
          </button>
        </div>
      </form>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {loading && (
        <div className="loading">
          Searching users...
        </div>
      )}

      {!loading && searchQuery && users.length === 0 && (
        <div className="no-results">
          <i className="fas fa-search no-results-icon"></i>
          <h3>No users found</h3>
          <p>Try adjusting your search terms</p>
        </div>
      )}

      {!loading && users.length > 0 && (
        <div className="search-results">
          <div className="results-header">
            <h3>Found {totalElements} user{totalElements !== 1 ? 's' : ''}</h3>
          </div>

          <div className="users-grid">
            {users.map((userItem) => (
              <div key={userItem.id} className="user-card">
                <div className="user-info">
                  <div className="user-avatar">
                    <i className="fas fa-user"></i>
                  </div>
                  <div className="user-details">
                    <h4 className="username">{userItem.username}</h4>
                    <p className="email">{userItem.email}</p>
                    {userItem.fullName && (
                      <p className="full-name">{userItem.fullName}</p>
                    )}
                  </div>
                </div>

                <div className="user-actions">
                  <Link 
                    to={`/user/${userItem.username}`} 
                    className="btn btn-secondary btn-sm"
                  >
                    View Profile
                  </Link>
                  
                  {user && user.id !== userItem.id && (
                    <button
                      className={`btn btn-sm ${userItem.isFollowed ? 'btn-unfollow' : 'btn-follow'}`}
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

          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="btn btn-secondary"
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
              >
                Previous
              </button>
              
              <span className="page-info">
                Page {currentPage + 1} of {totalPages}
              </span>
              
              <button
                className="btn btn-secondary"
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
              >
                Next
              </button>
            </div>
          )}
        </div>
      )}

      {!searchQuery && (
        <div className="search-tips">
          <h3>Search Tips</h3>
          <ul>
            <li>Search by username to find specific users</li>
            <li>Search by email if you know their email address</li>
            <li>Use partial matches for broader results</li>
            <li>Try different search types for better results</li>
          </ul>
        </div>
      )}
    </div>
  );
};

export default UserSearch;
