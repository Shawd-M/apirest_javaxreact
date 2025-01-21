import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faShoppingCart, faHeart, faPlus, faExpand } from '@fortawesome/free-solid-svg-icons';
import Cart from '../catalog/Cart';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../css/productcatalog.css'; 
import noImageAvailable from '../../public/image-non-disponible.jpg';

function ProductCatalog() {
    const [products, setProducts] = useState([]);
    const [cart, setCart] = useState([]);

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

    const addToCart = (product) => {
        setCart(prevCart => {
            const existingProduct = prevCart.find(item => item.id === product.id);
            if (existingProduct) {
                return prevCart.map(item =>
                    item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
                );
            } else {
                return [...prevCart, { ...product, quantity: 1 }];
            }
        });
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
                                                <li><a href="#"><FontAwesomeIcon icon={faShoppingCart} /></a></li>
                                                <li><a href="#"><FontAwesomeIcon icon={faHeart} /></a></li>
                                                <li><a href="#"><FontAwesomeIcon icon={faPlus} /></a></li>
                                                <li><a href="#"><FontAwesomeIcon icon={faExpand} /></a></li>
                                            </ul>
                                        </div>
                                    </div>
                                    <h3 className="product-title">{product.name}</h3>
                                    <h4 className="product-price">{product.description}</h4>
                                    <h4 className="product-price">{product.price} €</h4>
                                    <h4 className="product-price">Stock : {product.stockQuantity}</h4>
                                    <button onClick={() => addToCart(product)}>Ajouter au panier</button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>Aucun produit disponible.</p>
                    )}
                </div>
            </div>
            {/* <div className="cart-container"> */}
                {/* <Cart cart={cart} /> */}
            {/* </div> */}
        </div>
    );
}

export default ProductCatalog;
