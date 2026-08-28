import {
  useMemo,
  useState,
} from "react";

import {
  getSuperAdminReport,
} from "../../services/superAdminApi";

function SuperAdminReports() {
  const [startDate, setStartDate] =
    useState("");

  const [endDate, setEndDate] =
    useState("");

  const [report, setReport] =
    useState(null);

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  // ==========================================
  // LOAD REPORT
  // ==========================================

  const handleSubmit =
    async (event) => {
      event.preventDefault();

      setError("");

      if (
        !startDate ||
        !endDate
      ) {
        setError(
          "Please select both start date and end date"
        );
        return;
      }

      if (
        endDate < startDate
      ) {
        setError(
          "End date cannot be before start date"
        );
        return;
      }

      setLoading(true);

      try {
        const data =
          await getSuperAdminReport(
            startDate,
            endDate
          );

        setReport(
          data
        );
      } catch (err) {
        setError(
          err.message ||
            "Unable to load report"
        );
      } finally {
        setLoading(false);
      }
    };

  // ==========================================
  // TOTALS
  // ==========================================

  const totals = useMemo(() => {
    if (!report) {
      return {
        platformCommission: 0,
        restaurantEarnings: 0,
        deliveredRevenue: 0,
      };
    }

    const restaurants =
      Array.isArray(
        report.restaurants
      )
        ? report.restaurants
        : [];

    return restaurants.reduce(
      (
        result,
        restaurant
      ) => ({
        platformCommission:
          result.platformCommission +
          Number(
            restaurant.platformCommission ||
              0
          ),

        restaurantEarnings:
          result.restaurantEarnings +
          Number(
            restaurant.restaurantNetEarnings ||
              0
          ),

        deliveredRevenue:
          result.deliveredRevenue +
          Number(
            restaurant.deliveredRevenue ||
              0
          ),
      }),
      {
        platformCommission: 0,
        restaurantEarnings: 0,
        deliveredRevenue: 0,
      }
    );
  }, [report]);

  // ==========================================
  // HELPERS
  // ==========================================

  const formatPrice = (value) =>
    Number(
      value || 0
    ).toLocaleString(
      "en-IN",
      {
        style: "currency",
        currency: "INR",
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }
    );

  const formatPercentage = (
    value
  ) =>
    `${Number(
      value || 0
    ).toFixed(2)}%`;

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="super-admin-page">
      <div className="super-admin-page-header">
        <div>
          <h1>
            Reports
          </h1>

          <p>
            Generate platform reports
            for customers, orders and
            restaurant performance.
          </p>
        </div>
      </div>

      {/* =====================================
          REPORT FILTER
      ===================================== */}

      <form
        className="super-admin-filter-bar"
        onSubmit={
          handleSubmit
        }
      >
        <div className="super-admin-filter-field">
          <label>
            Start Date
          </label>

          <input
            type="date"
            value={
              startDate
            }
            onChange={(event) =>
              setStartDate(
                event.target.value
              )
            }
          />
        </div>

        <div className="super-admin-filter-field">
          <label>
            End Date
          </label>

          <input
            type="date"
            value={
              endDate
            }
            onChange={(event) =>
              setEndDate(
                event.target.value
              )
            }
          />
        </div>

        <button
          type="submit"
          disabled={
            loading
          }
          className="super-admin-button super-admin-button-primary"
        >
          {loading
            ? "Generating..."
            : "Generate Report"}
        </button>

        <button
          type="button"
          className="super-admin-button super-admin-button-secondary"
          onClick={() => {
            setStartDate("");
            setEndDate("");
            setReport(null);
            setError("");
          }}
        >
          Clear
        </button>
      </form>

      {error && (
        <div
          className="super-admin-error-card"
          style={{
            marginBottom:
              "18px",
          }}
        >
          {error}
        </div>
      )}

      {!report && !loading && (
        <div className="super-admin-empty-card">
          Select a date range and
          generate a report.
        </div>
      )}

      {loading && (
        <div className="super-admin-loading-card">
          Generating platform report...
        </div>
      )}

      {report && !loading && (
        <>
          {/* =================================
              DATE RANGE
          ================================= */}

          <div
            className="super-admin-card"
            style={{
              marginBottom:
                "22px",
            }}
          >
            Report Period:{" "}
            <strong>
              {
                report.startDate
              }
            </strong>{" "}
            to{" "}
            <strong>
              {
                report.endDate
              }
            </strong>
          </div>

          {/* =================================
              CUSTOMER REPORT
          ================================= */}

          <div className="super-admin-section">
            <h2 className="super-admin-section-title">
              Customer Report
            </h2>

            <div className="super-admin-stat-grid">
              <div className="super-admin-stat-card">
                <span>
                  Total Customers
                </span>

                <strong>
                  {
                    report.customers
                      ?.totalCustomers ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Active Customers
                </span>

                <strong>
                  {
                    report.customers
                      ?.activeCustomers ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Inactive Customers
                </span>

                <strong>
                  {
                    report.customers
                      ?.inactiveCustomers ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Verified Customers
                </span>

                <strong>
                  {
                    report.customers
                      ?.verifiedCustomers ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Unverified Customers
                </span>

                <strong>
                  {
                    report.customers
                      ?.unverifiedCustomers ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  New Customers
                </span>

                <strong>
                  {
                    report.customers
                      ?.newCustomers ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Active New Customers
                </span>

                <strong>
                  {
                    report.customers
                      ?.activeNewCustomers ??
                    0
                  }
                </strong>
              </div>
            </div>
          </div>

          {/* =================================
              ORDER REPORT
          ================================= */}

          <div className="super-admin-section">
            <h2 className="super-admin-section-title">
              Order Report
            </h2>

            <div className="super-admin-stat-grid">
              <div className="super-admin-stat-card">
                <span>
                  Total Orders
                </span>

                <strong>
                  {
                    report.orders
                      ?.totalOrders ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Placed
                </span>

                <strong>
                  {
                    report.orders
                      ?.placedOrders ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Confirmed
                </span>

                <strong>
                  {
                    report.orders
                      ?.confirmedOrders ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Preparing
                </span>

                <strong>
                  {
                    report.orders
                      ?.preparingOrders ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Ready
                </span>

                <strong>
                  {
                    report.orders
                      ?.readyOrders ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Out For Delivery
                </span>

                <strong>
                  {
                    report.orders
                      ?.outForDeliveryOrders ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Delivered
                </span>

                <strong>
                  {
                    report.orders
                      ?.deliveredOrders ??
                    0
                  }
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Cancelled
                </span>

                <strong>
                  {
                    report.orders
                      ?.cancelledOrders ??
                    0
                  }
                </strong>
              </div>
            </div>

            <div className="super-admin-dashboard-section">
              <div className="super-admin-revenue-card">
                <span>
                  Delivered Revenue
                </span>

                <strong>
                  {formatPrice(
                    report.orders
                      ?.deliveredRevenue
                  )}
                </strong>
              </div>
            </div>
          </div>

          {/* =================================
              PLATFORM FINANCIAL SUMMARY
          ================================= */}

          <div className="super-admin-section">
            <h2 className="super-admin-section-title">
              Financial Summary
            </h2>

            <div className="super-admin-stat-grid">
              <div className="super-admin-stat-card">
                <span>
                  Restaurant Revenue
                </span>

                <strong>
                  {formatPrice(
                    totals.deliveredRevenue
                  )}
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Platform Commission
                </span>

                <strong>
                  {formatPrice(
                    totals.platformCommission
                  )}
                </strong>
              </div>

              <div className="super-admin-stat-card">
                <span>
                  Restaurant Net Earnings
                </span>

                <strong>
                  {formatPrice(
                    totals.restaurantEarnings
                  )}
                </strong>
              </div>
            </div>
          </div>

          {/* =================================
              RESTAURANT PERFORMANCE
          ================================= */}

          <div className="super-admin-section">
            <h2 className="super-admin-section-title">
              Restaurant Performance
            </h2>

            {!report.restaurants
              ?.length ? (
              <div className="super-admin-empty-card">
                No restaurant performance
                data found for this range.
              </div>
            ) : (
              <div className="super-admin-table-wrapper">
                <table className="super-admin-table">
                  <thead>
                    <tr>
                      <th>
                        Restaurant
                      </th>

                      <th>
                        Status
                      </th>

                      <th>
                        Commission
                      </th>

                      <th>
                        Total Orders
                      </th>

                      <th>
                        Delivered
                      </th>

                      <th>
                        Cancelled
                      </th>

                      <th>
                        Revenue
                      </th>

                      <th>
                        Food Value
                      </th>

                      <th>
                        Platform Commission
                      </th>

                      <th>
                        Restaurant Earnings
                      </th>
                    </tr>
                  </thead>

                  <tbody>
                    {report.restaurants.map(
                      (
                        restaurant
                      ) => (
                        <tr
                          key={
                            restaurant.restaurantId
                          }
                        >
                          <td>
                            <strong>
                              {
                                restaurant.restaurantName
                              }
                            </strong>

                            <div>
                              <small>
                                ID:{" "}
                                {
                                  restaurant.restaurantId
                                }
                              </small>
                            </div>
                          </td>

                          <td>
                            <span
                              className={
                                restaurant.active
                                  ? "super-admin-badge super-admin-badge-success"
                                  : "super-admin-badge super-admin-badge-neutral"
                              }
                            >
                              {restaurant.active
                                ? "ACTIVE"
                                : "INACTIVE"}
                            </span>
                          </td>

                          <td>
                            {formatPercentage(
                              restaurant.commissionPercentage
                            )}
                          </td>

                          <td>
                            {
                              restaurant.totalOrders
                            }
                          </td>

                          <td>
                            {
                              restaurant.deliveredOrders
                            }
                          </td>

                          <td>
                            {
                              restaurant.cancelledOrders
                            }
                          </td>

                          <td>
                            {formatPrice(
                              restaurant.deliveredRevenue
                            )}
                          </td>

                          <td>
                            {formatPrice(
                              restaurant.deliveredSubtotal
                            )}
                          </td>

                          <td>
                            <strong>
                              {formatPrice(
                                restaurant.platformCommission
                              )}
                            </strong>
                          </td>

                          <td>
                            <strong>
                              {formatPrice(
                                restaurant.restaurantNetEarnings
                              )}
                            </strong>
                          </td>
                        </tr>
                      )
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}

export default SuperAdminReports;