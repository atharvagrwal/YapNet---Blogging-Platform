import React, { useState, useEffect } from 'react';
import { useAuth } from "@/contexts/AuthContext";
import { followAPI, postsAPI, commentsAPI, usersAPI } from '../../services/api';

const PostItem = ({ post, onUpdate, onDelete, onStatsUpdate }) => {
    const { user, token } = useAuth();
    const [likes, setLikes] = useState(post.likes || 0);
    const [liked, setLiked] = useState(post.likedByCurrentUser || false);
    const [comments, setComments] = useState([]);
    const [commentCount, setCommentCount] = useState(post.commentCount || 0);
    const [showComments, setShowComments] = useState(false);
    const [newComment, setNewComment] = useState('');
    const [loading, setLoading] = useState(false);
    const [followLoading, setFollowLoading] = useState(false);
    const [isFollowed, setIsFollowed] = useState(false);
    const isOwnPost = user && post.userId === user.id;

    useEffect(() => {
        console.log('PostItem useEffect - post.id:', post.id, 'post.username:', post.username);
        console.log('Post data:', post);
        console.log('post.likedByCurrentUser:', post.likedByCurrentUser);
        console.log('Current liked state:', liked);
        // Update liked state when post data changes
        setLiked(post.likedByCurrentUser || false);
        setLikes(post.likes || 0);
        fetchComments();
        checkFollowStatus();
    }, [post.id, post.likedByCurrentUser, post.likes, user]);

    const fetchComments = async () => {
        try {
            const response = await commentsAPI.getComments(post.id);
            setComments(response.data);
            setCommentCount(response.data.length);
        } catch (error) {
            console.error('Error fetching comments:', error);
        }
    };

    const checkFollowStatus = async () => {
        if (!user || isOwnPost) return;
        try {
            // Check if current user is following the post author
            const response = await usersAPI.getUserByUsername(post.username);
            console.log('Follow status response:', response.data);
            console.log('isFollowed from backend:', response.data.isFollowed);
            setIsFollowed(response.data.isFollowed || false);
            console.log('Updated isFollowed state to:', response.data.isFollowed || false);
        } catch (error) {
            console.error('Error checking follow status:', error);
        }
    };

    const handleLike = async () => {
        if (!user || isOwnPost) return;
        try {
            const response = await postsAPI.likePost(post.id);
            setLikes(response.data.likes);
            setLiked(response.data.likedByCurrentUser);
            if (onUpdate) onUpdate(response.data); // Pass updated post data up
        } catch (error) {
            console.error('Error liking post:', error);
        }
    };

    const handleUnlike = async () => {
        if (!user || isOwnPost) return;
        try {
            const response = await postsAPI.unlikePost(post.id);
            setLikes(response.data.likes);
            setLiked(response.data.likedByCurrentUser);
            if (onUpdate) onUpdate(response.data); // Pass updated post data up
        } catch (error) {
            console.error('Error unliking post:', error);
        }
    };

    const handleDeletePost = async () => {
        if (!user || !isOwnPost) return;
        if (!window.confirm('Are you sure you want to delete this post?')) return;
        
        try {
            await postsAPI.deletePost(post.id);
            if (onDelete) onDelete(post.id);
        } catch (error) {
            console.error('Error deleting post:', error);
        }
    };

    const handleAddComment = async (e) => {
        e.preventDefault();
        if (!user || !newComment.trim()) return;
        
        try {
            setLoading(true);
            await commentsAPI.createComment(post.id, { content: newComment });
            setNewComment('');
            await fetchComments();
            if (onUpdate) onUpdate();
        } catch (error) {
            console.error('Error adding comment:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteComment = async (commentId) => {
        try {
            await commentsAPI.deleteComment(commentId);
            setComments(comments.filter(comment => comment.id !== commentId));
        } catch (error) {
            console.error('Error deleting comment:', error);
        }
    };

    const handleFollowToggle = async () => {
        if (!user || isOwnPost || !post.userId) return;
        console.log('Follow toggle - post.userId:', post.userId, 'type:', typeof post.userId);
        console.log('Current isFollowed state before toggle:', isFollowed);
        setFollowLoading(true);
        try {
            await followAPI.toggleFollow(post.userId);
            console.log('Follow toggle API call completed');
            
            // Add a small delay to ensure backend has processed the request
            await new Promise(resolve => setTimeout(resolve, 500));
            
            // Re-fetch user info from backend to get the correct follow state
            await checkFollowStatus();
            console.log('Follow status check completed');
            
            // Refresh stats after follow/unfollow
            if (onStatsUpdate) {
                onStatsUpdate();
            }
        } catch (err) {
            console.error('Error toggling follow:', err);
        } finally {
            setFollowLoading(false);
        }
    };

    return (
        <div className="post-card">
            {console.log('Rendering PostItem - isFollowed:', isFollowed, 'post.username:', post.username, 'liked:', liked, 'likes:', likes)}
            <div className="post-header">
                <div style={{flex: 1}}>
                  <h2 className="post-title">{post.title || post.content?.split("\n")[0]}</h2>
                  <span className="post-author">{post.username}</span>
                  <span className="post-date">{new Date(post.createdAt).toLocaleDateString()}</span>
                </div>
                {user && !isOwnPost && (
                    <button
                        className={`btn btn-sm ${isFollowed ? 'btn-unfollow' : 'btn-follow'}`}
                        onClick={handleFollowToggle}
                        disabled={followLoading}
                        style={{ marginLeft: '8px' }}
                    >
                        {followLoading ? (isFollowed ? 'Unfollowing...' : 'Following...') : (isFollowed ? 'Unfollow' : 'Follow')}
                    </button>
                )}
                {isOwnPost && (
                    <button
                        onClick={handleDeletePost}
                        style={{
                            backgroundColor: '#dc3545',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            padding: '4px 8px',
                            fontSize: '0.8rem',
                            cursor: 'pointer',
                            marginLeft: '8px'
                        }}
                    >
                        Delete
                    </button>
                )}
            </div>
            <div className="post-content">
                {post.content}
            </div>
            <div className="post-actions">
                {!liked ? (
                    <button
                        onClick={handleLike}
                        disabled={isOwnPost}
                        style={{
                            backgroundColor: '#007bff',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            padding: '8px 16px',
                            fontWeight: '500',
                            cursor: isOwnPost ? 'not-allowed' : 'pointer',
                            opacity: isOwnPost ? '0.6' : '1'
                        }}
                    >
                        Like ({likes})
                    </button>
                ) : (
                    <button
                        onClick={handleUnlike}
                        style={{
                            backgroundColor: '#dc3545',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            padding: '8px 16px',
                            fontWeight: '500',
                            cursor: 'pointer'
                        }}
                    >
                        Unlike ({likes})
                    </button>
                )}
                <button
                    onClick={() => setShowComments(!showComments)}
                    style={{
                        backgroundColor: '#6c757d',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        padding: '8px 16px',
                        fontWeight: '500',
                        cursor: 'pointer'
                    }}
                >
                    Comments ({commentCount})
                </button>
            </div>
            
            {showComments && (
                <div className="comments-section">
                    <form onSubmit={handleAddComment} style={{ marginBottom: '1rem' }}>
                        <textarea
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                            placeholder="Write a comment..."
                            style={{
                                width: '100%',
                                minHeight: '60px',
                                padding: '8px',
                                border: '1px solid #ddd',
                                borderRadius: '4px',
                                resize: 'vertical'
                            }}
                            maxLength="1000"
                        />
                        <button
                            type="submit"
                            disabled={loading || !newComment.trim()}
                            style={{
                                backgroundColor: '#007bff',
                                color: 'white',
                                border: 'none',
                                borderRadius: '4px',
                                padding: '8px 16px',
                                marginTop: '8px',
                                cursor: loading || !newComment.trim() ? 'not-allowed' : 'pointer',
                                opacity: loading || !newComment.trim() ? '0.6' : '1'
                            }}
                        >
                            {loading ? 'Adding...' : 'Add Comment'}
                        </button>
                    </form>
                    
                    <div className="comments-list">
                        {comments.map(comment => (
                            <div key={comment.id} className="comment-item" style={{
                                border: '1px solid #eee',
                                borderRadius: '4px',
                                padding: '8px',
                                marginBottom: '8px',
                                backgroundColor: '#f8f9fa'
                            }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <span style={{ fontWeight: 'bold', fontSize: '0.9rem' }}>
                                        {comment.username}
                                    </span>
                                    <span style={{ fontSize: '0.8rem', color: '#666' }}>
                                        {new Date(comment.createdAt).toLocaleDateString()}
                                    </span>
                                </div>
                                <p style={{ margin: '4px 0', fontSize: '0.9rem' }}>{comment.content}</p>
                                {(user && comment.username === user.username) && (
                                    <button
                                        onClick={() => handleDeleteComment(comment.id)}
                                        style={{
                                            backgroundColor: '#dc3545',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '2px',
                                            padding: '2px 6px',
                                            fontSize: '0.7rem',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        Delete
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default PostItem;
