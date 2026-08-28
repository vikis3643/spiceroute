import { useEffect, useState } from "react";

const MY_ORDERS_API =
  `${import.meta.env.VITE_API_BASE_URL}/orders/my-orders`;

const ORDERS_API =
  `${import.meta.env.VITE_API_BASE_URL}/orders`;

const ORDER_PROGRESS = [
  "PLACED",
  "CONFIRMED",
  "PREPARING",
  "READY",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
];

function clearCustomerSession() {
  sessionStorage.removeItem("customerToken");
  sessionStorage.removeItem("customerId");
  sessionStorage.removeItem("customerName");
  sessionStorage.removeItem("customerEmail");
}

function handleUnauthorizedResponse(response) {
  if (response.status === 401) {
    clearCustomerSession();
    window.location.href = "/customer-login";

    throw new Error("Your login session expired");
  }
}

async function requestMyOrders() {
  const token =
    sessionStorage.getItem("customerToken");

  const response = await fetch(MY_ORDERS_API, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  handleUnauthorizedResponse(response);

  if (!response.ok) {
    throw new Error("Unable to load your orders");
  }

  return response.json();
}

function MyOrders({ onBack, formatPrice }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [cancellingOrderId, setCancellingOrderId] =
    useState(null);

  const customerName =
    sessionStorage.getItem("customerName");

  useEffect(() => {
    let requestIsActive = true;

    requestMyOrders()
      .then((orderData) => {
        if (requestIsActive) {
          setOrders(orderData);
          setError("");
        }
      })
      .catch(() => {
        if (requestIsActive) {
          setError(
            "Could not load your orders. Please try again.",
          );
        }
      })
      .finally(() => {
        if (requestIsActive) {
          setLoading(false);
        }
      });

    return () => {
      requestIsActive = false;
    };
  }, []);

  async function refreshOrders() {
    try {
      setLoading(true);
      setError("");

      const orderData =
        await requestMyOrders();

      setOrders(orderData);
    } catch {
      setError(
        "Could not refresh your orders.",
      );
    } finally {
      setLoading(false);
    }
  }

  async function cancelOrder(orderId) {
    const confirmed = window.confirm(
      `Are you sure you want to cancel Order #${orderId}?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      setCancellingOrderId(orderId);
      setError("");

      const token =
        sessionStorage.getItem(
          "customerToken",
        );

      const response = await fetch(
        `${ORDERS_API}/${orderId}/cancel`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );

      handleUnauthorizedResponse(response);

      if (!response.ok) {
        throw new Error(
          "This order could not be cancelled.",
        );
      }

      const cancelledOrder =
        await response.json();

      setOrders((currentOrders) =>
        currentOrders.map((order) =>
          order.id === cancelledOrder.id
            ? cancelledOrder
            : order,
        ),
      );
    } catch (requestError) {
      setError(
        requestError.message ||
          "The order could not be cancelled.",
      );
    } finally {
      setCancellingOrderId(null);
    }
  }

  function formatDate(dateValue) {
    return new Intl.DateTimeFormat("en-IN", {
      dateStyle: "medium",
      timeStyle: "short",
    }).format(new Date(dateValue));
  }

  function formatStatus(status) {
    if (!status) {
      return "Not available";
    }

    return status
      .toLowerCase()
      .split("_")
      .map(
        (word) =>
          word.charAt(0).toUpperCase() +
          word.slice(1),
      )
      .join(" ");
  }

  function formatPaymentMethod(method) {
    if (method === "DEMO_RAZORPAY") {
      return "Razorpay Demo";
    }

    return "Cash on delivery";
  }

  function formatPaymentStatus(order) {
    if (
      order.paymentMethod ===
      "DEMO_RAZORPAY"
    ) {
      if (order.paymentStatus === "PAID") {
        return "Paid (Demo)";
      }

      return formatStatus(
        order.paymentStatus,
      );
    }

    return "Pay on delivery";
  }

  function getProgressIndex(status) {
    return ORDER_PROGRESS.indexOf(status);
  }

  function canCancelOrder(status) {
    return (
      status === "PLACED" ||
      status === "CONFIRMED"
    );
  }

  return (
    <main className="my-orders-page">
      <header className="my-orders-header">
        <div>
          <span className="eyebrow">
            Customer account
          </span>

          <h1>My Orders</h1>

          <p>
            Welcome, {customerName}. Track
            your current orders and view your
            order history.
          </p>
        </div>

        <div className="my-orders-header-actions">
          <button
            type="button"
            onClick={refreshOrders}
          >
            Refresh
          </button>

          <button
            type="button"
            onClick={onBack}
          >
            Back to restaurant
          </button>
        </div>
      </header>

      <section className="my-orders-content">
        {loading && (
          <div className="status-message">
            Loading your orders...
          </div>
        )}

        {error && (
          <div className="status-message error-message">
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          orders.length === 0 && (
            <div className="empty-orders-card">
              <span>🧾</span>

              <h2>No orders yet</h2>

              <p>
                Your future SpiceRoute orders
                will appear here.
              </p>

              <button
                type="button"
                onClick={onBack}
              >
                Explore the menu
              </button>
            </div>
          )}

        <div className="customer-orders-list">
          {orders.map((order) => {
            const progressIndex =
              getProgressIndex(order.status);

            return (
              <article
                className="customer-order-card"
                key={order.id}
              >
                <div className="customer-order-heading">
                  <div>
                    <span>
                      Order #{order.id}
                    </span>

                    <small>
                      {formatDate(
                        order.createdAt,
                      )}
                    </small>
                  </div>

                  <strong
                    className={`customer-status status-${order.status.toLowerCase()}`}
                  >
                    {formatStatus(
                      order.status,
                    )}
                  </strong>
                </div>

                {order.status ===
                "CANCELLED" ? (
                  <div className="cancelled-order-message">
                    This order was cancelled.
                  </div>
                ) : (
                  <div className="order-progress">
                    {ORDER_PROGRESS.map(
                      (status, index) => (
                        <div
                          className={
                            index <=
                            progressIndex
                              ? "completed"
                              : ""
                          }
                          key={status}
                        >
                          <span>
                            {index <
                            progressIndex
                              ? "✓"
                              : index ===
                                  progressIndex
                                ? "●"
                                : ""}
                          </span>

                          <small>
                            {formatStatus(
                              status,
                            )}
                          </small>
                        </div>
                      ),
                    )}
                  </div>
                )}

                <div className="customer-order-items">
                  {order.items.map(
                    (item) => (
                      <div key={item.id}>
                        <span>
                          {item.quantity} ×{" "}
                          {item.itemName}
                        </span>

                        <strong>
                          {formatPrice(
                            item.lineTotal,
                          )}
                        </strong>
                      </div>
                    ),
                  )}
                </div>

                <div className="customer-order-address">
                  <div>
                    <span>
                      Delivery address
                    </span>

                    <strong>
                      {
                        order.deliveryAddress
                      }
                    </strong>
                  </div>

                  <div
                    className={`customer-schedule-information ${
                      order.orderTiming ===
                      "SCHEDULED"
                        ? "customer-schedule-information-active"
                        : ""
                    }`}
                  >
                    <span>Order timing</span>

                    <strong>
                      {order.orderTiming ===
                      "SCHEDULED"
                        ? "🗓️ Scheduled delivery"
                        : "⚡ Order now"}
                    </strong>

                    {order.orderTiming ===
                      "SCHEDULED" && (
                      <>
                        <small>
                          Meal: {formatStatus(
                            order.mealSlot,
                          )}
                        </small>

                        <small>
                          Delivery: {formatDate(
                            order.scheduledFor,
                          )}
                        </small>
                      </>
                    )}
                  </div>

                  <div className="customer-payment-information">
                    <span>
                      Payment method
                    </span>

                    <strong>
                      {formatPaymentMethod(
                        order.paymentMethod,
                      )}
                    </strong>

                    <small
                      className={
                        order.paymentStatus ===
                        "PAID"
                          ? "payment-status-paid"
                          : ""
                      }
                    >
                      {formatPaymentStatus(
                        order,
                      )}
                    </small>

                    {order.transactionId && (
                      <small className="customer-transaction-id">
                        Transaction ID:{" "}
                        {order.transactionId}
                      </small>
                    )}
                  </div>
                </div>

                <div className="customer-order-total">
                  {Number(
                    order.discountAmount ?? 0,
                  ) > 0 && (
                    <span className="order-discount-line">
                      Discount: −{formatPrice(
                        order.discountAmount,
                      )}
                      {order.appliedDiscountNames
                        ? ` (${order.appliedDiscountNames})`
                        : ""}
                    </span>
                  )}

                  <span>
                    Delivery:{" "}
                    {Number(
                      order.deliveryFee,
                    ) === 0
                      ? "Free"
                      : formatPrice(
                          order.deliveryFee,
                        )}
                  </span>

                  <strong>
                    Total:{" "}
                    {formatPrice(
                      order.totalAmount,
                    )}
                  </strong>
                  {order.status === "DELIVERED" && (
                      <a
                      className="review-order-button"
                      href={`/review?orderId=${order.id}`}
                      >
                      Give review
                      </a>
                    )}
                  {canCancelOrder(
                    order.status,
                  ) && (
                    <button
                      className="cancel-order-button"
                      type="button"
                      disabled={
                        cancellingOrderId ===
                        order.id
                      }
                      onClick={() =>
                        cancelOrder(order.id)
                      }
                    >
                      {cancellingOrderId ===
                      order.id
                        ? "Cancelling..."
                        : "Cancel order"}
                    </button>
                  )}
                </div>
              </article>
            );
          })}
        </div>
      </section>
    </main>
  );
}

export default MyOrders;