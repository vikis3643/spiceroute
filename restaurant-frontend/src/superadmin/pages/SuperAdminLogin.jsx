import { useState } from "react";

import {
  superAdminLogin,
} from "../../services/superAdminApi";

function SuperAdminLogin({
  onLoginSuccess,
}) {
  const [email, setEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const handleSubmit = async (
    event
  ) => {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      const response =
        await superAdminLogin(
          email.trim(),
          password
        );

      if (onLoginSuccess) {
        onLoginSuccess(response);
      }
    } catch (err) {
      setError(
        err.message ||
          "Super Admin login failed"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="super-admin-login-page">
      <div className="super-admin-login-card">
        <div className="super-admin-login-header">
          <h1>Super Admin</h1>

          <p>
            Sign in to manage the complete
            restaurant platform.
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="super-admin-login-form"
        >
          <div className="super-admin-form-group">
            <label htmlFor="superAdminEmail">
              Email
            </label>

            <input
              id="superAdminEmail"
              type="email"
              value={email}
              onChange={(event) =>
                setEmail(
                  event.target.value
                )
              }
              placeholder="Enter Super Admin email"
              autoComplete="email"
              required
            />
          </div>

          <div className="super-admin-form-group">
            <label htmlFor="superAdminPassword">
              Password
            </label>

            <input
              id="superAdminPassword"
              type="password"
              value={password}
              onChange={(event) =>
                setPassword(
                  event.target.value
                )
              }
              placeholder="Enter password"
              autoComplete="current-password"
              required
            />
          </div>

          {error && (
            <div className="super-admin-login-error">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="super-admin-login-button"
          >
            {loading
              ? "Signing in..."
              : "Sign In"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default SuperAdminLogin;