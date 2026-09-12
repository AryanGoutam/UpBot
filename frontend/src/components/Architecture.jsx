function Architecture() {
  return (
    <section
      id="architecture"
      className="min-h-screen px-6 py-4 text-white"
    >

      <div className="mx-auto max-w-6xl">

        {/* ================= HEADER ================= */}

        <div className="mb-12 text-center">

          <p className="mb-2 text-sm font-medium tracking-[0.3em] text-gray-400">
            UNDER THE HOOD
          </p>

          <h2 className="text-3xl font-semibold text-[#f6f4f0] md:text-4xl">
            Built like production infrastructure.
          </h2>

          <p className="mx-auto mt-4 max-w-2xl text-sm leading-6 text-gray-400">
            Every check runs through the same pipeline — scheduled, executed
            by independent workers, analyzed, and resolved through multiple
            monitoring stages.
          </p>

        </div>


        {/* ================= ARCHITECTURE DIAGRAM ================= */}

        <div className="rounded-xl border border-[#172938] bg-[#050D15] p-8 md:p-12">

          <div className="mx-auto flex max-w-lg flex-col items-center">

            <ArchitectureBox>
              Scheduler
            </ArchitectureBox>

            <Arrow />

            <ArchitectureBox>
              Distributed Workers
            </ArchitectureBox>

            <Arrow />

            <ArchitectureBox>
              Health Checks
            </ArchitectureBox>

            <Arrow />

            {/* Workers */}

            <div className="flex gap-3 md:gap-5">

              <ArchitectureBox small>
                Worker 1
              </ArchitectureBox>

              <ArchitectureBox small>
                Worker 2
              </ArchitectureBox>

              <ArchitectureBox small>
                Worker 3
              </ArchitectureBox>

            </div>

            <Arrow />

            <ArchitectureBox>
              Incident Detection
            </ArchitectureBox>

            <Arrow />

            <ArchitectureBox>
              PostgreSQL + Redis
            </ArchitectureBox>

            <Arrow />

            <ArchitectureBox>
              REST API
            </ArchitectureBox>

          </div>

        </div>


        {/* ================= INFO CARDS ================= */}

        <div className="mt-3 grid gap-4 md:grid-cols-3">

          <InfoCard
            icon="⌁"
            title="Distributed workers"
            text="Multiple workers execute checks independently, so one slow check never blocks the rest of your monitoring system."
          />

          <InfoCard
            icon="◉"
            title="Intelligent incident detection"
            text="Failures are evaluated over time to distinguish temporary network issues from real incidents."
          />

          <InfoCard
            icon="⌁"
            title="Real-time updates"
            text="Monitoring results flow continuously through UpBot so dashboards always reflect the latest service state."
          />

        </div>


        {/* ================= SECOND SECTION ================= */}

        <div className="mt-28">

          <div className="mb-12 text-center">

            <p className="mb-2 text-sm font-medium tracking-[0.3em] text-[#f6f4f0]">
              FROM HEALTHY TO RESOLVED
            </p>

            <h2 className="text-3xl font-semibold text-[#f6f4f0] md:text-4xl">
              Four states every service moves through.
            </h2>

            <p className="mx-auto mt-4 max-w-2xl text-sm leading-6 text-[#f6f4f0]">
              Every API and website your team monitors follows the same
              lifecycle. UpBot shows you exactly which state it is in.
            </p>

          </div>


          {/* ================= STATUS CARDS ================= */}

          <div className="grid gap-4 md:grid-cols-4">

            <StatusCard
              number="01"
              icon="✦"
              title="OPERATIONAL"
              subtitle="Healthy service"
              description="All monitored services are responding normally, with stable latency and uptime."
            />

            <StatusCard
              number="02"
              icon="◆"
              title="DEGRADED"
              subtitle="Response slow"
              description="Response times spike or errors rise while the service is still available."
            />

            <StatusCard
              number="03"
              icon="✦"
              title="MAJOR OUTAGE"
              subtitle="Service failure"
              description="A monitored service goes down, and UpBot begins the incident response."
            />

            <StatusCard
              number="04"
              icon="◈"
              title="RESOLVED"
              subtitle="Incident recovered"
              description="Service is back to normal and UpBot automatically marks the incident resolved."
            />

          </div>

        </div>

      </div>

    </section>
  );
}


/* =========================================================
   ARCHITECTURE BOX
========================================================= */

function ArchitectureBox({ children, small = false }) {
  return (
    <div
      className={`
        flex items-center justify-center
        rounded-md
        border border-[#294052]
        bg-[#07131F]
        px-4
        text-sm
        text-gray-300
        ${small ? "h-10 w-24" : "h-10 w-44"}
      `}
    >
      {children}
    </div>
  );
}


/* =========================================================
   ARROW
========================================================= */

function Arrow() {
  return (
    <div className="flex h-8 flex-col items-center">

      <div className="h-5 w-px bg-[#294052]" />

      <div className="text-sm text-gray-500">
        ↓
      </div>

    </div>
  );
}


/* =========================================================
   INFO CARD
========================================================= */

function InfoCard({ icon, title, text }) {
  return (
    <div className="rounded-lg border border-[#172938] bg-[#081521] p-5">

      <div className="mb-4 text-lg text-[#C9B777]">
        {icon}
      </div>

      <h3 className="mb-2 text-xl font-semibold text-[#E8D7A5]">
        {title}
      </h3>

      <p className="text-sm leading-6 text-gray-400">
        {text}
      </p>

    </div>
  );
}


/* =========================================================
   STATUS CARD
========================================================= */

function StatusCard({
  number,
  icon,
  title,
  subtitle,
  description,
}) {
  return (
    <div
      className="
        group
        relative
        rounded-lg
        border
        border-[#172938]
        bg-[#081521]
        p-5
        transition
        duration-300
        hover:-translate-y-1
        hover:border-[#294052]
      "
    >

      {/* Number */}

      <span className="absolute right-4 top-4 text-xs text-gray-600">
        {number}
      </span>


      {/* Icon */}

      <div className="mb-5 text-lg text-[#C9B777]">
        {icon}
      </div>


      {/* Title */}

      <h3 className="text-sm font-semibold tracking-wider text-[#C9B777]">
        {title}
      </h3>


      {/* Subtitle */}

      <p className="mt-2 text-sm text-gray-300">
        {subtitle}
      </p>


      {/* Description */}

      <p className="mt-3 text-sm leading-6 text-gray-400">
        {description}
      </p>

    </div>
  );
}


export default Architecture;