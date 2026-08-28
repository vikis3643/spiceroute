import {
  useEffect,
  useState,
} from "react";

import {
  getPlatformSettings,
  updatePlatformSettings,
} from "../../services/superAdminApi";

function SuperAdminSettings() {
  const [form, setForm] =
    useState({
      platformName: "",
      supportEmail: "",
      supportPhone: "",
      defaultCommissionPercentage: "",
      defaultDeliveryFee: "",
      minimumOrderAmount: "",
      maintenanceMode: false,
      restaurantRegistrationEnabled: true,
    });

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  const [updatedAt, setUpdatedAt] =
    useState(null);

  // ==========================================
  // LOAD SETTINGS
  // ==========================================

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings =
    async () => {
      setLoading(true);
      setError("");
      setSuccess("");

      try {
        const data =
          await getPlatformSettings();

        setForm({
          platformName:
            data?.platformName || "",

          supportEmail:
            data?.supportEmail || "",

          supportPhone:
            data?.supportPhone || "",

          defaultCommissionPercentage:
            data?.defaultCommissionPercentage ??
            0,

          defaultDeliveryFee:
            data?.defaultDeliveryFee ??
            0,

          minimumOrderAmount:
            data?.minimumOrderAmount ??
            0,

          maintenanceMode:
            Boolean(
              data?.maintenanceMode
            ),

          restaurantRegistrationEnabled:
            data?.restaurantRegistrationEnabled !==
            false,
        });

        setUpdatedAt(
          data?.updatedAt || null
        );
      } catch (err) {
        setError(
          err.message ||
            "Unable to load platform settings"
        );
      } finally {
        setLoading(false);
      }
    };

  // ==========================================
  // UPDATE FIELD
  // ==========================================

  const updateField = (
    field,
    value
  ) => {
    setForm(
      (current) => ({
        ...current,
        [field]: value,
      })
    );
  };

  // ==========================================
  // SAVE SETTINGS
  // ==========================================

  const handleSubmit =
    async (event) => {
      event.preventDefault();

      setError("");
      setSuccess("");

      const commission =
        Number(
          form.defaultCommissionPercentage
        );

      const deliveryFee =
        Number(
          form.defaultDeliveryFee
        );

      const minimumOrder =
        Number(
          form.minimumOrderAmount
        );

      if (
        Number.isNaN(commission) ||
        commission < 0 ||
        commission > 100
      ) {
        setError(
          "Default commission must be between 0 and 100"
        );

        return;
      }

      if (
        Number.isNaN(deliveryFee) ||
        deliveryFee < 0
      ) {
        setError(
          "Default delivery fee cannot be negative"
        );

        return;
      }

      if (
        Number.isNaN(minimumOrder) ||
        minimumOrder < 0
      ) {
        setError(
          "Minimum order amount cannot be negative"
        );

        return;
      }

      setSaving(true);

      try {
        const updated =
          await updatePlatformSettings({
            platformName:
              form.platformName.trim(),

            supportEmail:
              form.supportEmail.trim() ||
              null,

            supportPhone:
              form.supportPhone.trim() ||
              null,

            defaultCommissionPercentage:
              commission,

            defaultDeliveryFee:
              deliveryFee,

            minimumOrderAmount:
              minimumOrder,

            maintenanceMode:
              form.maintenanceMode,

            restaurantRegistrationEnabled:
              form.restaurantRegistrationEnabled,
          });

        setUpdatedAt(
          updated?.updatedAt || null
        );

        setSuccess(
          "Platform settings updated successfully."
        );
      } catch (err) {
        setError(
          err.message ||
            "Platform settings could not be updated"
        );
      } finally {
        setSaving(false);
      }
    };

  // ==========================================
  // FORMAT DATE
  // ==========================================

  const formatDate = (
    value
  ) => {
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
  // LOADING
  // ==========================================

  if (loading) {
    return (
      <div className="super-admin-page">
        <div className="super-admin-loading-card">
          Loading platform settings...
        </div>
      </div>
    );
  }

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="super-admin-page">
      <div className="super-admin-page-header">
        <div>
          <h1>
            Platform Settings
          </h1>

          <p>
            Configure platform-wide
            operational settings.
          </p>
        </div>

        <button
          type="button"
          className="super-admin-button super-admin-button-secondary"
          onClick={
            loadSettings
          }
        >
          Reload
        </button>
      </div>

      {success && (
        <div
          className="super-admin-card"
          style={{
            marginBottom:
              "18px",

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
              "18px",
          }}
        >
          {error}
        </div>
      )}

      {/* =====================================
          SYSTEM STATUS
      ===================================== */}

      <div className="super-admin-stat-grid">
        <div className="super-admin-stat-card">
          <span>
            Maintenance Mode
          </span>

          <strong
            style={{
              fontSize:
                "22px",
            }}
          >
            {form.maintenanceMode
              ? "ON"
              : "OFF"}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Restaurant Registration
          </span>

          <strong
            style={{
              fontSize:
                "22px",
            }}
          >
            {form.restaurantRegistrationEnabled
              ? "ENABLED"
              : "DISABLED"}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Default Commission
          </span>

          <strong
            style={{
              fontSize:
                "22px",
            }}
          >
            {Number(
              form.defaultCommissionPercentage ||
                0
            ).toFixed(2)}
            %
          </strong>
        </div>
      </div>

      {/* =====================================
          SETTINGS FORM
      ===================================== */}

      <div className="super-admin-section">
        <form
          className="super-admin-card"
          onSubmit={
            handleSubmit
          }
        >
          <h2 className="super-admin-section-title">
            General Settings
          </h2>

          <div
            style={{
              display:
                "grid",

              gridTemplateColumns:
                "repeat(auto-fit, minmax(260px, 1fr))",

              gap:
                "18px",
            }}
          >
            <div className="super-admin-form-group">
              <label>
                Platform Name
              </label>

              <input
                required
                minLength="2"
                maxLength="100"
                value={
                  form.platformName
                }
                onChange={(
                  event
                ) =>
                  updateField(
                    "platformName",
                    event.target.value
                  )
                }
              />
            </div>

            <div className="super-admin-form-group">
              <label>
                Support Email
              </label>

              <input
                type="email"
                value={
                  form.supportEmail
                }
                onChange={(
                  event
                ) =>
                  updateField(
                    "supportEmail",
                    event.target.value
                  )
                }
                placeholder="support@spiceroute.com"
              />
            </div>

            <div className="super-admin-form-group">
              <label>
                Support Phone
              </label>

              <input
                maxLength="20"
                value={
                  form.supportPhone
                }
                onChange={(
                  event
                ) =>
                  updateField(
                    "supportPhone",
                    event.target.value
                  )
                }
                placeholder="+91..."
              />
            </div>

            <div className="super-admin-form-group">
              <label>
                Default Commission (%)
              </label>

              <input
                required
                type="number"
                min="0"
                max="100"
                step="0.01"
                value={
                  form.defaultCommissionPercentage
                }
                onChange={(
                  event
                ) =>
                  updateField(
                    "defaultCommissionPercentage",
                    event.target.value
                  )
                }
              />
            </div>

            <div className="super-admin-form-group">
              <label>
                Default Delivery Fee (₹)
              </label>

              <input
                required
                type="number"
                min="0"
                step="0.01"
                value={
                  form.defaultDeliveryFee
                }
                onChange={(
                  event
                ) =>
                  updateField(
                    "defaultDeliveryFee",
                    event.target.value
                  )
                }
              />
            </div>

            <div className="super-admin-form-group">
              <label>
                Minimum Order Amount (₹)
              </label>

              <input
                required
                type="number"
                min="0"
                step="0.01"
                value={
                  form.minimumOrderAmount
                }
                onChange={(
                  event
                ) =>
                  updateField(
                    "minimumOrderAmount",
                    event.target.value
                  )
                }
              />
            </div>
          </div>

          {/* =================================
              OPERATIONAL TOGGLES
          ================================= */}

          <div
            style={{
              marginTop:
                "28px",
            }}
          >
            <h2 className="super-admin-section-title">
              Operational Controls
            </h2>

            <div
              style={{
                display:
                  "grid",

                gridTemplateColumns:
                  "repeat(auto-fit, minmax(280px, 1fr))",

                gap:
                  "16px",
              }}
            >
              <label
                className="super-admin-card"
                style={{
                  display:
                    "flex",

                  justifyContent:
                    "space-between",

                  alignItems:
                    "center",

                  gap:
                    "20px",

                  cursor:
                    "pointer",
                }}
              >
                <div>
                  <strong>
                    Maintenance Mode
                  </strong>

                  <p
                    style={{
                      margin:
                        "7px 0 0",

                      color:
                        "#6b7280",
                    }}
                  >
                    Blocks new order
                    quotes and order
                    placement while
                    enabled.
                  </p>
                </div>

                <input
                  type="checkbox"
                  checked={
                    form.maintenanceMode
                  }
                  onChange={(
                    event
                  ) =>
                    updateField(
                      "maintenanceMode",
                      event.target.checked
                    )
                  }
                  style={{
                    width:
                      "20px",

                    height:
                      "20px",
                  }}
                />
              </label>

              <label
                className="super-admin-card"
                style={{
                  display:
                    "flex",

                  justifyContent:
                    "space-between",

                  alignItems:
                    "center",

                  gap:
                    "20px",

                  cursor:
                    "pointer",
                }}
              >
                <div>
                  <strong>
                    Restaurant Registration
                  </strong>

                  <p
                    style={{
                      margin:
                        "7px 0 0",

                      color:
                        "#6b7280",
                    }}
                  >
                    Controls whether
                    new restaurants can
                    submit registration
                    requests.
                  </p>
                </div>

                <input
                  type="checkbox"
                  checked={
                    form.restaurantRegistrationEnabled
                  }
                  onChange={(
                    event
                  ) =>
                    updateField(
                      "restaurantRegistrationEnabled",
                      event.target.checked
                    )
                  }
                  style={{
                    width:
                      "20px",

                    height:
                      "20px",
                  }}
                />
              </label>
            </div>
          </div>

          <div
            style={{
              display:
                "flex",

              justifyContent:
                "space-between",

              alignItems:
                "center",

              flexWrap:
                "wrap",

              gap:
                "14px",

              marginTop:
                "28px",

              paddingTop:
                "20px",

              borderTop:
                "1px solid #e5e7eb",
            }}
          >
            <small
              style={{
                color:
                  "#6b7280",
              }}
            >
              Last updated:{" "}
              {formatDate(
                updatedAt
              )}
            </small>

            <button
              type="submit"
              disabled={
                saving
              }
              className="super-admin-button super-admin-button-primary"
            >
              {saving
                ? "Saving..."
                : "Save Settings"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default SuperAdminSettings;