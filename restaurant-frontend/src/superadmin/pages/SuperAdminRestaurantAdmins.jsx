import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  createRestaurantAdmin,
  getRestaurantAdmins,
  getRestaurants,
  resetRestaurantAdminPassword,
  updateRestaurantAdmin,
  updateRestaurantAdminActive,
} from "../../services/superAdminApi";

const EMPTY_CREATE_FORM = {
  restaurantId: "",
  fullName: "",
  email: "",
  password: "",
};

function SuperAdminRestaurantAdmins() {
  const [admins, setAdmins] =
    useState([]);

  const [restaurants, setRestaurants] =
    useState([]);

  const [
    restaurantFilter,
    setRestaurantFilter,
  ] = useState("");

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  const [
    processingAdminId,
    setProcessingAdminId,
  ] = useState(null);

  const [
    createModalOpen,
    setCreateModalOpen,
  ] = useState(false);

  const [
    editAdmin,
    setEditAdmin,
  ] = useState(null);

  const [
    passwordAdmin,
    setPasswordAdmin,
  ] = useState(null);

  const [
    createForm,
    setCreateForm,
  ] = useState(
    EMPTY_CREATE_FORM
  );

  const [
    editForm,
    setEditForm,
  ] = useState({
    fullName: "",
    email: "",
  });

  const [
    newPassword,
    setNewPassword,
  ] = useState("");

  // ==========================================
  // INITIAL LOAD
  // ==========================================

  useEffect(() => {
    loadRestaurants();
  }, []);

  useEffect(() => {
    loadAdmins();
  }, [restaurantFilter]);

  // ==========================================
  // LOAD RESTAURANTS
  // ==========================================

  const loadRestaurants = async () => {
    try {
      const data =
        await getRestaurants();

      setRestaurants(
        Array.isArray(data)
          ? data
          : []
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to load restaurants"
      );
    }
  };

  // ==========================================
  // LOAD ADMINS
  // ==========================================

  const loadAdmins = async () => {
    setLoading(true);
    setError("");

    try {
      const data =
        await getRestaurantAdmins(
          restaurantFilter || null
        );

      setAdmins(
        Array.isArray(data)
          ? data
          : []
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to load restaurant admins"
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

    admins.forEach((admin) => {
      if (admin.active) {
        active += 1;
      } else {
        inactive += 1;
      }
    });

    return {
      total: admins.length,
      active,
      inactive,
    };
  }, [admins]);

  // ==========================================
  // CREATE ADMIN
  // ==========================================

  const handleCreateSubmit =
    async (event) => {
      event.preventDefault();

      setError("");
      setSuccess("");

      if (!createForm.restaurantId) {
        setError(
          "Please select a restaurant"
        );
        return;
      }

      if (
        createForm.password.length < 8
      ) {
        setError(
          "Password must contain at least 8 characters"
        );
        return;
      }

      try {
        await createRestaurantAdmin({
          restaurantId: Number(
            createForm.restaurantId
          ),
          fullName:
            createForm.fullName.trim(),
          email:
            createForm.email
              .trim()
              .toLowerCase(),
          password:
            createForm.password,
        });

        setCreateModalOpen(false);

        setCreateForm(
          EMPTY_CREATE_FORM
        );

        setSuccess(
          "Restaurant admin created successfully."
        );

        await loadAdmins();
      } catch (err) {
        setError(
          err.message ||
            "Restaurant admin could not be created"
        );
      }
    };

  // ==========================================
  // OPEN EDIT
  // ==========================================

  const openEditModal = (admin) => {
    setError("");
    setSuccess("");

    setEditAdmin(admin);

    setEditForm({
      fullName:
        admin.fullName || "",
      email:
        admin.email || "",
    });
  };

  // ==========================================
  // UPDATE ADMIN
  // ==========================================

  const handleEditSubmit =
    async (event) => {
      event.preventDefault();

      if (!editAdmin) {
        return;
      }

      setError("");
      setSuccess("");

      setProcessingAdminId(
        editAdmin.id
      );

      try {
        await updateRestaurantAdmin(
          editAdmin.id,
          {
            fullName:
              editForm.fullName.trim(),

            email:
              editForm.email
                .trim()
                .toLowerCase(),
          }
        );

        setEditAdmin(null);

        setSuccess(
          "Restaurant admin updated successfully."
        );

        await loadAdmins();
      } catch (err) {
        setError(
          err.message ||
            "Restaurant admin could not be updated"
        );
      } finally {
        setProcessingAdminId(
          null
        );
      }
    };

  // ==========================================
  // ACTIVE STATUS
  // ==========================================

  const handleActiveToggle =
    async (admin) => {
      setError("");
      setSuccess("");

      setProcessingAdminId(
        admin.id
      );

      try {
        await updateRestaurantAdminActive(
          admin.id,
          !admin.active
        );

        setSuccess(
          admin.active
            ? "Restaurant admin deactivated successfully."
            : "Restaurant admin activated successfully."
        );

        await loadAdmins();
      } catch (err) {
        setError(
          err.message ||
            "Admin status could not be updated"
        );
      } finally {
        setProcessingAdminId(
          null
        );
      }
    };

  // ==========================================
  // PASSWORD RESET
  // ==========================================

  const handlePasswordReset =
    async (event) => {
      event.preventDefault();

      if (!passwordAdmin) {
        return;
      }

      setError("");
      setSuccess("");

      if (
        newPassword.length < 8
      ) {
        setError(
          "New password must contain at least 8 characters"
        );
        return;
      }

      setProcessingAdminId(
        passwordAdmin.id
      );

      try {
        await resetRestaurantAdminPassword(
          passwordAdmin.id,
          newPassword
        );

        setPasswordAdmin(null);
        setNewPassword("");

        setSuccess(
          "Restaurant admin password reset successfully."
        );
      } catch (err) {
        setError(
          err.message ||
            "Password could not be reset"
        );
      } finally {
        setProcessingAdminId(
          null
        );
      }
    };

  // ==========================================
  // RESTAURANT NAME HELPER
  // ==========================================

  const getRestaurantName = (
    admin
  ) => {
    if (admin.restaurantName) {
      return admin.restaurantName;
    }

    const restaurant =
      restaurants.find(
        (item) =>
          item.id ===
          admin.restaurantId
      );

    return (
      restaurant?.name ||
      `Restaurant #${
        admin.restaurantId ?? "-"
      }`
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
            Restaurant Admins
          </h1>

          <p>
            Create and manage administrator
            accounts for individual
            restaurants.
          </p>
        </div>

        <button
          type="button"
          className={
            "super-admin-button " +
            "super-admin-button-primary"
          }
          onClick={() => {
            setError("");
            setSuccess("");

            setCreateForm(
              EMPTY_CREATE_FORM
            );

            setCreateModalOpen(
              true
            );
          }}
        >
          Add Restaurant Admin
        </button>
      </div>

      <div className="super-admin-stat-grid">
        <div className="super-admin-stat-card">
          <span>
            Visible Admins
          </span>

          <strong>
            {counts.total}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Active Admins
          </span>

          <strong>
            {counts.active}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Inactive Admins
          </span>

          <strong>
            {counts.inactive}
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
                restaurantFilter
              }
              onChange={(event) =>
                setRestaurantFilter(
                  event.target.value
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

          <button
            type="button"
            className={
              "super-admin-button " +
              "super-admin-button-secondary"
            }
            onClick={
              loadAdmins
            }
          >
            Refresh
          </button>
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
            Loading restaurant
            admins...
          </div>
        ) : admins.length === 0 ? (
          <div className="super-admin-empty-card">
            No restaurant admins
            found.
          </div>
        ) : (
          <div className="super-admin-table-wrapper">
            <table className="super-admin-table">
              <thead>
                <tr>
                  <th>
                    Admin
                  </th>

                  <th>
                    Restaurant
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
                {admins.map(
                  (admin) => {
                    const processing =
                      processingAdminId ===
                      admin.id;

                    return (
                      <tr
                        key={
                          admin.id
                        }
                      >
                        <td>
                          <strong>
                            {
                              admin.fullName
                            }
                          </strong>

                          <div>
                            {
                              admin.email
                            }
                          </div>

                          <small>
                            ID:{" "}
                            {
                              admin.id
                            }
                          </small>
                        </td>

                        <td>
                          <strong>
                            {getRestaurantName(
                              admin
                            )}
                          </strong>

                          {admin.restaurantId !=
                            null && (
                            <div>
                              <small>
                                Restaurant
                                ID:{" "}
                                {
                                  admin.restaurantId
                                }
                              </small>
                            </div>
                          )}
                        </td>

                        <td>
                          <span
                            className={
                              admin.active
                                ? "super-admin-badge super-admin-badge-success"
                                : "super-admin-badge super-admin-badge-neutral"
                            }
                          >
                            {admin.active
                              ? "ACTIVE"
                              : "INACTIVE"}
                          </span>
                        </td>

                        <td>
                          <div
                            style={{
                              display:
                                "flex",
                              flexWrap:
                                "wrap",
                              gap:
                                "7px",
                            }}
                          >
                            <button
                              type="button"
                              disabled={
                                processing
                              }
                              className={
                                "super-admin-button " +
                                "super-admin-button-secondary"
                              }
                              onClick={() =>
                                openEditModal(
                                  admin
                                )
                              }
                            >
                              Edit
                            </button>

                            <button
                              type="button"
                              disabled={
                                processing
                              }
                              className={
                                admin.active
                                  ? "super-admin-button super-admin-button-danger"
                                  : "super-admin-button super-admin-button-success"
                              }
                              onClick={() =>
                                handleActiveToggle(
                                  admin
                                )
                              }
                            >
                              {admin.active
                                ? "Deactivate"
                                : "Activate"}
                            </button>

                            <button
                              type="button"
                              disabled={
                                processing
                              }
                              className={
                                "super-admin-button " +
                                "super-admin-button-primary"
                              }
                              onClick={() => {
                                setError(
                                  ""
                                );

                                setSuccess(
                                  ""
                                );

                                setNewPassword(
                                  ""
                                );

                                setPasswordAdmin(
                                  admin
                                );
                              }}
                            >
                              Reset Password
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
          CREATE MODAL
      ===================================== */}

      {createModalOpen && (
        <div className="super-admin-modal-backdrop">
          <div className="super-admin-modal">
            <div className="super-admin-modal-header">
              <h2>
                Add Restaurant Admin
              </h2>

              <button
                type="button"
                className={
                  "super-admin-button " +
                  "super-admin-button-secondary"
                }
                onClick={() =>
                  setCreateModalOpen(
                    false
                  )
                }
              >
                Close
              </button>
            </div>

            <form
              onSubmit={
                handleCreateSubmit
              }
            >
              <div className="super-admin-modal-body">
                <div className="super-admin-login-form">
                  <div className="super-admin-form-group">
                    <label>
                      Restaurant
                    </label>

                    <select
                      required
                      value={
                        createForm.restaurantId
                      }
                      onChange={(
                        event
                      ) =>
                        setCreateForm(
                          (
                            current
                          ) => ({
                            ...current,
                            restaurantId:
                              event
                                .target
                                .value,
                          })
                        )
                      }
                    >
                      <option value="">
                        Select Restaurant
                      </option>

                      {restaurants.map(
                        (
                          restaurant
                        ) => (
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

                  <div className="super-admin-form-group">
                    <label>
                      Full Name
                    </label>

                    <input
                      required
                      minLength="2"
                      maxLength="100"
                      value={
                        createForm.fullName
                      }
                      onChange={(
                        event
                      ) =>
                        setCreateForm(
                          (
                            current
                          ) => ({
                            ...current,
                            fullName:
                              event
                                .target
                                .value,
                          })
                        )
                      }
                      placeholder="Admin full name"
                    />
                  </div>

                  <div className="super-admin-form-group">
                    <label>
                      Email
                    </label>

                    <input
                      required
                      type="email"
                      value={
                        createForm.email
                      }
                      onChange={(
                        event
                      ) =>
                        setCreateForm(
                          (
                            current
                          ) => ({
                            ...current,
                            email:
                              event
                                .target
                                .value,
                          })
                        )
                      }
                      placeholder="Admin email"
                    />
                  </div>

                  <div className="super-admin-form-group">
                    <label>
                      Initial Password
                    </label>

                    <input
                      required
                      type="password"
                      minLength="8"
                      maxLength="100"
                      value={
                        createForm.password
                      }
                      onChange={(
                        event
                      ) =>
                        setCreateForm(
                          (
                            current
                          ) => ({
                            ...current,
                            password:
                              event
                                .target
                                .value,
                          })
                        )
                      }
                      placeholder="Minimum 8 characters"
                    />
                  </div>
                </div>
              </div>

              <div className="super-admin-modal-footer">
                <button
                  type="button"
                  className={
                    "super-admin-button " +
                    "super-admin-button-secondary"
                  }
                  onClick={() =>
                    setCreateModalOpen(
                      false
                    )
                  }
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className={
                    "super-admin-button " +
                    "super-admin-button-primary"
                  }
                >
                  Create Admin
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* =====================================
          EDIT MODAL
      ===================================== */}

      {editAdmin && (
        <div className="super-admin-modal-backdrop">
          <div className="super-admin-modal">
            <div className="super-admin-modal-header">
              <h2>
                Edit Restaurant Admin
              </h2>

              <button
                type="button"
                className={
                  "super-admin-button " +
                  "super-admin-button-secondary"
                }
                onClick={() =>
                  setEditAdmin(
                    null
                  )
                }
              >
                Close
              </button>
            </div>

            <form
              onSubmit={
                handleEditSubmit
              }
            >
              <div className="super-admin-modal-body">
                <div className="super-admin-login-form">
                  <div className="super-admin-form-group">
                    <label>
                      Restaurant
                    </label>

                    <input
                      value={getRestaurantName(
                        editAdmin
                      )}
                      disabled
                    />
                  </div>

                  <div className="super-admin-form-group">
                    <label>
                      Full Name
                    </label>

                    <input
                      required
                      minLength="2"
                      maxLength="100"
                      value={
                        editForm.fullName
                      }
                      onChange={(
                        event
                      ) =>
                        setEditForm(
                          (
                            current
                          ) => ({
                            ...current,
                            fullName:
                              event
                                .target
                                .value,
                          })
                        )
                      }
                    />
                  </div>

                  <div className="super-admin-form-group">
                    <label>
                      Email
                    </label>

                    <input
                      required
                      type="email"
                      value={
                        editForm.email
                      }
                      onChange={(
                        event
                      ) =>
                        setEditForm(
                          (
                            current
                          ) => ({
                            ...current,
                            email:
                              event
                                .target
                                .value,
                          })
                        )
                      }
                    />
                  </div>
                </div>
              </div>

              <div className="super-admin-modal-footer">
                <button
                  type="button"
                  className={
                    "super-admin-button " +
                    "super-admin-button-secondary"
                  }
                  onClick={() =>
                    setEditAdmin(
                      null
                    )
                  }
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className={
                    "super-admin-button " +
                    "super-admin-button-primary"
                  }
                  disabled={
                    processingAdminId ===
                    editAdmin.id
                  }
                >
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* =====================================
          PASSWORD RESET MODAL
      ===================================== */}

      {passwordAdmin && (
        <div className="super-admin-modal-backdrop">
          <div className="super-admin-modal">
            <div className="super-admin-modal-header">
              <h2>
                Reset Password
              </h2>

              <button
                type="button"
                className={
                  "super-admin-button " +
                  "super-admin-button-secondary"
                }
                onClick={() => {
                  setPasswordAdmin(
                    null
                  );

                  setNewPassword(
                    ""
                  );
                }}
              >
                Close
              </button>
            </div>

            <form
              onSubmit={
                handlePasswordReset
              }
            >
              <div className="super-admin-modal-body">
                <p>
                  Reset password for{" "}
                  <strong>
                    {
                      passwordAdmin.fullName
                    }
                  </strong>
                  .
                </p>

                <div className="super-admin-form-group">
                  <label>
                    New Password
                  </label>

                  <input
                    required
                    type="password"
                    minLength="8"
                    maxLength="100"
                    value={
                      newPassword
                    }
                    onChange={(
                      event
                    ) =>
                      setNewPassword(
                        event.target
                          .value
                      )
                    }
                    placeholder="Minimum 8 characters"
                  />
                </div>
              </div>

              <div className="super-admin-modal-footer">
                <button
                  type="button"
                  className={
                    "super-admin-button " +
                    "super-admin-button-secondary"
                  }
                  onClick={() => {
                    setPasswordAdmin(
                      null
                    );

                    setNewPassword(
                      ""
                    );
                  }}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className={
                    "super-admin-button " +
                    "super-admin-button-primary"
                  }
                  disabled={
                    processingAdminId ===
                    passwordAdmin.id
                  }
                >
                  Reset Password
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default SuperAdminRestaurantAdmins;