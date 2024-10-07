import React from 'react';
import { useNavigate } from 'react-router-dom';

function Cart({ cart }) {
    const navigate = useNavigate();

    const handleCheckout = () => {
        navigate('/order', { state: { cart } });
    };

    return (
        <div>
            <h2>Panier</h2>
            {cart.length === 0 ? (
                <p>Votre panier est vide.</p>
            ) : (
                <div>
                    <ul>
                        {cart.map((item, index) => (
                            <li key={index}>
                                {item.name} - {item.price} € (Quantité: {item.quantity})
                            </li>
                        ))}
                    </ul>
                    <button onClick={handleCheckout}>Valider le panier</button>
                </div>
            )}
        </div>
    );
}

export default Cart;
