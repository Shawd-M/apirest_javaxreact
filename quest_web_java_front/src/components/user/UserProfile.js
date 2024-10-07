import React, { useEffect, useState } from 'react';
import { useAuth } from '../auth/authContext';
import '../css/userdetail.css';


function UserProfile() {
  const { authToken, user } = useAuth();
  const [userDetails, setUserDetails] = useState(null);
  const [orders, setOrders] = useState([]);
  const idbis = localStorage.getItem('idbis');


  useEffect(() => {
    const fetchUserDetails = async () => {
      const response = await fetch('http://localhost:8090/me', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      });
  
      if (response.ok) {
        const data = await response.json();
        setUserDetails(data);
        localStorage.setItem('idbis', data.id);

        console.log("Idbis est égale à " + idbis);

        console.log("Les data sont: ", data);
      } else {
        console.error('Erreur lors de la récupération des détails utilisateur');
      }
    };
  
    const fetchUserOrders = async () => {
      const response = await fetch(`http://localhost:8090/orders/user/${user.id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      });
  
      if (response.ok) {
        const data = await response.json();
        setOrders(data);
      } else {
        console.error('Erreur lors de la récupération des commandes');
      }
    };
  
    fetchUserDetails();
    fetchUserOrders();
  }, [authToken, user.id]);

  return (
    <div className="container mt-4">
      <h1>Mon Profil</h1>
      {userDetails ? (
        <div>
          <h2>Détails Utilisateur</h2>
          <p>Nom d'utilisateur: {userDetails.username}</p>
          <p>Rôle: {userDetails.role}</p>
        </div>
      ) : (
        <p>Chargement des détails utilisateur...</p>
      )}

      <h2>Historique de Commande</h2>
      {orders.length > 0 ? (
        <ul>
          {orders.map(order => (
            <li key={order.id}>
              Commande #{order.id} - {order.orderDate} - Total: {order.total} €
              <ul>
                {order.items.map(item => (
                  <li key={item.productId}>
                    {item.productName} - Quantité: {item.quantity} - Prix: {item.price} €
                  </li>
                ))}
              </ul>
            </li>
          ))}
        </ul>
      ) : (
        <p>Aucune commande trouvée.</p>
      )}
    </div>
  );
}

export default UserProfile;
