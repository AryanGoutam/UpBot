import leftLogo from "../assets/Polygon 1.svg";
import centreLogo from "../assets/Brand mark.svg";
import rightLogo from  "../assets/Polygon 2.svg";

function Navbar(){

    return(
        <>
        <nav className="flex items-center justify-between p-2">

            {/* Logo */}
            <div className="flex items-center ">
                <img src={leftLogo} alt="Left logo" />
                <img src={centreLogo} alt="Centre logo" />
                <img src={rightLogo} alt="Right logo" />
                <span className="ml-2">UpBot</span>
            </div>

            {/* Navigation links */}
            <div className="flex items-center gap-8">
                <a href="#why">Why UpBot</a>
                <a href="#contact">Architecture</a>
                <a href="/signin">Sign In</a>
                <a href="/signup">Get Started</a>
            </div>

        </nav>
            
        </>
    )
}

export default Navbar;