import {
  useEffect,
  useState,
} from "react";

import AdminNav from "./AdminNav.jsx";

const SALES_API_URL =
  `${import.meta.env.VITE_API_BASE_URL}/restaurant-admin/sales`;

function AdminSales({
  formatPrice,
}) {
  const [todaySales, setTodaySales] =
    useState(null);

  const [rangeSales, setRangeSales] =
    useState(null);

  const [startDate, setStartDate] =
    useState("2026-08-01");

  const [endDate, setEndDate] =
    useState("2026-08-20");

  const [loading, setLoading] =
    useState(true);

  const [rangeLoading, setRangeLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const token =
    sessionStorage.getItem(
      "adminToken",
    );

  function authorizationHeaders() {
    return {
      Authorization: `Bearer ${token}`,
    };
  }

  function handleUnauthorized(
    response,
  ) {
    if (
      response.status === 401 ||
      response.status === 403
    ) {
      sessionStorage.removeItem(
        "adminToken",
      );

      sessionStorage.removeItem(
        "adminEmail",
      );

      window.location.href =
        "/admin";

      throw new Error(
        "Admin session expired",
      );
    }
  }

  async function loadTodaySales() {
    try {
      setLoading(true);
      setError("");

      const response = await fetch(
        `${SALES_API_URL}/today`,
        {
          headers:
            authorizationHeaders(),
        },
      );

      handleUnauthorized(
        response,
      );

      if (!response.ok) {
        throw new Error(
          "Unable to load today's sales",
        );
      }

      const data =
        await response.json();

      setTodaySales(data);
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          "Could not load sales data. Make sure Spring Boot is running.",
        );
      }
    } finally {
      setLoading(false);
    }
  }

  async function loadRangeSales(
    event,
  ) {
    if (event) {
      event.preventDefault();
    }

    try {
      setRangeLoading(true);
      setError("");

      const response = await fetch(
        `${SALES_API_URL}/range?startDate=${startDate}&endDate=${endDate}`,
        {
          headers:
            authorizationHeaders(),
        },
      );

      handleUnauthorized(
        response,
      );

      if (!response.ok) {
        let message =
          "Unable to load sales report";

        try {
          const errorData =
            await response.json();

          if (errorData.message) {
            message =
              errorData.message;
          }
        } catch {
          // Keep default message.
        }

        throw new Error(message);
      }

      const data =
        await response.json();

      setRangeSales(data);
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          requestError.message ||
            "Could not load sales report.",
        );
      }
    } finally {
      setRangeLoading(false);
    }
  }

  useEffect(() => {
    loadTodaySales();
  }, []);

  return (
    <div className="admin-page">
      <AdminNav
        activePage="sales"
      />

      <header className="admin-header">
        <div>
          <span className="eyebrow">
            Restaurant performance
          </span>

          <h1>
            Sales Analytics
          </h1>

          <p>
            Track orders, delivered
            revenue and sales performance.
          </p>
        </div>

        <div className="admin-header-actions">
          <button
            className="refresh-button"
            type="button"
            onClick={
              loadTodaySales
            }
          >
            Refresh
          </button>
        </div>
      </header>

      <main className="admin-content">
        {error && (
          <div className="status-message error-message">
            {error}
          </div>
        )}

        <div className="management-list-heading">
          <div>
            <span className="eyebrow">
              Today
            </span>

            <h2>
              Today's performance
            </h2>
          </div>
        </div>

        {loading && (
          <div className="status-message">
            Loading today's sales...
          </div>
        )}

        {!loading &&
          todaySales && (
            <section className="admin-stats">
              <article>
                <span>
                  Total orders
                </span>

                <strong>
                  {
                    todaySales.totalOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Delivered orders
                </span>

                <strong>
                  {
                    todaySales.deliveredOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Delivered revenue
                </span>

                <strong>
                  {formatPrice(
                    todaySales.deliveredRevenue ??
                      0,
                  )}
                </strong>
              </article>
            </section>
          )}

        <section className="admin-sales-range-card">
          <div className="management-list-heading">
            <div>
              <span className="eyebrow">
                Custom report
              </span>

              <h2>
                Sales by date range
              </h2>
            </div>
          </div>

          <form
            className="admin-sales-filter"
            onSubmit={
              loadRangeSales
            }
          >
            <label>
              Start date

              <input
                type="date"
                value={startDate}
                onChange={(event) =>
                  setStartDate(
                    event.target.value,
                  )
                }
                required
              />
            </label>

            <label>
              End date

              <input
                type="date"
                value={endDate}
                onChange={(event) =>
                  setEndDate(
                    event.target.value,
                  )
                }
                required
              />
            </label>

            <button
              className="refresh-button"
              type="submit"
              disabled={
                rangeLoading
              }
            >
              {rangeLoading
                ? "Loading..."
                : "Generate report"}
            </button>
          </form>
        </section>

        {rangeSales && (
          <>
            <div className="admin-sales-report-heading">
              <div>
                <span>
                  Selected range
                </span>

                <strong>
                  {startDate}
                  {" → "}
                  {endDate}
                </strong>
              </div>
            </div>

            <section className="admin-stats">
              <article>
                <span>
                  Total orders
                </span>

                <strong>
                  {
                    rangeSales.totalOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Delivered orders
                </span>

                <strong>
                  {
                    rangeSales.deliveredOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Cancelled orders
                </span>

                <strong>
                  {
                    rangeSales.cancelledOrders
                  }
                </strong>
              </article>
            </section>

            <section className="admin-stats">
              <article>
                <span>
                  Delivered revenue
                </span>

                <strong>
                  {formatPrice(
                    rangeSales.deliveredRevenue ??
                      0,
                  )}
                </strong>
              </article>

              <article>
                <span>
                  Delivered subtotal
                </span>

                <strong>
                  {formatPrice(
                    rangeSales.deliveredSubtotal ??
                      0,
                  )}
                </strong>
              </article>

              <article>
                <span>
                  Successful delivery rate
                </span>

                <strong>
                  {rangeSales.totalOrders >
                  0
                    ? `${(
                        (rangeSales.deliveredOrders /
                          rangeSales.totalOrders) *
                        100
                      ).toFixed(1)}%`
                    : "0.0%"}
                </strong>
              </article>
            </section>
          </>
        )}
      </main>
    </div>
  );
}

export default AdminSales;