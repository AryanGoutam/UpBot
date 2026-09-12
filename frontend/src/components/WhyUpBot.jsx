function WhyUpBot() {
  const cards = [
    {
      title: "The Lighthouse",
      icon: "🚨",
      text: "A lighthouse doesn't prevent storms — it watches the horizon and warns ships before danger arrives. UpBot does the same for your websites, APIs, and services.",
    },
    {
      title: "The Storm",
      icon: "🌩️",
      text: "Small signals become major incidents. A slow API response today can become tomorrow's outage. Upbot catches latency spikes and failures before they reach your users.",
    },
    {
      title: "Safe Harbor",
      icon: "⛵",
      text: "Every incident tells a story. Upbot tracks the full lifecycle — from healthy operation to failure and recovery — so your team knows exactly what happened and how fast you responded.",
    },
  ];

  return (
    <section className="min-h-screen  px-6 py-20 " id="why-upbot">

      {/* Heading */}
      <div className="mx-auto max-w-4xl text-center">

        <p className="mb-2 text-sm font-medium tracking-[0.3em] text-gray-400">
          WHY "UPBOT"?
        </p>

        <h2 className="text-3xl font-semibold text-[#f6f4f0] md:text-4xl">
          Inspired by how lighthouses guide ships through storms.
        </h2>

      </div>


      {/* Cards */}
      <div className="mx-auto mt-12 grid max-w-6xl gap-5 md:grid-cols-3">

        {cards.map((card) => (
          <div
            key={card.title}
            className="
              rounded-xl
              bg-[#3A2E09]
              px-6
              py-7
              text-center
              transition
              duration-300
              hover:-translate-y-1
              hover:bg-[#45370C]
            "
          >

            {/* Icon */}
            <div
              className="
                mx-auto
                mb-5
                flex
                h-20
                w-20
                items-center
                justify-center
                rounded-full
                bg-[#07131F]
                text-4xl
              "
            >
              {card.icon}
            </div>


            {/* Card title */}
            <h3 className="mb-3 text-xl font-semibold text-[#f6f4f0]">
              {card.title}
            </h3>


            {/* Card description */}
            <p className="text-sm leading-6 text-[#f6f4f0]">
              {card.text}
            </p>

          </div>
        ))}

      </div>

    </section>
  );
}

export default WhyUpBot;