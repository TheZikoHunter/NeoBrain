"use client"

import { useState, useEffect } from "react"

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

    // Simulate fetching products from an API
    useEffect(() => {
        // In a real app, you would fetch from an API
        // For now, we'll use the newArrivals data and expand it
        const fetchedProducts = [
            {
                id: 5,
                name: "Ergonomic Desk Lamp",
                price: 79.99,
                image: "/placeholder.svg?height=300&width=300",
                category: "Home & Kitchen",
                isNew: false,
            },
            {
                id: 6,
                name: "Wireless Gaming Mouse",
                price: 59.99,
                image: "/placeholder.svg?height=300&width=300",
                category: "Electronics",
                isNew: false,
                discount: 10,
            },
            {
                id: 7,
                name: "Leather Wallet",
                price: 45.99,
                image: "/placeholder.svg?height=300&width=300",
                category: "Fashion",
                isNew: false,
            },
            {
                id: 8,
                name: "Stainless Steel Water Bottle",
                price: 24.99,
                image: "/placeholder.svg?height=300&width=300",
                category: "Sports",
                isNew: false,
            },
            {
                id: 9,
                name: "Bluetooth Speaker",
                price: 89.99,
                image: "/placeholder.svg?height=300&width=300",
                category: "Electronics",
                isNew: true,
            },
            {
                id: 10,
                name: "Yoga Mat",
                price: 29.99,
                image: "/placeholder.svg?height=300&width=300",
                category: "Sports",
                isNew: false,
                discount: 15,
            },
            {
                id: 11,
                name: "Coffee Maker",
                price: 119.99,
                image: "/placeholder.svg?height=300&width=300",
                category: "Home & Kitchen",
                isNew: false,
            },
            {
                id: 12,
                name: "Skincare Set",
                price: 49.99,
                image: "/placeholder.svg?height=300&width=300",
                category: "Beauty",
                isNew: true,
            },
        ]

        // Extract unique categories
        const uniqueCategories = [...new Set(fetchedProducts.map((product) => product.category))]

        setProducts(fetchedProducts)
        setFilteredProducts(fetchedProducts)
        setCategories(uniqueCategories)
        setLoading(false)
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
                result.sort((a, b) => (a.isNew === b.isNew ? 0 : a.isNew ? -1 : 1))
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
                                    <div className="relative">
                                        {product.discount && (
                                            <span className="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded">
                        {product.discount}% OFF
                      </span>
                                        )}
                                        {product.isNew && !product.discount && (
                                            <span className="absolute top-2 left-2 bg-green-500 text-white text-xs font-bold px-2 py-1 rounded">
                        NEW
                      </span>
                                        )}
                                        <img
                                            src={product.image || "/placeholder.svg"}
                                            alt={product.name}
                                            className="w-full h-64 object-cover group-hover:scale-105 transition-transform duration-300"
                                        />
                                        <button className="absolute bottom-4 left-1/2 -translate-x-1/2 bg-black text-white font-medium py-2 px-4 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                                            Add to Cart
                                        </button>
                                    </div>
                                    <div className="p-4">
                                        <span className="text-sm text-gray-500">{product.category}</span>
                                        <h3 className="font-medium text-gray-900 mt-1">{product.name}</h3>
                                        <div className="flex items-center justify-between mt-2">
                                            <p className="font-bold text-gray-900">${product.price.toFixed(2)}</p>
                                            <div className="flex items-center">
                                                <button className="text-gray-400 hover:text-red-500 transition-colors">
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
                                </div>
                            ))}
                        </div>
                    )}

                    {/* Pagination */}
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
