import React from 'react';
import WishlistIcon from "../icons/WishlistIcon.jsx";
import UserIcon from "../icons/UserIcon.jsx";
import CartIcon from "../icons/CartIcon.jsx";

const Navigation = () => {
    return (
        <nav className="flex items-center py-5 px-16 justify-between gap-40 font-poppins">
                {/*Logo*/}
            <div className="flex items-center gap-6">
                <a className="text-3xl text-black font-bold gap-8" href="/">NeoBrain</a>
            </div>

            <div className="flex flex-wrap items-center gap-8 flex-1">
                {/*Navigation items*/}
                <ul className="flex gap-14">
                    <li className="text-gray-500 hover:text-black"><a href="/products">Shop</a></li>
                    <li className="text-gray-500 hover:text-black"><a href="/categories">Categories</a></li>
                    <li className="text-gray-500 hover:text-black"><a href="/">Offers</a></li>
                    <li className="text-gray-500 hover:text-black"><a href="/">Best Sellers</a></li>
                </ul>
            </div>
            <div className="flex justify-center">
                {/*Search bar*/}
                <div className="relative">
                    <span className="absolute top-0 left-0 bottom-0 flex items-center px-3">

                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor"
                         className="h-4 w-4 text-slate-400">
                            <path fillRule="evenodd"
                                  d="M9.965 11.026a5 5 0 1 1 1.06-1.06l2.755 2.754a.75.75 0 1 1-1.06 1.06l-2.755-2.754ZM10.5 7a3.5 3.5 0 1 1-7 0 3.5 3.5 0 0 1 7 0Z"
                                  clipRule="evenodd"/>
                        </svg>
                    </span>
                        <input type="text" className="border text-sm rounded-md border-solid border-slate-300 pl-8 pr-4 py-2 outline-none focus:ring-2 focus:ring-slate-200/50" placeholder="Search..." />
                </div>
            </div>

            <div className="flex flex-wrap items-center gap-4">
                {/*Action items*/}
                <ul className="flex items-center space-x-8">
                    <li><a><WishlistIcon className="size-5 text-slate-800 hover:fill-black cursor-pointer"></WishlistIcon></a></li>
                    <li><a href="/login"><UserIcon className="size-5 text-slate-800 hover:fill-black cursor-pointer"></UserIcon></a></li>
                    <li><a><CartIcon className="size-5 text-slate-800 hover:fill-black cursor-pointer"></CartIcon></a></li>
                </ul>
            </div>
        </nav>
    );
};

export default Navigation;