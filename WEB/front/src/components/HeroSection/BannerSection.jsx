import Banner from "../../assets/banner.jpg"

function BannerSection() {
    return (
        <div className="relative w-full h-[500px] overflow-hidden">
            {/* Banner Image */}
            <div
                className="absolute inset-0 bg-cover bg-center bg-no-repeat w-full h-full"
                style={{ backgroundImage: `url(${Banner})` }}
            >
                {/* Dark Overlay */}
                <div className="absolute inset-0 bg-black/40"></div>
            </div>

            {/* Content - Positioned on the right */}
            <div className="relative z-10 container mx-auto h-full flex flex-col justify-center px-4 md:px-6">
                <div className="ml-auto mr-0 max-w-xl">
                    <h1 className="text-4xl md:text-5xl font-bold text-white mb-4">Special Offers Just For You</h1>
                    <p className="text-lg md:text-xl text-white/90 mb-8">
                        Discover our exclusive collection of premium products with special discounts.
                    </p>
                    <div className="flex flex-col sm:flex-row gap-4">
                        <button className="bg-white text-black hover:bg-white/90 font-medium px-6 py-3 rounded-md text-base flex items-center transition-colors">
                            Shop Now
                            <span className="ml-2">
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
                  <circle cx="8" cy="21" r="1" />
                  <circle cx="19" cy="21" r="1" />
                  <path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12" />
                </svg>
              </span>
                        </button>
                        <button className="border-2 border-white text-white hover:bg-white/10 font-medium px-6 py-3 rounded-md text-base flex items-center transition-colors">
                            View Offers
                            <span className="ml-2">
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
                  <path d="M5 12h14" />
                  <path d="m12 5 7 7-7 7" />
                </svg>
              </span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default BannerSection
