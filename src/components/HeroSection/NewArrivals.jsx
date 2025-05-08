import newProducts from "../../data/NewProducts.json"

function NewArrivals() {
    return (
        <section className="py-12">
            <div className="container mx-auto px-4 md:px-6">
                <div className="flex justify-between items-center mb-8">
                    <div>
                        <h2 className="text-3xl font-bold text-gray-900">New Arrivals</h2>
                        <p className="text-gray-600 mt-2">Check out our latest products</p>
                    </div>
                    <a href="/products" className="text-gray-900 font-medium hover:underline flex items-center">
                        View All
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
                            className="ml-2"
                        >
                            <path d="M5 12h14" />
                            <path d="m12 5 7 7-7 7" />
                        </svg>
                    </a>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {newProducts.map((product) => (
                        <div key={product.id} className="group">
                            <div className="relative bg-gray-100 rounded-lg overflow-hidden mb-3">
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
                                <div className="absolute inset-0 bg-black/0 group-hover:bg-black/10 transition-colors duration-300"></div>
                                <button className="absolute bottom-4 left-1/2 -translate-x-1/2 bg-white text-gray-900 font-medium py-2 px-4 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center">
                                    Add to Cart
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        width="16"
                                        height="16"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        strokeWidth="2"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        className="ml-2"
                                    >
                                        <circle cx="8" cy="21" r="1" />
                                        <circle cx="19" cy="21" r="1" />
                                        <path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12" />
                                    </svg>
                                </button>
                            </div>
                            <div>
                                <span className="text-sm text-gray-500">{product.category}</span>
                                <h3 className="font-medium text-gray-900 mt-1">{product.name}</h3>
                                <p className="font-bold text-gray-900 mt-1">${product.price.toFixed(2)}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    )
}

export default NewArrivals
