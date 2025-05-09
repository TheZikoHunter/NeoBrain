"use client"

import { useState, useEffect } from "react"
import { Link } from "react-router-dom"

function Products() {
    const [products, setProducts] = useState([])
    const [filteredProducts, setFilteredProducts] = useState([])
    const [categories, setCategories] = useState([])
    const [loading, setLoading] = useState(true)
    const [filters, setFilters] = useState({
        category: "all",
        priceRange: "all",
        sortBy: "featured",
    })

    // Fetch products from API
    useEffect(() => {
        const fetchProducts = async () => {
            try {
                console.log("Fetching products from API")
                const res = await fetch("http://localhost:8080/api/products")
                const data = await res.json()

                console.log("Products received:", data)

                if (!Array.isArray(data)) {
                    throw new Error("Expected an array of products from API")
                }

                const uniqueCategories = [...new Set(data.map((product) => product.category))]

                setProducts(data)
                setFilteredProducts(data)
                setCategories(uniqueCategories)
            } catch (error) {
                console.error("Error fetching products:", error)
            } finally {
                setLoading(false)
            }
        }

        fetchProducts()
    }, [])

    // Apply filters and sorting
    useEffect(() => {
        let result = [...products]

        // Filter by category
        if (filters.category !== "all") {
            result = result.filter((product) => product.category === filters.category)
        }

        // Filter by price range
        if (filters.priceRange !== "all") {
            switch (filters.priceRange) {
                case "under50":
                    result = result.filter((product) => product.price < 50)
                    break
                case "50to100":
                    result = result.filter((product) => product.price >= 50 && product.price <= 100)
                    break
                case "over100":
                    result = result.filter((product) => product.price > 100)
                    break
                default:
                    break
            }
        }

        // Sort products
        switch (filters.sortBy) {
            case "priceLow":
                result.sort((a, b) => a.price - b.price)
                break
            case "priceHigh":
                result.sort((a, b) => b.price - a.price)
                break
            case "newest":
                result.sort((a, b) => (a.new === b.new ? 0 : a.new ? -1 : 1))
                break
            default:
                // Featured - no specific sorting
                break
        }

        setFilteredProducts(result)
    }, [filters, products])

    const handleFilterChange = (e) => {
        const { name, value } = e.target
        setFilters((prev) => ({
            ...prev,
            [name]: value,
        }))
    }

    return (
        <div className="min-h-screen flex flex-col">
            <main className="flex-grow py-8 bg-gray-50">
                <div className="container mx-auto px-4 md:px-6">
                    <div className="mb-8">
                        <h1 className="text-3xl font-bold text-gray-900 mb-2">All Products</h1>
                        <p className="text-gray-600">Browse our collection of high-quality products</p>
                    </div>

                    {/* Filters and Sorting */}
                    <div className="bg-white p-4 rounded-lg shadow-sm mb-8">
                        <div className="flex flex-col md:flex-row gap-4 md:items-center md:justify-between">
                            <div className="flex flex-col sm:flex-row gap-4">
                                <div>
                                    <label htmlFor="category" className="block text-sm font-medium text-gray-700 mb-1">
                                        Category
                                    </label>
                                    <select
                                        id="category"
                                        name="category"
                                        value={filters.category}
                                        onChange={handleFilterChange}
                                        className="w-full sm:w-40 px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                    >
                                        <option value="all">All Categories</option>
                                        {categories.map((category) => (
                                            <option key={category} value={category}>
                                                {category}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div>
                                    <label htmlFor="priceRange" className="block text-sm font-medium text-gray-700 mb-1">
                                        Price Range
                                    </label>
                                    <select
                                        id="priceRange"
                                        name="priceRange"
                                        value={filters.priceRange}
                                        onChange={handleFilterChange}
                                        className="w-full sm:w-40 px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                    >
                                        <option value="all">All Prices</option>
                                        <option value="under50">Under $50</option>
                                        <option value="50to100">$50 - $100</option>
                                        <option value="over100">Over $100</option>
                                    </select>
                                </div>
                            </div>

                            <div>
                                <label htmlFor="sortBy" className="block text-sm font-medium text-gray-700 mb-1">
                                    Sort By
                                </label>
                                <select
                                    id="sortBy"
                                    name="sortBy"
                                    value={filters.sortBy}
                                    onChange={handleFilterChange}
                                    className="w-full sm:w-40 px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                >
                                    <option value="featured">Featured</option>
                                    <option value="priceLow">Price: Low to High</option>
                                    <option value="priceHigh">Price: High to Low</option>
                                    <option value="newest">Newest First</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    {/* Product Grid */}
                    {loading ? (
                        <div className="flex justify-center items-center h-64">
                            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-gray-900"></div>
                        </div>
                    ) : filteredProducts.length === 0 ? (
                        <div className="bg-white p-8 rounded-lg shadow-sm text-center">
                            <h3 className="text-xl font-semibold text-gray-800 mb-2">No products found</h3>
                            <p className="text-gray-600">Try adjusting your filters to find what you're looking for.</p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                            {filteredProducts.map((product) => (
                                <div
                                    key={product.id}
                                    className="group bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-300"
                                >
                                    <Link to={`/product/${product.id}`} className="block">
                                        <div className="relative">
                                            {product.discount && (
                                                <span className="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded">
                          {product.discount}% OFF
                        </span>
                                            )}
                                            {product.new && (
                                                <span className="absolute top-2 left-2 bg-green-500 text-white text-xs font-bold px-2 py-1 rounded">
                          NEW
                        </span>
                                            )}
                                            <img
                                                src={product.image || "/placeholder.svg"}
                                                alt={product.name}
                                                className="w-full h-64 object-cover group-hover:scale-105 transition-transform duration-300"
                                            />
                                        </div>
                                        <div className="p-4">
                                            <span className="text-sm text-gray-500">{product.category}</span>
                                            <h3 className="font-medium text-gray-900 mt-1">{product.name}</h3>
                                            <div className="flex items-center justify-between mt-2">
                                                <p className="font-bold text-gray-900">${product.price.toFixed(2)}</p>
                                                <div className="flex items-center">
                                                    <button
                                                        className="text-gray-400 hover:text-red-500 transition-colors"
                                                        onClick={(e) => {
                                                            e.preventDefault()
                                                            e.stopPropagation()
                                                            // Add wishlist functionality here
                                                        }}
                                                    >
                                                        <svg
                                                            xmlns="http://www.w3.org/2000/svg"
                                                            width="20"
                                                            height="20"
                                                            viewBox="0 0 24 24"
                                                            fill="none"
                                                            stroke="currentColor"
                                                            strokeWidth="2"
                                                            strokeLinecap="round"
                                                            strokeLinejoin="round"
                                                        >
                                                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                                                        </svg>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </Link>
                                </div>
                            ))}
                        </div>
                    )}

                    {/* Pagination - simplified version */}
                    <div className="flex justify-center mt-12">
                        <nav className="flex items-center gap-1">
                            <button className="px-3 py-1 border rounded-md text-gray-600 hover:bg-gray-100">Previous</button>
                            <button className="px-3 py-1 border rounded-md bg-black text-white">1</button>
                            <button className="px-3 py-1 border rounded-md text-gray-600 hover:bg-gray-100">2</button>
                            <button className="px-3 py-1 border rounded-md text-gray-600 hover:bg-gray-100">3</button>
                            <button className="px-3 py-1 border rounded-md text-gray-600 hover:bg-gray-100">Next</button>
                        </nav>
                    </div>
                </div>
            </main>
        </div>
    )
}

export default Products
