"use client"

import { useState, useEffect } from "react"

function Categories() {
    const [categories, setCategories] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const res = await fetch("http://localhost:8080/api/categories")
                const data = await res.json()

                if (!Array.isArray(data)) {
                    throw new Error("Expected an array of categories from API")
                }

                setCategories(data)
            } catch (error) {
                console.error("Error fetching categories:", error)
                setError("Failed to load categories. Please try again later.")
            } finally {
                setLoading(false)
            }
        }

        fetchCategories()
    }, [])

    return (
        <div className="min-h-screen flex flex-col">
            <main className="flex-grow py-8 bg-gray-50">
                <div className="container mx-auto px-4 md:px-6">
                    <div className="mb-8">
                        <h1 className="text-3xl font-bold text-gray-900 mb-2">Shop by Category</h1>
                        <p className="text-gray-600">Browse our product categories to find what you're looking for</p>
                    </div>

                    {loading ? (
                        <div className="flex justify-center items-center h-64">
                            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-gray-900"></div>
                        </div>
                    ) : error ? (
                        <div className="bg-white p-8 rounded-lg shadow-sm text-center">
                            <h3 className="text-xl font-semibold text-gray-800 mb-2">Error</h3>
                            <p className="text-gray-600">{error}</p>
                        </div>
                    ) : categories.length === 0 ? (
                        <div className="bg-white p-8 rounded-lg shadow-sm text-center">
                            <h3 className="text-xl font-semibold text-gray-800 mb-2">No categories found</h3>
                            <p className="text-gray-600">Check back later for new categories.</p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                            {categories.map((category) => (
                                <a
                                    key={category.id}
                                    href={`/products?category=${category.name}`}
                                    className="group bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-300"
                                >
                                    <div className="relative">
                                        <div className="aspect-[3/2] relative overflow-hidden">
                                            <img
                                                src={category.image || "/placeholder.svg"}
                                                alt={category.name}
                                                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                            />
                                        </div>
                                        <div className="absolute inset-0 bg-black bg-opacity-30 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                                            <span className="text-white font-medium px-4 py-2 rounded-md">View Products</span>
                                        </div>
                                    </div>
                                    <div className="p-4">
                                        <div className="flex items-center justify-between">
                                            <h3 className="font-medium text-gray-900 capitalize">{category.name}</h3>
                                            <span className="bg-gray-100 text-gray-700 text-sm px-2 py-1 rounded-full">
                        {category.items} {category.items === 1 ? "item" : "items"}
                      </span>
                                        </div>
                                    </div>
                                </a>
                            ))}
                        </div>
                    )}
                </div>
            </main>
        </div>
    )
}

export default Categories
