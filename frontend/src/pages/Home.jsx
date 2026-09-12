import Architecture from "../components/Architecture";
import Hero from "../components/Hero";
import Navbar from "../components/Navbar";
import WhyUpBot from "../components/WhyUpBot";


function Home() {


  return (
    <>
    <div className="min-h-screen bg-[#EAE6DC] p-8 ">
        <Navbar /> 
        <Hero />
        <WhyUpBot />
        <Architecture />
    </div>
      
    </>
  )
}

export default Home;
