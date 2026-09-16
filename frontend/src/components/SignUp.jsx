import axios from "axios";
import  { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

const SignUp = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
  });
  const navigate = useNavigate();

  const baseUrl = import.meta.env.VITE_BASE_URL;

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

const handleSubmit = async (e) => {
  e.preventDefault();

  try {
     await axios.post(
      `${baseUrl}/api/auth/register`,
      formData
    );

    // console.log(response.data);

    toast.success("Account created successfully!");

    navigate("/login");
    toast.success("Please Log in with your credentials");


  } catch (error) {
    console.error(error);

    toast.error(
      error.response?.data?.message || "Error creating account"
    );
  }
};

  return (
    <div className="min-h-screen bg-[#EAE6DC] flex items-center justify-center px-4">
      <div className="w-full max-w-md">

        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-[#292722]">
            Create an Account
          </h1>

          <p className="mt-2 text-[#6F6A5F]">
            Sign up to get started
          </p>
        </div>

        {/* Signup Card */}
        <div className="bg-white rounded-2xl shadow-lg p-8">

          <form onSubmit={handleSubmit} className="space-y-5">

            {/* Name */}
            <div>
              <label
                htmlFor="name"
                className="block text-sm font-medium text-[#292722] mb-2"
              >
                Name
              </label>

              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Enter your name"
                required
                className="w-full px-4 py-3 rounded-lg border border-[#D4CFC3]
                           bg-[#FAF9F6] text-[#292722]
                           outline-none transition
                           focus:border-[#292722] focus:ring-1 focus:ring-[#292722]"
              />
            </div>

            {/* Email */}
            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-[#292722] mb-2"
              >
                Email
              </label>

              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Enter your email"
                required
                className="w-full px-4 py-3 rounded-lg border border-[#D4CFC3]
                           bg-[#FAF9F6] text-[#292722]
                           outline-none transition
                           focus:border-[#292722] focus:ring-1 focus:ring-[#292722]"
              />
            </div>

            {/* Password */}
            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-[#292722] mb-2"
              >
                Password
              </label>

              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter your password"
                required
                className="w-full px-4 py-3 rounded-lg border border-[#D4CFC3]
                           bg-[#FAF9F6] text-[#292722]
                           outline-none transition
                           focus:border-[#292722] focus:ring-1 focus:ring-[#292722]"
              />
            </div>

            {/* Signup Button */}
            <button
              type="submit"
              className="w-full py-3 rounded-lg
                         bg-[#292722] text-white font-semibold
                         hover:bg-[#3D3A34]
                         transition duration-200
                         cursor-pointer"
            >
              Sign Up
            </button>
          </form>

          {/* Login */}
          <p className="text-center text-sm text-[#6F6A5F] mt-6">
            Already a customer?{" "}
            <Link
              to="/login"
              className="font-semibold text-[#292722] hover:underline"
            >
              Log in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default SignUp;