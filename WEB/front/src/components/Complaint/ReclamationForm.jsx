"use client"

import { useState } from "react"

function ReclamationForm() {
    const [formData, setFormData] = useState({
        orderNumber: "",
        purchaseDate: "",
        name: "",
        email: "",
        phone: "",
        issueType: "",
        productName: "",
        description: "",
        photos: null,
    })

    const [submitted, setSubmitted] = useState(false)

    const handleChange = (e) => {
        const { name, value } = e.target
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }))
    }

    const handleFileChange = (e) => {
        setFormData((prev) => ({
            ...prev,
            photos: e.target.files,
        }))
    }

    const handleSubmit = async (e) => {
        e.preventDefault();

        const form = new FormData();
        form.append("data", new Blob([JSON.stringify({
            orderNumber: formData.orderNumber,
            purchaseDate: formData.purchaseDate,
            name: formData.name,
            email: formData.email,
            phone: formData.phone,
            issueType: formData.issueType,
            productName: formData.productName,
            description: formData.description,
        })], { type: "application/json" }));

        if (formData.photos) {
            for (let i = 0; i < formData.photos.length; i++) {
                form.append("photos", formData.photos[i]);
            }
        }

        const res = await fetch("http://localhost:8080/api/reclamations", {
            method: "POST",
            body: form
        });

        if (res.ok) {
            setSubmitted(true);
        }
    };

    return (
        <div className="max-w-3xl mx-auto p-4">
            <div className="mb-8 text-center">
                <h1 className="text-3xl font-bold mb-2">Product Reclamation</h1>
                <p className="text-gray-600">
                    If you've received a damaged product or your order is late, please fill out this form and we'll resolve your
                    issue as quickly as possible.
                </p>
            </div>

            {submitted ? (
                <div className="bg-green-50 border border-green-200 rounded-lg p-6 text-center">
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="48"
                        height="48"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        className="mx-auto text-green-500 mb-4"
                    >
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
                        <polyline points="22 4 12 14.01 9 11.01" />
                    </svg>
                    <h2 className="text-2xl font-bold text-green-700 mb-2">Reclamation Submitted!</h2>
                    <p className="text-green-600 mb-4">
                        Thank you for bringing this to our attention. Our customer service team will review your reclamation and
                        contact you within 24-48 hours.
                    </p>
                    <button
                        onClick={() => setSubmitted(false)}
                        className="bg-green-600 text-white px-6 py-2 rounded-md hover:bg-green-700 transition-colors"
                    >
                        Submit Another Reclamation
                    </button>
                </div>
            ) : (
                <form onSubmit={handleSubmit} className="bg-white shadow-md rounded-lg p-6">
                    <div className="mb-6">
                        <h2 className="text-xl font-semibold mb-4 pb-2 border-b">Order Information</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label htmlFor="orderNumber" className="block text-sm font-medium text-gray-700 mb-1">
                                    Order Number *
                                </label>
                                <input
                                    type="text"
                                    id="orderNumber"
                                    name="orderNumber"
                                    value={formData.orderNumber}
                                    onChange={handleChange}
                                    required
                                    placeholder="e.g. ORD-12345"
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                />
                            </div>

                            <div>
                                <label htmlFor="purchaseDate" className="block text-sm font-medium text-gray-700 mb-1">
                                    Purchase Date *
                                </label>
                                <input
                                    type="date"
                                    id="purchaseDate"
                                    name="purchaseDate"
                                    value={formData.purchaseDate}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                />
                            </div>
                        </div>
                    </div>

                    <div className="mb-6">
                        <h2 className="text-xl font-semibold mb-4 pb-2 border-b">Contact Information</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                            <div>
                                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">
                                    Full Name *
                                </label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                />
                            </div>

                            <div>
                                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                                    Email Address *
                                </label>
                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="phone" className="block text-sm font-medium text-gray-700 mb-1">
                                Phone Number
                            </label>
                            <input
                                type="tel"
                                id="phone"
                                name="phone"
                                value={formData.phone}
                                onChange={handleChange}
                                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                            />
                        </div>
                    </div>

                    <div className="mb-6">
                        <h2 className="text-xl font-semibold mb-4 pb-2 border-b">Issue Details</h2>

                        <div className="mb-4">
                            <label htmlFor="issueType" className="block text-sm font-medium text-gray-700 mb-1">
                                Type of Issue *
                            </label>
                            <select
                                id="issueType"
                                name="issueType"
                                value={formData.issueType}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                            >
                                <option value="">Select issue type</option>
                                <option value="damaged">Damaged Product</option>
                                <option value="defective">Defective Product</option>
                                <option value="wrong-item">Wrong Item Received</option>
                                <option value="missing-parts">Missing Parts</option>
                                <option value="late-delivery">Late Delivery</option>
                                <option value="not-delivered">Not Delivered</option>
                                <option value="other">Other</option>
                            </select>
                        </div>

                        <div className="mb-4">
                            <label htmlFor="productName" className="block text-sm font-medium text-gray-700 mb-1">
                                Product Name *
                            </label>
                            <input
                                type="text"
                                id="productName"
                                name="productName"
                                value={formData.productName}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                            />
                        </div>

                        <div className="mb-4">
                            <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
                                Describe the Issue *
                            </label>
                            <textarea
                                id="description"
                                name="description"
                                value={formData.description}
                                onChange={handleChange}
                                required
                                rows="4"
                                placeholder="Please provide details about the issue you're experiencing..."
                                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                            ></textarea>
                        </div>

                        <div>
                            <label htmlFor="photos" className="block text-sm font-medium text-gray-700 mb-1">
                                Upload Photos (Optional)
                            </label>
                            <input
                                type="file"
                                id="photos"
                                name="photos"
                                onChange={handleFileChange}
                                accept="image/*"
                                multiple
                                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                            />
                            <p className="text-xs text-gray-500 mt-1">You can upload up to 3 photos (5MB max each)</p>
                        </div>
                    </div>

                    <div className="flex justify-center">
                        <button
                            type="submit"
                            className="bg-red-600 text-white px-8 py-3 rounded-md hover:bg-red-700 transition-colors"
                        >
                            Submit Reclamation
                        </button>
                    </div>
                </form>
            )}
        </div>
    )
}

export default ReclamationForm
