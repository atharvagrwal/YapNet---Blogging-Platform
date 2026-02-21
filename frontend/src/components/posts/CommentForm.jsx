import React, { useState } from 'react';
import { commentsAPI } from '../../services/api';
import './CommentForm.css';

const CommentForm = ({ postId, onCommentAdded, onCancel }) => {
  const [content, setContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!content.trim()) {
      setError('Comment cannot be empty');
      return;
    }

    if (content.length > 200) {
      setError('Comment cannot exceed 200 characters');
      return;
    }

    try {
      setIsSubmitting(true);
      setError('');
      
      const response = await commentsAPI.createComment(postId, { content });
      onCommentAdded(response.data);
      setContent('');
    } catch (err) {
      setError('Failed to add comment. Please try again.');
      console.error('Error creating comment:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    setContent('');
    setError('');
    onCancel();
  };

  return (
    <div className="comment-form-container">
      <form onSubmit={handleSubmit} className="comment-form">
        <div className="form-group">
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Write a comment..."
            className="comment-textarea"
            rows="3"
            maxLength="200"
            disabled={isSubmitting}
          />
          <div className="character-count">
            {content.length}/200
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="form-actions">
          <button
            type="button"
            onClick={handleCancel}
            className="btn btn-secondary"
            disabled={isSubmitting}
          >
            Cancel
          </button>
          <button
            type="submit"
            className="btn btn-primary"
            disabled={isSubmitting || !content.trim()}
          >
            {isSubmitting ? 'Posting...' : 'Post Comment'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CommentForm; 