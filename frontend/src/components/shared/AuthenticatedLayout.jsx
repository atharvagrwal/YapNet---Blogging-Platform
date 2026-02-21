import React from 'react';
import Navigation from './Navigation';
import './AuthenticatedLayout.css';
import PopularUsers from '../stats/PopularUsers';
import PopularPosts from '../stats/PopularPosts';
import GlobalStats from '../stats/GlobalStats';

const AuthenticatedLayout = ({ children }) => {
    return (
        <div className="authenticated-layout">
            <Navigation />
            <div className="main-layout">
                <aside className="sidebar left-sidebar">
                  {/* Add nav/links here if needed */}
                </aside>
                <main className="main-content">{children}</main>
                {/* Removed right sidebar with stats widgets */}
            </div>
        </div>
    );
};

export default AuthenticatedLayout; 