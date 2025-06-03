import Categories from "../../data/Category.json";

function Category() {
    // Sample category data - replace with your actual data
    return (
        <section className="py-12 bg-gray-50">
            <div className="container mx-auto px-4 md:px-6">
                <div className="flex flex-col items-center mb-10">
                    <h2 className="text-3xl font-bold text-gray-900 mb-2">Shop by Category</h2>
                    <p className="text-gray-600 text-center max-w-2xl">
                        Browse our wide selection of products across popular categories
                    </p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
                    {Categories.map((category) => (
                        <a
                            key={category.id}
                            href="#"
                            className="group bg-white rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow duration-300"
                        >
                            <div className="relative h-40 overflow-hidden">
                                <img
                                    src={category.image || "/placeholder.svg"}
                                    alt={category.name}
                                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                />
                                <div className="absolute inset-0 bg-black/20 group-hover:bg-black/30 transition-colors duration-300"></div>
                            </div>
                            <div className="p-4 text-center">
                                <h3 className="font-semibold text-gray-900">{category.name}</h3>
                                <p className="text-sm text-gray-500 mt-1">{category.itemCount} items</p>
                            </div>
                        </a>
                    ))}
                </div>
            </div>
        </section>
    )
}

export default Category
