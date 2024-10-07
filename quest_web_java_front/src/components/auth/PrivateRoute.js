import React from 'react';
import { Route, Navigate } from 'react-router-dom';
import { useAuth } from './authContext';

const PrivateRoute = ({ children, roles }) => {
    const { isAuthenticated, user } = useAuth();
    
    if (!isAuthenticated) {
        return <Navigate to="/signin" replace />;
    }

    if (roles && roles.indexOf(user.role) === -1) {
        return <Navigate to="/" replace />;
    }

    return children;
};

export default PrivateRoute;
