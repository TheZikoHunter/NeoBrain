"use client"

import { useState } from "react"
import Navigation from "../Navigation/Navigation.jsx"
import Footer from "../HeroSection/Footer.jsx"

function JoinTeamForm() {
    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        position: "",
        experience: "",
        message: "",
        resume: null,
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
            resume: e.target.files[0],
        }))
    }

    const handleSubmit = (e) => {
        e.preventDefault()
        console.log("Form submitted:", formData)
        // Here you would typically send the data to your server
        // For now, we'll just simulate a successful submission
        setSubmitted(true)
    }

    return (
        <div className="min-h-screen flex flex-col">
            <main className="flex-grow py-12 bg-gray-50">
                <div className="container mx-auto px-4 md:px-6">
                    <div className="max-w-3xl mx-auto">
                        <div className="mb-10 text-center">
                            <h1 className="text-3xl md:text-4xl font-bold mb-4">Join Our Team</h1>
                            <p className="text-gray-600">
                                We're always looking for talented individuals to join our growing team. Fill out the form below to
                                apply.
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
                                <h2 className="text-2xl font-bold text-green-700 mb-2">Application Submitted!</h2>
                                <p className="text-green-600 mb-4">
                                    Thank you for your interest in joining our team. We'll review your application and get back to you
                                    soon.
                                </p>
                                <button
                                    onClick={() => setSubmitted(false)}
                                    className="bg-green-600 text-white px-6 py-2 rounded-md hover:bg-green-700 transition-colors"
                                >
                                    Submit Another Application
                                </button>
                            </div>
                        ) : (
                            <form onSubmit={handleSubmit} className="bg-white shadow-md rounded-lg p-6">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                                    <div>
                                        <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-1">
                                            First Name *
                                        </label>
                                        <input
                                            type="text"
                                            id="firstName"
                                            name="firstName"
                                            value={formData.firstName}
                                            onChange={handleChange}
                                            required
                                            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                        />
                                    </div>

                                    <div>
                                        <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-1">
                                            Last Name *
                                        </label>
                                        <input
                                            type="text"
                                            id="lastName"
                                            name="lastName"
                                            value={formData.lastName}
                                            onChange={handleChange}
                                            required
                                            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                        />
                                    </div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
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
                                    <label htmlFor="position" className="block text-sm font-medium text-gray-700 mb-1">
                                        Position You're Applying For *
                                    </label>
                                    <select
                                        id="position"
                                        name="position"
                                        value={formData.position}
                                        onChange={handleChange}
                                        required
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                    >
                                        <option value="">Select a position</option>
                                        <option value="developer">Developer</option>
                                        <option value="designer">Designer</option>
                                        <option value="marketing">Marketing Specialist</option>
                                        <option value="sales">Sales Representative</option>
                                        <option value="customer-support">Customer Support</option>
                                        <option value="other">Other</option>
                                    </select>
                                </div>

                                <div className="mb-6">
                                    <label htmlFor="experience" className="block text-sm font-medium text-gray-700 mb-1">
                                        Years of Experience *
                                    </label>
                                    <select
                                        id="experience"
                                        name="experience"
                                        value={formData.experience}
                                        onChange={handleChange}
                                        required
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                    >
                                        <option value="">Select experience level</option>
                                        <option value="0-1">0-1 years</option>
                                        <option value="1-3">1-3 years</option>
                                        <option value="3-5">3-5 years</option>
                                        <option value="5-10">5-10 years</option>
                                        <option value="10+">10+ years</option>
                                    </select>
                                </div>

                                <div className="mb-6">
                                    <label htmlFor="message" className="block text-sm font-medium text-gray-700 mb-1">
                                        Why do you want to join our team?
                                    </label>
                                    <textarea
                                        id="message"
                                        name="message"
                                        value={formData.message}
                                        onChange={handleChange}
                                        rows="4"
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                    ></textarea>
                                </div>

                                <div className="mb-8">
                                    <label htmlFor="resume" className="block text-sm font-medium text-gray-700 mb-1">
                                        Upload Resume (PDF, DOC, DOCX) *
                                    </label>
                                    <input
                                        type="file"
                                        id="resume"
                                        name="resume"
                                        onChange={handleFileChange}
                                        accept=".pdf,.doc,.docx"
                                        required
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-gray-400 focus:outline-none"
                                    />
                                    <p className="text-xs text-gray-500 mt-1">Maximum file size: 5MB</p>
                                </div>

                                <div className="flex justify-center">
                                    <button
                                        type="submit"
                                        className="bg-black text-white px-8 py-3 rounded-md hover:bg-gray-800 transition-colors"
                                    >
                                        Submit Application
                                    </button>
                                </div>
                            </form>
                        )}
                    </div>
                </div>
            </main>
        </div>
    )
}

export default JoinTeamForm
