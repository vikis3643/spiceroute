import {
  useEffect,
  useState,
} from "react";

import AdminNav from "./AdminNav.jsx";

const API_BASE_URL =
  "http://localhost:8080/api/restaurant-admin";

function AdminDashboard({
  formatPrice,
}) {
  const [summary, setSummary] =
    useState(null);

  const [profile, setProfile] =
    useState(null);

  const [reviewCount, setReviewCount] =
    useState(0);

  const [loading, setLoading] =
    useState(true);

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

  async function loadDashboard() {
    try {
      setLoading(true);
      setError("");

      const [
        summaryResponse,
        profileResponse,
        reviewResponse,
      ] = await Promise.all([
        fetch(
          `${API_BASE_URL}/dashboard/summary`,
          {
            headers:
              authorizationHeaders(),
          },
        ),

        fetch(
          `${API_BASE_URL}/profile`,
          {
            headers:
              authorizationHeaders(),
          },
        ),

        fetch(
          `${API_BASE_URL}/reviews/count`,
          {
            headers:
              authorizationHeaders(),
          },
        ),
      ]);

      handleUnauthorized(
        summaryResponse,
      );

      handleUnauthorized(
        profileResponse,
      );

      handleUnauthorized(
        reviewResponse,
      );

      if (
        !summaryResponse.ok ||
        !profileResponse.ok ||
        !reviewResponse.ok
      ) {
        throw new Error(
          "Unable to load dashboard",
        );
      }

      const summaryData =
        await summaryResponse.json();

      const profileData =
        await profileResponse.json();

      const reviewData =
        await reviewResponse.json();

      setSummary(summaryData);
      setProfile(profileData);

      setReviewCount(
        reviewData.count ?? 0,
      );
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          "Could not load the admin dashboard. Make sure Spring Boot is running.",
        );
      }
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadDashboard();
  }, []);

  function goTo(path) {
    window.location.href = path;
  }

  if (loading) {
    return (
      <div className="admin-page">
        <AdminNav
          activePage="dashboard"
        />

        <div className="status-message">
          Loading dashboard...
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page">
      <AdminNav
        activePage="dashboard"
      />

      <header className="admin-header">
        <div>
          <span className="eyebrow">
            Restaurant management
          </span>

          <h1>
            {profile?.name ??
              "Restaurant Dashboard"}
          </h1>

          <p>
            Manage your restaurant,
            orders, menu, offers and
            performance from one place.
          </p>
        </div>

        <div className="admin-header-actions">
          <button
            className="refresh-button"
            type="button"
            onClick={loadDashboard}
          >
            Refresh
          </button>

          <button
            className="back-button"
            type="button"
            onClick={() =>
              goTo("/")
            }
          >
            View restaurant
          </button>
        </div>
      </header>

      <main className="admin-content">
        {error && (
          <div className="status-message error-message">
            {error}
          </div>
        )}

        {summary && (
          <>
            <section className="admin-stats">
              <article>
                <span>
                  Total orders
                </span>

                <strong>
                  {
                    summary.totalOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Delivered orders
                </span>

                <strong>
                  {
                    summary.deliveredOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Delivered revenue
                </span>

                <strong>
                  {formatPrice(
                    summary.deliveredRevenue ??
                      0,
                  )}
                </strong>
              </article>
            </section>

            <section className="admin-stats">
              <article>
                <span>
                  Placed
                </span>

                <strong>
                  {
                    summary.placedOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Preparing
                </span>

                <strong>
                  {
                    summary.preparingOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Ready
                </span>

                <strong>
                  {
                    summary.readyOrders
                  }
                </strong>
              </article>
            </section>

            <section className="admin-stats">
              <article>
                <span>
                  Cancelled
                </span>

                <strong>
                  {
                    summary.cancelledOrders
                  }
                </strong>
              </article>

              <article>
                <span>
                  Customer reviews
                </span>

                <strong>
                  {reviewCount}
                </strong>
              </article>

              <article>
                <span>
                  Restaurant status
                </span>

                <strong>
                  {profile?.active
                    ? "Active"
                    : "Inactive"}
                </strong>
              </article>
            </section>
          </>
        )}

        <section className="admin-dashboard-actions">
          <button
            type="button"
            onClick={() =>
              goTo("/admin/orders")
            }
          >
            <span>🛒</span>

            <div>
              <strong>
                Orders
              </strong>

              <small>
                View and manage
                customer orders
              </small>
            </div>
          </button>

          <button
            type="button"
            onClick={() =>
              goTo("/admin/menu")
            }
          >
            <span>🍽️</span>

            <div>
              <strong>
                Menu
              </strong>

              <small>
                Manage dishes,
                categories and prices
              </small>
            </div>
          </button>

          <button
            type="button"
            onClick={() =>
              goTo(
                "/admin/discounts",
              )
            }
          >
            <span>🏷️</span>

            <div>
              <strong>
                Offers & Discounts
              </strong>

              <small>
                Create restaurant
                offers
              </small>
            </div>
          </button>

          <button
            type="button"
            onClick={() =>
              goTo("/admin/reviews")
            }
          >
            <span>⭐</span>

            <div>
              <strong>
                Reviews
              </strong>

              <small>
                Read customer
                feedback
              </small>
            </div>
          </button>

          <button
            type="button"
            onClick={() =>
              goTo("/admin/sales")
            }
          >
            <span>📈</span>

            <div>
              <strong>
                Sales
              </strong>

              <small>
                View revenue and
                sales reports
              </small>
            </div>
          </button>

          <button
            type="button"
            onClick={() =>
              goTo("/admin/support")
            }
          >
            <span>🎧</span>

            <div>
              <strong>
                Customer Support
              </strong>

              <small>
                Handle customer
                tickets
              </small>
            </div>
          </button>

          <button
            type="button"
            onClick={() =>
              goTo("/admin/profile")
            }
          >
            <span>🏪</span>

            <div>
              <strong>
                Restaurant Profile
              </strong>

              <small>
                Update restaurant
                information
              </small>
            </div>
          </button>
        </section>
      </main>
    </div>
  );
}

export default AdminDashboard;