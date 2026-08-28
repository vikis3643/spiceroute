import { useEffect, useState } from "react";

const PROFILE_API =
  "http://localhost:8080/api/customer/profile";

const EMPTY_PROFILE = {
  fullName: "",
  email: "",
  phone: "",
  defaultDeliveryAddress: "",
};

function getCustomerHeaders(includeJson = false) {
  const token =
    sessionStorage.getItem("customerToken");

  const headers = {
    Authorization: `Bearer ${token}`,
  };

  if (includeJson) {
    headers["Content-Type"] = "application/json";
  }

  return headers;
}

function handleUnauthorized(response) {
  if (response.status === 401) {
    sessionStorage.removeItem("customerToken");
    sessionStorage.removeItem("customerId");
    sessionStorage.removeItem("customerName");
    sessionStorage.removeItem("customerEmail");

    window.location.href = "/customer-login";

    throw new Error("Your login session expired");
  }
}

async function requestProfile() {
  const response = await fetch(PROFILE_API, {
    headers: getCustomerHeaders(),
  });

  handleUnauthorized(response);

  if (!response.ok) {
    throw new Error("Unable to load profile");
  }

  return response.json();
}

function CustomerProfile({ onBack, onOrders }) {
  const [formData, setFormData] =
    useState(EMPTY_PROFILE);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] =
    useState("");

  useEffect(() => {
    let requestIsActive = true;

    requestProfile()
      .then((profile) => {
        if (requestIsActive) {
          setFormData({
            fullName: profile.fullName ?? "",
            email: profile.email ?? "",
            phone: profile.phone ?? "",
            defaultDeliveryAddress:
              profile.defaultDeliveryAddress ?? "",
          });

          setError("");
        }
      })
      .catch(() => {
        if (requestIsActive) {
          setError(
            "Could not load your profile. Please try again.",
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

  function handleInputChange(event) {
    const { name, value } = event.target;

    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }));
  }

  async function saveProfile(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccessMessage("");

      const response = await fetch(PROFILE_API, {
        method: "PUT",
        headers: getCustomerHeaders(true),
        body: JSON.stringify({
          fullName: formData.fullName,
          phone: formData.phone,
          defaultDeliveryAddress:
            formData.defaultDeliveryAddress,
        }),
      });

      handleUnauthorized(response);

      const savedProfile = await response.json();

      if (!response.ok) {
        throw new Error(
          savedProfile.message ||
            "Unable to save profile",
        );
      }

      setFormData({
        fullName: savedProfile.fullName ?? "",
        email: savedProfile.email ?? "",
        phone: savedProfile.phone ?? "",
        defaultDeliveryAddress:
          savedProfile.defaultDeliveryAddress ?? "",
      });

      sessionStorage.setItem(
        "customerName",
        savedProfile.fullName,
      );

      setSuccessMessage(
        "Your profile was saved successfully.",
      );
    } catch (requestError) {
      setError(
        requestError.message ||
          "Your profile could not be saved.",
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="customer-profile-page">
      <header className="customer-profile-header">
        <div>
          <span className="eyebrow">
            Customer account
          </span>

          <h1>My Profile</h1>

          <p>
            Save your contact and delivery information
            for faster checkout.
          </p>
        </div>

        <div className="profile-header-actions">
          <button
            type="button"
            onClick={onOrders}
          >
            My Orders
          </button>

          <button
            type="button"
            onClick={onBack}
          >
            Back to restaurant
          </button>
        </div>
      </header>

      <section className="customer-profile-content">
        {loading ? (
          <div className="status-message">
            Loading your profile...
          </div>
        ) : (
          <form
            className="customer-profile-card"
            onSubmit={saveProfile}
          >
            <div className="profile-avatar">
              {formData.fullName
                .charAt(0)
                .toUpperCase() || "C"}
            </div>

            <div className="profile-form-heading">
              <h2>Personal information</h2>

              <p>
                Your email cannot be changed because
                it identifies your account.
              </p>
            </div>

            <label>
              Full name
              <input
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={handleInputChange}
                minLength="2"
                maxLength="100"
                required
              />
            </label>

            <label>
              Email address
              <input
                type="email"
                value={formData.email}
                disabled
              />
            </label>

            <label>
              Phone number
              <input
                type="tel"
                name="phone"
                placeholder="Enter 10-digit phone number"
                value={formData.phone}
                onChange={handleInputChange}
                pattern="[0-9]{10}"
                maxLength="10"
                required
              />
            </label>

            <label>
              Default delivery address
              <textarea
                name="defaultDeliveryAddress"
                placeholder="House number, street, city and PIN code"
                value={
                  formData.defaultDeliveryAddress
                }
                onChange={handleInputChange}
                minLength="10"
                maxLength="1000"
                rows="5"
                required
              />
            </label>

            {error && (
              <p className="admin-login-error">
                {error}
              </p>
            )}

            {successMessage && (
              <p className="profile-success-message">
                {successMessage}
              </p>
            )}

            <button
              className="admin-login-submit"
              type="submit"
              disabled={saving}
            >
              {saving
                ? "Saving profile..."
                : "Save profile"}
            </button>
          </form>
        )}
      </section>
    </main>
  );
}

export default CustomerProfile;