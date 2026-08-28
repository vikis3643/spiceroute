import { useState } from "react";

const CHANGE_PASSWORD_API_URL =
  "http://localhost:8080/api/restaurant-admin/auth/change-password";

function AdminChangePassword() {
  const [formData, setFormData] =
    useState({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });

  const [submitting, setSubmitting] =
    useState(false);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  function handleInputChange(event) {
    const { name, value } =
      event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (
      formData.newPassword.length < 8
    ) {
      setError(
        "New password must contain at least 8 characters.",
      );

      return;
    }

    if (
      formData.newPassword !==
      formData.confirmPassword
    ) {
      setError(
        "New password and confirm password do not match.",
      );

      return;
    }

    if (
      formData.currentPassword ===
      formData.newPassword
    ) {
      setError(
        "New password must be different from your temporary password.",
      );

      return;
    }

    const token =
      sessionStorage.getItem(
        "adminToken",
      );

    if (!token) {
      window.location.href =
        "/admin";

      return;
    }

    try {
      setSubmitting(true);

      const response = await fetch(
        CHANGE_PASSWORD_API_URL,
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json",

            Authorization:
              `Bearer ${token}`,
          },

          body: JSON.stringify({
            currentPassword:
              formData.currentPassword,

            newPassword:
              formData.newPassword,
          }),
        },
      );

      if (!response.ok) {
        let message =
          "Password could not be changed.";

        try {
          const data =
            await response.json();

          message =
            data.message ||
            data.detail ||
            data.error ||
            message;
        } catch {
          // Ignore parsing error.
        }

        throw new Error(message);
      }

      sessionStorage.setItem(
        "adminMustChangePassword",
        "false",
      );

      setSuccess(
        "Password changed successfully. Opening your dashboard...",
      );

      setTimeout(() => {
        window.location.href =
          "/admin";
      }, 1200);
    } catch (err) {
      setError(
        err.message ||
          "Password could not be changed.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  function handleLogout() {
    sessionStorage.removeItem(
      "adminToken",
    );

    sessionStorage.removeItem(
      "adminId",
    );

    sessionStorage.removeItem(
      "adminName",
    );

    sessionStorage.removeItem(
      "adminEmail",
    );

    sessionStorage.removeItem(
      "restaurantId",
    );

    sessionStorage.removeItem(
      "restaurantName",
    );

    sessionStorage.removeItem(
      "adminMustChangePassword",
    );

    window.location.href =
      "/admin";
  }

  return (
    <main className="admin-login-page">
      <section className="admin-login-card">

        <div className="login-brand">
          <div className="login-brand-icon">
            🔐
          </div>

          <div>
            <strong>
              SpiceRoute
            </strong>

            <span>
              Restaurant management
            </span>
          </div>
        </div>

        <div className="login-heading">
          <span className="eyebrow">
            Security Required
          </span>

          <h1>
            Change your password
          </h1>

          <p>
            You are signing in with a
            temporary password. Create a
            new password before accessing
            your restaurant dashboard.
          </p>
        </div>

        <form
          className="admin-login-form"
          onSubmit={handleSubmit}
        >
          <label>
            Temporary Password

            <input
              type="password"
              name="currentPassword"
              placeholder="Enter temporary password"
              value={
                formData.currentPassword
              }
              onChange={
                handleInputChange
              }
              autoComplete="current-password"
              required
            />
          </label>

          <label>
            New Password

            <input
              type="password"
              name="newPassword"
              placeholder="Minimum 8 characters"
              value={
                formData.newPassword
              }
              onChange={
                handleInputChange
              }
              autoComplete="new-password"
              minLength="8"
              required
            />
          </label>

          <label>
            Confirm New Password

            <input
              type="password"
              name="confirmPassword"
              placeholder="Enter new password again"
              value={
                formData.confirmPassword
              }
              onChange={
                handleInputChange
              }
              autoComplete="new-password"
              minLength="8"
              required
            />
          </label>

          {error && (
            <p className="admin-login-error">
              {error}
            </p>
          )}

          {success && (
            <p
              style={{
                color: "#15803d",
                fontWeight: "600",
              }}
            >
              {success}
            </p>
          )}

          <button
            className="admin-login-submit"
            type="submit"
            disabled={submitting}
          >
            {submitting
              ? "Changing password..."
              : "Change Password & Continue"}
          </button>
        </form>

        <button
          type="button"
          className="login-back-button"
          onClick={handleLogout}
          disabled={submitting}
        >
          Sign out
        </button>

        <p className="login-security-note">
          🔒 Your temporary password will
          stop working after your password
          is changed.
        </p>

      </section>
    </main>
  );
}

export default AdminChangePassword;