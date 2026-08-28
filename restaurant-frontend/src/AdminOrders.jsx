import {
  useEffect,
  useState,
} from "react";

import AdminNav from "./AdminNav.jsx";

const ORDERS_API_URL =
  "http://localhost:8080/api/restaurant-admin/orders";

const ORDER_STATUSES = [
  "PLACED",
  "CONFIRMED",
  "PREPARING",
  "READY",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
  "CANCELLED",
];

function getAuthorizationHeaders() {
  const token =
    sessionStorage.getItem(
      "adminToken",
    );

  return {
    Authorization:
      `Bearer ${token}`,
  };
}

function handleUnauthorizedResponse(
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

async function requestOrders() {
  const response = await fetch(
    ORDERS_API_URL,
    {
      headers:
        getAuthorizationHeaders(),
    },
  );

  handleUnauthorizedResponse(
    response,
  );

  if (!response.ok) {
    throw new Error(
      "Unable to load orders",
    );
  }

  return response.json();
}

function AdminOrders({
  formatPrice,
}) {
  const [orders, setOrders] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [
    updatingOrderId,
    setUpdatingOrderId,
  ] = useState(null);

  useEffect(() => {
    let requestIsActive = true;

    requestOrders()
      .then((orderData) => {
        if (requestIsActive) {
          setOrders(orderData);
          setError("");
        }
      })
      .catch(
        (requestError) => {
          if (
            requestIsActive &&
            requestError.message !==
              "Admin session expired"
          ) {
            setError(
              "Could not load orders. Make sure Spring Boot is running.",
            );
          }
        },
      )
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
        await requestOrders();

      setOrders(orderData);
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          "Could not load orders. Make sure Spring Boot is running.",
        );
      }
    } finally {
      setLoading(false);
    }
  }

  async function updateOrderStatus(
    orderId,
    newStatus,
  ) {
    try {
      setUpdatingOrderId(
        orderId,
      );

      setError("");

      const response = await fetch(
        `${ORDERS_API_URL}/${orderId}/status`,
        {
          method: "PATCH",

          headers: {
            ...getAuthorizationHeaders(),
            "Content-Type":
              "application/json",
          },

          body: JSON.stringify({
            status: newStatus,
          }),
        },
      );

      handleUnauthorizedResponse(
        response,
      );

      if (!response.ok) {
        let message =
          "Unable to update order status";

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

      const updatedOrder =
        await response.json();

      setOrders(
        (currentOrders) =>
          currentOrders.map(
            (order) =>
              order.id ===
              updatedOrder.id
                ? updatedOrder
                : order,
          ),
      );
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          requestError.message ||
            "The order status could not be updated.",
        );
      }
    } finally {
      setUpdatingOrderId(null);
    }
  }

  function formatDate(
    dateValue,
  ) {
    if (!dateValue) {
      return "Not available";
    }

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

  function formatStatus(
    status,
  ) {
    if (!status) {
      return "Not available";
    }

    return status
      .toLowerCase()
      .split("_")
      .map(
        (word) =>
          word
            .charAt(0)
            .toUpperCase() +
          word.slice(1),
      )
      .join(" ");
  }

  function formatPaymentMethod(
    method,
  ) {
    if (
      method ===
      "DEMO_RAZORPAY"
    ) {
      return "Razorpay Demo";
    }

    return "Cash on delivery";
  }

  function formatPaymentStatus(
    order,
  ) {
    if (
      order.paymentMethod ===
      "DEMO_RAZORPAY"
    ) {
      if (
        order.paymentStatus ===
        "PAID"
      ) {
        return "Paid (Demo)";
      }

      return formatStatus(
        order.paymentStatus,
      );
    }

    return "Pay on delivery";
  }

  function getDeliveryMapUrl(
    order,
  ) {
    return `https://www.google.com/maps?q=${order.deliveryLatitude},${order.deliveryLongitude}`;
  }

  const totalRevenue =
    orders
      .filter(
        (order) =>
          order.status !==
          "CANCELLED",
      )
      .reduce(
        (total, order) =>
          total +
          Number(
            order.totalAmount,
          ),
        0,
      );

  const activeOrders =
    orders.filter(
      (order) =>
        order.status !==
          "DELIVERED" &&
        order.status !==
          "CANCELLED",
    ).length;

  return (
    <div className="admin-page">
      <AdminNav
        activePage="orders"
      />

      <header className="admin-header">
        <div>
          <span className="eyebrow">
            Restaurant management
          </span>

          <h1>
            Orders
          </h1>

          <p>
            View your restaurant
            orders and update their
            progress.
          </p>
        </div>

        <div className="admin-header-actions">
          <button
            className="refresh-button"
            type="button"
            onClick={
              refreshOrders
            }
          >
            Refresh orders
          </button>
        </div>
      </header>

      <main className="admin-content">
        <section className="admin-stats">
          <article>
            <span>
              Total orders
            </span>

            <strong>
              {orders.length}
            </strong>
          </article>

          <article>
            <span>
              Active orders
            </span>

            <strong>
              {activeOrders}
            </strong>
          </article>

          <article>
            <span>
              Total revenue
            </span>

            <strong>
              {formatPrice(
                totalRevenue,
              )}
            </strong>
          </article>
        </section>

        {loading && (
          <div className="status-message">
            Loading orders...
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
            <div className="status-message">
              No orders have been
              placed yet.
            </div>
          )}

        <section className="admin-orders">
          {orders.map(
            (order) => (
              <article
                className="admin-order-card"
                key={order.id}
              >
                <div className="order-card-heading">
                  <div>
                    <span className="order-number">
                      Order #{order.id}
                    </span>

                    <small>
                      {formatDate(
                        order.createdAt,
                      )}
                    </small>
                  </div>

                  <span
                    className={`status-badge status-${order.status.toLowerCase()}`}
                  >
                    {formatStatus(
                      order.status,
                    )}
                  </span>
                </div>

                <div className="order-information">
                  <div>
                    <span>
                      Customer
                    </span>

                    <strong>
                      {
                        order.customerName
                      }
                    </strong>

                    <small>
                      {order.phone}
                    </small>
                  </div>

                  <div>
                    <span>
                      Delivery address
                    </span>

                    <strong>
                      {
                        order.deliveryAddress
                      }
                    </strong>

                    {order.deliveryLatitude !=
                      null &&
                      order.deliveryLongitude !=
                        null && (
                        <a
                          className="admin-location-link"
                          href={getDeliveryMapUrl(
                            order,
                          )}
                          target="_blank"
                          rel="noreferrer"
                        >
                          📍 View delivery
                          location
                        </a>
                      )}
                  </div>

                  <div
                    className={`admin-schedule-information ${
                      order.orderTiming ===
                      "SCHEDULED"
                        ? "admin-schedule-information-active"
                        : ""
                    }`}
                  >
                    <span>
                      Order timing
                    </span>

                    <strong>
                      {order.orderTiming ===
                      "SCHEDULED"
                        ? "🗓️ Scheduled order"
                        : "⚡ Order now"}
                    </strong>

                    {order.orderTiming ===
                      "SCHEDULED" && (
                      <>
                        <small>
                          Meal:{" "}
                          {formatStatus(
                            order.mealSlot,
                          )}
                        </small>

                        <small>
                          Delivery:{" "}
                          {formatDate(
                            order.scheduledFor,
                          )}
                        </small>

                        <small>
                          Start preparing:{" "}
                          {formatDate(
                            order.preparationStartAt,
                          )}
                        </small>
                      </>
                    )}
                  </div>

                  <div className="admin-payment-information">
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
                      <small className="admin-transaction-id">
                        Transaction ID:{" "}
                        {
                          order.transactionId
                        }
                      </small>
                    )}
                  </div>
                </div>

                <div className="admin-order-items">
                  <h3>
                    Items
                  </h3>

                  {order.items.map(
                    (item) => (
                      <div
                        key={item.id}
                      >
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

                <div className="admin-order-footer">
                  <div className="admin-order-total">
                    <span>
                      Subtotal:{" "}
                      {formatPrice(
                        order.subtotal,
                      )}
                    </span>

                    {Number(
                      order.discountAmount ??
                        0,
                    ) > 0 && (
                      <span className="order-discount-line">
                        Discount: −
                        {formatPrice(
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
                  </div>

                  <label className="status-select">
                    Update status

                    <select
                      value={
                        order.status
                      }
                      disabled={
                        updatingOrderId ===
                        order.id
                      }
                      onChange={(
                        event,
                      ) =>
                        updateOrderStatus(
                          order.id,
                          event.target
                            .value,
                        )
                      }
                    >
                      {ORDER_STATUSES.map(
                        (status) => (
                          <option
                            key={status}
                            value={status}
                          >
                            {formatStatus(
                              status,
                            )}
                          </option>
                        ),
                      )}
                    </select>
                  </label>
                </div>
              </article>
            ),
          )}
        </section>
      </main>
    </div>
  );
}

export default AdminOrders;