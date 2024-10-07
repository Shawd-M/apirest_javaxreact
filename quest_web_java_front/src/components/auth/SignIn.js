import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from './authContext'; 
import 'bootstrap/dist/css/bootstrap.min.css';
import '../css/signin.css'; 

function SignIn() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (event) => {
        event.preventDefault();
        const response = await fetch('http://localhost:8090/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json();
            if (data.token && data.user) {
                login(data.token, { username: data.user.username, role: data.user.role });
                navigate('/');  // Rediriger après connexion
            } else {
                console.error('Login response is missing token or user details');
            }
        } else {
            console.error('Failed to log in');
        }
    };

    return (
        <div className="signin-page">
            <form onSubmit={handleSubmit} className="form-signin">
                <h1>Sign In</h1>
                <div className="form-label-group">
                    <input
                        type="text"
                        id="inputUsername"
                        className="form-control"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Nom d'utilisateur"
                        required
                    />
                    <label htmlFor="inputUsername">Nom d'utilisateur</label>
                </div>
                <div className="form-label-group">
                    <input
                        type="password"
                        id="inputPassword"
                        className="form-control"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Mot de passe"
                        required
                    />
                    <label htmlFor="inputPassword">Mot de passe</label>
                </div>
                <button className="btn btn-lg btn-primary btn-block btnSubmit" type="submit">Login</button>
                <p>Vous n'avez pas de compte ? Enregistrez-vous <Link to="/signup">ici !</Link></p>
            </form>
        </div>
    );
}

export default SignIn;
