import { useEffect, useState } from "react";
import AdminNav from "./AdminNav.jsx";

const PROFILE_API_URL =
  "http://localhost:8080/api/restaurant-admin/profile";

function AdminProfile() {
  const [profile, setProfile] =
    useState(null);

  const [formData, setFormData] =
    useState({
      name: "",
      description: "",
      email: "",
      phone: "",
      address: "",
      city: "",
      state: "",
      logoUrl: "",
    });

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  const adminToken =
    sessionStorage.getItem(
      "adminToken",
    );

  async function loadProfile() {
    try {
      setLoading(true);
      setError("");

      const response = await fetch(
        PROFILE_API_URL,
        {
          method: "GET",

          headers: {
            Authorization:
              `Bearer ${adminToken}`,
          },
        },
      );

      if (!response.ok) {
        throw new Error(
          "Unable to load restaurant profile.",
        );
      }

      const data =
        await response.json();

      setProfile(data);

      setFormData({
        name:
          data.name ?? "",

        description:
          data.description ?? "",

        email:
          data.email ?? "",

        phone:
          data.phone ?? "",

        address:
          data.address ?? "",

        city:
          data.city ?? "",

        state:
          data.state ?? "",

        logoUrl:
          data.logoUrl ?? "",
      });
    } catch (err) {
      setError(
        err.message,
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadProfile();
  }, []);

  function handleChange(event) {
    const {
      name,
      value,
    } = event.target;

    setFormData(
      (current) => ({
        ...current,
        [name]: value,
      }),
    );

    setSuccess("");
  }

  async function handleSubmit(
    event,
  ) {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      const response = await fetch(
        PROFILE_API_URL,
        {
          method: "PUT",

          headers: {
            Authorization:
              `Bearer ${adminToken}`,

            "Content-Type":
              "application/json",
          },

          body:
            JSON.stringify(
              formData,
            ),
        },
      );

      if (!response.ok) {
        let message =
          "Unable to update restaurant profile.";

        try {
          const errorData =
            await response.json();

          if (
            errorData.message
          ) {
            message =
              errorData.message;
          }
        } catch {
          // Keep default message.
        }

        throw new Error(
          message,
        );
      }

      const updatedProfile =
        await response.json();

      setProfile(
        updatedProfile,
      );

      setFormData({
        name:
          updatedProfile.name ??
          "",

        description:
          updatedProfile.description ??
          "",

        email:
          updatedProfile.email ??
          "",

        phone:
          updatedProfile.phone ??
          "",

        address:
          updatedProfile.address ??
          "",

        city:
          updatedProfile.city ??
          "",

        state:
          updatedProfile.state ??
          "",

        logoUrl:
          updatedProfile.logoUrl ??
          "",
      });

      setSuccess(
        "Restaurant profile updated successfully.",
      );
    } catch (err) {
      setError(
        err.message,
      );
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <main className="admin-page">
        <AdminNav
          activePage="profile"
        />

        <section className="admin-page-loading">
          <h2>
            Loading restaurant
            profile...
          </h2>
        </section>
      </main>
    );
  }

  return (
    <main className="admin-page">
      <AdminNav
        activePage="profile"
      />

      <header className="admin-page-header">
        <div>
          <span className="eyebrow">
            Restaurant settings
          </span>

          <h1>
            Restaurant Profile
          </h1>

          <p>
            Manage your restaurant
            information visible across
            the platform.
          </p>
        </div>

        <div className="admin-page-actions">
          <button
            type="button"
            className="admin-primary-button"
            onClick={
              loadProfile
            }
          >
            Refresh
          </button>
        </div>
      </header>

      <section className="admin-page-content">
        {error && (
          <div className="admin-message error">
            {error}
          </div>
        )}

        {success && (
          <div className="admin-message success">
            {success}
          </div>
        )}

        {profile && (
          <section className="admin-profile-status-grid">
            <article className="admin-stat-card">
              <span>
                Restaurant ID
              </span>

              <strong>
                #{profile.id}
              </strong>
            </article>

            <article className="admin-stat-card">
              <span>
                Approval Status
              </span>

              <strong>
                {
                  profile.approvalStatus
                }
              </strong>
            </article>

            <article className="admin-stat-card">
              <span>
                Restaurant Status
              </span>

              <strong>
                {profile.active
                  ? "Active"
                  : "Inactive"}
              </strong>
            </article>

            <article className="admin-stat-card">
              <span>
                Commission
              </span>

              <strong>
                {
                  profile.commissionPercentage
                }
                %
              </strong>
            </article>
          </section>
        )}

        <section className="admin-profile-form-card">
          <div className="admin-profile-form-heading">
            <span className="eyebrow">
              Restaurant information
            </span>

            <h2>
              Edit profile
            </h2>

            <p>
              Update the public
              information for your
              restaurant.
            </p>
          </div>

          <form
            className="admin-profile-form"
            onSubmit={
              handleSubmit
            }
          >
            <div className="admin-profile-form-grid">
              <label>
                Restaurant name

                <input
                  type="text"
                  name="name"
                  value={
                    formData.name
                  }
                  onChange={
                    handleChange
                  }
                  required
                />
              </label>

              <label>
                Email

                <input
                  type="email"
                  name="email"
                  value={
                    formData.email
                  }
                  onChange={
                    handleChange
                  }
                  required
                />
              </label>

              <label>
                Phone

                <input
                  type="tel"
                  name="phone"
                  value={
                    formData.phone
                  }
                  onChange={
                    handleChange
                  }
                  placeholder="Restaurant phone number"
                />
              </label>

              <label>
                City

                <input
                  type="text"
                  name="city"
                  value={
                    formData.city
                  }
                  onChange={
                    handleChange
                  }
                  placeholder="City"
                />
              </label>

              <label>
                State

                <input
                  type="text"
                  name="state"
                  value={
                    formData.state
                  }
                  onChange={
                    handleChange
                  }
                  placeholder="State"
                />
              </label>

              <label>
                Logo URL

                <input
                  type="text"
                  name="logoUrl"
                  value={
                    formData.logoUrl
                  }
                  onChange={
                    handleChange
                  }
                  placeholder="Restaurant logo URL"
                />
              </label>
            </div>

            <label className="admin-profile-full-field">
              Address

              <textarea
                name="address"
                value={
                  formData.address
                }
                onChange={
                  handleChange
                }
                rows="3"
                placeholder="Restaurant address"
              />
            </label>

            <label className="admin-profile-full-field">
              Description

              <textarea
                name="description"
                value={
                  formData.description
                }
                onChange={
                  handleChange
                }
                rows="5"
                placeholder="Tell customers about your restaurant"
              />
            </label>

            <div className="admin-profile-form-footer">
              <p>
                Approval status,
                active status and
                commission are controlled
                by the Super Admin.
              </p>

              <button
                type="submit"
                className="admin-primary-button"
                disabled={
                  saving
                }
              >
                {saving
                  ? "Saving..."
                  : "Save changes"}
              </button>
            </div>
          </form>
        </section>
      </section>
    </main>
  );
}

export default AdminProfile;