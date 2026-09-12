import leftLogo from "../assets/Polygon 1.svg";
import centreLogo from "../assets/Brand mark.svg";
import rightLogo from  "../assets/Polygon 2.svg";
import { Link } from "react-router-dom";

function Navbar(){

    return(
        <>
        <nav className="flex items-center justify-between p-2">

            {/* Logo */}
            <div className="flex items-center ">
                <img src={leftLogo} alt="Left logo" />
                <img src={centreLogo} alt="Centre logo" />
                <img src={rightLogo} alt="Right logo" />
                <span className="ml-2 text-[#f6f4f0] font-bold text-2xl">UpBot</span>
            </div>

            {/* Navigation links */}
            <div className="flex items-center gap-8  text-[#f6f4f0] ">
                <a href="#why-upbot">Why UpBot</a>
                <a href="#architecture">Architecture</a>
                
                <Link to = "/sign-in"> Sign In</Link>
                <Link to = "/sign-up">Get Started </Link>
            </div>

        </nav>
            
        </>
    )
}

export default Navbar;