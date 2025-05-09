"use client"

import { useState, useEffect } from "react"
import { useParams, Link } from "react-router-dom"
import Navigation from "../components/Navigation/Navigation.jsx"
import Footer from "../components/HeroSection/Footer.jsx"

function ProductDetailPage() {
    const { productId } = useParams()
    const [product, setProduct] = useState(null)
    const [loading, setLoading] = useState(true)
    const [quantity, setQuantity] = useState(1)
    const [activeTab, setActiveTab] = useState("description")

    useEffect(() => {
        const fetchProductDetails = async () => {
            setLoading(true)
            try {
                // Fetch the specific product
                const res = await fetch(`http://localhost:8080/api/products/${productId}`)
                const productData = await res.json()

                setProduct(productData)
            } catch (error) {
                console.error("Error fetching product details:", error)
            } finally {
                setLoading(false)
            }
        }

        fetchProductDetails()

        // Scroll to top when product changes
        window.scrollTo(0, 0)
    }, [productId])

    const handleQuantityChange = (e) => {
        const value = Number.parseInt(e.target.value)
        if (value > 0) {
            setQuantity(value)
        }
    }

    const handleAddToCart = () => {
        // Add your cart functionality here
        alert(`Added ${quantity} ${product.name}(s) to cart`)
    }

    if (loading) {
        return (
            <div>
                <Navigation />
                <div className="container mx-auto px-4 py-16 flex justify-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-gray-900"></div>
                </div>
                <Footer />
            </div>
        )
    }

    if (!product) {
        return (
            <div>
                <Navigation />
                <div className="container mx-auto px-4 py-16 text-center">
                    <h2 className="text-2xl font-bold text-gray-900 mb-4">Product Not Found</h2>
                    <p className="text-gray-600 mb-8">The product you're looking for doesn't exist or has been removed.</p>
                    <Link to="/products" className="bg-black text-white px-6 py-3 rounded-md hover:bg-gray-800 transition-colors">
                        Back to Products
                    </Link>
                </div>
                <Footer />
            </div>
        )
    }

    return (
        <div>
            <Navigation />
            <div className="bg-gray-50 py-12">
                <div className="container mx-auto px-4">
                    {/* Breadcrumbs */}
                    <div className="mb-8">
                        <nav className="flex text-sm">
                            <Link to="/" className="text-gray-500 hover:text-gray-900">
                                Home
                            </Link>
                            <span className="mx-2 text-gray-500">/</span>
                            <Link to="/products" className="text-gray-500 hover:text-gray-900">
                                Products
                            </Link>
                            <span className="mx-2 text-gray-500">/</span>
                            <Link to={`/products?category=${product.category}`} className="text-gray-500 hover:text-gray-900">
                                {product.category}
                            </Link>
                            <span className="mx-2 text-gray-500">/</span>
                            <span className="text-gray-900">{product.name}</span>
                        </nav>
                    </div>

                    {/* Product Details */}
                    <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 p-6">
                            {/* Product Image */}
                            <div className="relative">
                                {product.discount && (
                                    <span className="absolute top-4 left-4 bg-red-500 text-white text-sm font-bold px-2 py-1 rounded z-10">
                    {product.discount}% OFF
                  </span>
                                )}
                                {product.new && (
                                    <span className="absolute top-4 left-4 bg-green-500 text-white text-sm font-bold px-2 py-1 rounded z-10">
                    NEW
                  </span>
                                )}
                                <div className="bg-gray-100 rounded-lg overflow-hidden">
                                    <img
                                        src={product.image || "/placeholder.svg"}
                                        alt={product.name}
                                        className="w-full h-auto object-contain aspect-square"
                                    />
                                </div>
                            </div>

                            {/* Product Info */}
                            <div className="flex flex-col">
                                <h1 className="text-3xl font-bold text-gray-900 mb-2">{product.name}</h1>
                                <div className="flex items-center mb-4">
                                    <div className="flex items-center">
                                        {[1, 2, 3, 4, 5].map((star) => (
                                            <svg
                                                key={star}
                                                xmlns="http://www.w3.org/2000/svg"
                                                className={`h-5 w-5 ${star <= (product.rating || 4) ? "text-yellow-400" : "text-gray-300"}`}
                                                viewBox="0 0 20 20"
                                                fill="currentColor"
                                            >
                                                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                                            </svg>
                                        ))}
                                    </div>
                                    <span className="text-gray-600 ml-2">
                    {product.reviews ? `${product.reviews.length} reviews` : "No reviews yet"}
                  </span>
                                </div>

                                <div className="mb-6">
                                    <p className="text-3xl font-bold text-gray-900">
                                        ${product.price.toFixed(2)}
                                        {product.discount && (
                                            <span className="ml-2 text-lg text-gray-500 line-through">
                        ${((product.price * 100) / (100 - product.discount)).toFixed(2)}
                      </span>
                                        )}
                                    </p>
                                    {product.discount && (
                                        <p className="text-green-600 mt-1">
                                            You save: ${((product.price * product.discount) / (100 - product.discount)).toFixed(2)}
                                        </p>
                                    )}
                                </div>

                                <p className="text-gray-600 mb-6">
                                    {product.description || "No description available for this product."}
                                </p>

                                <div className="flex items-center mb-6">
                                    <div className="mr-4">
                                        <label htmlFor="quantity" className="block text-sm font-medium text-gray-700 mb-1">
                                            Quantity
                                        </label>
                                        <div className="flex items-center">
                                            <button
                                                onClick={() => quantity > 1 && setQuantity(quantity - 1)}
                                                className="px-3 py-1 border border-gray-300 rounded-l-md bg-gray-100 text-gray-600 hover:bg-gray-200"
                                            >
                                                -
                                            </button>
                                            <input
                                                type="number"
                                                id="quantity"
                                                name="quantity"
                                                min="1"
                                                value={quantity}
                                                onChange={handleQuantityChange}
                                                className="w-16 px-3 py-1 border-t border-b border-gray-300 text-center focus:outline-none"
                                            />
                                            <button
                                                onClick={() => setQuantity(quantity + 1)}
                                                className="px-3 py-1 border border-gray-300 rounded-r-md bg-gray-100 text-gray-600 hover:bg-gray-200"
                                            >
                                                +
                                            </button>
                                        </div>
                                    </div>

                                    <div className="flex-grow">
                                        <button
                                            onClick={handleAddToCart}
                                            className="w-full bg-black text-white py-3 px-6 rounded-md hover:bg-gray-800 transition-colors flex items-center justify-center"
                                        >
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                className="h-5 w-5 mr-2"
                                                viewBox="0 0 20 20"
                                                fill="currentColor"
                                            >
                                                <path d="M3 1a1 1 0 000 2h1.22l.305 1.222a.997.997 0 00.01.042l1.358 5.43-.893.892C3.74 11.846 4.632 14 6.414 14H15a1 1 0 000-2H6.414l1-1H14a1 1 0 00.894-.553l3-6A1 1 0 0017 3H6.28l-.31-1.243A1 1 0 005 1H3z" />
                                            </svg>
                                            Add to Cart
                                        </button>
                                    </div>
                                </div>

                                <div className="border-t border-gray-200 pt-6">
                                    <div className="flex items-center text-sm text-gray-600">
                                        <div className="flex items-center mr-6">
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                className="h-5 w-5 mr-1 text-green-500"
                                                viewBox="0 0 20 20"
                                                fill="currentColor"
                                            >
                                                <path
                                                    fillRule="evenodd"
                                                    d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                                                    clipRule="evenodd"
                                                />
                                            </svg>
                                            In Stock
                                        </div>
                                        <div className="flex items-center">
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                className="h-5 w-5 mr-1 text-gray-500"
                                                viewBox="0 0 20 20"
                                                fill="currentColor"
                                            >
                                                <path d="M8 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM15 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
                                                <path d="M3 4a1 1 0 00-1 1v10a1 1 0 001 1h1.05a2.5 2.5 0 014.9 0H10a1 1 0 001-1v-1H3V5h11v5h1V5a1 1 0 00-1-1H3zM14 7h5a1 1 0 011 1v5a1 1 0 01-1 1h-5a1 1 0 01-1-1V8a1 1 0 011-1z" />
                                            </svg>
                                            Free Shipping
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Tabs: Description, Specifications, Reviews */}
                        <div className="border-t border-gray-200">
                            <div className="flex border-b border-gray-200">
                                <button
                                    className={`px-6 py-3 text-sm font-medium ${
                                        activeTab === "description"
                                            ? "border-b-2 border-black text-black"
                                            : "text-gray-500 hover:text-gray-700"
                                    }`}
                                    onClick={() => setActiveTab("description")}
                                >
                                    Description
                                </button>
                                <button
                                    className={`px-6 py-3 text-sm font-medium ${
                                        activeTab === "specifications"
                                            ? "border-b-2 border-black text-black"
                                            : "text-gray-500 hover:text-gray-700"
                                    }`}
                                    onClick={() => setActiveTab("specifications")}
                                >
                                    Specifications
                                </button>
                                <button
                                    className={`px-6 py-3 text-sm font-medium ${
                                        activeTab === "reviews" ? "border-b-2 border-black text-black" : "text-gray-500 hover:text-gray-700"
                                    }`}
                                    onClick={() => setActiveTab("reviews")}
                                >
                                    Reviews
                                </button>
                            </div>

                            <div className="p-6">
                                {activeTab === "description" && (
                                    <div>
                                        <p className="text-gray-600">
                                            {product.description || "No detailed description available for this product."}
                                        </p>
                                    </div>
                                )}

                                {activeTab === "specifications" && (
                                    <div>
                                        {product.specifications ? (
                                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                                {Object.entries(product.specifications).map(([key, value]) => (
                                                    <div key={key} className="flex">
                                                        <span className="font-medium text-gray-900 w-1/3">{key}:</span>
                                                        <span className="text-gray-600 w-2/3">{value}</span>
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <p className="text-gray-600">No specifications available for this product.</p>
                                        )}
                                    </div>
                                )}

                                {activeTab === "reviews" && (
                                    <div>
                                        {product.reviews && product.reviews.length > 0 ? (
                                            <div className="space-y-6">
                                                {product.reviews.map((review) => (
                                                    <div key={review.id} className="border-b border-gray-200 pb-6">
                                                        <div className="flex items-center mb-2">
                                                            <div className="flex items-center">
                                                                {[1, 2, 3, 4, 5].map((star) => (
                                                                    <svg
                                                                        key={star}
                                                                        xmlns="http://www.w3.org/2000/svg"
                                                                        className={`h-4 w-4 ${star <= review.rating ? "text-yellow-400" : "text-gray-300"}`}
                                                                        viewBox="0 0 20 20"
                                                                        fill="currentColor"
                                                                    >
                                                                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                                                                    </svg>
                                                                ))}
                                                            </div>
                                                            <span className="ml-2 text-sm font-medium text-gray-900">{review.user}</span>
                                                        </div>
                                                        <p className="text-gray-600">{review.comment}</p>
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <div>
                                                <p className="text-gray-600 mb-4">This product has no reviews yet.</p>
                                                <button className="text-black font-medium hover:underline">
                                                    Be the first to write a review
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <Footer />
        </div>
    )
}

export default ProductDetailPage
