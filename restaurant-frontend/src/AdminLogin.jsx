import { useState } from "react";

const LOGIN_API_URL =
  `${import.meta.env.VITE_API_BASE_URL}/restaurant-admin/auth/login`;

function AdminLogin({ onLogin }) {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [submitting, setSubmitting] =
    useState(false);

  const [error, setError] =
    useState("");

  function handleInputChange(event) {
    const { name, value } =
      event.target;

    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSubmitting(true);
      setError("");

      const response = await fetch(
        LOGIN_API_URL,
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json",
          },

          body: JSON.stringify({
            email:
              formData.email
                .trim()
                .toLowerCase(),

            password:
              formData.password,
          }),
        },
      );

      if (!response.ok) {
        throw new Error(
          "Invalid email or password",
        );
      }

      const loginData =
        await response.json();

      if (!loginData.token) {
        throw new Error(
          "Login token was not returned",
        );
      }

      // ==========================================
      // SAVE ADMIN SESSION
      // ==========================================

      sessionStorage.setItem(
        "adminToken",
        loginData.token,
      );

      if (loginData.adminId != null) {
        sessionStorage.setItem(
          "adminId",
          String(loginData.adminId),
        );
      }

      if (loginData.adminName) {
        sessionStorage.setItem(
          "adminName",
          loginData.adminName,
        );
      }

      if (loginData.adminEmail) {
        sessionStorage.setItem(
          "adminEmail",
          loginData.adminEmail,
        );
      }

      if (loginData.restaurantId != null) {
        sessionStorage.setItem(
          "restaurantId",
          String(
            loginData.restaurantId,
          ),
        );
      }

      if (loginData.restaurantName) {
        sessionStorage.setItem(
          "restaurantName",
          loginData.restaurantName,
        );
      }

      const mustChangePassword =
        loginData.mustChangePassword ===
        true;

      sessionStorage.setItem(
        "adminMustChangePassword",
        String(mustChangePassword),
      );

      // ==========================================
      // FIRST LOGIN
      // ==========================================

      if (mustChangePassword) {
        window.location.href =
          "/admin/change-password";

        return;
      }

      // ==========================================
      // NORMAL LOGIN
      // ==========================================

      if (onLogin) {
        onLogin(
          loginData.token,
        );
      } else {
        window.location.href =
          "/admin";
      }
    } catch {
      setError(
        "Login failed. Check your email and password.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  function returnToRestaurant() {
    window.location.href = "/";
  }

  return (
    <main className="admin-login-page">
      <section className="admin-login-card">

        <button
          className="login-back-button"
          type="button"
          onClick={
            returnToRestaurant
          }
        >
          ← Back to restaurant
        </button>

        <div className="login-brand">

          <div className="login-brand-icon">
            🍽️
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
            Restaurant Admin
          </span>

          <h1>
            Welcome back
          </h1>

          <p>
            Sign in to manage your
            restaurant, orders, menu and
            performance.
          </p>

        </div>

        <form
          className="admin-login-form"
          onSubmit={
            handleSubmit
          }
        >

          <label>
            Email

            <input
              type="email"
              name="email"
              placeholder="Enter admin email"
              value={
                formData.email
              }
              onChange={
                handleInputChange
              }
              autoComplete="email"
              required
            />
          </label>

          <label>
            Password

            <input
              type="password"
              name="password"
              placeholder="Enter admin password"
              value={
                formData.password
              }
              onChange={
                handleInputChange
              }
              autoComplete="current-password"
              required
            />
          </label>

          {error && (
            <p className="admin-login-error">
              {error}
            </p>
          )}

          <button
            className="admin-login-submit"
            type="submit"
            disabled={
              submitting
            }
          >
            {submitting
              ? "Signing in..."
              : "Sign in to dashboard"}
          </button>

        </form>

        <p className="login-security-note">
          🔒 This dashboard is protected
          with secure restaurant admin
          authentication.
        </p>

      </section>
    </main>
  );
}

export default AdminLogin;