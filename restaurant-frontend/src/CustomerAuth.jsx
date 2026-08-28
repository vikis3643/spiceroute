import {
  useEffect,
  useRef,
  useState,
} from "react";

const CUSTOMER_AUTH_API =
  "http://localhost:8080/api/customer-auth";

const EMPTY_FORM = {
  fullName: "",
  email: "",
  phone: "",
  identifier: "",
  password: "",
  confirmPassword: "",
};

function CustomerAuth({ onSuccess }) {
  const googleButtonRef =
    useRef(null);

  const [mode, setMode] =
    useState("login");

  const [formData, setFormData] =
    useState(EMPTY_FORM);

  const [submitting, setSubmitting] =
    useState(false);

  const [error, setError] =
    useState("");

  const [
    successMessage,
    setSuccessMessage,
  ] = useState("");

  const isRegistering =
    mode === "register";

  const isForgotPassword =
    mode === "forgot";

  const googleClientId =
    import.meta.env
      .VITE_GOOGLE_CLIENT_ID;

  useEffect(() => {
    if (
      isForgotPassword ||
      !googleButtonRef.current ||
      !googleClientId
    ) {
      return undefined;
    }

    let cancelled = false;
    let attempts = 0;

    function initializeGoogleButton() {
      if (cancelled) {
        return;
      }

      if (!window.google?.accounts?.id) {
        attempts += 1;

        if (attempts < 20) {
          window.setTimeout(
            initializeGoogleButton,
            250,
          );
        } else {
          setError(
            "Google Sign-In could not be loaded.",
          );
        }

        return;
      }

      window.google.accounts.id.initialize({
        client_id: googleClientId,

        callback: async (
          credentialResponse,
        ) => {
          try {
            setSubmitting(true);
            setError("");
            setSuccessMessage("");

            const response = await fetch(
              `${CUSTOMER_AUTH_API}/google`,
              {
                method: "POST",
                headers: {
                  "Content-Type":
                    "application/json",
                },
                body: JSON.stringify({
                  credential:
                    credentialResponse
                      .credential,
                }),
              },
            );

            const responseData =
              await response.json();

            if (!response.ok) {
              throw new Error(
                responseData.message ||
                  "Google Sign-In failed.",
              );
            }

            saveCustomerSession(
              responseData,
            );

            onSuccess();
          } catch (requestError) {
            setError(
              requestError.message ||
                "Google Sign-In failed.",
            );
          } finally {
            setSubmitting(false);
          }
        },
      });

      googleButtonRef.current.innerHTML =
        "";

      window.google.accounts.id.renderButton(
        googleButtonRef.current,
        {
          type: "standard",
          theme: "outline",
          size: "large",
          text: isRegistering
            ? "signup_with"
            : "signin_with",
          shape: "rectangular",
          width: 400,
        },
      );
    }

    initializeGoogleButton();

    return () => {
      cancelled = true;
    };
  }, [
    googleClientId,
    isForgotPassword,
    isRegistering,
    onSuccess,
  ]);

  function saveCustomerSession(
    responseData,
  ) {
    sessionStorage.setItem(
      "customerToken",
      responseData.token,
    );

    sessionStorage.setItem(
      "customerId",
      String(responseData.customerId),
    );

    sessionStorage.setItem(
      "customerName",
      responseData.fullName,
    );

    sessionStorage.setItem(
      "customerEmail",
      responseData.email,
    );
  }

  function handleInputChange(event) {
    const { name, value } =
      event.target;

    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }));
  }

  function changeMode(newMode) {
    setMode(newMode);
    setFormData(EMPTY_FORM);
    setError("");
    setSuccessMessage("");
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (
      isRegistering &&
      formData.password !==
        formData.confirmPassword
    ) {
      setError(
        "The passwords do not match.",
      );
      return;
    }

    if (
      isRegistering &&
      !/^[0-9]{10}$/.test(
        formData.phone,
      )
    ) {
      setError(
        "Please enter a valid 10-digit phone number.",
      );
      return;
    }

    let endpoint;
    let requestBody;

    if (isForgotPassword) {
      endpoint = "forgot-password";

      requestBody = {
        email: formData.email,
      };
    } else if (isRegistering) {
      endpoint = "register";

      requestBody = {
        fullName: formData.fullName,
        email: formData.email,
        phone: formData.phone,
        password: formData.password,
        confirmPassword:
          formData.confirmPassword,
      };
    } else {
      endpoint = "login";

      requestBody = {
        identifier:
          formData.identifier,
        password: formData.password,
      };
    }

    try {
      setSubmitting(true);
      setError("");
      setSuccessMessage("");

      const response = await fetch(
        `${CUSTOMER_AUTH_API}/${endpoint}`,
        {
          method: "POST",
          headers: {
            "Content-Type":
              "application/json",
          },
          body: JSON.stringify(
            requestBody,
          ),
        },
      );

      const responseData =
        await response.json();

      if (!response.ok) {
        throw new Error(
          responseData.message ||
            "Request failed",
        );
      }

      if (isForgotPassword) {
        setSuccessMessage(
          responseData.message ||
            "If an account exists with this email, a reset link has been sent.",
        );

        return;
      }

      saveCustomerSession(
        responseData,
      );

      onSuccess();
    } catch (requestError) {
      setError(
        requestError.message ||
          "The request failed. Please try again.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  function returnToRestaurant() {
    window.location.href = "/";
  }

  return (
    <main className="customer-auth-page">
      <section className="customer-auth-card">
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
              Fresh food, fast delivery
            </span>
          </div>
        </div>

        {!isForgotPassword && (
          <div className="customer-auth-tabs">
            <button
              className={
                mode === "login"
                  ? "active"
                  : ""
              }
              type="button"
              onClick={() =>
                changeMode("login")
              }
            >
              Customer Login
            </button>

            <button
              className={
                mode === "register"
                  ? "active"
                  : ""
              }
              type="button"
              onClick={() =>
                changeMode("register")
              }
            >
              Create Account
            </button>
          </div>
        )}

        <div className="login-heading">
          <span className="eyebrow">
            {isForgotPassword
              ? "Account recovery"
              : isRegistering
                ? "Join SpiceRoute"
                : "Customer access"}
          </span>

          <h1>
            {isForgotPassword
              ? "Forgot your password?"
              : isRegistering
                ? "Create your account"
                : "Welcome back"}
          </h1>

          <p>
            {isForgotPassword
              ? "Enter your registered email and we will send you a secure reset link."
              : isRegistering
                ? "Register with your email and phone number or continue with Google."
                : "Sign in using your email, phone number or Google account."}
          </p>
        </div>

        <form
          className="admin-login-form"
          onSubmit={handleSubmit}
        >
          {isRegistering && (
            <label>
              Full name

              <input
                type="text"
                name="fullName"
                placeholder="Enter your full name"
                value={
                  formData.fullName
                }
                onChange={
                  handleInputChange
                }
                minLength="2"
                maxLength="100"
                autoComplete="name"
                required
              />
            </label>
          )}

          {mode === "login" ? (
            <label>
              Email or phone number

              <input
                type="text"
                name="identifier"
                placeholder="Enter email or 10-digit phone number"
                value={
                  formData.identifier
                }
                onChange={
                  handleInputChange
                }
                maxLength="150"
                autoComplete="username"
                required
              />
            </label>
          ) : (
            <label>
              Email address

              <input
                type="email"
                name="email"
                placeholder={
                  isForgotPassword
                    ? "Enter your registered email"
                    : "Enter your email address"
                }
                value={formData.email}
                onChange={
                  handleInputChange
                }
                maxLength="150"
                autoComplete="email"
                required
              />
            </label>
          )}

          {isRegistering && (
            <label>
              Phone number

              <input
                type="tel"
                name="phone"
                placeholder="Enter 10-digit phone number"
                value={formData.phone}
                onChange={
                  handleInputChange
                }
                pattern="[0-9]{10}"
                maxLength="10"
                inputMode="numeric"
                autoComplete="tel"
                required
              />
            </label>
          )}

          {!isForgotPassword && (
            <label>
              Password

              <input
                type="password"
                name="password"
                placeholder="Enter your password"
                value={
                  formData.password
                }
                onChange={
                  handleInputChange
                }
                minLength="8"
                maxLength="72"
                autoComplete={
                  isRegistering
                    ? "new-password"
                    : "current-password"
                }
                required
              />
            </label>
          )}

          {mode === "login" && (
            <button
              className="forgot-password-button"
              type="button"
              onClick={() =>
                changeMode("forgot")
              }
            >
              Forgot Password?
            </button>
          )}

          {isRegistering && (
            <label>
              Confirm password

              <input
                type="password"
                name="confirmPassword"
                placeholder="Enter the password again"
                value={
                  formData.confirmPassword
                }
                onChange={
                  handleInputChange
                }
                minLength="8"
                maxLength="72"
                autoComplete="new-password"
                required
              />
            </label>
          )}

          {error && (
            <p className="admin-login-error">
              {error}
            </p>
          )}

          {successMessage && (
            <div className="forgot-password-success">
              <strong>
                Check your email
              </strong>

              <p>{successMessage}</p>

              <small>
                Also check your Spam
                folder. The link expires
                after 15 minutes.
              </small>
            </div>
          )}

          {!successMessage && (
            <button
              className="admin-login-submit"
              type="submit"
              disabled={submitting}
            >
              {submitting
                ? "Please wait..."
                : isForgotPassword
                  ? "Send reset link"
                  : isRegistering
                    ? "Create customer account"
                    : "Sign in as customer"}
            </button>
          )}

          {isForgotPassword && (
            <button
              className="return-to-login-button"
              type="button"
              onClick={() =>
                changeMode("login")
              }
            >
              ← Return to Customer Login
            </button>
          )}
        </form>

        {!isForgotPassword && (
          <>
            <div className="google-divider">
              <span>or</span>
            </div>

            {googleClientId ? (
              <div
                className="google-login-button"
                ref={googleButtonRef}
              />
            ) : (
              <p className="admin-login-error">
                Google Client ID is not
                configured.
              </p>
            )}
          </>
        )}

        <p className="login-security-note">
          🔒 Your password and Google
          credential are securely
          verified.
        </p>
      </section>
    </main>
  );
}

export default CustomerAuth;