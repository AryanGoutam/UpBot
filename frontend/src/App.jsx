// import { useState } from 'react'
// import './App.css'
import SignIn from "./components/SignIn";
import WhyUpBot from "./components/WhyUpBot";
import "./index.css";
import Home from "./pages/Home";
import { BrowserRouter, Route, Routes } from "react-router-dom";

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/sign-in" element = {<SignIn />} />
          <Route path="/why" element = {<WhyUpBot /> } />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
