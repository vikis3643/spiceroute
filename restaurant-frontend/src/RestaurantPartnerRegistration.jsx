import {
  useState,
} from "react";

const API_BASE_URL =
  `${import.meta.env.VITE_API_BASE_URL}`;

const INITIAL_FORM = {
  restaurantName: "",
  description: "",
  restaurantEmail: "",
  restaurantPhone: "",
  address: "",
  city: "",
  state: "",
  logoUrl: "",
  ownerName: "",
  ownerEmail: "",
};

function RestaurantPartnerRegistration() {
  const [form, setForm] =
    useState(INITIAL_FORM);

  const [submitting, setSubmitting] =
    useState(false);

  const [error, setError] =
    useState("");

  const [registration, setRegistration] =
    useState(null);

  function updateField(
    field,
    value,
  ) {
    setForm((current) => ({
      ...current,
      [field]: value,
    }));
  }

  // ==========================================
  // SUBMIT APPLICATION
  // ==========================================

  async function handleSubmit(event) {
    event.preventDefault();

    setSubmitting(true);
    setError("");

    try {
      const response = await fetch(
        `${API_BASE_URL}/restaurant-registration`,
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json",
          },

          body: JSON.stringify({
            restaurantName:
              form.restaurantName.trim(),

            description:
              form.description.trim(),

            restaurantEmail:
              form.restaurantEmail
                .trim()
                .toLowerCase(),

            restaurantPhone:
              form.restaurantPhone.trim(),

            address:
              form.address.trim(),

            city:
              form.city.trim(),

            state:
              form.state.trim(),

            logoUrl:
              form.logoUrl.trim(),

            ownerName:
              form.ownerName.trim(),

            ownerEmail:
              form.ownerEmail
                .trim()
                .toLowerCase(),
          }),
        },
      );

      if (!response.ok) {
        let message =
          "Restaurant registration could not be submitted.";

        try {
          const body =
            await response.json();

          message =
            body.message ||
            body.detail ||
            body.error ||
            message;
        } catch {
          // Ignore response parsing failure.
        }

        throw new Error(message);
      }

      const data =
        await response.json();

      setRegistration(data);

      setForm(
        INITIAL_FORM,
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to submit restaurant registration.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  // ==========================================
  // SUCCESS SCREEN
  // ==========================================

  if (registration) {
    return (
      <div
        style={{
          minHeight: "100vh",
          background: "#f8fafc",
          padding: "40px 20px",
        }}
      >
        <div
          style={{
            maxWidth: "720px",
            margin: "0 auto",
            background: "#ffffff",
            borderRadius: "18px",
            padding: "36px",
            boxShadow:
              "0 16px 45px rgba(15, 23, 42, 0.08)",
          }}
        >
          <div
            style={{
              fontSize: "52px",
              textAlign: "center",
              marginBottom: "12px",
            }}
          >
            ✅
          </div>

          <h1
            style={{
              textAlign: "center",
              marginBottom: "10px",
            }}
          >
            Application Submitted
          </h1>

          <p
            style={{
              textAlign: "center",
              color: "#64748b",
              lineHeight: "1.6",
            }}
          >
            Your restaurant registration
            request has been sent to the
            SpiceRoute Super Admin for
            verification.
          </p>

          <div
            style={{
              marginTop: "28px",
              background: "#f8fafc",
              borderRadius: "14px",
              padding: "22px",
            }}
          >
            <p>
              <strong>
                Restaurant:
              </strong>{" "}
              {
                registration.restaurantName
              }
            </p>

            <p>
              <strong>
                Application ID:
              </strong>{" "}
              {
                registration.restaurantId
              }
            </p>

            <p>
              <strong>
                Owner:
              </strong>{" "}
              {
                registration.ownerName
              }
            </p>

            <p>
              <strong>
                Status:
              </strong>{" "}
              {
                registration.approvalStatus
              }
            </p>
          </div>

          <div
            style={{
              marginTop: "24px",
              padding: "18px",
              borderRadius: "12px",
              background: "#fff7ed",
              color: "#9a3412",
              lineHeight: "1.6",
            }}
          >
            After Super Admin approval,
            Restaurant Admin login
            credentials and a temporary
            password will be sent to your
            owner email address.
          </div>

          <div
            style={{
              display: "flex",
              justifyContent: "center",
              gap: "12px",
              marginTop: "28px",
              flexWrap: "wrap",
            }}
          >
            <button
              type="button"
              onClick={() =>
                setRegistration(null)
              }
            >
              Submit Another
              Restaurant
            </button>

            <button
              type="button"
              onClick={() => {
                window.location.href =
                  "/";
              }}
            >
              Back to SpiceRoute
            </button>
          </div>
        </div>
      </div>
    );
  }

  // ==========================================
  // FORM
  // ==========================================

  return (
    <div
      style={{
        minHeight: "100vh",
        background: "#f8fafc",
      }}
    >
      <header
        style={{
          background: "#ffffff",
          borderBottom:
            "1px solid #e2e8f0",
          padding: "16px 24px",
        }}
      >
        <div
          style={{
            maxWidth: "1100px",
            margin: "0 auto",
            display: "flex",
            justifyContent:
              "space-between",
            alignItems: "center",
            gap: "16px",
          }}
        >
          <a
            href="/"
            style={{
              textDecoration: "none",
              color: "#111827",
              fontSize: "22px",
              fontWeight: "700",
            }}
          >
            🍽️ SpiceRoute
          </a>

          <a href="/">
            Back to Home
          </a>
        </div>
      </header>

      <main
        style={{
          maxWidth: "900px",
          margin: "0 auto",
          padding: "42px 20px",
        }}
      >
        <div
          style={{
            textAlign: "center",
            marginBottom: "32px",
          }}
        >
          <span
            style={{
              color: "#ea580c",
              fontWeight: "700",
            }}
          >
            PARTNER WITH US
          </span>

          <h1>
            Add Your Restaurant
          </h1>

          <p
            style={{
              color: "#64748b",
              lineHeight: "1.6",
            }}
          >
            Submit your restaurant
            details. Our Super Admin
            will review your application
            before your restaurant joins
            SpiceRoute.
          </p>
        </div>

        {error && (
          <div
            style={{
              background: "#fef2f2",
              color: "#b91c1c",
              padding: "15px",
              borderRadius: "10px",
              marginBottom: "20px",
            }}
          >
            {error}
          </div>
        )}

        <form
          onSubmit={
            handleSubmit
          }
          style={{
            background: "#ffffff",
            borderRadius: "18px",
            padding: "30px",
            boxShadow:
              "0 16px 45px rgba(15, 23, 42, 0.08)",
          }}
        >
          <h2>
            Restaurant Details
          </h2>

          <div
            style={{
              display: "grid",
              gridTemplateColumns:
                "repeat(auto-fit, minmax(260px, 1fr))",
              gap: "18px",
            }}
          >
            <FormField
              label="Restaurant Name"
              required
              value={
                form.restaurantName
              }
              onChange={(value) =>
                updateField(
                  "restaurantName",
                  value,
                )
              }
            />

            <FormField
              label="Restaurant Email"
              required
              type="email"
              value={
                form.restaurantEmail
              }
              onChange={(value) =>
                updateField(
                  "restaurantEmail",
                  value,
                )
              }
            />

            <FormField
              label="Restaurant Phone"
              required
              value={
                form.restaurantPhone
              }
              onChange={(value) =>
                updateField(
                  "restaurantPhone",
                  value,
                )
              }
            />

            <FormField
              label="City"
              required
              value={form.city}
              onChange={(value) =>
                updateField(
                  "city",
                  value,
                )
              }
            />

            <FormField
              label="State"
              required
              value={form.state}
              onChange={(value) =>
                updateField(
                  "state",
                  value,
                )
              }
            />

            <FormField
              label="Logo URL"
              value={form.logoUrl}
              onChange={(value) =>
                updateField(
                  "logoUrl",
                  value,
                )
              }
              placeholder="Optional"
            />
          </div>

          <div
            style={{
              marginTop: "18px",
            }}
          >
            <label>
              <strong>
                Restaurant Address *
              </strong>

              <textarea
                required
                value={
                  form.address
                }
                onChange={(event) =>
                  updateField(
                    "address",
                    event.target.value,
                  )
                }
                rows="3"
                style={{
                  display: "block",
                  width: "100%",
                  marginTop: "7px",
                  boxSizing:
                    "border-box",
                  padding: "12px",
                }}
              />
            </label>
          </div>

          <div
            style={{
              marginTop: "18px",
            }}
          >
            <label>
              <strong>
                Description
              </strong>

              <textarea
                value={
                  form.description
                }
                onChange={(event) =>
                  updateField(
                    "description",
                    event.target.value,
                  )
                }
                rows="4"
                maxLength="1000"
                style={{
                  display: "block",
                  width: "100%",
                  marginTop: "7px",
                  boxSizing:
                    "border-box",
                  padding: "12px",
                }}
              />
            </label>
          </div>

          <hr
            style={{
              margin: "30px 0",
              border: 0,
              borderTop:
                "1px solid #e2e8f0",
            }}
          />

          <h2>
            Owner Details
          </h2>

          <div
            style={{
              display: "grid",
              gridTemplateColumns:
                "repeat(auto-fit, minmax(260px, 1fr))",
              gap: "18px",
            }}
          >
            <FormField
              label="Owner Name"
              required
              value={
                form.ownerName
              }
              onChange={(value) =>
                updateField(
                  "ownerName",
                  value,
                )
              }
            />

            <FormField
              label="Owner Email"
              required
              type="email"
              value={
                form.ownerEmail
              }
              onChange={(value) =>
                updateField(
                  "ownerEmail",
                  value,
                )
              }
            />
          </div>

          <div
            style={{
              marginTop: "24px",
              background: "#eff6ff",
              color: "#1e40af",
              padding: "16px",
              borderRadius: "10px",
              lineHeight: "1.6",
            }}
          >
            You do not need to create an
            Admin password now. If your
            restaurant is approved,
            SpiceRoute will email your
            Restaurant Admin credentials
            and temporary password.
          </div>

          <button
            type="submit"
            disabled={
              submitting
            }
            style={{
              width: "100%",
              marginTop: "24px",
              padding: "14px",
              cursor:
                submitting
                  ? "not-allowed"
                  : "pointer",
            }}
          >
            {submitting
              ? "Submitting Application..."
              : "Submit Restaurant Application"}
          </button>
        </form>
      </main>
    </div>
  );
}

function FormField({
  label,
  value,
  onChange,
  type = "text",
  required = false,
  placeholder = "",
}) {
  return (
    <label>
      <strong>
        {label}
        {required ? " *" : ""}
      </strong>

      <input
        type={type}
        required={required}
        value={value}
        placeholder={
          placeholder
        }
        onChange={(event) =>
          onChange(
            event.target.value,
          )
        }
        style={{
          display: "block",
          width: "100%",
          marginTop: "7px",
          boxSizing: "border-box",
          padding: "12px",
        }}
      />
    </label>
  );
}

export default RestaurantPartnerRegistration;