import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  getEarningsByRange,
  getPaymentSummary,
  getPlatformEarnings,
} from "../../services/superAdminApi";

function SuperAdminPayments() {
  const [paymentSummary, setPaymentSummary] =
    useState(null);

  const [earnings, setEarnings] =
    useState(null);

  const [rangeData, setRangeData] =
    useState(null);

  const [startDate, setStartDate] =
    useState("");

  const [endDate, setEndDate] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [rangeLoading, setRangeLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  // ==========================================
  // INITIAL LOAD
  // ==========================================

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setError("");

    try {
      const [
        paymentData,
        earningsData,
      ] = await Promise.all([
        getPaymentSummary(),
        getPlatformEarnings(),
      ]);

      setPaymentSummary(
        paymentData
      );

      setEarnings(
        earningsData
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to load payment and earnings data"
      );
    } finally {
      setLoading(false);
    }
  };

  // ==========================================
  // DATE RANGE
  // ==========================================

  const handleRangeSubmit =
    async (event) => {
      event.preventDefault();

      setError("");

      if (
        !startDate ||
        !endDate
      ) {
        setError(
          "Please select both start and end dates"
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

      setRangeLoading(true);

      try {
        const data =
          await getEarningsByRange(
            startDate,
            endDate
          );

        setRangeData(
          data
        );
      } catch (err) {
        setError(
          err.message ||
            "Unable to load date-range earnings"
        );
      } finally {
        setRangeLoading(false);
      }
    };

  const clearRange = () => {
    setStartDate("");
    setEndDate("");
    setRangeData(null);
    setError("");
  };

  // ==========================================
  // CURRENT DISPLAY DATA
  // ==========================================

  const currentEarnings =
    rangeData || earnings;

  // ==========================================
  // TOTALS
  // ==========================================

  const restaurantCount =
    useMemo(() => {
      return Array.isArray(
        currentEarnings?.restaurants
      )
        ? currentEarnings
            .restaurants.length
        : 0;
    }, [currentEarnings]);

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
  // LOADING
  // ==========================================

  if (loading) {
    return (
      <div className="super-admin-page">
        <div className="super-admin-loading-card">
          Loading payments and
          earnings...
        </div>
      </div>
    );
  }

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="super-admin-page">
      <div className="super-admin-page-header">
        <div>
          <h1>
            Payments & Earnings
          </h1>

          <p>
            Monitor platform payments,
            commissions and restaurant
            earnings.
          </p>
        </div>

        <button
          type="button"
          className="super-admin-button super-admin-button-secondary"
          onClick={
            loadData
          }
        >
          Refresh
        </button>
      </div>

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

      {/* =====================================
          PAYMENT SUMMARY
      ===================================== */}

      <div className="super-admin-section">
        <h2 className="super-admin-section-title">
          Payment Summary
        </h2>

        <div className="super-admin-stat-grid">
          <div className="super-admin-stat-card">
            <span>
              Total Orders
            </span>

            <strong>
              {
                paymentSummary?.totalOrders ??
                0
              }
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Cash on Delivery
            </span>

            <strong>
              {
                paymentSummary
                  ?.cashOnDeliveryOrders ??
                0
              }
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Razorpay Orders
            </span>

            <strong>
              {
                paymentSummary
                  ?.razorpayOrders ??
                0
              }
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Paid Orders
            </span>

            <strong>
              {
                paymentSummary?.paidOrders ??
                0
              }
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Pending Payments
            </span>

            <strong>
              {
                paymentSummary
                  ?.pendingPayments ??
                0
              }
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Failed Payments
            </span>

            <strong>
              {
                paymentSummary
                  ?.failedPayments ??
                0
              }
            </strong>
          </div>
        </div>

        <div className="super-admin-dashboard-section">
          <div className="super-admin-revenue-card">
            <span>
              Total Paid Amount
            </span>

            <strong>
              {formatPrice(
                paymentSummary
                  ?.totalPaidAmount
              )}
            </strong>
          </div>
        </div>
      </div>

      {/* =====================================
          DATE RANGE FILTER
      ===================================== */}

      <div className="super-admin-section">
        <h2 className="super-admin-section-title">
          Earnings Filter
        </h2>

        <form
          className="super-admin-filter-bar"
          onSubmit={
            handleRangeSubmit
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
              rangeLoading
            }
            className="super-admin-button super-admin-button-primary"
          >
            {rangeLoading
              ? "Loading..."
              : "Apply Range"}
          </button>

          <button
            type="button"
            className="super-admin-button super-admin-button-secondary"
            onClick={
              clearRange
            }
          >
            Clear
          </button>
        </form>

        {rangeData && (
          <div
            className="super-admin-card"
            style={{
              marginBottom:
                "18px",
            }}
          >
            Showing earnings from{" "}
            <strong>
              {
                rangeData.startDate
              }
            </strong>{" "}
            to{" "}
            <strong>
              {
                rangeData.endDate
              }
            </strong>
          </div>
        )}
      </div>

      {/* =====================================
          PLATFORM EARNINGS
      ===================================== */}

      <div className="super-admin-section">
        <h2 className="super-admin-section-title">
          Platform Earnings
        </h2>

        <div className="super-admin-stat-grid">
          <div className="super-admin-stat-card">
            <span>
              Delivered Orders
            </span>

            <strong>
              {
                currentEarnings
                  ?.totalDeliveredOrders ??
                0
              }
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Restaurants
            </span>

            <strong>
              {restaurantCount}
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Delivered Food Value
            </span>

            <strong>
              {formatPrice(
                currentEarnings
                  ?.totalDeliveredSubtotal
              )}
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Platform Commission
            </span>

            <strong>
              {formatPrice(
                currentEarnings
                  ?.totalPlatformCommission
              )}
            </strong>
          </div>

          <div className="super-admin-stat-card">
            <span>
              Restaurant Net Earnings
            </span>

            <strong>
              {formatPrice(
                currentEarnings
                  ?.totalRestaurantNetEarnings
              )}
            </strong>
          </div>
        </div>
      </div>

      {/* =====================================
          RESTAURANT-WISE EARNINGS
      ===================================== */}

      <div className="super-admin-section">
        <h2 className="super-admin-section-title">
          Restaurant Earnings
        </h2>

        {!currentEarnings
          ?.restaurants?.length ? (
          <div className="super-admin-empty-card">
            No restaurant earnings
            available.
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
                    Delivered Orders
                  </th>

                  <th>
                    Commission Rate
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
                {currentEarnings
                  .restaurants.map(
                    (restaurant) => (
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
                          {
                            restaurant.deliveredOrders ??
                            0
                          }
                        </td>

                        <td>
                          <span className="super-admin-badge super-admin-badge-info">
                            {formatPercentage(
                              restaurant.commissionPercentage
                            )}
                          </span>
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
    </div>
  );
}

export default SuperAdminPayments;