import axios from 'axios';

const API_BASE_URL = '/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  getCurrentUser: () => api.get('/auth/me'),
  checkUser: (username) => api.get(`/auth/check-user/${username}`),
  debug: () => api.get('/auth/debug'),
};

// Posts API
export const postsAPI = {
  getAllPosts: () => api.get('/posts'),
  getMyPosts: () => api.get('/posts/me'),
  getFeed: () => api.get('/posts/feed'),
  getPost: (postId) => api.get(`/posts/${postId}`),
  getUserPosts: (userId) => api.get(`/posts/user/${userId}`),
  createPost: (postData) => api.post('/posts', postData),
  updatePost: (postId, postData) => api.put(`/posts/${postId}`, postData),
  deletePost: (postId) => api.delete(`/posts/${postId}`),
  likePost: (postId) => api.post(`/posts/${postId}/likes`),
  unlikePost: (postId) => api.delete(`/posts/${postId}/likes`),
  getPopularPosts: (limit = 10) => api.get(`/stats/posts/popular?limit=${limit}`),
};

// Comments API
export const commentsAPI = {
  getComments: (postId) => api.get(`/posts/${postId}/comments`),
  getComment: (commentId) => api.get(`/posts/comments/${commentId}`),
  createComment: (postId, commentData) => api.post(`/posts/${postId}/comments`, commentData),
  updateComment: (commentId, commentData) => api.put(`/posts/comments/${commentId}`, commentData),
  deleteComment: (commentId) => api.delete(`/posts/comments/${commentId}`),
};

// Users API
export const usersAPI = {
  searchUsers: (query, page = 0, size = 10) => 
    api.get(`/users/search?query=${query}&page=${page}&size=${size}`),
  searchUsersByUsername: (query) => api.get(`/users/search/username?query=${query}`),
  searchUsersByEmail: (query) => api.get(`/users/search/email?query=${query}`),
  advancedSearch: (params) => api.get('/users/search/advanced', { params }),
  searchUsersPublic: (query) => api.get(`/users/search/public?query=${query}`),
  checkUsernameExists: (username) => api.get(`/users/check-username/${username}`),
  getAllUsers: () => api.get('/users/all'),
  getUserByUsername: (username) => api.get(`/users/username/${username}`),
  getUserProfile: () => api.get('/users/profile'),
  updateUserProfile: (profileData) => api.put('/users/profile', profileData),
  getMostPopularUsers: (limit = 10) => api.get(`/stats/users/popular?limit=${limit}`),
};

// Follow API
export const followAPI = {
  toggleFollow: (followingId) => api.post('/follow', { followingId }),
  deleteFollow: (followerId, followingId) => api.delete(`/follow/${followerId}/${followingId}`),
  getFollowerCount: (userId) => api.get(`/follow/followers/${userId}`),
  getFollowingCount: (userId) => api.get(`/follow/following/${userId}`),
};

// Stats API
export const statsAPI = {
  getUserStats: () => api.get('/stats/user'),
  getAllUserStats: () => api.get('/stats'),
  getGlobalStats: () => api.get('/stats/global'),
  getTopUsers: () => api.get('/stats/top'),
  getTop5Users: () => api.get('/stats/top5'),
  getMostPopularUsers: (limit = 10) => api.get(`/stats/users/popular?limit=${limit}`),
  getMostPopularPosts: (limit = 10) => api.get(`/stats/posts/popular?limit=${limit}`),
  exportStatsCSV: () => api.get('/stats/export/csv', { responseType: 'blob' }),
};

export default api; 