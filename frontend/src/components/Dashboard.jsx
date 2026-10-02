import { useState, useEffect } from "react";
import axios from "axios";

const Dashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showAddMonitor, setShowAddMonitor] = useState(false);

  const [monitorForm, setMonitorForm] = useState({
    name: "",
    url: "",
    check_interval: 30,
  });

  const [addingMonitor, setAddingMonitor] = useState(false);

  const baseUrl = import.meta.env.VITE_BASE_URL;

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      setError("");

      const token = localStorage.getItem("token");

      const response = await axios.get(`${baseUrl}/api/dashboard`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setDashboard(response.data);
    } catch (err) {
      console.error("Failed to fetch dashboard:", err);
      console.error("Status:", err.response?.status);

      if (err.response?.status === 401) {
        localStorage.removeItem("token");
        setError("Your session has expired. Please login again.");
        return;
      }

      setError("Unable to load dashboard data. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  const handleAddMonitor = async (e) => {
    e.preventDefault();

    try {
      setAddingMonitor(true);
      const token = localStorage.getItem("token");

      const response = await axios.post(
        `${baseUrl}/api/website`,
        {
          name: monitorForm.name,
          url: monitorForm.url,
          status: "OPERATIONAL",
          check_interval: Number(monitorForm.check_interval),
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
        },
      );

      console.log("Monitor added:", response.data);

      // Close modal
      setShowAddMonitor(false);

      // Reset form
      setMonitorForm({
        name: "",
        url: "",
        check_interval: 30,
      });

      // Get updated dashboard
      await fetchDashboard();
    } catch (err) {
      console.error("Failed to add monitor:", err);

      if (err.response) {
        console.error("Backend response:", err.response.data);
        alert(err.response.data?.message || "Failed to add monitor.");
      } else {
        alert("Unable to connect to backend.");
      }
    } finally {
      setAddingMonitor(false);
    }
  };
  const handlePauseResume = async (monitor) => {
  try {
    const token = localStorage.getItem("token");

    const newStatus =
      monitor.status === "PAUSED"
        ? "OPERATIONAL"
        : "PAUSED";

    await axios.put(
      `${baseUrl}/api/website/${monitor.id}/status`,
      {
        status: newStatus,
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );

    await fetchDashboard();

  } catch (err) {
    console.error("Failed to update monitor status:", err);
    alert("Failed to update monitor status.");
  }
};


const handleRemoveMonitor = async (monitorId) => {
  try {
    const token = localStorage.getItem("token");

    await axios.delete(
      `${baseUrl}/api/website/${monitorId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    await fetchDashboard();

  } catch (err) {
    console.error("Failed to remove monitor:", err);
    alert("Failed to remove monitor.");
  }
};

  const getStatusStyle = (status) => {
    switch (status?.toUpperCase()) {
      case "OPERATIONAL":
        return "bg-[#294f43] text-[#8bd6a2] border border-[#376c58]";

      case "DEGRADED":
        return "bg-[#62522c] text-[#f0d27b] border border-[#806b39]";

      case "STORM":
      case "DOWN":
        return "bg-[#653f36] text-[#efa99a] border border-[#815146]";

      default:
        return "bg-[#293746] text-[#b4c3d0] border border-[#3a4d60]";
    }
  };

  const getStatusDot = (status) => {
    switch (status?.toUpperCase()) {
      case "OPERATIONAL":
        return "bg-[#5bbb73]";

      case "DEGRADED":
        return "bg-[#e0b849]";

      case "DOWN":
      case "STORM":
        return "bg-[#df7867]";

      default:
        return "bg-gray-500";
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#061522] text-[#eee5d0] flex items-center justify-center">
        <div className="text-[#d9c49b] text-lg">Loading dashboard...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-[#061522] text-[#eee5d0] flex items-center justify-center">
        <div className="bg-[#10263a] border border-[#263f53] rounded-xl p-6 text-center">
          <p className="text-red-300 mb-4">{error}</p>

          <button
            onClick={() => {
              localStorage.removeItem("token");
              window.location.href = "/login";
            }}
            className="px-4 py-2 rounded-lg bg-[#426b4c] hover:bg-[#507d5a] text-[#f1e8d4] transition-colors"
          >
            Go to Login
          </button>
        </div>
      </div>
    );
  }

  if (!dashboard) return null;

  const monitors = dashboard.monitors || [];

  return (
    <div className="min-h-screen bg-[#061522] text-[#eee5d0] flex">
      {/* =========================================================
          SIDEBAR
      ========================================================= */}
      <aside className="w-[248px] min-h-screen bg-[#0d2538] border-r border-[#21394c] flex flex-col fixed left-0 top-0">
        {/* Logo */}
        <div className="px-6 pt-6 pb-5">
          <div className="flex items-center gap-3">
            <div className="text-xl">🚦</div>

            <h1 className="text-[#f1e8d4] font-bold text-lg tracking-wide">
              UPBOT
            </h1>
          </div>
        </div>

        {/* Navigation */}
        <div className="px-4">
          <p className="text-[11px] tracking-[2px] text-[#718495] uppercase mb-2">
            Monitoring
          </p>

          <h2 className="text-[#f1e8d4] font-semibold text-base px-2 mb-4">
            Dashboard
          </h2>

          {/* Overview */}
          <button
            className="
              w-full
              flex items-center gap-3
              px-3 py-2.5
              rounded-lg
              bg-[#19334a]
              border-l-2 border-[#d3a646]
              text-[#f2e8d2]
              text-sm
            "
          >
            <span className="text-base">⊞</span>
            <span>Overview</span>
          </button>

          {/* Monitors */}
          <button
            className="
              w-full
              flex items-center gap-3
              px-3 py-2.5
              rounded-lg
              text-[#aab8c5]
              hover:bg-[#19334a]
              text-sm
            "
          >
            <span>◷</span>
            <span>Monitors</span>
          </button>

          {/* Incidents */}
          <button
            className="
              w-full
              flex items-center gap-3
              px-3 py-2.5
              rounded-lg
              text-[#aab8c5]
              hover:bg-[#19334a]
              text-sm
            "
          >
            <span>△</span>
            <span>Incidents</span>
          </button>
        </div>

        {/* Monitor list */}
        <div className="mx-4 mt-7 border-t border-[#203a4d] pt-5">
          <p className="text-[11px] tracking-[2px] text-[#718495] uppercase mb-2">
            Monitors
          </p>

          <p className="text-[#f1e8d4] font-semibold text-sm mb-3">
            Active Monitors
          </p>

          <div className="space-y-3">
            {monitors
              .filter((monitor) => monitor.status !== "PAUSED")
              .map((monitor) => (
                <div
                  key={monitor.id}
                  className="flex items-center justify-between text-sm"
                >
                  <div className="flex items-center gap-2">
                    <span
                      className={`w-2.5 h-2.5 rounded-full ${getStatusDot(
                        monitor.status,
                      )}`}
                    />

                    <span className="text-[#d2dce3]">{monitor.name}</span>
                  </div>

                  <span className="text-[#9bb2c7] text-xs">
                    {Number(monitor.uptime24h || 0).toFixed(1)}%
                  </span>
                </div>
              ))}
          </div>
        </div>

        {/* Bottom */}
        <div className="mt-auto px-5 pb-5 flex justify-between items-center text-xs">
          <span className="text-[#667d90]">v1.3.0</span>

          <span className="flex items-center gap-1.5 text-[#71bd84] bg-[#102e27] px-3 py-1.5 rounded-full">
            <span className="w-2 h-2 rounded-full bg-[#55bd70]" />
            Live
          </span>
        </div>
      </aside>

      {/* =========================================================
          MAIN CONTENT
      ========================================================= */}
      <main className="ml-62 flex-1 min-h-screen px-8 py-5">
        {/* Header */}
        <header className="flex justify-between items-start mb-7">
          <div>
            <h1 className="font-serif text-[25px] font-bold text-[#eee5d0]">
              Dashboard
            </h1>

            <p className="text-sm text-[#8ca0b0] mt-1">
              {dashboard.activeMonitorCount || 0} of{" "}
              {dashboard.totalMonitorCount || 0} operational · live reporting
            </p>
          </div>

          <div className="flex items-center gap-7 text-sm text-[#aab9c7]">
            <button className="flex items-center gap-2 hover:text-white">
              <span>⌂</span>
              Home Deck
            </button>

            <button className="flex items-center gap-2 hover:text-white">
              <span>↪</span>
              Sign out
            </button>
          </div>
        </header>

        {/* =====================================================
            STAT CARDS
        ===================================================== */}
        <section className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
          {/* Operational */}
          <div className="bg-[#f3e7c9] text-[#332d20] rounded-xl p-5 min-h-[133px]">
            <div className="flex justify-between">
              <div>
                <p className="text-[11px] uppercase tracking-[1.5px] text-[#81765e] font-semibold">
                  Operational
                  <br />
                  Monitors
                </p>

                <p className="text-4xl font-bold mt-2">
                  {dashboard.operationalMonitors || 0}
                </p>
              </div>

              <span className="h-fit px-2 py-1 rounded-md bg-[#c8dcb9] text-[#426847] text-[11px] font-bold">
                OPERATIONAL
              </span>
            </div>

            <p className="text-xs text-[#82775f] mt-[-2px]">
              {dashboard.activeMonitorCount || 0} active monitors
            </p>
          </div>

          {/* Degraded */}
          <div className="bg-[#f3e7c9] text-[#332d20] rounded-xl p-5 min-h-[133px]">
            <div className="flex justify-between">
              <div>
                <p className="text-[11px] uppercase tracking-[1.5px] text-[#81765e] font-semibold">
                  Degraded Monitors
                </p>

                <p className="text-4xl font-bold mt-2">
                  {dashboard.degradedMonitors || 0}
                </p>
              </div>

              <span className="h-fit px-2 py-1 rounded-md bg-[#ead69f] text-[#765d20] text-[11px] font-bold">
                DEGRADED
              </span>
            </div>

            <p className="text-xs text-[#82775f] mt-2">
              {dashboard.degradedMessage || "Slight response time lags"}
            </p>
          </div>

          {/* Storm alerts */}
          <div className="bg-[#f3e7c9] text-[#332d20] rounded-xl p-5 min-h-[133px]">
            <div className="flex justify-between">
              <div>
                <p className="text-[11px] uppercase tracking-[1.5px] text-[#81765e] font-semibold">
                  Storm Alerts
                </p>

                <p className="text-4xl font-bold mt-2">
                  {dashboard.stormAlerts || 0}
                </p>
              </div>

              <span className="h-fit px-2 py-1 rounded-md bg-[#edc2b5] text-[#82483c] text-[11px] font-bold">
                STORM
              </span>
            </div>

            <p className="text-xs text-[#82775f] mt-2">
              {dashboard.stormMessage || "Active outages reported"}
            </p>
          </div>

          {/* Uptime */}
          <div className="bg-[#f3e7c9] text-[#332d20] rounded-xl p-5 min-h-[133px]">
            <div className="flex justify-between">
              <div>
                <p className="text-[11px] uppercase tracking-[1.5px] text-[#81765e] font-semibold">
                  Overall Uptime
                </p>

                <p className="text-4xl font-bold mt-2 font-mono">
                  {Number(dashboard.overallUptime || 0).toFixed(2)}%
                </p>
              </div>

              <span className="text-[#aa9d7e] text-xl">◷</span>
            </div>

            <p className="text-xs text-[#82775f] mt-1">24-hour average</p>

            <div className="w-full h-[4px] bg-[#d7ccb0] rounded-full mt-3 overflow-hidden">
              <div
                className="h-full bg-[#58af6d] rounded-full"
                style={{
                  width: `${Math.min(
                    Number(dashboard.overallUptime || 0),
                    100,
                  )}%`,
                }}
              />
            </div>
          </div>
        </section>

        {/* =====================================================
            SIGNAL NETWORK
        ===================================================== */}
        <section className="mt-8">
          <div className="bg-[#0d2538] border border-[#1d3a4e] rounded-xl h-[184px] relative overflow-hidden">
            {/* Heading */}
            <div className="absolute top-5 left-5 flex items-center gap-2">
              <span className="text-[#eee5d0] text-lg">◎</span>

              <h2 className="font-serif text-lg font-bold text-[#eee5d0]">
                Signal Network
              </h2>
            </div>

            {/* Network */}
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="relative w-[400px] h-[100px]">
                {/* UpBot */}
                <div className="absolute top-0 left-1/2 -translate-x-1/2 text-center">
                  <p className="text-[#d7a93c] font-semibold mb-2">UpBot</p>

                  <div className="text-[#e45c46] text-xl">▲</div>

                  <div className="w-[2px] h-[28px] bg-[#eee5d0] mx-auto" />
                </div>

                {/* Monitor connection */}
                <div className="absolute top-[57px] left-[50px] right-[50px] h-[2px] bg-[#5ab878]" />

                {/* Vertical endpoint */}
                <div className="absolute top-[45px] right-[50px] h-[28px] w-[2px] bg-[#5ab878]" />

                {/* Monitor name */}
                <div className="absolute left-0 top-[48px] -translate-x-full pr-3 text-[#55bb77] text-sm">
                  {monitors[0]?.name || ""}
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* =====================================================
            ACTIVE MONITORS HEADER
        ===================================================== */}
        <section className="mt-12">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-serif text-lg font-bold tracking-wide text-[#d7c7a5]">
              ACTIVE MONITORS
            </h2>

            <div className="flex items-center gap-4">
              {/* Filters */}
              <div className="flex bg-[#0b1d2d] border border-[#1f3749] rounded-lg p-1">
                <button className="px-4 py-2 rounded-md bg-[#162f43] border border-[#30485a] text-[#eee5d0] text-xs font-semibold">
                  All ({monitors.length})
                </button>

                <button className="px-4 py-2 text-[#a6b7c7] text-xs">
                  Active (
                  {
                    monitors.filter((monitor) => monitor.status !== "PAUSED")
                      .length
                  }
                  )
                </button>

                <button className="px-4 py-2 text-[#a6b7c7] text-xs">
                  Paused (
                  {
                    monitors.filter((monitor) => monitor.status === "PAUSED")
                      .length
                  }
                  )
                </button>
              </div>
              {/* Add monitor */}
              <button
                onClick={() => setShowAddMonitor(true)}
                className="
                  px-5 py-2.5
                  rounded-lg
                  bg-gradient-to-b from-[#5d8a60] to-[#35583d]
                  border border-[#9a9b54]
                  text-[#e9e5c9]
                  text-sm
                  font-semibold
                  shadow-[0_0_15px_rgba(106,137,83,0.25)]
                  hover:brightness-110
                "
              >
                Add Monitor
              </button>
            </div>
          </div>

          {/* ===================================================
              MONITOR CARDS
          =================================================== */}
          <div className="flex flex-wrap gap-5">
            {monitors.map((monitor) => (
              <div
                key={monitor.id}
                className="
                  w-[393px]
                  bg-[#0d2538]
                  border border-[#1d3a4e]
                  rounded-xl
                  p-4
                "
              >
                {/* Card top */}
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-serif text-base font-bold text-[#eee5d0]">
                      {monitor.name}
                    </h3>

                    <p className="text-xs text-[#728798] mt-1">{monitor.url}</p>
                  </div>

                  <span
                    className={`px-2.5 py-1 rounded-md text-[11px] font-semibold ${getStatusStyle(
                      monitor.status,
                    )}`}
                  >
                    {monitor.status}
                  </span>
                </div>

                {/* Metrics */}
                <div className="grid grid-cols-3 mt-4">
                  <div>
                    <p className="text-[10px] uppercase tracking-wider text-[#718495]">
                      Latency
                    </p>

                    <p className="text-lg font-bold text-[#eee5d0] mt-1">
                      {monitor.latency}ms
                    </p>
                  </div>

                  <div>
                    <p className="text-[10px] uppercase tracking-wider text-[#718495]">
                      Uptime 24H
                    </p>

                    <p className="text-lg font-bold text-[#eee5d0] mt-1">
                      {Number(monitor.uptime24h || 0).toFixed(1)}%
                    </p>
                  </div>

                  <div>
                    <p className="text-[10px] uppercase tracking-wider text-[#718495]">
                      Interval
                    </p>

                    <p className="text-lg font-bold text-[#eee5d0] mt-1">
                      {monitor.interval}s
                    </p>
                  </div>
                </div>

                {/* Uptime graph */}
                <div className="h-[45px] mt-4 flex items-end gap-1">
                  {(monitor.history || []).map((value, index) => (
                    <div
                      key={index}
                      className="flex-1 bg-[#5ab373] rounded-t-[2px] min-w-[4px]"
                      style={{
                        height: `${Math.max(
                          10,
                          Math.min(Number(value), 100),
                        )}%`,
                      }}
                    />
                  ))}
                </div>

                {/* Bottom */}
                <div className="border-t border-[#1d3a4e] mt-3 pt-3 flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <span className="text-sm text-[#a7b5c1]">
                      Score{" "}
                      <span className="font-bold text-[#eee5d0]">
                        {monitor.score}
                      </span>
                    </span>

                    <span className="px-2 py-1 rounded-md bg-[#4a4a37] text-[#e0c56b] text-[10px] font-bold">
                      {monitor.scoreStatus || "—"}
                    </span>
                  </div>

                  <div className="flex gap-2">
                    <button
                      onClick={() => handlePauseResume(monitor)}
                      className="px-3 py-1.5 rounded-md border border-[#263f52] text-[#8296a7] text-xs hover:bg-[#142d40]"
                    >
                      {monitor.status === "PAUSED" ? "Resume" : "Pause"}
                    </button>

                    <button
                      onClick={() => handleRemoveMonitor(monitor.id)}
                      className="px-3 py-1.5 rounded-md border border-[#263f52] text-[#8296a7] text-xs hover:bg-[#142d40]"
                    >
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* No monitors */}
          {monitors.length === 0 && (
            <div className="border border-dashed border-[#294457] rounded-xl p-10 text-center text-[#718495]">
              No monitors found.
            </div>
          )}
        </section>
        {showAddMonitor && (
          <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
            <div className="w-[420px] bg-[#0d2538] border border-[#294457] rounded-xl p-6 shadow-2xl">
              <div className="flex justify-between items-center mb-6">
                <h2 className="font-serif text-xl font-bold text-[#eee5d0]">
                  Add Monitor
                </h2>

                <button
                  onClick={() => setShowAddMonitor(false)}
                  className="text-[#718495] hover:text-white text-xl"
                >
                  ×
                </button>
              </div>

              <form onSubmit={handleAddMonitor}>
                {/* Name */}

                <div className="mb-4">
                  <label className="block text-xs uppercase tracking-wider text-[#718495] mb-2">
                    Monitor Name
                  </label>

                  <input
                    type="text"
                    required
                    value={monitorForm.name}
                    onChange={(e) =>
                      setMonitorForm({
                        ...monitorForm,
                        name: e.target.value,
                      })
                    }
                    placeholder="My Website"
                    className="w-full bg-[#081b2b] border border-[#294457] rounded-lg px-3 py-2.5 text-[#eee5d0] outline-none focus:border-[#5d8a60]"
                  />
                </div>

                {/* URL */}

                <div className="mb-4">
                  <label className="block text-xs uppercase tracking-wider text-[#718495] mb-2">
                    Website URL
                  </label>

                  <input
                    type="url"
                    required
                    value={monitorForm.url}
                    onChange={(e) =>
                      setMonitorForm({
                        ...monitorForm,
                        url: e.target.value,
                      })
                    }
                    placeholder="https://example.com"
                    className="w-full bg-[#081b2b] border border-[#294457] rounded-lg px-3 py-2.5 text-[#eee5d0] outline-none focus:border-[#5d8a60]"
                  />
                </div>

                {/* Interval */}

                <div className="mb-6">
                  <label className="block text-xs uppercase tracking-wider text-[#718495] mb-2">
                    Check Interval (seconds)
                  </label>

                  <input
                    type="number"
                    required
                    min="5"
                    value={monitorForm.check_interval}
                    onChange={(e) =>
                      setMonitorForm({
                        ...monitorForm,
                        check_interval: e.target.value,
                      })
                    }
                    className="w-full bg-[#081b2b] border border-[#294457] rounded-lg px-3 py-2.5 text-[#eee5d0] outline-none focus:border-[#5d8a60]"
                  />
                </div>

                {/* Buttons */}

                <div className="flex justify-end gap-3">
                  <button
                    type="button"
                    onClick={() => setShowAddMonitor(false)}
                    className="px-4 py-2 rounded-lg border border-[#294457] text-[#8296a7] hover:bg-[#142d40]"
                  >
                    Cancel
                  </button>

                  <button
                    type="submit"
                    disabled={addingMonitor}
                    className="px-5 py-2 rounded-lg bg-[#426b4c] text-[#f1e8d4] font-semibold hover:bg-[#507d5a] disabled:opacity-50"
                  >
                    {addingMonitor ? "Adding..." : "Add Monitor"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default Dashboard;
