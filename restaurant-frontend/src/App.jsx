import {
  useEffect,
  useMemo,
  useState,
} from "react";
import "./App.css";
import CheckoutModal from "./CheckoutModal";

const API_BASE_URL =
  `${import.meta.env.VITE_API_BASE_URL}`;

function App() {
   const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [categories, setCategories] =
    useState([]);

  const [menuItems, setMenuItems] =
    useState([]);

  const [
    selectedCategory,
    setSelectedCategory,
  ] = useState("all");

  const [searchText, setSearchText] =
    useState("");

  const [cart, setCart] = useState(() => {
    const savedCart =
      sessionStorage.getItem(
        "restaurantCart",
      );

    if (!savedCart) {
      return [];
    }

    try {
      return JSON.parse(savedCart);
    } catch {
      return [];
    }
  });

  const [wishlistIds, setWishlistIds] =
    useState([]);

  const [
    updatingWishlistId,
    setUpdatingWishlistId,
  ] = useState(null);

  const [
    wishlistMessage,
    setWishlistMessage,
  ] = useState("");

  const [
    checkoutOpen,
    setCheckoutOpen,
  ] = useState(false);

  const [
    accountMenuOpen,
    setAccountMenuOpen,
  ] = useState(false);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const customerName =
    sessionStorage.getItem(
      "customerName",
    );

  const customerEmail =
    sessionStorage.getItem(
      "customerEmail",
    );

  const customerToken =
    sessionStorage.getItem(
      "customerToken",
    );

  useEffect(() => {
    loadRestaurantData();
  }, []);

  useEffect(() => {
    sessionStorage.setItem(
      "restaurantCart",
      JSON.stringify(cart),
    );
  }, [cart]);

  useEffect(() => {
    if (!customerToken) {
      return undefined;
    }

    let requestIsActive = true;

    fetch(`${API_BASE_URL}/wishlist`, {
      headers: {
        Authorization:
          `Bearer ${customerToken}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            "Unable to load wishlist",
          );
        }

        return response.json();
      })
      .then((wishlistItems) => {
        if (requestIsActive) {
          setWishlistIds(
            wishlistItems.map(
              (wishlistItem) =>
                wishlistItem.menuItem.id,
            ),
          );
        }
      })
      .catch(() => {
        // The menu remains usable if wishlist loading fails.
      });

    return () => {
      requestIsActive = false;
    };
  }, [customerToken]);

  async function loadRestaurantData() {
  try {
    setLoading(true);
    setError("");

    const menuResponse =
      await fetch(
        `${API_BASE_URL}/marketplace/menu-items`,
      );

    if (!menuResponse.ok) {
      throw new Error(
        "Unable to load marketplace",
      );
    }

    const menuData =
      await menuResponse.json();

    setMenuItems(menuData);

   // Build marketplace categories by NAME.
//
// Different restaurants can have their own
// category IDs for the same category:
//
// SpiceRoute  -> Pizza (id 9)
// Pizza Point -> pizza (id 20)
//
// Customer marketplace should show only
// one "Pizza" category.

const categoryMap = new Map();

menuData.forEach((item) => {
  if (!item.categoryName) {
    return;
  }

  const normalizedName =
    item.categoryName
      .trim()
      .toLowerCase();

  if (
    !categoryMap.has(
      normalizedName,
    )
  ) {
    const displayName =
      item.categoryName
        .trim()
        .replace(
          /\b\w/g,
          (letter) =>
            letter.toUpperCase(),
        );

    categoryMap.set(
      normalizedName,
      {
        id: normalizedName,
        name: displayName,
      },
    );
  }
});

const marketplaceCategories =
  Array.from(
    categoryMap.values(),
  ).sort((a, b) =>
    a.name.localeCompare(
      b.name,
    ),
  );

setCategories(
  marketplaceCategories,
);
  } catch {
    setError(
      "Could not load the food marketplace. Please make sure Spring Boot is running.",
    );
  } finally {
    setLoading(false);
  }
}

  async function toggleWishlist(
    menuItem,
  ) {
    if (!customerToken) {
      window.location.href =
        "/customer-login";
      return;
    }

    const itemIsSaved =
      wishlistIds.includes(
        menuItem.id,
      );

    try {
      setUpdatingWishlistId(
        menuItem.id,
      );

      setWishlistMessage("");

      const response = await fetch(
        `${API_BASE_URL}/wishlist/${menuItem.id}`,
        {
          method: itemIsSaved
            ? "DELETE"
            : "POST",
          headers: {
            Authorization:
              `Bearer ${customerToken}`,
          },
        },
      );

      if (
        response.status === 401 ||
        response.status === 403
      ) {
        logoutCustomer();
        return;
      }

      if (!response.ok) {
        throw new Error(
          "Wishlist could not be updated",
        );
      }

      setWishlistIds(
        (currentIds) =>
          itemIsSaved
            ? currentIds.filter(
                (id) =>
                  id !== menuItem.id,
              )
            : [
                ...currentIds,
                menuItem.id,
              ],
      );

      setWishlistMessage(
        itemIsSaved
          ? `${menuItem.name} removed from your wishlist.`
          : `${menuItem.name} added to your wishlist.`,
      );
    } catch {
      setWishlistMessage(
        "Wishlist could not be updated. Please try again.",
      );
    } finally {
      setUpdatingWishlistId(null);
    }
  }

 const filteredMenuItems =
  useMemo(() => {
    return menuItems.filter(
      (item) => {
        const itemCategory =
          item.categoryName
            ?.trim()
            .toLowerCase() ||
          "";

        const belongsToCategory =
          selectedCategory ===
            "all" ||
          itemCategory ===
            selectedCategory;

        const matchesSearch =
          item.name
            .toLowerCase()
            .includes(
              searchText
                .toLowerCase()
                .trim(),
            );

        return (
          belongsToCategory &&
          matchesSearch
        );
      },
    );
  }, [
    menuItems,
    selectedCategory,
    searchText,
  ]);

  const cartCount = cart.reduce(
    (total, item) =>
      total + item.quantity,
    0,
  );

  const cartTotal = cart.reduce(
    (total, item) =>
      total +
      Number(item.price) *
        item.quantity,
    0,
  );
function addToCart(menuItem) {
  setCart((currentCart) => {
    // ==========================================
    // EMPTY CART
    // ==========================================

    if (currentCart.length === 0) {
      return [
        {
          ...menuItem,
          quantity: 1,
        },
      ];
    }

    // ==========================================
    // CURRENT CART RESTAURANT
    // ==========================================

    const currentRestaurantId =
      currentCart[0]?.restaurantId;

    const currentRestaurantName =
      currentCart[0]?.restaurantName ||
      "another restaurant";

    const newRestaurantId =
      menuItem.restaurantId;

    const newRestaurantName =
      menuItem.restaurantName ||
      "this restaurant";

    // ==========================================
    // DIFFERENT RESTAURANT
    // ==========================================

    if (
      currentRestaurantId != null &&
      newRestaurantId != null &&
      currentRestaurantId !==
        newRestaurantId
    ) {
      const shouldStartNewCart =
        window.confirm(
          `Your cart already contains items from ${currentRestaurantName}.\n\n` +
            `Adding this item will clear your current cart and start a new order from ${newRestaurantName}.\n\n` +
            `Do you want to continue?`,
        );

      if (!shouldStartNewCart) {
        return currentCart;
      }

      return [
        {
          ...menuItem,
          quantity: 1,
        },
      ];
    }

    // ==========================================
    // SAME ITEM
    // ==========================================

    const existingItem =
      currentCart.find(
        (cartItem) =>
          cartItem.id === menuItem.id,
      );

    if (existingItem) {
      return currentCart.map(
        (cartItem) =>
          cartItem.id ===
          menuItem.id
            ? {
                ...cartItem,
                quantity:
                  cartItem.quantity +
                  1,
              }
            : cartItem,
      );
    }

    // ==========================================
    // SAME RESTAURANT, NEW ITEM
    // ==========================================

    return [
      ...currentCart,
      {
        ...menuItem,
        quantity: 1,
      },
    ];
  });
}

  function updateQuantity(
    itemId,
    change,
  ) {
    setCart((currentCart) =>
      currentCart
        .map((item) =>
          item.id === itemId
            ? {
                ...item,
                quantity:
                  item.quantity +
                  change,
              }
            : item,
        )
        .filter(
          (item) =>
            item.quantity > 0,
        ),
    );
  }

  function openCart() {
    document
      .getElementById("cart")
      ?.scrollIntoView({
        behavior: "smooth",
      });
  }

  function openCheckout() {
    if (cart.length > 0) {
      setCheckoutOpen(true);
    }
  }

  function handleOrderPlaced() {
    setCart([]);
  }

  function logoutCustomer() {
    sessionStorage.removeItem(
      "customerToken",
    );

    sessionStorage.removeItem(
      "customerId",
    );

    sessionStorage.removeItem(
      "customerName",
    );

    sessionStorage.removeItem(
      "customerEmail",
    );

    window.location.reload();
  }

  function formatPrice(price) {
    return new Intl.NumberFormat(
      "en-IN",
      {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0,
      },
    ).format(price);
  }

  return (
    <div className="app">
  <header className="navbar">
    <a className="brand" href="#">
      <span className="brand-icon">
        <img
          src="/spiceroute-logo.png"
          alt="SpiceRoute"
          className="brand-logo-image"
        />
      </span>

      <span>
        <strong>SpiceRoute</strong>

        <small>
          Fresh food, fast delivery
        </small>
      </span>
    </a>
<button
  type="button"
  className="mobile-menu-toggle"
  aria-label="Toggle navigation menu"
  aria-expanded={mobileMenuOpen}
  onClick={() =>
    setMobileMenuOpen((current) => !current)
  }
>
  <span></span>
  <span></span>
  <span></span>
</button>
              <nav>
          <a href="#menu">Menu</a>

          <a href="#about">About</a>

          <a
            className="recommendation-nav-link"
            href="/recommend-food"
          >
            ✨ Recommend Food
          </a>
          {customerName ? (
            <div className="customer-nav-actions">
              <a
                className="profile-nav-link"
                href="/customer-profile"
                title={customerEmail}
              >
                👤 My Profile
              </a>

              <div className="account-menu-wrapper">
                <button
                  className="account-menu-trigger"
                  type="button"
                  aria-expanded={accountMenuOpen}
                  aria-haspopup="menu"
                  onClick={() =>
                    setAccountMenuOpen(
                      (current) => !current,
                    )
                  }
                >
                  <span>My Account</span>
                  <span
                    className={`account-menu-arrow ${
                      accountMenuOpen ? "open" : ""
                    }`}
                    aria-hidden="true"
                  >
                    ▾
                  </span>
                </button>

                {accountMenuOpen && (
                  <div
                    className="account-dropdown"
                    role="menu"
                  >
                    <div className="account-dropdown-header">
                      <strong>{customerName}</strong>
                      <small>{customerEmail}</small>
                    </div>

                    <a href="/my-orders" role="menuitem">
                      <span>🛒</span>
                      <span>My Orders</span>
                    </a>

                    <a href="/wishlist" role="menuitem">
                      <span>❤️</span>
                      <span>My Wishlist</span>
                    </a>

                    <a href="/support" role="menuitem">
                      <span>🎧</span>
                      <span>Customer Support</span>
                    </a>

                    <button
                      className="account-logout-button"
                      type="button"
                      role="menuitem"
                      onClick={logoutCustomer}
                    >
                      <span>🚪</span>
                      <span>Logout</span>
                    </button>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <a
              className="customer-login-link"
              href="/customer-login"
            >
              Customer Login
            </a>
          )}

          <button
            className="cart-button"
            type="button"
            onClick={openCart}
          >
            Cart <span>{cartCount}</span>
          </button>
        </nav>
{mobileMenuOpen && (
  <div className="mobile-menu">
    <a
      href="#menu"
      onClick={() => setMobileMenuOpen(false)}
    >
      Menu
    </a>

    <a
      href="#about"
      onClick={() => setMobileMenuOpen(false)}
    >
      About
    </a>

    <a
      href="/recommend-food"
      onClick={() => setMobileMenuOpen(false)}
    >
      ✨ Recommend Food
    </a>

    {customerName ? (
      <>
        <a
          href="/customer-profile"
          onClick={() => setMobileMenuOpen(false)}
        >
          👤 My Profile
        </a>

        <a
          href="/my-orders"
          onClick={() => setMobileMenuOpen(false)}
        >
          🛒 My Orders
        </a>

        <a
          href="/wishlist"
          onClick={() => setMobileMenuOpen(false)}
        >
          ❤️ My Wishlist
        </a>

        <a
          href="/support"
          onClick={() => setMobileMenuOpen(false)}
        >
          🎧 Customer Support
        </a>

        <button
          type="button"
          onClick={() => {
            setMobileMenuOpen(false);
            logoutCustomer();
          }}
          className="mobile-menu-logout"
        >
          🚪 Logout
        </button>
      </>
    ) : (
      <a
        href="/customer-login"
        onClick={() => setMobileMenuOpen(false)}
      >
        Customer Login
      </a>
    )}
  </div>
)}
      </header>

      <main>
       <section className="hero">

  {/* ==========================================
      HERO BACKGROUND VIDEO
      ========================================== */}

  <video
    className="hero-background-video"
    autoPlay
    muted
    loop
    playsInline
    preload="auto"
  >
    <source
      src="/sr-video.mp4"
      type="video/mp4"
    />
  </video>

  <div className="hero-video-overlay" />

  {/* ==========================================
      EXISTING HERO CONTENT
      ========================================== */}

  <div className="hero-content">
    <span className="eyebrow">
      Made fresh every day
    </span>

    <h1>
      Delicious food delivered
      to your door.
    </h1>

    <p>
      Explore comforting Indian
      favourites prepared with
      fresh ingredients and a
      generous pinch of
      happiness.
    </p>

    <a
      className="primary-button"
      href="#menu"
    >
      Explore our menu
    </a>

    <div className="hero-stats">
      <div>
        <strong>
          {menuItems.length}+
        </strong>

        <span>
          Fresh dishes
        </span>
      </div>

      <div>
        <strong>
          30 min
        </strong>

        <span>
          Average delivery
        </span>
      </div>

      <div>
        <strong>
          4.8
        </strong>

        <span>
          Customer rating
        </span>
      </div>
    </div>
  </div>

  {/* ==========================================
      EXISTING RIGHT VISUAL
      ========================================== */}

  <div className="hero-visual">
   
  </div>

</section>

        <section
          className="menu-section"
          id="menu"
        >
          <div className="section-heading">
            <div>
              <span className="eyebrow">
                Our menu
              </span>

              <h2>
                Choose your favourite
                dish
              </h2>
            </div>

            <label className="search-box">
              <span>⌕</span>

              <input
                type="search"
                placeholder="Search food..."
                value={searchText}
                onChange={(event) =>
                  setSearchText(
                    event.target.value,
                  )
                }
              />
            </label>
          </div>

          <div className="category-list">
            <button
              className={
                selectedCategory ===
                "all"
                  ? "active"
                  : ""
              }
              onClick={() =>
                setSelectedCategory(
                  "all",
                )
              }
              type="button"
            >
              All dishes
            </button>

            {categories.map(
              (category) => (
                <button
                  className={
                    selectedCategory ===
                    String(category.id)
                      ? "active"
                      : ""
                  }
                  key={category.id}
                  onClick={() =>
                    setSelectedCategory(
                      String(
                        category.id,
                      ),
                    )
                  }
                  type="button"
                >
                  {category.name}
                </button>
              ),
            )}
          </div>

          {wishlistMessage && (
            <div className="wishlist-toast">
              {wishlistMessage}

              {customerToken && (
                <a href="/wishlist">
                  View wishlist
                </a>
              )}
            </div>
          )}

          {loading && (
            <div className="status-message">
              Loading the menu...
            </div>
          )}

          {error && (
            <div className="status-message error-message">
              <p>{error}</p>

              <button
                type="button"
                onClick={
                  loadRestaurantData
                }
              >
                Try again
              </button>
            </div>
          )}

          {!loading &&
            !error &&
            filteredMenuItems.length ===
              0 && (
              <div className="status-message">
                No dishes match your
                selection.
              </div>
            )}

          <div className="content-layout">
            <div className="menu-grid">
              {filteredMenuItems.map(
                (item) => {
                  const itemIsSaved =
                    wishlistIds.includes(
                      item.id,
                    );

                  return (
                    <article
                      className="menu-card"
                      key={item.id}
                    >
                      <div className="menu-image">
                        {item.imageUrl ? (
                          <img
                            src={
                              item.imageUrl
                            }
                            alt={item.name}
                          />
                        ) : (
                          <span>🍲</span>
                        )}

                        <span
                          className={
                            item.vegetarian
                              ? "food-type veg"
                              : "food-type non-veg"
                          }
                        >
                          {item.vegetarian
                            ? "VEG"
                            : "NON-VEG"}
                        </span>

                        <button
                          className={`wishlist-heart ${
                            itemIsSaved
                              ? "wishlist-heart-active"
                              : ""
                          }`}
                          type="button"
                          disabled={
                            updatingWishlistId ===
                            item.id
                          }
                          aria-label={
                            itemIsSaved
                              ? `Remove ${item.name} from wishlist`
                              : `Add ${item.name} to wishlist`
                          }
                          title={
                            customerToken
                              ? itemIsSaved
                                ? "Remove from wishlist"
                                : "Add to wishlist"
                              : "Login to use wishlist"
                          }
                          onClick={() =>
                            toggleWishlist(
                              item,
                            )
                          }
                        >
                          {updatingWishlistId ===
                          item.id
                            ? "…"
                            : itemIsSaved
                              ? "♥"
                              : "♡"}
                        </button>
                      </div>

                      <div className="menu-card-body">
                        <span className="category-name">
                        {item.categoryName}
                          </span>
                        <span
  className="restaurant-name"
  title={`${item.restaurantCity || ""}${
    item.restaurantState
      ? `, ${item.restaurantState}`
      : ""
  }`}
>
  🏪 {item.restaurantName}
</span>
                        <h3>{item.name}</h3>

                        <p>
                          {
                            item.description
                          }
                        </p>

                        <div className="menu-card-footer">
                          <strong>
                            {formatPrice(
                              item.price,
                            )}
                          </strong>

                          <button
                            type="button"
                            onClick={() =>
                              addToCart(
                                item,
                              )
                            }
                          >
                            Add +
                          </button>
                        </div>
                      </div>
                    </article>
                  );
                },
              )}
            </div>

            <aside
              className="cart-panel"
              id="cart"
            >
              <div className="cart-title">
  <div>
    <span className="eyebrow">
      Your order
    </span>

    <h2>Cart</h2>

    {cart.length > 0 && (
      <small>
        🏪 {cart[0].restaurantName}
      </small>
    )}
  </div>

  <span className="cart-count">
    {cartCount}
  </span>
</div>

              {cart.length === 0 ? (
                <div className="empty-cart">
                  <span>🛒</span>

                  <p>
                    Your cart is empty.
                  </p>

                  <small>
                    Add something
                    delicious from the
                    menu.
                  </small>
                </div>
              ) : (
                <>
                  <div className="cart-items">
                    {cart.map(
                      (item) => (
                        <div
                          className="cart-item"
                          key={item.id}
                        >
                          <div>
                            <strong>
                              {item.name}
                            </strong>

                            <span>
                              {formatPrice(
                                item.price,
                              )}
                            </span>
                          </div>

                          <div className="quantity-controls">
                            <button
                              type="button"
                              onClick={() =>
                                updateQuantity(
                                  item.id,
                                  -1,
                                )
                              }
                            >
                              −
                            </button>

                            <span>
                              {
                                item.quantity
                              }
                            </span>

                            <button
                              type="button"
                              onClick={() =>
                                updateQuantity(
                                  item.id,
                                  1,
                                )
                              }
                            >
                              +
                            </button>
                          </div>
                        </div>
                      ),
                    )}
                  </div>

                  <div className="cart-total">
                    <span>Subtotal</span>

                    <strong>
                      {formatPrice(
                        cartTotal,
                      )}
                    </strong>
                  </div>

                  <button
                    className="checkout-button"
                    type="button"
                    onClick={openCheckout}
                  >
                    Continue to checkout
                  </button>
                </>
              )}
            </aside>
          </div>
        </section>

        <section
          className="about-section"
          id="about"
        >
          <span className="eyebrow">
            Why SpiceRoute?
          </span>

          <h2>
            Good food should feel like
            home.
          </h2>

          <div className="benefit-grid">
            <article>
              <span>🥬</span>

              <h3>
                Fresh ingredients
              </h3>

              <p>
                Carefully selected
                ingredients prepared every
                day.
              </p>
            </article>

            <article>
              <span>👨‍🍳</span>

              <h3>Skilled chefs</h3>

              <p>
                Authentic flavours created
                by experienced chefs.
              </p>
            </article>

            <article>
              <span>🛵</span>

              <h3>Fast delivery</h3>

              <p>
                Your order arrives hot,
                fresh and right on time.
              </p>
            </article>
          </div>
        </section>
      </main>

      <footer>
        <strong>SpiceRoute</strong>

        <span>
          © 2026 SpiceRoute Restaurant.
          All rights reserved.
        </span>
      </footer>

      <CheckoutModal
        isOpen={checkoutOpen}
        cart={cart}
        onClose={() =>
          setCheckoutOpen(false)
        }
        onOrderPlaced={
          handleOrderPlaced
        }
        formatPrice={formatPrice}
      />
    </div>
  );
}

export default App;