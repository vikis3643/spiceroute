import {
  useEffect,
  useMemo,
  useState,
} from "react";

import AdminNav from "./AdminNav.jsx";

const REVIEWS_API_URL =
  `${import.meta.env.VITE_API_BASE_URL}/restaurant-admin/reviews`;

function AdminReviews() {
  const [reviews, setReviews] =
    useState([]);

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

  async function loadReviews() {
    try {
      setLoading(true);
      setError("");

      const response = await fetch(
        REVIEWS_API_URL,
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
          "Unable to load reviews",
        );
      }

      const data =
        await response.json();

      setReviews(data);
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          "Could not load customer reviews. Make sure Spring Boot is running.",
        );
      }
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadReviews();
  }, []);

  const averageFoodRating =
    useMemo(() => {
      if (reviews.length === 0) {
        return 0;
      }

      const total =
        reviews.reduce(
          (sum, review) =>
            sum +
            Number(
              review.foodRating ??
                0,
            ),
          0,
        );

      return (
        total / reviews.length
      ).toFixed(1);
    }, [reviews]);

  const averageServiceRating =
    useMemo(() => {
      if (reviews.length === 0) {
        return 0;
      }

      const total =
        reviews.reduce(
          (sum, review) =>
            sum +
            Number(
              review.customerServiceRating ??
                0,
            ),
          0,
        );

      return (
        total / reviews.length
      ).toFixed(1);
    }, [reviews]);

  function formatDate(
    dateValue,
  ) {
    return new Intl.DateTimeFormat(
      "en-IN",
      {
        dateStyle: "medium",
        timeStyle: "short",
      },
    ).format(
      new Date(dateValue),
    );
  }

  function renderStars(
    rating,
  ) {
    const safeRating =
      Math.max(
        0,
        Math.min(
          5,
          Number(rating) || 0,
        ),
      );

    return (
      "★".repeat(
        safeRating,
      ) +
      "☆".repeat(
        5 - safeRating,
      )
    );
  }

  return (
    <div className="admin-page">
      <AdminNav
        activePage="reviews"
      />

      <header className="admin-header">
        <div>
          <span className="eyebrow">
            Customer feedback
          </span>

          <h1>
            Reviews
          </h1>

          <p>
            Read food and customer
            service ratings submitted
            for your restaurant.
          </p>
        </div>

        <div className="admin-header-actions">
          <button
            className="refresh-button"
            type="button"
            onClick={
              loadReviews
            }
          >
            Refresh
          </button>
        </div>
      </header>

      <main className="admin-content">
        <section className="admin-stats">
          <article>
            <span>
              Total reviews
            </span>

            <strong>
              {reviews.length}
            </strong>
          </article>

          <article>
            <span>
              Average food rating
            </span>

            <strong>
              {averageFoodRating} / 5
            </strong>
          </article>

          <article>
            <span>
              Average service rating
            </span>

            <strong>
              {averageServiceRating} / 5
            </strong>
          </article>
        </section>

        {loading && (
          <div className="status-message">
            Loading reviews...
          </div>
        )}

        {error && (
          <div className="status-message error-message">
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          reviews.length === 0 && (
            <div className="status-message">
              No customer reviews yet.
            </div>
          )}

        {!loading &&
          !error &&
          reviews.length > 0 && (
            <section className="admin-review-list">
              {reviews.map(
                (review) => (
                  <article
                    className="admin-review-card"
                    key={
                      review.id
                    }
                  >
                    <div className="admin-review-heading">
                      <div>
                        <span>
                          Review #
                          {
                            review.id
                          }
                        </span>

                        <small>
                          {formatDate(
                            review.createdAt,
                          )}
                        </small>
                      </div>
                    </div>

                    <div className="admin-review-ratings">
                      <div>
                        <span>
                          Food rating
                        </span>

                        <strong>
                          {renderStars(
                            review.foodRating,
                          )}
                        </strong>

                        <small>
                          {
                            review.foodRating
                          }{" "}
                          / 5
                        </small>
                      </div>

                      <div>
                        <span>
                          Customer service
                        </span>

                        <strong>
                          {renderStars(
                            review.customerServiceRating,
                          )}
                        </strong>

                        <small>
                          {
                            review.customerServiceRating
                          }{" "}
                          / 5
                        </small>
                      </div>
                    </div>

                    <div className="admin-review-comment">
                      <span>
                        Customer comment
                      </span>

                      <p>
                        {review.comment?.trim()
                          ? review.comment
                          : "No written comment was provided."}
                      </p>
                    </div>
                  </article>
                ),
              )}
            </section>
          )}
      </main>
    </div>
  );
}

export default AdminReviews;