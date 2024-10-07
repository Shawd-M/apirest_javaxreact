import React, { useState } from 'react';
import { Link } from 'react-router-dom';


function SignUp() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();
        const response = await fetch('http://localhost:8090/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });
        if (response.ok) {
            console.log('Registration successful');
        } else {
            console.error('Failed to register');
        }
    };

    return (
        <div className="signin-page">
            <form onSubmit={handleSubmit} className="form-signin">
                <h1>Sign Up</h1>
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
                <button className="btn btn-lg btn-primary btn-block btnSubmit" type="submit">Register</button>
                <p>Vous avez déjà un compte ? Connectez vous <Link to={`/signin`}>ici !</Link></p>
            </form>
        </div>
    );
}

export default SignUp;
