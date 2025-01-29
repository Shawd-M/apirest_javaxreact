import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/authContext';
import { Modal, Button } from 'react-bootstrap';
import { useCart } from '../catalog/CartContext';


function Order() {
    const location = useLocation();
    const { cart } = useCart();
    const { authToken, user, isAuthenticated } = useAuth();
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);
    const { userId } = useAuth();
    const idbis = localStorage.getItem('idbis');




    const handleConfirmOrder = async () => {
        console.log("User:", userId);

    
        if (cart.length === 0) return;
    
        const orderDetails = {
            userId: userId,
            orderItems: cart.map(item => ({
              product: { id: item.id },
              quantity: item.quantity,
              price: item.price
            }))
          };

        const response = await fetch('http://localhost:8090/orders/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(orderDetails)
        });
    
        if (response.ok) {
            console.log('Commande créée avec succès');
            navigate('/');
        } else {
            console.error('Erreur lors de la création de la commande');
        }
    };

    return (
        <div>
            <h1>Détails de la Commande</h1>
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
                    <button onClick={handleConfirmOrder}>Confirmer la commande</button>
                </div>
            )}

            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Connexion Requise</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Veuillez vous connecter pour valider votre panier.
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        Fermer
                    </Button>
                    <Button variant="primary" onClick={() => navigate('/signin')}>
                        Se connecter
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default Order;
