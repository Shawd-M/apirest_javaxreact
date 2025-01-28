import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faShoppingCart, faHeart, faPlus, faExpand } from '@fortawesome/free-solid-svg-icons';
import Cart from '../catalog/Cart';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../css/productcatalog.css'; 
import noImageAvailable from '../../public/image-non-disponible.jpg';
import { useCart } from './CartContext';

function ProductCatalog() {
    const [products, setProducts] = useState([]);
    const [cart, setCart] = useState([]);

    const { addToCart, handleCheckout } = useCart();

    const [selectedImage, setSelectedImage] = useState(null); // État pour l'image agrandie

    const purchaseInstant = (product) => {
        addToCart(product);
        handleCheckout();
    };



    useEffect(() => {
        fetch('http://localhost:8090/products')
            .then(response => response.json())
            .then(data => {
                if (Array.isArray(data)) {
                    setProducts(data);
                } else {
                    console.error('La réponse de l\'API n\'est pas un tableau:', data);
                }
            })
            .catch(error => console.error('Erreur lors de la récupération des produits:', error));
    }, []);

    const displayFull = (image) => {
        setSelectedImage(image); // Afficher l'image sélectionnée
    };

    const closeFullScreen = () => {
        setSelectedImage(null); // Fermer l'affichage plein écran
    };


    return (
        <div className="product-catalog-container">
            <div className="product-list-container">
                <div className="row">
                    {products.length > 0 ? (
                        products.map(product => (
                            <div key={product.id} className="col-md-6 col-lg-4 col-xl-3">
                                <div className="single-product">
                                    <div className="product-image-container">
                                        <img 
                                            src={`http://localhost:8090${product.imageUrl}`  || noImageAvailable} 
                                            alt={product.name} 
                                            className="product-image"
                                        />
                                        <div className="part-1">
                                            <ul>
                                                <li><a href="#"><FontAwesomeIcon icon={faShoppingCart} onClick={() => purchaseInstant(product)}/></a></li>
                                                <li><a href="#"><FontAwesomeIcon icon={faHeart} /></a></li>
                                                <li><a href="#"><FontAwesomeIcon icon={faPlus} onClick={() => addToCart(product)}/></a></li>
                                                <li>                                                    
                                                    <a href="#" onClick={(e) => { e.preventDefault(); displayFull(`http://localhost:8090${product.imageUrl}`); }}>
                                                        <FontAwesomeIcon icon={faExpand} />
                                                    </a></li>
                                            </ul>
                                        </div>
                                    </div>
                                    <h3 className="product-title">{product.name}</h3>
                                    <h4 className="product-price">{product.description}</h4>
                                    <h4 className="product-price">{product.price} €</h4>
                                    <h4 className="product-price">Stock : {product.stockQuantity}</h4>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>Aucun produit disponible.</p>
                    )}
                </div>
            </div>
            {selectedImage && (
                <div 
                    style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        width: '100vw',
                        height: '100vh',
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000,
                    }}
                    onClick={closeFullScreen}
                >
                    <img 
                        src={selectedImage} 
                        alt="Full Screen" 
                        style={{ maxHeight: '90%', maxWidth: '90%' }} 
                    />
                </div>
            )}
        </div>
    );
}

export default ProductCatalog;
