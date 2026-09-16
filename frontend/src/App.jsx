// import { useState } from 'react'
// import './App.css'
import Dashboard from "./components/Dashboard";
import Login from "./components/LogIn";
import ProtectedRoute from "./components/security/ProtectRoute";
import SignUp from "./components/SignUp";
import "./index.css";
import Home from "./pages/Home";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { ToastContainer } from "react-toastify";

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/sign-up" element = {<SignUp />} />
          <Route path="/login" element = {<Login /> } />
          <Route path="/dashboard" element = {
            
            <ProtectedRoute>
              <Dashboard />

            </ProtectedRoute>
            

          } /> 
         
        </Routes>
      </BrowserRouter>
      <ToastContainer />  
    </>
  );
}

export default App;
