import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { postsAPI, commentsAPI, followAPI } from '../../services/api';
import CommentForm from './CommentForm';
import CommentList from './CommentList';
import './PostDetail.css';

const PostDetail = () => {
  const { postId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCommentForm, setShowCommentForm] = useState(false);
  const [isLiking, setIsLiking] = useState(false);
  const [isFollowed, setIsFollowed] = useState(post?.isFollowed !== undefined ? post.isFollowed : false);
  const [followLoading, setFollowLoading] = useState(false);

  useEffect(() => {
    loadPost();
    loadComments();
  }, [postId]);

  useEffect(() => {
    if (post && user && post.userId !== user.id) {
      setIsFollowed(post.isFollowed !== undefined ? post.isFollowed : false);
    }
  }, [post, user]);

  const loadPost = async () => {
    try {
      setLoading(true);
      const response = await postsAPI.getPost(postId);
      setPost(response.data);
    } catch (err) {
      setError('Failed to load post');
      console.error('Error loading post:', err);
    } finally {
      setLoading(false);
    }
  };

  const loadComments = async () => {
    try {
      const response = await commentsAPI.getComments(postId);
      setComments(response.data);
    } catch (err) {
      console.error('Error loading comments:', err);
    }
  };

  const handleLike = async () => {
    if (!user) return;
    
    try {
      setIsLiking(true);
      const response = await postsAPI.likePost(postId);
      setPost(response.data);
    } catch (err) {
      console.error('Error liking post:', err);
    } finally {
      setIsLiking(false);
    }
  };

  const handleUnlike = async () => {
    if (!user) return;
    
    try {
      setIsLiking(true);
      const response = await postsAPI.unlikePost(postId);
      setPost(response.data);
    } catch (err) {
      console.error('Error unliking post:', err);
    } finally {
      setIsLiking(false);
    }
  };

  const handleFollowToggle = async () => {
    if (!user || !post?.userId || post.userId === user.id) return;
    setFollowLoading(true);
    try {
      await followAPI.toggleFollow(post.userId);
      setIsFollowed(prev => !prev);
    } catch (err) {
      console.error('Error toggling follow:', err);
    } finally {
      setFollowLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete this post?')) return;
    
    try {
      await postsAPI.deletePost(postId);
      navigate('/posts');
    } catch (err) {
      console.error('Error deleting post:', err);
    }
  };

  const handleCommentAdded = (newComment) => {
    setComments([newComment, ...comments]);
    setShowCommentForm(false);
  };

  const handleCommentDeleted = (commentId) => {
    setComments(comments.filter(comment => comment.id !== commentId));
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <div className="post-detail-container">
        <div className="loading">Loading post...</div>
      </div>
    );
  }

  if (error || !post) {
    return (
      <div className="post-detail-container">
        <div className="error">{error || 'Post not found'}</div>
      </div>
    );
  }

  return (
    <div className="post-detail-container">
      <div className="post-detail-header">
        <h1 className="post-title">{post.title || post.content?.split("\n")[0]}</h1>
            <div className="author-info">
          <h3 className="author-name">{post.username}
            {user && post.userId !== user.id && (
              <button
                className={`btn btn-sm ${isFollowed ? 'btn-unfollow' : 'btn-follow'}`}
                onClick={handleFollowToggle}
                disabled={followLoading}
                style={{ marginLeft: '8px' }}
              >
                {followLoading ? (isFollowed ? 'Unfollowing...' : 'Following...') : (isFollowed ? 'Unfollow' : 'Follow')}
              </button>
            )}
          </h3>
              <span className="post-date">{formatDate(post.createdAt)}</span>
            </div>
          {user && post.userId === user.id && (
            <div className="post-actions">
              <button 
                className="btn btn-edit"
                onClick={() => navigate(`/posts/edit/${postId}`)}
              >
                Edit
              </button>
              <button 
                className="btn btn-delete"
                onClick={handleDelete}
              >
                Delete
              </button>
            </div>
          )}
        </div>
        <div className="post-content">
          <p>{post.content}</p>
        </div>
        <div className="post-footer">
          <div className="post-stats">
            <span className="likes-count">
              <i className="fas fa-heart"></i>
              {post.likes} likes
            </span>
            <span className="comments-count">
              <i className="fas fa-comment"></i>
              {comments.length} comments
            </span>
          </div>
          <div className="post-interactions">
            <button
              className={`btn btn-like ${post.likedByCurrentUser ? 'liked' : ''}`}
              onClick={post.likedByCurrentUser ? handleUnlike : handleLike}
              disabled={isLiking}
            >
              <i className={`fas fa-heart ${post.likedByCurrentUser ? 'filled' : ''}`}></i>
              {post.likedByCurrentUser ? 'Unlike' : 'Like'}
            </button>
            <button
              className="btn btn-comment"
              onClick={() => setShowCommentForm(!showCommentForm)}
            >
              <i className="fas fa-comment"></i>
              Comment
            </button>
          </div>
        </div>
        {showCommentForm && (
          <CommentForm
            postId={postId}
            onCommentAdded={handleCommentAdded}
            onCancel={() => setShowCommentForm(false)}
          />
        )}
        <div className="comments-section">
          <h4>Comments ({comments.length})</h4>
          <CommentList
            comments={comments}
            onCommentDeleted={handleCommentDeleted}
            currentUser={user}
          />
      </div>
    </div>
  );
};

export default PostDetail; 