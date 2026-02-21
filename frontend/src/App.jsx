import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import AuthRequired from './components/auth/AuthRequired';
import AuthenticatedLayout from './components/shared/AuthenticatedLayout';
import Dashboard from './components/dashboard/Dashboard';
import Timeline from './components/timeline/Timeline';
import PostList from './components/posts/PostList';
import PostForm from './components/posts/PostForm';
import PostDetail from './components/posts/PostDetail';
import UserProfile from './components/user/UserProfile';
import UserSearch from './components/user/UserSearch';
import UserStats from './components/user/UserStats';
import GlobalStats from './components/stats/GlobalStats';
import PopularUsers from './components/stats/PopularUsers';
import PopularPosts from './components/stats/PopularPosts';
import MyPosts from './components/posts/MyPosts';
import UserPosts from './components/posts/UserPosts';
import './App.css';

// Error Boundary Component
const ErrorBoundary = ({ children }) => {
  const [hasError, setHasError] = React.useState(false);
  const [error, setError] = React.useState(null);

  React.useEffect(() => {
    if (hasError) {
      console.error('Error in component:', error);
    }
  }, [hasError, error]);

  if (hasError) {
    return (
      <div className="error-boundary">
        <h2>Something went wrong</h2>
        <p>{error?.message || 'An unexpected error occurred'}</p>
      </div>
    );
  }

  return children;
};

const App = () => {
  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <AuthProvider>
        <ErrorBoundary>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            
            {/* Protected Routes */}
            <Route path="/dashboard" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <Dashboard />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            
            {/* Posts Routes */}
            <Route path="/posts" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <PostList />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/posts/new" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <PostForm />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/posts/:postId" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <PostDetail />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/posts/edit/:postId" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <PostForm />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/my-posts" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <MyPosts />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/user/:userId/posts" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <UserPosts />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            
            {/* Timeline/Feed */}
            <Route path="/timeline" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <Timeline />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/feed" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <Timeline />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            
            {/* User Management */}
            <Route path="/profile" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <UserProfile />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/search" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <UserSearch />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/user/:username" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <UserProfile />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            
            {/* Statistics */}
            <Route path="/stats" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <UserStats />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/stats/global" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <GlobalStats />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/stats/popular-users" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <PopularUsers />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            <Route path="/stats/popular-posts" element={
              <AuthRequired>
                <AuthenticatedLayout>
                  <PopularPosts />
                </AuthenticatedLayout>
              </AuthRequired>
            } />
            
            {/* Catch all route */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </ErrorBoundary>
      </AuthProvider>
    </Router>
  );
};

export default App;