import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/authContext';
import '../css/userdetail.css';

function UserDetail() {
    const [user, setUser] = useState({});
    const [isEditing, setIsEditing] = useState(false);
    const [isEditingAddress, setIsEditingAddress] = useState(false);
    const [editedUser, setEditedUser] = useState({});
    const [editedAddress, setEditedAddress] = useState({});
    const { user: currentUser, authToken, login, logout } = useAuth();
    const [addressId, setAddressId] = useState(null); 
    const [street, setStreet] = useState('');
    const [postalCode, setPostalCode] = useState('');
    const [city, setCity] = useState('');
    const [country, setCountry] = useState('');
    const navigate = useNavigate();
    const [currentUserId, setCurrentUserId] = useState(null);
    const { userId } = useParams();
    const { userIdco } = useAuth();

    useEffect(() => {
        const fetchUserDetails = async () => {
            const response = await fetch(`http://localhost:8090/user/${userId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                }
            });
    
            if (response.ok) {
                const data = await response.json();
                setUser({
                    ...data,
                    address: data.address || []
                });
                setEditedUser(data);
    
                const storedId = localStorage.getItem('userIdco');  // Utilisation du bon item localStorage
                if (storedId) {
                    setCurrentUserId(parseInt(storedId));
                }
    
                if (data.address && data.address.length > 0) {
                    setEditedAddress(data.address[0]);
                    setAddressId(data.address[0].id);
                } else {
                    console.log('No address data available for this user');
                    setAddressId(null);
                    setEditedAddress({});
                }
    
    
            } else {
                console.error('Failed to fetch user details');
            }
        };
    
        fetchUserDetails();
    }, [userId, authToken, currentUser]);

    const handleEditAddress = (address) => {
        setEditedAddress(address);
        setIsEditingAddress(true);
    };

    const handleSaveAddress = async () => {
        if (!addressId) {
            console.error('Address ID is not defined');
            return;
        }

        const response = await fetch(`http://localhost:8090/address/${addressId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(editedAddress) 
        });

        if (response.ok) {
            const updatedAddress = await response.json();
            setUser(prevUser => ({
                ...prevUser,
                address: prevUser.address.map(a => a.id === addressId ? updatedAddress : a)
            }));
            setIsEditingAddress(false);
        } else {
            console.error('Failed to update address');
        }
    };

    const handleAddAddress = async (event) => {
        event.preventDefault();
        const response = await fetch('http://localhost:8090/address', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ street, postalCode, city, country })
        });
        if (response.ok) {
            const addedAddress = await response.json(); 
            setUser(prevUser => ({
                ...prevUser,
                address: [...(prevUser.address || []), addedAddress] 
            }));
            console.log('Adresse ajoutée!');
            setStreet('');
            setPostalCode('');
            setCity('');
            setCountry('');
        } else {
            console.error("Erreur lors de l'ajout de l'adresse");
        }
    };

    const handleDeleteAddress = async (addressId) => {
        if (!addressId) {
            console.error('Address ID is not defined');
            return;
        }

        const response = await fetch(`http://localhost:8090/address/${addressId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            setUser(prevUser => ({
                ...prevUser,
                address: prevUser.address.filter(a => a.id !== addressId)
            }));
            console.log('Adresse supprimée!');
        } else {
            console.error('Failed to delete address');
        }
    };

    const handleSaveUser = async () => {
        try {
            const response = await fetch(`http://localhost:8090/user/${userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(editedUser)
            });

            if (response.ok) {
                const data = await response.json();
                const newToken = data.token;
                setUser(data.user);
                setIsEditing(false);
                login(newToken, data.user);
                navigate('/user');
            } else {
                console.error('Erreur lors de la mise à jour de l\'utilisateur');
            }
        } catch (error) {
            console.error('Erreur lors de la mise à jour de l\'utilisateur', error);
        }
    };

    const handleChange = (e) => {
        setEditedUser({ ...editedUser, [e.target.name]: e.target.value });
    };

    const handleAddressChange = (e) => {
        setEditedAddress({ ...editedAddress, [e.target.name]: e.target.value });
    };

    if (!user) {
        return <div>Chargement...</div>;
    }

    return (
        <div className="container-xl px-4 mt-4">
            <nav className="nav nav-borders">
                <a className="nav-link active ms-0" href="https://www.bootdey.com/snippets/view/bs5-edit-profile-account-details" target="__blank">Profile</a>
                <a className="nav-link" href="https://www.bootdey.com/snippets/view/bs5-profile-billing-page" target="__blank">Billing</a>
                <a className="nav-link" href="https://www.bootdey.com/snippets/view/bs5-profile-security-page" target="__blank">Security</a>
                <a className="nav-link" href="https://www.bootdey.com/snippets/view/bs5-edit-notifications-page" target="__blank">Notifications</a>
            </nav>
            <hr className="mt-0 mb-4" />
            <div className="row">
                <div className="col-xl-4">
                    <div className="card mb-4 mb-xl-0">
                        <div className="card-header">Profile Picture</div>
                        <div className="card-body text-center">
                            <img className="img-account-profile rounded-circle mb-2" src="http://bootdey.com/img/Content/avatar/avatar1.png" alt="" />
                            <div className="small font-italic text-muted mb-4">JPG or PNG no larger than 5 MB</div>
                        </div>
                    </div>
                </div>
                <div className="col-xl-8">
                    <div className="card mb-4">
                        <div className="card-header">Détails Utilisateur</div>
                        <div className="card-body">
                            <form>
                                <div className="mb-3">
                                    <label className="small mb-1" htmlFor="inputUserId">ID</label>
                                    <p>Id: {user.id}</p>
                                </div>
                                <div className="mb-3">
                                    <label className="small mb-1" htmlFor="inputUsername">Username</label>
                                    {isEditing ? (
                                        <input
                                            className="form-control"
                                            id="inputUsername"
                                            type="text"
                                            name="username"
                                            value={editedUser.username}
                                            onChange={handleChange}
                                            placeholder="Enter your username"
                                        />
                                    ) : (
                                        <p>{user.username}</p>
                                    )}
                                </div>
                                <div className="row gx-3 mb-3">
                                    <div className="col-md-6">
                                        <label className="small mb-1" htmlFor="inputRole">Role</label>
                                        {isEditing && currentUser?.role === 'ROLE_ADMIN' ? (
                                            <input
                                                className="form-control"
                                                id="inputRole"
                                                type="text"
                                                name="role"
                                                value={editedUser.role}
                                                onChange={handleChange}
                                                placeholder="Only admin can change"
                                            />
                                        ) : (
                                            <p>{user.role}</p>
                                        )}
                                    </div>
                                </div>
                                {(currentUser?.role === 'ROLE_ADMIN' || currentUser.username === user.username) && (
                                    <button
                                        className="btn btn-primary"
                                        type="button"
                                        onClick={() => setIsEditing(!isEditing)}
                                    >
                                        {isEditing ? 'Annuler' : 'Editer utilisateur'}
                                    </button>
                                )}
                                {isEditing && (
                                    <button
                                        className="btn btn-primary"
                                        type="button"
                                        onClick={handleSaveUser}
                                    >
                                        Sauvegarder utilisateur
                                    </button>
                                )}
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <div className="container">
                <div className="row">
                    <div className="col-12 mb-3 mb-lg-5">
                        <div className="overflow-hidden card table-nowrap table-card">
                            <div className="card-header d-flex justify-content-between align-items-center">
                                <h5 className="mb-0">Adresses</h5>
                            </div>
                            <div className="table-responsive">
                                <table className="table mb-0">
                                    <thead className="small text-uppercase bg-body text-muted">
                                        <tr>
                                            <th>Street</th>
                                            <th>Postal Code</th>
                                            <th>City</th>
                                            <th>Country</th>
                                            <th className="text-end">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {user.address && (currentUser?.role === 'ROLE_ADMIN' || currentUser.username === user.username) && user.address.map((address) => (
                                            <tr className="align-middle" key={address.id}>
                                                <td>{address.street}</td>
                                                <td>{address.postalCode}</td>
                                                <td>{address.city}</td>
                                                <td>{address.country}</td>
                                                <td className="text-end">
                                                    <div className="btn-group" role="group">
                                                        <button className="btn btn-primary btn-sm" onClick={() => handleEditAddress(address)}>Éditer</button>
                                                        <button className="btn btn-danger btn-sm" onClick={() => handleDeleteAddress(address.id)}>Supprimer</button>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div>
                {(currentUser?.role === 'ROLE_ADMIN' || currentUser.username === user.username) ? (
                    <form onSubmit={handleAddAddress} className="mt-4">
                        <h3>Ajouter une adresse</h3>
                        <div className="mb-3">
                            <label className="form-label" htmlFor="inputStreet">Rue</label>
                            <input
                                className="form-control"
                                id="inputStreet"
                                type="text"
                                value={street}
                                onChange={(e) => setStreet(e.target.value)}
                                placeholder="Rue"
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label className="form-label" htmlFor="inputPostalCode">Code Postal</label>
                            <input
                                className="form-control"
                                id="inputPostalCode"
                                type="text"
                                value={postalCode}
                                onChange={(e) => setPostalCode(e.target.value)}
                                placeholder="Code Postal"
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label className="form-label" htmlFor="inputCity">Ville</label>
                            <input
                                className="form-control"
                                id="inputCity"
                                type="text"
                                value={city}
                                onChange={(e) => setCity(e.target.value)}
                                placeholder="Ville"
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label className="form-label" htmlFor="inputCountry">Pays</label>
                            <input
                                className="form-control"
                                id="inputCountry"
                                type="text"
                                value={country}
                                onChange={(e) => setCountry(e.target.value)}
                                placeholder="Pays"
                                required
                            />
                        </div>
                        <button className="btn btn-primary" type="submit">Ajouter une adresse</button>
                    </form>
                ) : (
                    <p>Vous ne pouvez pas ajouter d'adresse aux autres utilisateurs.</p>
                )}
            </div>
        </div>
    );
}

export default UserDetail;