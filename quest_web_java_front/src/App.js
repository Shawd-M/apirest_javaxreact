import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import SignIn from './components/auth/SignIn';
import SignUp from './components/auth/SignUp';
import UserList from './components/user/UserList';
import UserDetail from './components/user/UserDetail';
import { AuthProvider } from './components/auth/authContext';
import NavigationBar from './components/NavigationBar';
import ProductCatalog from './components/catalog/ProductCatalog';
import Cart from './components/catalog/Cart';
import PrivateRoute from './components/auth/PrivateRoute';
import SellerProductManagement from './components/catalog/SellerProductManagement';
import Order from './components/catalog/Order';
import UserProfile from './components/user/UserProfile';
import 'bootstrap/dist/css/bootstrap.min.css';
import { CartProvider } from './components/catalog/CartContext'; // Import du contexte

function App() {
  return (
    <Router>
      <AuthProvider>
        <CartProvider>
          <NavigationBar />
          <Routes>
            <Route path="/signin" element={<SignIn />} />
            <Route path="/signup" element={<SignUp />} />
            <Route path="/user" element={<PrivateRoute roles={['ROLE_USER', 'ROLE_ADMIN', 'ROLE_SELLER']}><UserList /></PrivateRoute>} />
            <Route path="/user/:userId" element={<PrivateRoute roles={['ROLE_USER', 'ROLE_ADMIN', 'ROLE_SELLER']}><UserDetail /></PrivateRoute>} />
            <Route path="/manage-products" element={<PrivateRoute roles={['ROLE_SELLER']}><SellerProductManagement /></PrivateRoute>} />
            <Route path="/" element={<ProductCatalog />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/order" element={<Order />} />
            <Route path="/profile" element={<UserProfile />} />
          </Routes>
        </CartProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
