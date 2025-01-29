import React, { createContext, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';


// Création du contexte
const CartContext = createContext();

// Hook personnalisé pour utiliser le contexte
export const useCart = () => useContext(CartContext);


// Provider pour gérer le panier
export const CartProvider = ({ children }) => {
    const [cart, setCart] = useState([]);
    const navigate = useNavigate(); // Initialisation de navigate

    // Fonction pour ajouter un produit au panier
    const addToCart = (product) => {
        setCart((prevCart) => {
            const existingProduct = prevCart.find((item) => item.id === product.id);
            if (existingProduct) {
                return prevCart.map((item) =>
                    item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
                );
            }
            return [...prevCart, { ...product, quantity: 1 }];
        });
    };

    // Fonction pour supprimer un produit du panier
    const removeFromCart = (productId) => {
        setCart((prevCart) => prevCart.filter((item) => item.id !== productId));
    };

    const handleCheckout = () => {
        navigate('/order', { state: { cart } });
    };


    return (
        <CartContext.Provider value={{ cart, addToCart, removeFromCart, handleCheckout }}>
            {children}
        </CartContext.Provider>
    );
};
