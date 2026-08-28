import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  getRestaurants,
  updateRestaurantActive,
  updateRestaurantApproval,
  updateRestaurantCommission,
} from "../../services/superAdminApi";

function SuperAdminRestaurants() {
  const [restaurants, setRestaurants] =
    useState([]);

  const [statusFilter, setStatusFilter] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [
    updatingRestaurantId,
    setUpdatingRestaurantId,
  ] = useState(null);

  const [
    commissionDrafts,
    setCommissionDrafts,
  ] = useState({});

  // ==========================================
  // LOAD RESTAURANTS
  // ==========================================

  useEffect(() => {
    loadRestaurants();
  }, [statusFilter]);

  const loadRestaurants = async () => {
    setLoading(true);
    setError("");

    try {
      const data =
        await getRestaurants(
          statusFilter || null
        );

      setRestaurants(
        Array.isArray(data)
          ? data
          : []
      );

      const drafts = {};

      (Array.isArray(data)
        ? data
        : []
      ).forEach((restaurant) => {
        drafts[restaurant.id] =
          restaurant
            .commissionPercentage ?? 0;
      });

      setCommissionDrafts(
        drafts
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to load restaurants"
      );
    } finally {
      setLoading(false);
    }
  };

  // ==========================================
  // COUNTS
  // ==========================================

  const counts = useMemo(() => {
    const summary = {
      total: restaurants.length,
      pending: 0,
      approved: 0,
      rejected: 0,
      active: 0,
    };

    restaurants.forEach(
      (restaurant) => {
        if (
          restaurant.approvalStatus ===
          "PENDING"
        ) {
          summary.pending += 1;
        }

        if (
          restaurant.approvalStatus ===
          "APPROVED"
        ) {
          summary.approved += 1;
        }

        if (
          restaurant.approvalStatus ===
          "REJECTED"
        ) {
          summary.rejected += 1;
        }

        if (restaurant.active) {
          summary.active += 1;
        }
      }
    );

    return summary;
  }, [restaurants]);

  // ==========================================
  // APPROVAL UPDATE
  // ==========================================

  const handleApproval = async (
    restaurantId,
    approvalStatus
  ) => {
    setUpdatingRestaurantId(
      restaurantId
    );

    setError("");

    try {
      await updateRestaurantApproval(
        restaurantId,
        approvalStatus
      );

      await loadRestaurants();
    } catch (err) {
      setError(
        err.message ||
          "Restaurant status could not be updated"
      );
    } finally {
      setUpdatingRestaurantId(
        null
      );
    }
  };

  // ==========================================
  // ACTIVE UPDATE
  // ==========================================

  const handleActiveToggle = async (
    restaurant
  ) => {
    setUpdatingRestaurantId(
      restaurant.id
    );

    setError("");

    try {
      await updateRestaurantActive(
        restaurant.id,
        !restaurant.active
      );

      await loadRestaurants();
    } catch (err) {
      setError(
        err.message ||
          "Restaurant active status could not be updated"
      );
    } finally {
      setUpdatingRestaurantId(
        null
      );
    }
  };

  // ==========================================
  // COMMISSION UPDATE
  // ==========================================

  const handleCommissionUpdate =
    async (restaurantId) => {
      const value = Number(
        commissionDrafts[
          restaurantId
        ]
      );

      if (
        Number.isNaN(value) ||
        value < 0 ||
        value > 100
      ) {
        setError(
          "Commission must be between 0 and 100"
        );

        return;
      }

      setUpdatingRestaurantId(
        restaurantId
      );

      setError("");

      try {
        await updateRestaurantCommission(
          restaurantId,
          value
        );

        await loadRestaurants();
      } catch (err) {
        setError(
          err.message ||
            "Commission could not be updated"
        );
      } finally {
        setUpdatingRestaurantId(
          null
        );
      }
    };

  // ==========================================
  // BADGES
  // ==========================================

  const approvalBadgeClass = (
    status
  ) => {
    if (status === "APPROVED") {
      return (
        "super-admin-badge " +
        "super-admin-badge-success"
      );
    }

    if (status === "PENDING") {
      return (
        "super-admin-badge " +
        "super-admin-badge-warning"
      );
    }

    if (status === "REJECTED") {
      return (
        "super-admin-badge " +
        "super-admin-badge-danger"
      );
    }

    return (
      "super-admin-badge " +
      "super-admin-badge-neutral"
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
            Restaurants
          </h1>

          <p>
            Review restaurant
            applications, manage
            activation and commission.
          </p>
        </div>

        <button
          type="button"
          className={
            "super-admin-button " +
            "super-admin-button-secondary"
          }
          onClick={
            loadRestaurants
          }
        >
          Refresh
        </button>
      </div>

      <div className="super-admin-stat-grid">
        <div className="super-admin-stat-card">
          <span>
            Visible Restaurants
          </span>

          <strong>
            {counts.total}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Pending
          </span>

          <strong>
            {counts.pending}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Approved
          </span>

          <strong>
            {counts.approved}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Rejected
          </span>

          <strong>
            {counts.rejected}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Active
          </span>

          <strong>
            {counts.active}
          </strong>
        </div>
      </div>

      <div className="super-admin-section">
        <div className="super-admin-filter-bar">
          <div className="super-admin-filter-field">
            <label>
              Approval Status
            </label>

            <select
              value={statusFilter}
              onChange={(event) =>
                setStatusFilter(
                  event.target.value
                )
              }
            >
              <option value="">
                All
              </option>

              <option value="PENDING">
                Pending
              </option>

              <option value="APPROVED">
                Approved
              </option>

              <option value="REJECTED">
                Rejected
              </option>
            </select>
          </div>
        </div>

        {error && (
          <div className="super-admin-error-card">
            {error}
          </div>
        )}

        {loading ? (
          <div className="super-admin-loading-card">
            Loading restaurants...
          </div>
        ) : restaurants.length ===
          0 ? (
          <div className="super-admin-empty-card">
            No restaurants found.
          </div>
        ) : (
          <div className="super-admin-table-wrapper">
            <table className="super-admin-table">
              <thead>
                <tr>
                  <th>
                    Restaurant
                  </th>

                  <th>
                    Location
                  </th>

                  <th>
                    Contact
                  </th>

                  <th>
                    Approval
                  </th>

                  <th>
                    Active
                  </th>

                  <th>
                    Admins
                  </th>

                  <th>
                    Commission
                  </th>

                  <th>
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody>
                {restaurants.map(
                  (restaurant) => {
                    const isUpdating =
                      updatingRestaurantId ===
                      restaurant.id;

                    return (
                      <tr
                        key={
                          restaurant.id
                        }
                      >
                        <td>
                          <strong>
                            {
                              restaurant.name
                            }
                          </strong>

                          <div>
                            <small>
                              ID:{" "}
                              {
                                restaurant.id
                              }
                            </small>
                          </div>
                        </td>

                        <td>
                          <div>
                            {
                              restaurant.city ||
                              "-"
                            }
                          </div>

                          <small>
                            {
                              restaurant.state ||
                              ""
                            }
                          </small>
                        </td>

                        <td>
                          <div>
                            {
                              restaurant.email
                            }
                          </div>

                          <small>
                            {
                              restaurant.phone ||
                              "-"
                            }
                          </small>
                        </td>

                        <td>
                          <span
                            className={approvalBadgeClass(
                              restaurant
                                .approvalStatus
                            )}
                          >
                            {
                              restaurant
                                .approvalStatus
                            }
                          </span>
                        </td>

                        <td>
                          <span
                            className={
                              restaurant.active
                                ? "super-admin-badge super-admin-badge-success"
                                : "super-admin-badge super-admin-badge-neutral"
                            }
                          >
                            {restaurant.active
                              ? "ACTIVE"
                              : "INACTIVE"}
                          </span>
                        </td>

                        <td>
                          {
                            restaurant.adminCount ??
                            0
                          }
                        </td>

                        <td>
                          <div
                            style={{
                              display:
                                "flex",
                              gap: "6px",
                              alignItems:
                                "center",
                            }}
                          >
                            <input
                              type="number"
                              min="0"
                              max="100"
                              step="0.01"
                              value={
                                commissionDrafts[
                                  restaurant
                                    .id
                                ] ?? ""
                              }
                              onChange={(
                                event
                              ) =>
                                setCommissionDrafts(
                                  (
                                    current
                                  ) => ({
                                    ...current,
                                    [restaurant.id]:
                                      event
                                        .target
                                        .value,
                                  })
                                )
                              }
                              style={{
                                width:
                                  "80px",
                                height:
                                  "36px",
                                border:
                                  "1px solid #d1d5db",
                                borderRadius:
                                  "8px",
                                padding:
                                  "0 8px",
                              }}
                            />

                            <span>
                              %
                            </span>

                            <button
                              type="button"
                              disabled={
                                isUpdating
                              }
                              className={
                                "super-admin-button " +
                                "super-admin-button-secondary"
                              }
                              onClick={() =>
                                handleCommissionUpdate(
                                  restaurant.id
                                )
                              }
                            >
                              Save
                            </button>
                          </div>
                        </td>

                        <td>
                          <div
                            style={{
                              display:
                                "flex",
                              flexWrap:
                                "wrap",
                              gap: "7px",
                            }}
                          >
                            {restaurant
                              .approvalStatus !==
                              "APPROVED" && (
                              <button
                                type="button"
                                disabled={
                                  isUpdating
                                }
                                className={
                                  "super-admin-button " +
                                  "super-admin-button-success"
                                }
                                onClick={() =>
                                  handleApproval(
                                    restaurant.id,
                                    "APPROVED"
                                  )
                                }
                              >
                                Approve
                              </button>
                            )}

                            {restaurant
                              .approvalStatus !==
                              "REJECTED" && (
                              <button
                                type="button"
                                disabled={
                                  isUpdating
                                }
                                className={
                                  "super-admin-button " +
                                  "super-admin-button-danger"
                                }
                                onClick={() =>
                                  handleApproval(
                                    restaurant.id,
                                    "REJECTED"
                                  )
                                }
                              >
                                Reject
                              </button>
                            )}

                            {restaurant.approvalStatus ===
  "APPROVED" && (
  <button
    type="button"
    disabled={isUpdating}
    className={
      restaurant.active
        ? "super-admin-button super-admin-button-danger"
        : "super-admin-button super-admin-button-primary"
    }
    onClick={() =>
      handleActiveToggle(
        restaurant
      )
    }
  >
    {restaurant.active
      ? "Deactivate"
      : "Activate"}
  </button>
)}
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
    </div>
  );
}

export default SuperAdminRestaurants;