import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { statsAPI, postsAPI } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import './PopularPosts.css';

const PopularPosts = ({ limit = 10, sidebar = false }) => {
  const { user } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [postLimit, setPostLimit] = useState(limit);

  useEffect(() => {
    loadPopularPosts();
  }, [postLimit]);

  // Periodic refresh every 10 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      loadPopularPosts();
    }, 10000);
    return () => clearInterval(interval);
  }, [postLimit]);

  const loadPopularPosts = async () => {
    try {
      setLoading(true);
      const response = await statsAPI.getMostPopularPosts(postLimit);
      setPosts(response.data);
    } catch (err) {
      setError('Failed to load popular posts');
      console.error('Error loading popular posts:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleLike = async (postId) => {
    try {
      const response = await postsAPI.likePost(postId);
      setPosts(posts.map(post => 
        post.id === postId ? response.data : post
      ));
    } catch (err) {
      console.error('Error liking post:', err);
    }
  };

  const handleUnlike = async (postId) => {
    try {
      const response = await postsAPI.unlikePost(postId);
      setPosts(posts.map(post => 
        post.id === postId ? response.data : post
      ));
    } catch (err) {
      console.error('Error unliking post:', err);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const truncateContent = (content, maxLength = 150) => {
    if (content.length <= maxLength) return content;
    return content.substring(0, maxLength) + '...';
  };

  if (loading) {
    return (
      <div className={`popular-posts-container${sidebar ? ' compact' : ''}`}>
        <div className="loading">Loading popular posts...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={`popular-posts-container${sidebar ? ' compact' : ''}`}>
        <div className="error">{error}</div>
      </div>
    );
  }

  return (
    <div className={`popular-posts-container${sidebar ? ' compact' : ''}`}>
      {!sidebar && (
        <div className="stats-header">
          <h2>Popular Posts</h2>
          <p>Most liked and commented posts on YapNet</p>
        </div>
      )}
      {!sidebar && (
        <div className="controls">
          <div className="limit-control">
            <label htmlFor="limit">Show top:</label>
            <select
              id="limit"
              value={postLimit}
              onChange={(e) => setPostLimit(Number(e.target.value))}
              className="limit-select"
            >
              <option value={5}>5 posts</option>
              <option value={10}>10 posts</option>
              <option value={20}>20 posts</option>
              <option value={50}>50 posts</option>
            </select>
          </div>
          
          <button 
            className="btn btn-primary"
            onClick={loadPopularPosts}
          >
            <i className="fas fa-sync-alt"></i>
            Refresh
          </button>
        </div>
      )}

      {posts.length === 0 ? (
        <div className="no-posts">
          <i className="fas fa-fire no-posts-icon"></i>
          <h3>No popular posts found</h3>
          <p>Start creating content to see trending posts!</p>
        </div>
      ) : (
        <div className="posts-leaderboard">
          {posts.slice(0, sidebar ? 5 : postLimit).map((post, index) => (
            <div key={post.id} className={`post-card rank-${index + 1}${sidebar ? ' compact' : ''}`} style={sidebar ? {padding: '0.5rem 0.75rem', minHeight: 'unset', marginBottom: '0.5rem'} : {}}>
              <div className="rank-badge">
                #{index + 1}
              </div>
              
              <div className="post-header">
                <div className="post-author">
                  <div className="author-avatar">
                    <i className="fas fa-user"></i>
                  </div>
                  <div className="author-info">
                    <h4 className="author-name">{post.username}</h4>
                    <span className="post-date">{formatDate(post.createdAt)}</span>
                  </div>
                </div>
                
                <div className="post-stats" style={sidebar ? {fontSize: '0.9rem'} : {}}>
                  <div className="stat-item">
                    <i className="fas fa-heart"></i>
                    <span>{post.likes}</span>
                  </div>
                  <div className="stat-item">
                    <i className="fas fa-comment"></i>
                    <span>{post.commentCount || 0}</span>
                  </div>
                </div>
              </div>

              <div className="post-content">
                <h4 className="post-title">{post.title || post.content?.split("\n")[0]}</h4>
                <p>{truncateContent(post.content, 60)}</p>
              </div>

              <div className="post-actions" style={sidebar ? {marginTop: '0.5rem'} : {}}>
                <Link 
                  to={`/posts/${post.id}`} 
                  className="btn btn-secondary btn-sm"
                >
                  View Post
                </Link>
                
                <Link 
                  to={`/user/${post.username}`} 
                  className="btn btn-outline btn-sm"
                >
                  View Author
                </Link>
                
                {user && (
                  <button
                    className={`btn btn-sm ${post.likedByCurrentUser ? 'btn-liked' : 'btn-like'}`}
                    onClick={post.likedByCurrentUser ? () => handleUnlike(post.id) : () => handleLike(post.id)}
                  >
                    <i className={`fas fa-heart ${post.likedByCurrentUser ? 'filled' : ''}`}></i>
                    {post.likedByCurrentUser ? 'Unlike' : 'Like'}
                  </button>
                )}
              </div>

              <div className="post-engagement">
                <div className="engagement-metrics">
                  <div className="metric">
                    <span className="metric-label">Engagement Rate:</span>
                    <span className="metric-value">
                      {post.likes > 0 ? ((post.likes / (post.likes + (post.commentCount || 0))) * 100).toFixed(1) : '0'}%
                    </span>
                  </div>
                  <div className="metric">
                    <span className="metric-label">Total Interactions:</span>
                    <span className="metric-value">
                      {post.likes + (post.commentCount || 0)}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {!sidebar && (
        <div className="leaderboard-info">
          <h3>About Popular Posts</h3>
          <p>
            This leaderboard showcases the most engaging posts on YapNet based on likes, comments, 
            and overall user interaction. Posts are ranked by their total engagement and the 
            quality of discussions they generate.
          </p>
          <p>
            Create compelling content, engage with other users, and you might see your posts 
            featured on this leaderboard!
          </p>
        </div>
      )}
    </div>
  );
};

export default PopularPosts; 