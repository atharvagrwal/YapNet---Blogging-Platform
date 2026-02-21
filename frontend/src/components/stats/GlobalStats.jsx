import React, { useState, useEffect, useImperativeHandle, forwardRef } from 'react';
import { statsAPI } from '../../services/api';
import './GlobalStats.css';

const GlobalStats = forwardRef(({ sidebar = false, onStatsUpdate }, ref) => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadGlobalStats();
  }, []);

  const loadGlobalStats = async () => {
    try {
      setLoading(true);
      const response = await statsAPI.getGlobalStats();
      setStats(response.data);
    } catch (err) {
      setError('Failed to load global statistics');
      console.error('Error loading global stats:', err);
    } finally {
      setLoading(false);
    }
  };

  // Expose loadGlobalStats function to parent component
  useImperativeHandle(ref, () => ({
    refresh: loadGlobalStats
  }));

  // Call onStatsUpdate when stats are loaded
  useEffect(() => {
    if (stats && onStatsUpdate) {
      onStatsUpdate(stats);
    }
  }, [stats, onStatsUpdate]);

  if (loading) {
    return (
      <div className={`global-stats-container${sidebar ? ' compact' : ''}`}>
        <div className="loading">Loading global statistics...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={`global-stats-container${sidebar ? ' compact' : ''}`}>
        <div className="error">{error}</div>
      </div>
    );
  }

  if (!stats) {
    return (
      <div className={`global-stats-container${sidebar ? ' compact' : ''}`}>
        <div className="error">No statistics available</div>
      </div>
    );
  }

  return (
    <div className={`global-stats-container${sidebar ? ' compact' : ''}`}>
      {!sidebar && (
        <div className="stats-header">
          <h2>Global Statistics</h2>
          <p>Platform-wide metrics and insights</p>
        </div>
      )}
      <div className="stats-grid" style={sidebar ? {gridTemplateColumns: '1fr'} : {}}>
        <div className="stat-card primary">
          <div className="stat-icon"><i className="fas fa-users"></i></div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.totalUsers?.toLocaleString() || '0'}</h3>
            <p className="stat-label">Users</p>
          </div>
        </div>
        <div className="stat-card secondary">
          <div className="stat-icon"><i className="fas fa-edit"></i></div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.totalPosts?.toLocaleString() || '0'}</h3>
            <p className="stat-label">Posts</p>
          </div>
        </div>
        <div className="stat-card tertiary">
          <div className="stat-icon"><i className="fas fa-comment"></i></div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.totalComments?.toLocaleString() || '0'}</h3>
            <p className="stat-label">Comments</p>
          </div>
        </div>
        <div className="stat-card quaternary">
          <div className="stat-icon"><i className="fas fa-heart"></i></div>
          <div className="stat-content">
            <h3 className="stat-number">{stats.totalLikes?.toLocaleString() || '0'}</h3>
            <p className="stat-label">Likes</p>
          </div>
        </div>
      </div>

      {!sidebar && (
        <>
          <div className="stats-insights">
            <div className="insight-section">
              <h3>Platform Insights</h3>
              <div className="insights-grid">
                <div className="insight-card">
                  <h4>Engagement Rate</h4>
                  <p>
                    {stats.totalPosts > 0 
                      ? ((stats.totalLikes / stats.totalPosts) * 100).toFixed(1)
                      : '0'}% average likes per post
                  </p>
                </div>
                
                <div className="insight-card">
                  <h4>Interaction Rate</h4>
                  <p>
                    {stats.totalPosts > 0 
                      ? ((stats.totalComments / stats.totalPosts) * 100).toFixed(1)
                      : '0'}% average comments per post
                  </p>
                </div>
                
                <div className="insight-card">
                  <h4>Content Creation</h4>
                  <p>
                    {stats.totalUsers > 0 
                      ? (stats.totalPosts / stats.totalUsers).toFixed(1)
                      : '0'} posts per user on average
                  </p>
                </div>
                
                <div className="insight-card">
                  <h4>Community Activity</h4>
                  <p>
                    {stats.totalUsers > 0 
                      ? ((stats.totalLikes + stats.totalComments) / stats.totalUsers).toFixed(1)
                      : '0'} interactions per user
                  </p>
                </div>
              </div>
            </div>

            <div className="insight-section">
              <h3>Activity Summary</h3>
              <div className="activity-summary">
                <p>
                  YapNet has grown to <strong>{stats.totalUsers?.toLocaleString() || '0'}</strong> active users 
                  who have shared <strong>{stats.totalPosts?.toLocaleString() || '0'}</strong> posts.
                </p>
                <p>
                  The community has generated <strong>{stats.totalComments?.toLocaleString() || '0'}</strong> comments 
                  and <strong>{stats.totalLikes?.toLocaleString() || '0'}</strong> likes, 
                  creating a vibrant and engaging social platform.
                </p>
                <p>
                  With an average of {stats.totalUsers > 0 ? (stats.totalPosts / stats.totalUsers).toFixed(1) : '0'} posts per user, 
                  YapNet fosters active content creation and meaningful interactions.
                </p>
              </div>
            </div>
          </div>

          <div className="stats-actions">
            <button 
              className="btn btn-primary"
              onClick={loadGlobalStats}
            >
              <i className="fas fa-sync-alt"></i>
              Refresh Statistics
            </button>
            
            <button 
              className="btn btn-secondary"
              onClick={async () => {
                try {
                  const response = await statsAPI.exportStatsCSV();
                  const url = window.URL.createObjectURL(new Blob([response.data]));
                  const link = document.createElement('a');
                  link.href = url;
                  link.setAttribute('download', 'yapnet_stats.csv');
                  document.body.appendChild(link);
                  link.click();
                  link.remove();
                } catch (err) {
                  console.error('Error exporting stats:', err);
                }
              }}
            >
              <i className="fas fa-download"></i>
              Export CSV
            </button>
          </div>
        </>
      )}
    </div>
  );
});

GlobalStats.displayName = 'GlobalStats';

export default GlobalStats; 