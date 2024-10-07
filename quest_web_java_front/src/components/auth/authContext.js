import React, { createContext, useContext, useState, useEffect } from 'react';
//import { useNavigate } from 'react-router-dom';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [authToken, setAuthToken] = useState(null);
  const [userInfo, setUserInfo] = useState(null);
  const [userId, setUserId] = useState(null);



  //const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    const userInfoString = localStorage.getItem('userInfo');
    const userInfo = userInfoString ? JSON.parse(userInfoString) : null;

    if (token && userInfo) {
      setIsAuthenticated(true);
      setUser(userInfo);
      setAuthToken(token);
    } else {
      setIsAuthenticated(false);
      setUser(null);
      setAuthToken(null);
    }
  }, []);

  const logout = async () => {
    try {
      const response = await fetch('http://localhost:8090/out', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
          'Content-Type': 'application/json',
        },
      });
  
      if (response.ok) {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userInfo');
        localStorage.removeItem('role');
        setIsAuthenticated(false);
        //navigate('/signin');  // Rediriger après déconnexion
      } else {
        console.error('Erreur lors de la déconnexion');
      }
    } catch (error) {
      console.error('Erreur lors de la déconnexion', error);
    }
  };

  const login = (token, userDetails) => {
    localStorage.setItem('authToken', token);
    localStorage.setItem('userInfo', JSON.stringify(userDetails));
    localStorage.setItem('role', userDetails.role);
    localStorage.setItem('id', userDetails.id);
    setIsAuthenticated(true);
    setUser(userDetails);
    setAuthToken(token);
    setUserId(userDetails.id);


    console.log("Les users infos sont :  " + JSON.stringify(userDetails));

    if (userDetails && userDetails.id) {
        setUserInfo(userDetails.id);
    } else {
        console.log("L'objet userDetails ne contient pas d'ID");
    }
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, authToken, userInfo, userId, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
