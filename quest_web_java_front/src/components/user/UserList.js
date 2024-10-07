import React, { useEffect, useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import Modal from 'react-modal';
import '../css/userlist.css';
import { useAuth } from '../auth/authContext';
//Modal.setAppElement('#root');

function UserList() {
    const [users, setUsers] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);
    const { userId, username, role } = useParams();
    const {user: currentUserbis } = useAuth();

    const [user, setUser] = useState({});
    const [editedUser, setEditedUser] = useState({});
    const [editedAddress, setEditedAddress] = useState({});
    const [addressId, setAddressId] = useState(null);
    const [currentUserId, setCurrentUserId] = useState(null);

    const roleCurrentUser = localStorage.getItem("role");
    let usernameCurrentUser = localStorage.getItem("username");
    localStorage.setItem('userInfo', JSON.stringify({ id: userId, username: username, role: role }));

    const [modalIsOpen, setModalIsOpen] = useState(false);
    const [userIdToDelete, setUserIdToDelete] = useState(null);
    const navigate = useNavigate();

    const [searchTerm, setSearchTerm] = useState('');



    useEffect(() => {
        const authToken = localStorage.getItem('authToken');
        const fetchUsers = async () => {
            const response = await fetch('http://localhost:8090/user', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                }
            });
            const data = await response.json();
            setUsers(data);
            setCurrentUser(roleCurrentUser);

        };
        fetchUsers();
    }, []);

    const handleDeleteUser = async (userId) => {
        try {
            const response = await fetch(`http://localhost:8090/user/${userId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                }
            });

            if (response.ok) {
                setUsers(users.filter(user => user.id !== userId));
            } else {
                console.error("Erreur lors de la suppression de l'utilisateur'");
            }
        } catch (error) {
            console.error("Erreur lors de la suppression de l'utilisateur", error);
        }
    };

    const openModal = (userId) => {
        setUserIdToDelete(userId);
        setModalIsOpen(true);
    };

    const closeModal = () => {
        setModalIsOpen(false);
    };

    const confirmDelete = async () => {
        if (userIdToDelete) {
            await handleDeleteUser(userIdToDelete);
            closeModal();
        }
    };

    const filteredUsers = users.filter((user) =>
    user.username.toLowerCase().includes(searchTerm.toLowerCase())
);

    return (
      <div className="container">
      <div className="row">
          <div className="col-12 mb-3 mb-lg-5">
              <div className="overflow-hidden card table-nowrap table-card">
                  <div className="card-header d-flex justify-content-between align-items-center">
                      <h5 className="mb-0">Utilisateurs</h5>
                      <a href="#!" className="btn btn-light btn-sm">Voir Tout</a>
                  </div>
                  <div className="mb-3">
                      <input
                          type="text"
                          className="form-control"
                          placeholder="Rechercher un utilisateur..."
                          value={searchTerm}
                          onChange={(e) => setSearchTerm(e.target.value)}
                      />
                  </div>
                  <div className="table-responsive">
                      <table className="table mb-0">
                                <thead className="small text-uppercase bg-body text-muted">
                                    <tr>
                                        <th>ID</th>
                                        <th>Nom d'utilisateur</th>
                                        <th>Rôle</th>
                                        <th className="text-end">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {Array.isArray(filteredUsers) && filteredUsers.length > 0 ? (
                                        filteredUsers.map(user => (
                                            <tr key={user.id} className="align-middle">
                                              <td>{user.id}</td>
                                                <td>
                                                    <div className="d-flex align-items-center">
                                                        <img src="https://bootdey.com/img/Content/avatar/avatar1.png" className="avatar sm rounded-pill me-3 flex-shrink-0" alt="User" />
                                                        <div>
                                                            <div className="h6 mb-0 lh-1">
                                                                <Link to={`/user/${user.id}`}>{user.username}</Link>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>{user.role}</td>
                                                <td className="text-end">
                                                    {(roleCurrentUser === 'ROLE_ADMIN' ||  currentUserbis.username === user.username) && (
                                                        <div className="dropdown">
                                                            <button className="btn p-1 dropdown-toggle" type="button" id={`dropdownMenuButton-${user.id}`} data-bs-toggle="dropdown" aria-expanded="false">
                                                                <i className="fa fa-bars" aria-hidden="true"></i>
                                                            </button>
                                                            <ul className="dropdown-menu dropdown-menu-end" aria-labelledby={`dropdownMenuButton-${user.id}`}>
                                                              <li>
                                                                  <Link className="dropdown-item" to={`/user/${user.id}`}>
                                                                      Voir les Détails
                                                                  </Link>
                                                              </li>
                                                              <li>
                                                                  <button className="dropdown-item" onClick={() => openModal(user.id)}>
                                                                      Supprimer
                                                                  </button>
                                                              </li>
                                                          </ul>
                                                        </div>
                                                    )}
                                                </td>
                                            </tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan="4">Aucun utilisateur trouvé</td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <Modal
                isOpen={modalIsOpen}
                onRequestClose={closeModal}
                contentLabel="Confirmation de suppression"
            >
                <h2>Confirmation</h2>
                <p>Êtes-vous sûr de vouloir supprimer cet utilisateur ?</p>
                <button className="btn btn-danger" onClick={confirmDelete}>Oui, supprimer</button>
                <button className="btn btn-secondary" onClick={closeModal}>Non, annuler</button>
            </Modal>
        </div>
    );
}

export default UserList;
