import { useEffect, useState } from "react";

import {
  getSuperAdminDashboard,
} from "../../services/superAdminApi";

function SuperAdminDashboard() {
  const [summary, setSummary] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    setLoading(true);
    setError("");

    try {
      const data =
        await getSuperAdminDashboard();

      setSummary(data);
    } catch (err) {
      setError(
        err.message ||
          "Unable to load dashboard"
      );
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="super-admin-page">
        <p>Loading dashboard...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="super-admin-page">
        <div className="super-admin-page-header">
          <div>
            <h1>Dashboard</h1>
            <p>
              Platform overview and key
              statistics.
            </p>
          </div>
        </div>

        <div className="super-admin-error-card">
          <p>{error}</p>

          <button
            type="button"
            onClick={loadDashboard}
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  const cards = [
    {
      label: "Total Restaurants",
      value:
        summary?.totalRestaurants ?? 0,
    },
    {
      label: "Pending Restaurants",
      value:
        summary?.pendingRestaurants ?? 0,
    },
    {
      label: "Approved Restaurants",
      value:
        summary?.approvedRestaurants ?? 0,
    },
    {
      label: "Active Restaurants",
      value:
        summary?.activeRestaurants ?? 0,
    },
    {
      label: "Restaurant Admins",
      value:
        summary?.totalRestaurantAdmins ??
        0,
    },
    {
      label: "Customers",
      value:
        summary?.totalCustomers ?? 0,
    },
    {
      label: "Total Orders",
      value:
        summary?.totalOrders ?? 0,
    },
    {
      label: "Delivered Orders",
      value:
        summary?.deliveredOrders ?? 0,
    },
    {
      label: "Cancelled Orders",
      value:
        summary?.cancelledOrders ?? 0,
    },
  ];

  return (
    <div className="super-admin-page">
      <div className="super-admin-page-header">
        <div>
          <h1>Dashboard</h1>

          <p>
            Complete platform overview.
          </p>
        </div>

        <button
          type="button"
          onClick={loadDashboard}
          className="super-admin-refresh-button"
        >
          Refresh
        </button>
      </div>

      <div className="super-admin-stat-grid">
        {cards.map((card) => (
          <div
            key={card.label}
            className="super-admin-stat-card"
          >
            <span>{card.label}</span>

            <strong>
              {card.value}
            </strong>
          </div>
        ))}
      </div>

      <div className="super-admin-dashboard-section">
        <div className="super-admin-revenue-card">
          <span>
            Delivered Revenue
          </span>

          <strong>
            ₹
            {Number(
              summary?.deliveredRevenue ??
                0
            ).toLocaleString(
              "en-IN",
              {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              }
            )}
          </strong>
        </div>
      </div>
    </div>
  );
}

export default SuperAdminDashboard;