import React, { useEffect, useState } from 'react';
import noImageAvailable from '../../public/image-non-disponible.jpg';
import '../css/productcatalog.css'; 

function SellerProductManagement() {
    const [products, setProducts] = useState([]);
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [price, setPrice] = useState('');
    const [stockQuantity, setStockQuantity] = useState('');
    const [image, setImage] = useState(null);
    const [editingProductId, setEditingProductId] = useState(null);

    useEffect(() => {
        fetch('http://localhost:8090/products')
            .then(response => response.json())
            .then(data => setProducts(data))
            .catch(error => console.error('Erreur lors de la récupération des produits:', error));
    }, []);

    const handleAddProduct = (e) => {
        e.preventDefault();
        const newProduct = { name, description, price, stockQuantity };

        fetch('http://localhost:8090/products/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            },
            body: JSON.stringify(newProduct)
        })
            .then(response => response.json())
            .then(data => {
                setProducts([...products, data]);
                if (image) {
                    handleUploadImage(data.id, image);
                }
                setName('');
                setDescription('');
                setPrice('');
                setStockQuantity('');
                setImage(null);
            })
            .catch(error => console.error('Erreur lors de l\'ajout du produit:', error));
    };

    const handleUploadImage = (productId, image) => {
        const formData = new FormData();
        formData.append('image', image);

        fetch(`http://localhost:8090/products/${productId}/image`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            },
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                setProducts(products.map(product => (product.id === productId ? data : product)));
            })
            .catch(error => console.error('Erreur lors de l\'upload de l\'image:', error));
    };

    const handleDeleteProduct = (id) => {
        fetch(`http://localhost:8090/products/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            }
        })
            .then(() => {
                setProducts(products.filter(product => product.id !== id));
            })
            .catch(error => console.error('Erreur lors de la suppression du produit:', error));
    };

    const handleUpdateProduct = (id) => {
        const updatedProduct = { name, description, price, stockQuantity };

        fetch(`http://localhost:8090/products/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            },
            body: JSON.stringify(updatedProduct)
        })
            .then(response => response.json())
            .then(data => {
                setProducts(products.map(product => (product.id === id ? data : product)));
                if (image) {
                    handleUploadImage(id, image);
                }
                setEditingProductId(null); // Stop editing
                setName('');
                setDescription('');
                setPrice('');
                setStockQuantity('');
                setImage(null);
            })
            .catch(error => console.error('Erreur lors de la mise à jour du produit:', error));
    };

    const startEditingProduct = (product) => {
        setEditingProductId(product.id);
        setName(product.name);
        setDescription(product.description);
        setPrice(product.price);
        setStockQuantity(product.stockQuantity);
        setImage(null);
    };

    return (
        <div className="container mt-4">
            <h1>Gestion des Produits</h1>
            <form onSubmit={handleAddProduct} className="mb-4">
                <div className="mb-3">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Nom"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <input
                        type="number"
                        className="form-control"
                        placeholder="Prix"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <input
                        type="number"
                        className="form-control"
                        placeholder="Quantité en Stock"
                        value={stockQuantity}
                        onChange={(e) => setStockQuantity(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <input
                        type="file"
                        className="form-control"
                        onChange={(e) => setImage(e.target.files[0])}
                    />
                </div>
                <button type="submit" className="btn btn-primary">Ajouter Produit</button>
            </form>
            <div className="overflow-hidden card table-nowrap table-card">
                <div className="card-header d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">Liste des Produits</h5>
                </div>
                <div className="table-responsive">
                    <table className="table mb-0">
                        <thead className="small text-uppercase bg-body text-muted">
                            <tr>
                                <th>Image</th>
                                <th>Nom</th>
                                <th>Description</th>
                                <th>Prix</th>
                                <th>Quantité</th>
                                <th className="text-end">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {products.map(product => (
                                <tr className="align-middle" key={product.id}>
                                    <td>
                                        <div className="product-image-container">
                                            <img 
                                                src={product.imageUrl || noImageAvailable} 
                                                alt={product.name} 
                                                className="product-image"
                                            />
                                        </div>
                                    </td>
                                    <td>{product.id === editingProductId ? (
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={name}
                                            onChange={(e) => setName(e.target.value)}
                                        />
                                    ) : (
                                        product.name
                                    )}</td>
                                    <td>{product.id === editingProductId ? (
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={description}
                                            onChange={(e) => setDescription(e.target.value)}
                                        />
                                    ) : (
                                        product.description
                                    )}</td>
                                    <td>{product.id === editingProductId ? (
                                        <input
                                            type="number"
                                            className="form-control"
                                            value={price}
                                            onChange={(e) => setPrice(e.target.value)}
                                        />
                                    ) : (
                                        `${product.price} €`
                                    )}</td>
                                    <td>{product.id === editingProductId ? (
                                        <input
                                            type="number"
                                            className="form-control"
                                            value={stockQuantity}
                                            onChange={(e) => setStockQuantity(e.target.value)}
                                        />
                                    ) : (
                                        product.stockQuantity
                                    )}</td>
                                    <td className="text-end">
                                        <div className="btn-group" role="group">
                                            {product.id === editingProductId ? (
                                                <button className="btn btn-success btn-sm" onClick={() => handleUpdateProduct(product.id)}>Sauvegarder</button>
                                            ) : (
                                                <button className="btn btn-primary btn-sm" onClick={() => startEditingProduct(product)}>Modifier</button>
                                            )}
                                            <button className="btn btn-danger btn-sm" onClick={() => handleDeleteProduct(product.id)}>Supprimer</button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default SellerProductManagement;
