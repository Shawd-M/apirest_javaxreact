import { React, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { Navbar, Nav } from 'react-bootstrap';
import { useAuth } from './auth/authContext';
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import NavDropdown from 'react-bootstrap/NavDropdown';
import { CiShoppingCart } from "react-icons/ci";
import Cart from "./catalog/Cart"; // Ton composant existant
import { useCart } from '../components/catalog/CartContext';


function NavigationBar() {
  const { isAuthenticated, logout, user } = useAuth();
  const { cart } = useCart();
  const [isCartOpen, setCartOpen] = useState(false); // Gère l'affichage du panier


  const toggleCart = () => setCartOpen((prev) => !prev);

  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container fluid>
        {/* Logo */}
        <Navbar.Brand href="#">E-Co</Navbar.Brand>
        <Navbar.Toggle aria-controls="navbarScroll" />
        <Navbar.Collapse id="navbarScroll">
          {/* Navigation à gauche */}
          <Nav className="me-auto my-2 my-lg-0" navbarScroll>
            <Nav.Link href="/">Home</Nav.Link>
          </Nav>

          {/* Formulaire centré */}
          <Form className="d-flex mx-auto" style={{ maxWidth: "400px" }}>
            <Form.Control
              type="search"
              placeholder="Search"
              className="me-2"
              aria-label="Search"
            />
            <Button variant="outline-success">Search</Button>
          </Form>

          {/* Navigation à droite */}
          <Nav className="ms-auto" navbarScroll>
            <NavDropdown title="Langue" id="navbarScrollingDropdown">
              <NavDropdown.Item href="#action3">Français</NavDropdown.Item>
              <NavDropdown.Item href="#action4">Anglais</NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item href="#action5">Espagnol</NavDropdown.Item>
            </NavDropdown>
            <Nav.Link href="#" disabled>Prochain update</Nav.Link>

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
              <Nav.Link as={NavLink} to="/signin">Identifiez-vous</Nav.Link>
            )}

            {/* Icône panier */}
            <CiShoppingCart
              onClick={toggleCart}
              style={{ cursor: "pointer", width: '35px', height: '35px', marginLeft: '15px' }}
            />
          </Nav>
        </Navbar.Collapse>
      </Container>

      {/* Affichage conditionnel du composant Cart */}
      {isCartOpen && (
        <div
          style={{
            position: "absolute",
            right: "10px",
            top: "60px",
            background: "white",
            border: "1px solid #ccc",
            zIndex: 1000,
            padding: "10px",
            boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
          }}
        >
          <Cart cart={cart} />
        </div>
      )}
    </Navbar>
  );
}

export default NavigationBar;