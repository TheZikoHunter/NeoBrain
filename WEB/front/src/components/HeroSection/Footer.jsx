function Footer() {
    return (
        <footer className="bg-black text-white py-12">
            <div className="container mx-auto px-4 md:px-6">
                <div className="text-center grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
                    {/* Need Help Column */}
                    <div>
                        <h3 className="text-xl font-bold mb-6">Need Help</h3>
                        <ul className="space-y-4">
                            <li>
                                <a href="#" className="hover:text-gray-300 transition-colors">
                                    Contact Us
                                </a>
                            </li>
                            <li>
                                <a href="#" className="hover:text-gray-300 transition-colors">
                                    Track Order
                                </a>
                            </li>
                            <li>
                                <a href="#" className="hover:text-gray-300 transition-colors">
                                    FAQ's
                                </a>
                            </li>
                            <li>
                                <a href="/reclamation" className="hover:text-gray-300 transition-colors">
                                    Complaints
                                </a>
                            </li>

                        </ul>
                    </div>

                    {/* Company Column */}
                    <div>
                        <h3 className="text-xl font-bold mb-6">Company</h3>
                        <ul className="space-y-4">
                            <li>
                                <a href="#" className="hover:text-gray-300 transition-colors">
                                    About Us
                                </a>
                            </li>
                            <li>
                                <a href="#" className="hover:text-gray-300 transition-colors">
                                    Media
                                </a>
                            </li>
                            <li>
                                <a href="/join-team" className="hover:text-gray-300 transition-colors">Join Us</a>
                            </li>
                        </ul>
                    </div>

                    {/* More Info Column */}
                    <div>
                        <h3 className="text-xl font-bold mb-6">More Info</h3>
                        <ul className="space-y-4">
                            <li>
                                <a href="#" className="hover:text-gray-300 transition-colors">
                                    Term and Conditions
                                </a>
                            </li>
                            <li>
                                <a href="#" className="hover:text-gray-300 transition-colors">
                                    Privacy Policy
                                </a>
                            </li>
                            <li>
                                <a href="#" className="hover:text-gray-300 transition-colors">
                                    Shipping Policy
                                </a>
                            </li>
                        </ul>
                    </div>

                    {/* Location Column */}
                    <div>
                        <h3 className="text-xl font-bold mb-6">Location</h3>
                        <p className="text-gray-300">Rabat, Morocco</p>
                    </div>
                </div>

                <div className="border-t border-gray-800 mt-12 pt-8 text-center text-gray-400">
                    <p>Copyright © {new Date().getFullYear()} NeoBrain</p>
                </div>
            </div>
        </footer>
    )
}

export default Footer
