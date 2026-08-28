import { useEffect, useState } from "react";

const WISHLIST_API =
  `${import.meta.env.VITE_API_BASE_URL}/wishlist`;

function clearCustomerSession() {
  sessionStorage.removeItem("customerToken");
  sessionStorage.removeItem("customerId");
  sessionStorage.removeItem("customerName");
  sessionStorage.removeItem("customerEmail");
}

function Wishlist({
  onBack,
  formatPrice,
}) {
  const [wishlistItems, setWishlistItems] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [message, setMessage] =
    useState("");

  const [removingId, setRemovingId] =
    useState(null);

  useEffect(() => {
    let requestIsActive = true;

    async function loadWishlist() {
      const token =
        sessionStorage.getItem(
          "customerToken",
        );

      if (!token) {
        window.location.href =
          "/customer-login";
        return;
      }

      try {
        const response = await fetch(
          WISHLIST_API,
          {
            headers: {
              Authorization:
                `Bearer ${token}`,
            },
          },
        );

        if (
          response.status === 401 ||
          response.status === 403
        ) {
          clearCustomerSession();

          window.location.href =
            "/customer-login";

          return;
        }

        if (!response.ok) {
          throw new Error(
            "Unable to load wishlist",
          );
        }

        const data =
          await response.json();

        if (requestIsActive) {
          setWishlistItems(data);
          setError("");
        }
      } catch {
        if (requestIsActive) {
          setError(
            "Could not load your wishlist.",
          );
        }
      } finally {
        if (requestIsActive) {
          setLoading(false);
        }
      }
    }

    loadWishlist();

    return () => {
      requestIsActive = false;
    };
  }, []);

  async function removeItem(menuItemId) {
    const token =
      sessionStorage.getItem(
        "customerToken",
      );

    try {
      setRemovingId(menuItemId);
      setError("");
      setMessage("");

      const response = await fetch(
        `${WISHLIST_API}/${menuItemId}`,
        {
          method: "DELETE",
          headers: {
            Authorization:
              `Bearer ${token}`,
          },
        },
      );

      if (
        response.status === 401 ||
        response.status === 403
      ) {
        clearCustomerSession();

        window.location.href =
          "/customer-login";

        return;
      }

      if (!response.ok) {
        throw new Error(
          "Unable to remove item",
        );
      }

      setWishlistItems(
        (currentItems) =>
          currentItems.filter(
            (wishlistItem) =>
              wishlistItem.menuItem.id !==
              menuItemId,
          ),
      );

      setMessage(
        "Item removed from your wishlist.",
      );
    } catch {
      setError(
        "The item could not be removed.",
      );
    } finally {
      setRemovingId(null);
    }
  }

  function addToCart(menuItem) {
    let currentCart = [];

    try {
      currentCart = JSON.parse(
        sessionStorage.getItem(
          "restaurantCart",
        ) ?? "[]",
      );
    } catch {
      // Keep the existing empty array when saved data is invalid.
    }

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
    <main className="wishlist-page">
      <header className="wishlist-header">
        <div>
          <span className="eyebrow">
            Customer account
          </span>

          <h1>My Wishlist</h1>

          <p>
            Save your favourite dishes and
            order them whenever you want.
          </p>
        </div>

        <button
          type="button"
          onClick={onBack}
        >
          Back to restaurant
        </button>
      </header>

      <section className="wishlist-content">
        {loading && (
          <div className="status-message">
            Loading your wishlist...
          </div>
        )}

        {error && (
          <div className="status-message error-message">
            {error}
          </div>
        )}

        {message && (
          <div className="wishlist-message">
            {message}
          </div>
        )}

        {!loading &&
          !error &&
          wishlistItems.length === 0 && (
            <div className="empty-wishlist">
              <span>♡</span>

              <h2>
                Your wishlist is empty
              </h2>

              <p>
                Select the heart on a dish
                to save it here.
              </p>

              <button
                type="button"
                onClick={onBack}
              >
                Explore menu
              </button>
            </div>
          )}

        <div className="wishlist-grid">
          {wishlistItems.map(
            (wishlistItem) => {
              const menuItem =
                wishlistItem.menuItem;

              return (
                <article
                  className="wishlist-card"
                  key={wishlistItem.id}
                >
                  <div className="wishlist-card-image">
                    {menuItem.imageUrl ? (
                      <img
                        src={
                          menuItem.imageUrl
                        }
                        alt={menuItem.name}
                      />
                    ) : (
                      <span>🍽️</span>
                    )}
                  </div>

                  <div className="wishlist-card-content">
                    <div>
                      <span>
                        {menuItem.vegetarian
                          ? "🟢 Vegetarian"
                          : "🔴 Non-vegetarian"}
                      </span>

                      <h2>
                        {menuItem.name}
                      </h2>

                      <p>
                        {
                          menuItem.description
                        }
                      </p>
                    </div>

                    <strong>
                      {formatPrice(
                        menuItem.price,
                      )}
                    </strong>

                    <div className="wishlist-card-actions">
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

                      <button
                        className="wishlist-remove-button"
                        type="button"
                        disabled={
                          removingId ===
                          menuItem.id
                        }
                        onClick={() =>
                          removeItem(
                            menuItem.id,
                          )
                        }
                      >
                        {removingId ===
                        menuItem.id
                          ? "Removing..."
                          : "Remove"}
                      </button>
                    </div>
                  </div>
                </article>
              );
            },
          )}
        </div>
      </section>
    </main>
  );
}

export default Wishlist;