import React, { createContext, useState, useContext, useEffect } from 'react';
import axios from 'axios';

// Configure axios defaults
axios.defaults.timeout = 30000; // Increased timeout to 30 seconds

// Add error interceptor
axios.interceptors.response.use(
    response => response,
    error => {
        console.error('Axios error:', {
            status: error.response?.status,
            statusText: error.response?.statusText,
            data: error.response?.data,
            message: error.message,
            request: error.request?.url,
            config: error.config
        });
        
        if (!error.response) {
            const requestUrl = error.config?.url || 'unknown URL';
            console.error('Network error occurred. Request URL:', requestUrl);
            throw new Error(`Network error. Please check if the backend server is running.`);
        }
        
        // For CORS issues
        if (error.response.status === 0) {
            throw new Error('CORS error. Please check if the backend server is properly configured.');
        }
        
        return Promise.reject(error);
    }
);

const AuthContext = createContext(null);

const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token') || '');
  const [user, setUser] = useState({
    username: '',
    email: '',
    followers: 0,
    following: 0,
    posts: 0
  });

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    if (storedToken) {
      setToken(storedToken);
      axios.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
      fetchUserData();
    } else {
      axios.defaults.headers.common['Authorization'] = '';
      setUser({
        username: '',
        email: '',
        followers: 0,
        following: 0,
        posts: 0
      });
    }
  }, []);

  // Watch for token changes and fetch user data
  useEffect(() => {
    if (token) {
      fetchUserData();
    }
  }, [token]);

  const fetchUserData = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        return null;
      }
      const response = await axios.get('/api/auth/me');
      const userData = response.data || {};
      setUser({ ...userData });
      return userData;
    } catch (error) {
      setUser({
        username: '',
        email: '',
        followers: 0,
        following: 0,
        posts: 0
      });
      return null;
    }
  };

  const login = async (credentials) => {
    try {
      const config = {
        headers: {
          'Content-Type': 'application/json'
        }
      };
      const response = await axios.post('/api/auth/login', credentials, config);
      
      console.log('Login response data:', response.data);
      
      if (!response.data) {
        console.error('Empty response from server');
        throw new Error('Empty response from server');
      }

      const { token, username, email } = response.data;
      if (!token || !username || !email) {
        console.error('Incomplete user data:', response.data);
        throw new Error('Missing required fields in response');
      }
      
      // Set token
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      setToken(token);
      localStorage.setItem('token', token);
      
      // Set initial user data from login response
      setUser({ 
        username, 
        email, 
        token: null,  // Explicitly set token to null
        followers: 0,
        following: 0,
        posts: 0
      });
      
      // Fetch complete user data
      try {
        const userData = await fetchUserData();
        if (userData) {
          setUser(userData);
        } else {
          console.error('Failed to fetch complete user data');
        }
      } catch (fetchError) {
        console.error('Error fetching user data:', fetchError);
        // Continue with basic user data from login response
      }
      
      return {
        token,
        username,
        email
      };
    } catch (error) {
      console.error('Login error:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message
      });
      
      // Clear any existing auth state on error
      setToken(null);
      setUser(null);
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      delete axios.defaults.headers.common['Authorization'];
      
      // Throw the error directly
      throw error;
    }
  };

  const register = async (userData) => {
    try {
      console.log('Registering user:', userData);
      
      const response = await axios.post('/api/auth/register', userData, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Registration successful:', response.data);
      
      // Store token and user info
      const { token, username, email } = response.data;
      setToken(token);
      setUser({ username, email });
      localStorage.setItem('token', token);
      localStorage.setItem('username', username);
      
      return response.data;
    } catch (error) {
      console.error('Registration error:', error);
      console.error('Request failed:', error);
      throw new Error('Request failed. Please check if the backend server is running.');
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    delete axios.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export { AuthProvider, useAuth };
export default AuthContext;
