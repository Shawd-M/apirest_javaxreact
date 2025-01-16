import React from 'react';
import { NavLink } from 'react-router-dom';
import { Navbar, Nav } from 'react-bootstrap';
import { useAuth } from './auth/authContext';

function NavigationBar() {
  const { isAuthenticated, logout, user } = useAuth();

  return (
    <Navbar bg="light" expand="lg">
      <Navbar.Brand as={NavLink} to="/">E-Co</Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="mr-auto">
          {isAuthenticated ? (
            <>
              <Nav.Link as={NavLink} to="/user">Utilisateurs</Nav.Link>
              {user && user.role === 'ROLE_SELLER' && (
                <Nav.Link as={NavLink} to="/manage-products">Gérer les Produits</Nav.Link>
              )}
              <Nav.Link as={NavLink} to="/profile">Mon profil</Nav.Link>
              <Nav.Link onClick={logout}>Se déconnecter</Nav.Link>
            </>
          ) : (
            <>
              <Nav.Link as={NavLink} to="/signin">Se connecter</Nav.Link>
              <Nav.Link as={NavLink} to="/signup">S'enregistrer</Nav.Link>
            </>
          )}
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
}

export default NavigationBar;
