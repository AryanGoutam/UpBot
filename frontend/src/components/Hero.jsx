import { Link } from "react-router-dom";

function Hero(){
    return(
        <>
            <section className="min-h-[85vh] flex flex-col items-center text-center px-6 pt-20">

            {/* Eyebrow */}
            <p className="text-sm text-green-400 mb-6">
                — Real-time website monitoring
            </p>

            {/* Main heading */}
            <h1 className="text-6xl md:text-8xl font-semibold tracking-tight max-w-5xl">
                Monitor every service 
                <br />
                you business depend on.
            </h1>

            {/* Description */}
            <p className="mt-8 max-w-2xl text-lg text-gray-400">
                UpBot continuously monitors your websites from multiple
                locations and alerts you the moment latency rises or
                a service goes down.
            </p>

            {/* Buttons */}
            <div className="mt-10 flex gap-4">

                {/* take this get started to sign up page */}
                <Link
                    to="/sign-up"
                    className="px-6 py-3 bg-white text-black rounded-lg hover:bg-gray-200 transition"
                >
                    Get started
                </Link>

                <a
                    href="#architecture"
                    className="px-6 py-3 border border-gray-700 rounded-lg hover:bg-gray-900 transition"
                >
                    See how it works
                </a>
            </div>

            

        </section>
        </>
    )
}

export default Hero;