import { useState } from "react";

const RESET_PASSWORD_API =
  `${import.meta.env.VITE_API_BASE_URL}/customer-auth/reset-password`;

function ResetPassword() {
  const resetToken = new URLSearchParams(
    window.location.search,
  ).get("token");

  const [formData, setFormData] = useState({
    newPassword: "",
    confirmPassword: "",
  });

  const [submitting, setSubmitting] =
    useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  function handleInputChange(event) {
    const { name, value } = event.target;

    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!resetToken) {
      setError("The password-reset link is invalid.");
      return;
    }

    if (
      formData.newPassword !==
      formData.confirmPassword
    ) {
      setError("The passwords do not match.");
      return;
    }

    try {
      setSubmitting(true);
      setError("");

      const response = await fetch(
        RESET_PASSWORD_API,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            token: resetToken,
            newPassword: formData.newPassword,
            confirmPassword:
              formData.confirmPassword,
          }),
        },
      );

      const responseData = await response.json();

      if (!response.ok) {
        throw new Error(
          responseData.message ||
            "Password reset failed",
        );
      }

      setSuccess(true);
    } catch (requestError) {
      setError(
        requestError.message ||
          "The reset link is invalid or expired.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="customer-auth-page">
      <section className="customer-auth-card">
        <div className="login-brand">
          <div className="login-brand-icon">🍽️</div>

          <div>
            <strong>SpiceRoute</strong>
            <span>Secure account recovery</span>
          </div>
        </div>

        {success ? (
          <div className="password-reset-success">
            <span className="success-icon">✓</span>

            <span className="eyebrow">
              Password updated
            </span>

            <h1>Your password has been reset</h1>

            <p>
              You can now sign in using your new
              password.
            </p>

            <a
              className="admin-login-submit"
              href="/customer-login"
            >
              Continue to Customer Login
            </a>
          </div>
        ) : (
          <>
            <div className="login-heading">
              <span className="eyebrow">
                Account recovery
              </span>

              <h1>Create a new password</h1>

              <p>
                Choose a secure password containing
                uppercase, lowercase and a number.
              </p>
            </div>

            <form
              className="admin-login-form"
              onSubmit={handleSubmit}
            >
              <label>
                New password
                <input
                  type="password"
                  name="newPassword"
                  placeholder="Enter a new password"
                  value={formData.newPassword}
                  onChange={handleInputChange}
                  minLength="8"
                  maxLength="72"
                  autoComplete="new-password"
                  required
                />
              </label>

              <label>
                Confirm new password
                <input
                  type="password"
                  name="confirmPassword"
                  placeholder="Enter the password again"
                  value={formData.confirmPassword}
                  onChange={handleInputChange}
                  minLength="8"
                  maxLength="72"
                  autoComplete="new-password"
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
                  submitting || !resetToken
                }
              >
                {submitting
                  ? "Updating password..."
                  : "Reset password"}
              </button>
            </form>
          </>
        )}
      </section>
    </main>
  );
}

export default ResetPassword;