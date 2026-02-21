import React, { useState } from 'react';
import { commentsAPI } from '../../services/api';
import './CommentList.css';

const CommentList = ({ comments, onCommentDeleted, currentUser }) => {
  const [editingComment, setEditingComment] = useState(null);
  const [editContent, setEditContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleEdit = (comment) => {
    setEditingComment(comment.id);
    setEditContent(comment.content);
  };

  const handleSaveEdit = async (commentId) => {
    if (!editContent.trim()) return;

    try {
      setIsSubmitting(true);
      await commentsAPI.updateComment(commentId, { content: editContent });
      setEditingComment(null);
      setEditContent('');
      // Reload comments or update the comment in the list
      window.location.reload();
    } catch (err) {
      console.error('Error updating comment:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (commentId) => {
    if (!confirm('Are you sure you want to delete this comment?')) return;

    try {
      await commentsAPI.deleteComment(commentId);
      onCommentDeleted(commentId);
    } catch (err) {
      console.error('Error deleting comment:', err);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  if (comments.length === 0) {
    return (
      <div className="comment-list-container">
        <div className="no-comments">
          <p>No comments yet. Be the first to comment!</p>
        </div>
      </div>
    );
  }

  return (
    <div className="comment-list-container">
      {comments.map((comment) => (
        <div key={comment.id} className="comment-item">
          <div className="comment-header">
            <div className="comment-author">
              <span className="author-name">{comment.username}</span>
              <span className="comment-date">{formatDate(comment.createdAt)}</span>
            </div>
            
            {currentUser && comment.userId === currentUser.id && (
              <div className="comment-actions">
                {editingComment === comment.id ? (
                  <>
                    <button
                      className="btn btn-sm btn-primary"
                      onClick={() => handleSaveEdit(comment.id)}
                      disabled={isSubmitting}
                    >
                      Save
                    </button>
                    <button
                      className="btn btn-sm btn-secondary"
                      onClick={() => {
                        setEditingComment(null);
                        setEditContent('');
                      }}
                      disabled={isSubmitting}
                    >
                      Cancel
                    </button>
                  </>
                ) : (
                  <>
                    <button
                      className="btn btn-sm btn-edit"
                      onClick={() => handleEdit(comment)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-delete"
                      onClick={() => handleDelete(comment.id)}
                    >
                      Delete
                    </button>
                  </>
                )}
              </div>
            )}
          </div>

          <div className="comment-content">
            {editingComment === comment.id ? (
              <textarea
                value={editContent}
                onChange={(e) => setEditContent(e.target.value)}
                className="edit-comment-textarea"
                maxLength="200"
                disabled={isSubmitting}
              />
            ) : (
              <p>{comment.content}</p>
            )}
          </div>

          {editingComment === comment.id && (
            <div className="character-count">
              {editContent.length}/200
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default CommentList; 