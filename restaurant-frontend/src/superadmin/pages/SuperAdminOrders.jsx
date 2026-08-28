import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  getRestaurants,
  getSuperAdminOrder,
  getSuperAdminOrders,
} from "../../services/superAdminApi";

function SuperAdminOrders() {
  const [orders, setOrders] =
    useState([]);

  const [restaurants, setRestaurants] =
    useState([]);

  const [selectedOrder, setSelectedOrder] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [detailLoading, setDetailLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const [filters, setFilters] =
    useState({
      restaurantId: "",
      status: "",
      paymentMethod: "",
      paymentStatus: "",
    });

  // ==========================================
  // LOAD
  // ==========================================

  useEffect(() => {
    loadRestaurants();
  }, []);

  useEffect(() => {
    loadOrders();
  }, [
    filters.restaurantId,
    filters.status,
    filters.paymentMethod,
    filters.paymentStatus,
  ]);

  const loadRestaurants = async () => {
    try {
      const data =
        await getRestaurants();

      setRestaurants(
        Array.isArray(data)
          ? data
          : []
      );
    } catch {
      // Orders page can still work
      // without restaurant dropdown data.
    }
  };

  const loadOrders = async () => {
    setLoading(true);
    setError("");

    try {
      const data =
        await getSuperAdminOrders({
          restaurantId:
            filters.restaurantId ||
            null,

          status:
            filters.status ||
            null,

          paymentMethod:
            filters.paymentMethod ||
            null,

          paymentStatus:
            filters.paymentStatus ||
            null,
        });

      setOrders(
        Array.isArray(data)
          ? data
          : []
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to load orders"
      );
    } finally {
      setLoading(false);
    }
  };

  // ==========================================
  // COUNTS
  // ==========================================

  const counts = useMemo(() => {
    let delivered = 0;
    let cancelled = 0;
    let active = 0;

    orders.forEach((order) => {
      if (
        order.status ===
        "DELIVERED"
      ) {
        delivered += 1;
      } else if (
        order.status ===
        "CANCELLED"
      ) {
        cancelled += 1;
      } else {
        active += 1;
      }
    });

    return {
      total: orders.length,
      active,
      delivered,
      cancelled,
    };
  }, [orders]);

  // ==========================================
  // DETAIL
  // ==========================================

  const openOrderDetails =
    async (orderId) => {
      setDetailLoading(true);
      setError("");

      try {
        const data =
          await getSuperAdminOrder(
            orderId
          );

        setSelectedOrder(
          data
        );
      } catch (err) {
        setError(
          err.message ||
            "Unable to load order details"
        );
      } finally {
        setDetailLoading(false);
      }
    };

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
      }
    );

  const formatDate = (value) => {
    if (!value) {
      return "-";
    }

    return new Date(
      value
    ).toLocaleString(
      "en-IN"
    );
  };

  const statusClass = (status) => {
    if (
      status === "DELIVERED"
    ) {
      return "super-admin-badge super-admin-badge-success";
    }

    if (
      status === "CANCELLED"
    ) {
      return "super-admin-badge super-admin-badge-danger";
    }

    if (
      status === "PLACED" ||
      status === "CONFIRMED"
    ) {
      return "super-admin-badge super-admin-badge-warning";
    }

    return "super-admin-badge super-admin-badge-info";
  };

  const paymentStatusClass = (
    status
  ) => {
    if (status === "PAID") {
      return "super-admin-badge super-admin-badge-success";
    }

    if (status === "FAILED") {
      return "super-admin-badge super-admin-badge-danger";
    }

    if (status === "PENDING") {
      return "super-admin-badge super-admin-badge-warning";
    }

    return "super-admin-badge super-admin-badge-neutral";
  };

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="super-admin-page">
      <div className="super-admin-page-header">
        <div>
          <h1>
            Orders
          </h1>

          <p>
            View platform-wide orders
            across all restaurants.
          </p>
        </div>

        <button
          type="button"
          className="super-admin-button super-admin-button-secondary"
          onClick={
            loadOrders
          }
        >
          Refresh
        </button>
      </div>

      <div className="super-admin-stat-grid">
        <div className="super-admin-stat-card">
          <span>
            Visible Orders
          </span>

          <strong>
            {counts.total}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Active Orders
          </span>

          <strong>
            {counts.active}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Delivered
          </span>

          <strong>
            {counts.delivered}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Cancelled
          </span>

          <strong>
            {counts.cancelled}
          </strong>
        </div>
      </div>

      <div className="super-admin-section">
        <div className="super-admin-filter-bar">

          <div className="super-admin-filter-field">
            <label>
              Restaurant
            </label>

            <select
              value={
                filters.restaurantId
              }
              onChange={(event) =>
                setFilters(
                  (current) => ({
                    ...current,
                    restaurantId:
                      event.target.value,
                  })
                )
              }
            >
              <option value="">
                All Restaurants
              </option>

              {restaurants.map(
                (restaurant) => (
                  <option
                    key={
                      restaurant.id
                    }
                    value={
                      restaurant.id
                    }
                  >
                    {
                      restaurant.name
                    }
                  </option>
                )
              )}
            </select>
          </div>

          <div className="super-admin-filter-field">
            <label>
              Order Status
            </label>

            <select
              value={
                filters.status
              }
              onChange={(event) =>
                setFilters(
                  (current) => ({
                    ...current,
                    status:
                      event.target.value,
                  })
                )
              }
            >
              <option value="">
                All Statuses
              </option>

              <option value="PLACED">
                Placed
              </option>

              <option value="CONFIRMED">
                Confirmed
              </option>

              <option value="PREPARING">
                Preparing
              </option>

              <option value="READY">
                Ready
              </option>

              <option value="OUT_FOR_DELIVERY">
                Out For Delivery
              </option>

              <option value="DELIVERED">
                Delivered
              </option>

              <option value="CANCELLED">
                Cancelled
              </option>
            </select>
          </div>

          <div className="super-admin-filter-field">
            <label>
              Payment Method
            </label>

            <select
              value={
                filters.paymentMethod
              }
              onChange={(event) =>
                setFilters(
                  (current) => ({
                    ...current,
                    paymentMethod:
                      event.target.value,
                  })
                )
              }
            >
              <option value="">
                All Methods
              </option>

              <option value="CASH_ON_DELIVERY">
                Cash on Delivery
              </option>

              <option value="DEMO_RAZORPAY">
                Razorpay
              </option>
            </select>
          </div>

          <div className="super-admin-filter-field">
            <label>
              Payment Status
            </label>

            <select
              value={
                filters.paymentStatus
              }
              onChange={(event) =>
                setFilters(
                  (current) => ({
                    ...current,
                    paymentStatus:
                      event.target.value,
                  })
                )
              }
            >
              <option value="">
                All Payment Statuses
              </option>

              <option value="NOT_REQUIRED">
                Not Required
              </option>

              <option value="PENDING">
                Pending
              </option>

              <option value="PAID">
                Paid
              </option>

              <option value="FAILED">
                Failed
              </option>
            </select>
          </div>

          <button
            type="button"
            className="super-admin-button super-admin-button-secondary"
            onClick={() =>
              setFilters({
                restaurantId: "",
                status: "",
                paymentMethod: "",
                paymentStatus: "",
              })
            }
          >
            Clear Filters
          </button>
        </div>

        {error && (
          <div
            className="super-admin-error-card"
            style={{
              marginBottom:
                "16px",
            }}
          >
            {error}
          </div>
        )}

        {loading ? (
          <div className="super-admin-loading-card">
            Loading orders...
          </div>
        ) : orders.length ===
          0 ? (
          <div className="super-admin-empty-card">
            No orders found.
          </div>
        ) : (
          <div className="super-admin-table-wrapper">
            <table className="super-admin-table">
              <thead>
                <tr>
                  <th>
                    Order
                  </th>

                  <th>
                    Restaurant
                  </th>

                  <th>
                    Customer
                  </th>

                  <th>
                    Status
                  </th>

                  <th>
                    Payment
                  </th>

                  <th>
                    Total
                  </th>

                  <th>
                    Created
                  </th>

                  <th>
                    Action
                  </th>
                </tr>
              </thead>

              <tbody>
                {orders.map(
                  (order) => (
                    <tr
                      key={
                        order.id
                      }
                    >
                      <td>
                        <strong>
                          #
                          {
                            order.id
                          }
                        </strong>
                      </td>

                      <td>
                        <strong>
                          {
                            order.restaurantName
                          }
                        </strong>

                        <div>
                          <small>
                            ID:{" "}
                            {
                              order.restaurantId
                            }
                          </small>
                        </div>
                      </td>

                      <td>
                        <strong>
                          {
                            order.customerName
                          }
                        </strong>

                        <div>
                          {
                            order.customerEmail ||
                            "-"
                          }
                        </div>

                        <small>
                          {
                            order.phone ||
                            "-"
                          }
                        </small>
                      </td>

                      <td>
                        <span
                          className={statusClass(
                            order.status
                          )}
                        >
                          {
                            order.status
                          }
                        </span>
                      </td>

                      <td>
                        <div>
                          {
                            order.paymentMethod
                          }
                        </div>

                        <span
                          className={paymentStatusClass(
                            order.paymentStatus
                          )}
                        >
                          {
                            order.paymentStatus
                          }
                        </span>
                      </td>

                      <td>
                        <strong>
                          {formatPrice(
                            order.totalAmount
                          )}
                        </strong>
                      </td>

                      <td>
                        {formatDate(
                          order.createdAt
                        )}
                      </td>

                      <td>
                        <button
                          type="button"
                          className="super-admin-button super-admin-button-secondary"
                          disabled={
                            detailLoading
                          }
                          onClick={() =>
                            openOrderDetails(
                              order.id
                            )
                          }
                        >
                          View Details
                        </button>
                      </td>
                    </tr>
                  )
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {selectedOrder && (
        <div className="super-admin-modal-backdrop">
          <div
            className="super-admin-modal"
            style={{
              maxWidth:
                "980px",
            }}
          >
            <div className="super-admin-modal-header">
              <div>
                <h2>
                  Order #
                  {
                    selectedOrder.id
                  }
                </h2>

                <small>
                  {formatDate(
                    selectedOrder.createdAt
                  )}
                </small>
              </div>

              <button
                type="button"
                className="super-admin-button super-admin-button-secondary"
                onClick={() =>
                  setSelectedOrder(
                    null
                  )
                }
              >
                Close
              </button>
            </div>

            <div className="super-admin-modal-body">

              <div
                style={{
                  display:
                    "grid",
                  gridTemplateColumns:
                    "repeat(auto-fit, minmax(210px, 1fr))",
                  gap:
                    "14px",
                }}
              >
                <div className="super-admin-card">
                  <strong>
                    Restaurant
                  </strong>

                  <p>
                    {
                      selectedOrder.restaurantName
                    }
                  </p>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Customer
                  </strong>

                  <p>
                    {
                      selectedOrder.customerName
                    }
                  </p>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Status
                  </strong>

                  <p>
                    {
                      selectedOrder.status
                    }
                  </p>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Payment
                  </strong>

                  <p>
                    {
                      selectedOrder.paymentMethod
                    }
                    {" / "}
                    {
                      selectedOrder.paymentStatus
                    }
                  </p>
                </div>
              </div>

              <div className="super-admin-section">
                <div className="super-admin-card">
                  <strong>
                    Delivery Address
                  </strong>

                  <p>
                    {
                      selectedOrder.deliveryAddress
                    }
                  </p>
                </div>
              </div>

              <div className="super-admin-section">
                <h3 className="super-admin-section-title">
                  Order Items
                </h3>

                {selectedOrder.items
                  ?.length ? (
                  <div className="super-admin-table-wrapper">
                    <table className="super-admin-table">
                      <thead>
                        <tr>
                          <th>
                            Item
                          </th>

                          <th>
                            Quantity
                          </th>

                          <th>
                            Unit Price
                          </th>

                          <th>
                            Total
                          </th>
                        </tr>
                      </thead>

                      <tbody>
                        {selectedOrder.items.map(
                          (item) => (
                            <tr
                              key={
                                item.id
                              }
                            >
                              <td>
                                {
                                  item.itemName
                                }
                              </td>

                              <td>
                                {
                                  item.quantity
                                }
                              </td>

                              <td>
                                {formatPrice(
                                  item.unitPrice
                                )}
                              </td>

                              <td>
                                {formatPrice(
                                  item.lineTotal
                                )}
                              </td>
                            </tr>
                          )
                        )}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="super-admin-empty-card">
                    No order items found.
                  </div>
                )}
              </div>

              <div className="super-admin-section">
                <div
                  style={{
                    display:
                      "grid",
                    gridTemplateColumns:
                      "repeat(auto-fit, minmax(180px, 1fr))",
                    gap:
                      "14px",
                  }}
                >
                  <div className="super-admin-card">
                    <span>
                      Subtotal
                    </span>

                    <h3>
                      {formatPrice(
                        selectedOrder.subtotal
                      )}
                    </h3>
                  </div>

                  <div className="super-admin-card">
                    <span>
                      Discount
                    </span>

                    <h3>
                      {formatPrice(
                        selectedOrder.discountAmount
                      )}
                    </h3>
                  </div>

                  <div className="super-admin-card">
                    <span>
                      Delivery Fee
                    </span>

                    <h3>
                      {formatPrice(
                        selectedOrder.deliveryFee
                      )}
                    </h3>
                  </div>

                  <div className="super-admin-card">
                    <span>
                      Total
                    </span>

                    <h3>
                      {formatPrice(
                        selectedOrder.totalAmount
                      )}
                    </h3>
                  </div>
                </div>
              </div>

            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default SuperAdminOrders;