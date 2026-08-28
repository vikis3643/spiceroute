import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  getCustomer,
  getCustomerOrders,
  getCustomers,
  updateCustomerActive,
} from "../../services/superAdminApi";

function SuperAdminCustomers() {
  const [customers, setCustomers] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  const [searchText, setSearchText] =
    useState("");

  const [
    processingCustomerId,
    setProcessingCustomerId,
  ] = useState(null);

  const [
    selectedCustomer,
    setSelectedCustomer,
  ] = useState(null);

  const [
    customerOrders,
    setCustomerOrders,
  ] = useState([]);

  const [
    detailLoading,
    setDetailLoading,
  ] = useState(false);

  // ==========================================
  // LOAD CUSTOMERS
  // ==========================================

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async () => {
    setLoading(true);
    setError("");

    try {
      const data =
        await getCustomers();

      setCustomers(
        Array.isArray(data)
          ? data
          : []
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to load customers"
      );
    } finally {
      setLoading(false);
    }
  };

  // ==========================================
  // COUNTS
  // ==========================================

  const counts = useMemo(() => {
    let active = 0;
    let inactive = 0;
    let verified = 0;

    customers.forEach(
      (customer) => {
        if (customer.active) {
          active += 1;
        } else {
          inactive += 1;
        }

        if (
          customer.emailVerified
        ) {
          verified += 1;
        }
      }
    );

    return {
      total: customers.length,
      active,
      inactive,
      verified,
    };
  }, [customers]);

  // ==========================================
  // SEARCH
  // ==========================================

  const filteredCustomers =
    useMemo(() => {
      const query =
        searchText
          .trim()
          .toLowerCase();

      if (!query) {
        return customers;
      }

      return customers.filter(
        (customer) => {
          return [
            customer.fullName,
            customer.email,
            customer.phone,
          ]
            .filter(Boolean)
            .some((value) =>
              String(value)
                .toLowerCase()
                .includes(query)
            );
        }
      );
    }, [
      customers,
      searchText,
    ]);

  // ==========================================
  // ACTIVATE / DEACTIVATE
  // ==========================================

  const handleActiveToggle =
    async (customer) => {
      setError("");
      setSuccess("");

      setProcessingCustomerId(
        customer.id
      );

      try {
        await updateCustomerActive(
          customer.id,
          !customer.active
        );

        setSuccess(
          customer.active
            ? "Customer deactivated successfully."
            : "Customer activated successfully."
        );

        await loadCustomers();
      } catch (err) {
        setError(
          err.message ||
            "Customer status could not be updated"
        );
      } finally {
        setProcessingCustomerId(
          null
        );
      }
    };

  // ==========================================
  // OPEN CUSTOMER DETAILS
  // ==========================================

  const openCustomerDetails =
    async (customerId) => {
      setDetailLoading(true);
      setError("");
      setSuccess("");

      try {
        const [
          customer,
          orders,
        ] = await Promise.all([
          getCustomer(customerId),
          getCustomerOrders(
            customerId
          ),
        ]);

        setSelectedCustomer(
          customer
        );

        setCustomerOrders(
          Array.isArray(orders)
            ? orders
            : []
        );
      } catch (err) {
        setError(
          err.message ||
            "Customer details could not be loaded"
        );
      } finally {
        setDetailLoading(false);
      }
    };

  // ==========================================
  // FORMAT DATE
  // ==========================================

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

  // ==========================================
  // FORMAT PRICE
  // ==========================================

  const formatPrice = (value) => {
    return Number(
      value || 0
    ).toLocaleString(
      "en-IN",
      {
        style: "currency",
        currency: "INR",
      }
    );
  };

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="super-admin-page">
      <div className="super-admin-page-header">
        <div>
          <h1>
            Customers
          </h1>

          <p>
            View customer accounts,
            order history and account
            status.
          </p>
        </div>

        <button
          type="button"
          className={
            "super-admin-button " +
            "super-admin-button-secondary"
          }
          onClick={
            loadCustomers
          }
        >
          Refresh
        </button>
      </div>

      <div className="super-admin-stat-grid">
        <div className="super-admin-stat-card">
          <span>
            Total Customers
          </span>

          <strong>
            {counts.total}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Active Customers
          </span>

          <strong>
            {counts.active}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Inactive Customers
          </span>

          <strong>
            {counts.inactive}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Email Verified
          </span>

          <strong>
            {counts.verified}
          </strong>
        </div>
      </div>

      <div className="super-admin-section">
        <div className="super-admin-filter-bar">
          <div className="super-admin-filter-field">
            <label>
              Search Customer
            </label>

            <input
              type="search"
              placeholder="Name, email or phone"
              value={
                searchText
              }
              onChange={(event) =>
                setSearchText(
                  event.target.value
                )
              }
            />
          </div>
        </div>

        {success && (
          <div
            className="super-admin-card"
            style={{
              marginBottom:
                "16px",
              borderColor:
                "#bbf7d0",
              background:
                "#f0fdf4",
              color:
                "#166534",
            }}
          >
            {success}
          </div>
        )}

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
            Loading customers...
          </div>
        ) : filteredCustomers.length ===
          0 ? (
          <div className="super-admin-empty-card">
            No customers found.
          </div>
        ) : (
          <div className="super-admin-table-wrapper">
            <table className="super-admin-table">
              <thead>
                <tr>
                  <th>
                    Customer
                  </th>

                  <th>
                    Phone
                  </th>

                  <th>
                    Provider
                  </th>

                  <th>
                    Verified
                  </th>

                  <th>
                    Orders
                  </th>

                  <th>
                    Status
                  </th>

                  <th>
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody>
                {filteredCustomers.map(
                  (customer) => {
                    const processing =
                      processingCustomerId ===
                      customer.id;

                    return (
                      <tr
                        key={
                          customer.id
                        }
                      >
                        <td>
                          <strong>
                            {
                              customer.fullName
                            }
                          </strong>

                          <div>
                            {
                              customer.email
                            }
                          </div>

                          <small>
                            ID:{" "}
                            {
                              customer.id
                            }
                          </small>
                        </td>

                        <td>
                          {
                            customer.phone ||
                            "-"
                          }
                        </td>

                        <td>
                          <span className="super-admin-badge super-admin-badge-neutral">
                            {
                              customer.provider ||
                              "LOCAL"
                            }
                          </span>
                        </td>

                        <td>
                          <span
                            className={
                              customer.emailVerified
                                ? "super-admin-badge super-admin-badge-success"
                                : "super-admin-badge super-admin-badge-warning"
                            }
                          >
                            {customer.emailVerified
                              ? "VERIFIED"
                              : "UNVERIFIED"}
                          </span>
                        </td>

                        <td>
                          {
                            customer.totalOrders ??
                            0
                          }
                        </td>

                        <td>
                          <span
                            className={
                              customer.active
                                ? "super-admin-badge super-admin-badge-success"
                                : "super-admin-badge super-admin-badge-neutral"
                            }
                          >
                            {customer.active
                              ? "ACTIVE"
                              : "INACTIVE"}
                          </span>
                        </td>

                        <td>
                          <div
                            style={{
                              display:
                                "flex",
                              gap:
                                "7px",
                              flexWrap:
                                "wrap",
                            }}
                          >
                            <button
                              type="button"
                              className={
                                "super-admin-button " +
                                "super-admin-button-secondary"
                              }
                              disabled={
                                detailLoading
                              }
                              onClick={() =>
                                openCustomerDetails(
                                  customer.id
                                )
                              }
                            >
                              View Details
                            </button>

                            <button
                              type="button"
                              disabled={
                                processing
                              }
                              className={
                                customer.active
                                  ? "super-admin-button super-admin-button-danger"
                                  : "super-admin-button super-admin-button-success"
                              }
                              onClick={() =>
                                handleActiveToggle(
                                  customer
                                )
                              }
                            >
                              {customer.active
                                ? "Deactivate"
                                : "Activate"}
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  }
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* =====================================
          CUSTOMER DETAIL MODAL
      ===================================== */}

      {selectedCustomer && (
        <div className="super-admin-modal-backdrop">
          <div
            className="super-admin-modal"
            style={{
              maxWidth:
                "900px",
            }}
          >
            <div className="super-admin-modal-header">
              <div>
                <h2>
                  {
                    selectedCustomer.fullName
                  }
                </h2>

                <small>
                  Customer #
                  {
                    selectedCustomer.id
                  }
                </small>
              </div>

              <button
                type="button"
                className={
                  "super-admin-button " +
                  "super-admin-button-secondary"
                }
                onClick={() => {
                  setSelectedCustomer(
                    null
                  );

                  setCustomerOrders(
                    []
                  );
                }}
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
                    "repeat(auto-fit, minmax(200px, 1fr))",
                  gap:
                    "14px",
                  marginBottom:
                    "24px",
                }}
              >
                <div className="super-admin-card">
                  <strong>
                    Email
                  </strong>

                  <p>
                    {
                      selectedCustomer.email
                    }
                  </p>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Phone
                  </strong>

                  <p>
                    {
                      selectedCustomer.phone ||
                      "-"
                    }
                  </p>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Provider
                  </strong>

                  <p>
                    {
                      selectedCustomer.provider ||
                      "-"
                    }
                  </p>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Created
                  </strong>

                  <p>
                    {formatDate(
                      selectedCustomer.createdAt
                    )}
                  </p>
                </div>
              </div>

              <div className="super-admin-card">
                <strong>
                  Default Delivery
                  Address
                </strong>

                <p>
                  {
                    selectedCustomer.defaultDeliveryAddress ||
                    "No default address"
                  }
                </p>
              </div>

              <div className="super-admin-section">
                <h3 className="super-admin-section-title">
                  Order History
                </h3>

                {customerOrders.length ===
                0 ? (
                  <div className="super-admin-empty-card">
                    No orders found for
                    this customer.
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
                            Status
                          </th>

                          <th>
                            Payment
                          </th>

                          <th>
                            Amount
                          </th>

                          <th>
                            Created
                          </th>
                        </tr>
                      </thead>

                      <tbody>
                        {customerOrders.map(
                          (order) => (
                            <tr
                              key={
                                order.id
                              }
                            >
                              <td>
                                #
                                {
                                  order.id
                                }
                              </td>

                              <td>
                                {order
                                  .restaurant
                                  ?.name ||
                                  `Restaurant #${
                                    order
                                      .restaurant
                                      ?.id ||
                                    "-"
                                  }`}
                              </td>

                              <td>
                                <span className="super-admin-badge super-admin-badge-info">
                                  {
                                    order.status
                                  }
                                </span>
                              </td>

                              <td>
                                {
                                  order.paymentMethod
                                }
                              </td>

                              <td>
                                {formatPrice(
                                  order.totalAmount
                                )}
                              </td>

                              <td>
                                {formatDate(
                                  order.createdAt
                                )}
                              </td>
                            </tr>
                          )
                        )}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default SuperAdminCustomers;