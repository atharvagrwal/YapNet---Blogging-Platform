import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import './Navigation.css';

const Navigation = () => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
    <nav className="navigation">
      <div className="nav-container">
        <div className="nav-brand">
          <div className="nav-logo">
            YapNet
          </div>
        </div>

        <button className="nav-toggle" onClick={toggleMenu}>
          <span className="hamburger"></span>
        </button>

        <div className={`nav-menu ${isMenuOpen ? 'active' : ''}`}>
          <div className="nav-links">
            <Link 
              to="/dashboard" 
              className={`nav-link ${isActive('/dashboard') ? 'active' : ''}`}
              onClick={() => setIsMenuOpen(false)}
            >
              <i className="fas fa-home"></i>
              Dashboard
            </Link>
            
            <Link 
              to="/timeline" 
              className={`nav-link ${isActive('/timeline') ? 'active' : ''}`}
              onClick={() => setIsMenuOpen(false)}
            >
              <i className="fas fa-stream"></i>
              Timeline
            </Link>
            
            <Link 
              to="/posts" 
              className={`nav-link ${isActive('/posts') ? 'active' : ''}`}
              onClick={() => setIsMenuOpen(false)}
            >
              <i className="fas fa-list"></i>
              All Posts
            </Link>
            
            <Link 
              to="/my-posts" 
              className={`nav-link ${isActive('/my-posts') ? 'active' : ''}`}
              onClick={() => setIsMenuOpen(false)}
            >
              <i className="fas fa-user-edit"></i>
              My Posts
            </Link>
            
            <Link 
              to="/posts/new" 
              className={`nav-link ${isActive('/posts/new') ? 'active' : ''}`}
              onClick={() => setIsMenuOpen(false)}
            >
              <i className="fas fa-plus"></i>
              New Post
            </Link>
            
            <Link 
              to="/search" 
              className={`nav-link ${isActive('/search') ? 'active' : ''}`}
              onClick={() => setIsMenuOpen(false)}
            >
              <i className="fas fa-search"></i>
              Search Users
            </Link>
          </div>

          <div className="nav-dropdown">
            <button className="nav-dropdown-btn">
              <i className="fas fa-chart-bar"></i>
              Statistics
              <i className="fas fa-chevron-down"></i>
            </button>
            <div className="nav-dropdown-content">
              <Link 
                to="/stats" 
                className={`nav-link ${isActive('/stats') && !isActive('/stats/') ? 'active' : ''}`}
                onClick={() => setIsMenuOpen(false)}
              >
                My Stats
              </Link>
              <Link 
                to="/stats/global" 
                className={`nav-link ${isActive('/stats/global') ? 'active' : ''}`}
                onClick={() => setIsMenuOpen(false)}
              >
                Global Stats
              </Link>
              <Link 
                to="/stats/popular-users" 
                className={`nav-link ${isActive('/stats/popular-users') ? 'active' : ''}`}
                onClick={() => setIsMenuOpen(false)}
              >
                Popular Users
              </Link>
              <Link 
                to="/stats/popular-posts" 
                className={`nav-link ${isActive('/stats/popular-posts') ? 'active' : ''}`}
                onClick={() => setIsMenuOpen(false)}
              >
                Popular Posts
              </Link>
            </div>
          </div>

          <div className="nav-user">
            <div className="nav-dropdown">
              <button className="nav-dropdown-btn">
                <i className="fas fa-user"></i>
                {user?.username || 'User'}
                <i className="fas fa-chevron-down"></i>
              </button>
              <div className="nav-dropdown-content">
                <Link 
                  to="/profile" 
                  className={`nav-link ${isActive('/profile') ? 'active' : ''}`}
                  onClick={() => setIsMenuOpen(false)}
                >
                  <i className="fas fa-user-circle"></i>
                  Profile
                </Link>
                <button 
                  className="nav-link logout-btn" 
                  onClick={handleLogout}
                >
                  <i className="fas fa-sign-out-alt"></i>
                  Logout
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navigation; 