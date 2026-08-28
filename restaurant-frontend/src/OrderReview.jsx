import { useState } from "react";

const REVIEWS_API =
  `${import.meta.env.VITE_API_BASE_URL}/reviews`;

function StarRating({
  label,
  value,
  onChange,
}) {
  return (
    <div className="review-rating-group">
      <span>{label}</span>

      <div className="star-rating">
        {[1, 2, 3, 4, 5].map(
          (rating) => (
            <button
              className={
                rating <= value
                  ? "star-selected"
                  : ""
              }
              type="button"
              key={rating}
              onClick={() =>
                onChange(rating)
              }
              aria-label={`${rating} stars for ${label}`}
            >
              ★
            </button>
          ),
        )}
      </div>

      <small>
        {value === 0
          ? "Select 1 to 5 stars"
          : `${value} out of 5 stars`}
      </small>
    </div>
  );
}

function OrderReview({
  orderId,
  onBack,
}) {
  const [foodRating, setFoodRating] =
    useState(0);

  const [
    customerServiceRating,
    setCustomerServiceRating,
  ] = useState(0);

  const [comment, setComment] =
    useState("");

  const [submitting, setSubmitting] =
    useState(false);

  const [error, setError] =
    useState("");

  const [savedReview, setSavedReview] =
    useState(null);

  async function submitReview(event) {
    event.preventDefault();

    if (
      foodRating === 0 ||
      customerServiceRating === 0
    ) {
      setError(
        "Please select both star ratings.",
      );
      return;
    }

    const token =
      sessionStorage.getItem(
        "customerToken",
      );

    if (!token) {
      window.location.href =
        `/customer-login`;
      return;
    }

    try {
      setSubmitting(true);
      setError("");

      const response = await fetch(
        `${REVIEWS_API}/orders/${orderId}`,
        {
          method: "POST",
          headers: {
            "Content-Type":
              "application/json",
            Authorization:
              `Bearer ${token}`,
          },
          body: JSON.stringify({
            foodRating,
            customerServiceRating,
            comment,
          }),
        },
      );

      if (
        response.status === 401 ||
        response.status === 403
      ) {
        window.location.href =
          "/customer-login";
        return;
      }

      if (response.status === 409) {
        throw new Error(
          "You have already reviewed this order.",
        );
      }

      if (!response.ok) {
        throw new Error(
          "Review could not be submitted. The order must be delivered first.",
        );
      }

      const savedData =
        await response.json();

      setSavedReview(savedData);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (!orderId) {
    return (
      <main className="review-page">
        <section className="review-card">
          <span className="eyebrow">
            Customer review
          </span>

          <h1>Order not selected</h1>

          <p>
            Open the review page from a
            delivered order.
          </p>

          <button
            className="review-submit-button"
            type="button"
            onClick={onBack}
          >
            View my orders
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="review-page">
      <section className="review-card">
        {savedReview ? (
          <div className="review-success">
            <span>✓</span>

            <span className="eyebrow">
              Review submitted
            </span>

            <h1>Thank you!</h1>

            <p>
              Your feedback for Order #
              {savedReview.orderId} has
              been saved.
            </p>

            <div>
              <strong>
                Food quality
              </strong>

              <span>
                {"★".repeat(
                  savedReview.foodRating,
                )}
                {"☆".repeat(
                  5 -
                    savedReview.foodRating,
                )}
              </span>
            </div>

            <div>
              <strong>
                Customer service
              </strong>

              <span>
                {"★".repeat(
                  savedReview
                    .customerServiceRating,
                )}
                {"☆".repeat(
                  5 -
                    savedReview
                      .customerServiceRating,
                )}
              </span>
            </div>

            <button
              className="review-submit-button"
              type="button"
              onClick={onBack}
            >
              Back to my orders
            </button>
          </div>
        ) : (
          <>
            <header className="review-heading">
              <span className="eyebrow">
                Customer review
              </span>

              <h1>
                How was your order?
              </h1>

              <p>
                Review SpiceRoute Order #
                {orderId}. Your feedback
                helps us improve.
              </p>
            </header>

            <form
              className="review-form"
              onSubmit={submitReview}
            >
              <StarRating
                label="Food quality"
                value={foodRating}
                onChange={setFoodRating}
              />

              <StarRating
                label="Customer service"
                value={
                  customerServiceRating
                }
                onChange={
                  setCustomerServiceRating
                }
              />

              <label className="review-comment">
                Write your review
                (optional)

                <textarea
                  value={comment}
                  onChange={(event) =>
                    setComment(
                      event.target.value,
                    )
                  }
                  placeholder="Tell us what you liked or what we can improve..."
                  rows="5"
                  maxLength="1000"
                />

                <small>
                  {comment.length}/1000
                </small>
              </label>

              {error && (
                <div className="checkout-error">
                  {error}
                </div>
              )}

              <button
                className="review-submit-button"
                type="submit"
                disabled={submitting}
              >
                {submitting
                  ? "Submitting review..."
                  : "Submit review"}
              </button>

              <button
                className="review-back-button"
                type="button"
                onClick={onBack}
              >
                Back to my orders
              </button>
            </form>
          </>
        )}
      </section>
    </main>
  );
}

export default OrderReview;