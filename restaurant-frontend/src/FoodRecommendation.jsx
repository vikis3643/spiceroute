import { useState } from "react";

const RECOMMENDATION_API =
  `${import.meta.env.VITE_API_BASE_URL}/recommendations`;

const INITIAL_PREFERENCES = {
  maximumBudget: "300",
  vegetarian: "ANY",
  spiceLevel: "MEDIUM",
  tasteType: "SPICY",
  proteinLevel: "NORMAL",
};

function readStoredCart() {
  try {
    const savedCart =
      sessionStorage.getItem(
        "restaurantCart",
      );

    return savedCart
      ? JSON.parse(savedCart)
      : [];
  } catch {
    return [];
  }
}

function readableName(value) {
  if (!value) {
    return "Not specified";
  }

  return value
    .toLowerCase()
    .split("_")
    .map(
      (word) =>
        word.charAt(0).toUpperCase() +
        word.slice(1),
    )
    .join(" ");
}

function FoodRecommendation({
  onBack,
  formatPrice,
}) {
  const [preferences, setPreferences] =
    useState(INITIAL_PREFERENCES);

  const [recommendations, setRecommendations] =
    useState([]);

  const [loading, setLoading] =
    useState(false);

  const [searched, setSearched] =
    useState(false);

  const [error, setError] = useState("");
  const [message, setMessage] =
    useState("");

  function handlePreferenceChange(event) {
    const { name, value } = event.target;

    setPreferences(
      (currentPreferences) => ({
        ...currentPreferences,
        [name]: value,
      }),
    );
  }

  async function findRecommendations(event) {
    event.preventDefault();

    const token =
      sessionStorage.getItem(
        "customerToken",
      );

    if (!token) {
      window.location.href =
        "/customer-login";
      return;
    }

    const requestBody = {
      maximumBudget: Number(
        preferences.maximumBudget,
      ),
      vegetarian:
        preferences.vegetarian === "ANY"
          ? null
          : preferences.vegetarian ===
            "VEGETARIAN",
      spiceLevel:
        preferences.spiceLevel ||
        null,
      tasteType:
        preferences.tasteType ||
        null,
      proteinLevel:
        preferences.proteinLevel ||
        null,
    };

    try {
      setLoading(true);
      setError("");
      setMessage("");
      setSearched(true);

      const response = await fetch(
        RECOMMENDATION_API,
        {
          method: "POST",
          headers: {
            "Content-Type":
              "application/json",
            Authorization:
              `Bearer ${token}`,
          },
          body: JSON.stringify(
            requestBody,
          ),
        },
      );

      if (response.status === 401) {
        sessionStorage.removeItem(
          "customerToken",
        );

        window.location.href =
          "/customer-login";

        return;
      }

      if (!response.ok) {
        throw new Error(
          "Unable to find recommendations",
        );
      }

      const recommendationData =
        await response.json();

      setRecommendations(
        recommendationData,
      );
    } catch {
      setError(
        "Recommendations could not be loaded. Make sure the backend is running.",
      );
    } finally {
      setLoading(false);
    }
  }

  function addToCart(menuItem) {
    const currentCart =
      readStoredCart();

    const existingItem =
      currentCart.find(
        (item) =>
          item.id === menuItem.id,
      );

    const updatedCart = existingItem
      ? currentCart.map((item) =>
          item.id === menuItem.id
            ? {
                ...item,
                quantity:
                  item.quantity + 1,
              }
            : item,
        )
      : [
          ...currentCart,
          {
            ...menuItem,
            quantity: 1,
          },
        ];

    sessionStorage.setItem(
      "restaurantCart",
      JSON.stringify(updatedCart),
    );

    setMessage(
      `${menuItem.name} added to your cart.`,
    );

    setError("");
  }

  return (
    <main className="recommendation-page">
      <header className="recommendation-header">
        <div>
          <span className="eyebrow">
            Smart food assistant
          </span>

          <h1>
            Find your perfect dish
          </h1>

          <p>
            Tell us your budget and food
            preferences. We will recommend
            the best matching dishes.
          </p>
        </div>

        <button
          type="button"
          onClick={onBack}
        >
          Back to restaurant
        </button>
      </header>

      <section className="recommendation-layout">
        <form
          className="recommendation-form"
          onSubmit={findRecommendations}
        >
          <h2>Your preferences</h2>

          <label>
            Maximum budget
            <input
              type="number"
              name="maximumBudget"
              value={
                preferences.maximumBudget
              }
              onChange={
                handlePreferenceChange
              }
              min="1"
              step="1"
              required
            />
          </label>

          <label>
            Food type
            <select
              name="vegetarian"
              value={
                preferences.vegetarian
              }
              onChange={
                handlePreferenceChange
              }
            >
              <option value="ANY">
                Vegetarian and non-vegetarian
              </option>
              <option value="VEGETARIAN">
                Vegetarian
              </option>
              <option value="NON_VEGETARIAN">
                Non-vegetarian
              </option>
            </select>
          </label>

          <label>
            Spice level
            <select
              name="spiceLevel"
              value={
                preferences.spiceLevel
              }
              onChange={
                handlePreferenceChange
              }
            >
              <option value="MILD">
                Mild
              </option>
              <option value="MEDIUM">
                Medium
              </option>
              <option value="HOT">
                Hot
              </option>
            </select>
          </label>

          <label>
            Preferred taste
            <select
              name="tasteType"
              value={
                preferences.tasteType
              }
              onChange={
                handlePreferenceChange
              }
            >
              <option value="SPICY">
                Spicy
              </option>
              <option value="SWEET">
                Sweet
              </option>
              <option value="CREAMY">
                Creamy
              </option>
              <option value="TANGY">
                Tangy
              </option>
              <option value="SAVOURY">
                Savoury
              </option>
            </select>
          </label>

          <label>
            Protein preference
            <select
              name="proteinLevel"
              value={
                preferences.proteinLevel
              }
              onChange={
                handlePreferenceChange
              }
            >
              <option value="NORMAL">
                Normal
              </option>
              <option value="HIGH">
                High protein
              </option>
            </select>
          </label>

          <button
            className="recommendation-submit"
            type="submit"
            disabled={loading}
          >
            {loading
              ? "Finding dishes..."
              : "Recommend food"}
          </button>
        </form>

        <section className="recommendation-results">
          <div className="recommendation-results-heading">
            <span className="eyebrow">
              Recommended for you
            </span>

            <h2>Best matches</h2>
          </div>

          {error && (
            <div className="status-message error-message">
              {error}
            </div>
          )}

          {message && (
            <div className="recommendation-message">
              {message}
            </div>
          )}

          {!searched && (
            <div className="recommendation-empty">
              <span>✨</span>

              <h3>
                Ready to find your meal?
              </h3>

              <p>
                Complete your preferences
                and select Recommend food.
              </p>
            </div>
          )}

          {searched &&
            !loading &&
            !error &&
            recommendations.length ===
              0 && (
              <div className="recommendation-empty">
                <span>🍽️</span>

                <h3>
                  No matching dishes found
                </h3>

                <p>
                  Increase your budget or
                  change a preference.
                </p>
              </div>
            )}

          <div className="recommendation-grid">
            {recommendations.map(
              (recommendation) => {
                const menuItem =
                  recommendation.menuItem;

                return (
                  <article
                    className="recommendation-card"
                    key={menuItem.id}
                  >
                    <div className="recommendation-card-image">
                      {menuItem.imageUrl ? (
                        <img
                          src={
                            menuItem.imageUrl
                          }
                          alt={menuItem.name}
                        />
                      ) : (
                        <span>🍲</span>
                      )}

                      <strong>
                        {
                          recommendation.matchScore
                        }{" "}
                        match points
                      </strong>
                    </div>

                    <div className="recommendation-card-content">
                      <div className="recommendation-card-tags">
                        <span>
                          {menuItem.vegetarian
                            ? "Vegetarian"
                            : "Non-vegetarian"}
                        </span>

                        <span>
                          {readableName(
                            menuItem.spiceLevel,
                          )}
                        </span>

                        <span>
                          {readableName(
                            menuItem.proteinLevel,
                          )}
                        </span>
                      </div>

                      <h3>
                        {menuItem.name}
                      </h3>

                      <p>
                        {
                          menuItem.description
                        }
                      </p>

                      <ul>
                        {recommendation.reasons.map(
                          (reason) => (
                            <li key={reason}>
                              ✓ {reason}
                            </li>
                          ),
                        )}
                      </ul>

                      <div className="recommendation-card-footer">
                        <strong>
                          {formatPrice(
                            menuItem.price,
                          )}
                        </strong>

                        <button
                          type="button"
                          onClick={() =>
                            addToCart(
                              menuItem,
                            )
                          }
                        >
                          Add to cart
                        </button>
                      </div>
                    </div>
                  </article>
                );
              },
            )}
          </div>
        </section>
      </section>
    </main>
  );
}

export default FoodRecommendation;