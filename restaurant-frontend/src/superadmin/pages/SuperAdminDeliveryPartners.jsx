import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  createDeliveryPartner,
  getDeliveryPartners,
  updateDeliveryPartner,
  updateDeliveryPartnerActive,
  updateDeliveryPartnerStatus,
} from "../../services/superAdminApi";

const EMPTY_FORM = {
  fullName: "",
  email: "",
  phone: "",
  vehicleNumber: "",
  vehicleType: "",
};

function SuperAdminDeliveryPartners() {
  const [partners, setPartners] =
    useState([]);

  const [statusFilter, setStatusFilter] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  const [
    processingPartnerId,
    setProcessingPartnerId,
  ] = useState(null);

  const [
    createModalOpen,
    setCreateModalOpen,
  ] = useState(false);

  const [
    editPartner,
    setEditPartner,
  ] = useState(null);

  const [form, setForm] =
    useState(EMPTY_FORM);

  // ==========================================
  // LOAD
  // ==========================================

  useEffect(() => {
    loadPartners();
  }, [statusFilter]);

  const loadPartners = async () => {
    setLoading(true);
    setError("");

    try {
      const data =
        await getDeliveryPartners(
          statusFilter || null
        );

      setPartners(
        Array.isArray(data)
          ? data
          : []
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to load delivery partners"
      );
    } finally {
      setLoading(false);
    }
  };

  // ==========================================
  // COUNTS
  // ==========================================

  const counts = useMemo(() => {
    const result = {
      total: partners.length,
      available: 0,
      busy: 0,
      offline: 0,
      active: 0,
    };

    partners.forEach((partner) => {
      if (partner.active) {
        result.active += 1;
      }

      if (
        partner.status === "AVAILABLE"
      ) {
        result.available += 1;
      }

      if (
        partner.status === "BUSY"
      ) {
        result.busy += 1;
      }

      if (
        partner.status === "OFFLINE"
      ) {
        result.offline += 1;
      }
    });

    return result;
  }, [partners]);

  // ==========================================
  // CREATE
  // ==========================================

  const openCreateModal = () => {
    setError("");
    setSuccess("");
    setForm(EMPTY_FORM);
    setCreateModalOpen(true);
  };

  const handleCreate =
    async (event) => {
      event.preventDefault();

      setError("");
      setSuccess("");

      try {
        await createDeliveryPartner({
          fullName:
            form.fullName.trim(),

          email:
            form.email
              .trim()
              .toLowerCase(),

          phone:
            form.phone.trim(),

          vehicleNumber:
            form.vehicleNumber
              .trim() || null,

          vehicleType:
            form.vehicleType
              .trim() || null,
        });

        setCreateModalOpen(false);
        setForm(EMPTY_FORM);

        setSuccess(
          "Delivery partner created successfully."
        );

        await loadPartners();
      } catch (err) {
        setError(
          err.message ||
            "Delivery partner could not be created"
        );
      }
    };

  // ==========================================
  // EDIT
  // ==========================================

  const openEditModal = (partner) => {
    setError("");
    setSuccess("");

    setEditPartner(partner);

    setForm({
      fullName:
        partner.fullName || "",

      email:
        partner.email || "",

      phone:
        partner.phone || "",

      vehicleNumber:
        partner.vehicleNumber || "",

      vehicleType:
        partner.vehicleType || "",
    });
  };

  const handleEdit =
    async (event) => {
      event.preventDefault();

      if (!editPartner) {
        return;
      }

      setProcessingPartnerId(
        editPartner.id
      );

      setError("");
      setSuccess("");

      try {
        await updateDeliveryPartner(
          editPartner.id,
          {
            fullName:
              form.fullName.trim(),

            email:
              form.email
                .trim()
                .toLowerCase(),

            phone:
              form.phone.trim(),

            vehicleNumber:
              form.vehicleNumber
                .trim() || null,

            vehicleType:
              form.vehicleType
                .trim() || null,
          }
        );

        setEditPartner(null);
        setForm(EMPTY_FORM);

        setSuccess(
          "Delivery partner updated successfully."
        );

        await loadPartners();
      } catch (err) {
        setError(
          err.message ||
            "Delivery partner could not be updated"
        );
      } finally {
        setProcessingPartnerId(
          null
        );
      }
    };

  // ==========================================
  // ACTIVE STATUS
  // ==========================================

  const handleActiveToggle =
    async (partner) => {
      setProcessingPartnerId(
        partner.id
      );

      setError("");
      setSuccess("");

      try {
        await updateDeliveryPartnerActive(
          partner.id,
          !partner.active
        );

        setSuccess(
          partner.active
            ? "Delivery partner deactivated successfully."
            : "Delivery partner activated successfully."
        );

        await loadPartners();
      } catch (err) {
        setError(
          err.message ||
            "Delivery partner active status could not be updated"
        );
      } finally {
        setProcessingPartnerId(
          null
        );
      }
    };

  // ==========================================
  // WORK STATUS
  // ==========================================

  const handleStatusChange =
    async (
      partner,
      status
    ) => {
      setProcessingPartnerId(
        partner.id
      );

      setError("");
      setSuccess("");

      try {
        await updateDeliveryPartnerStatus(
          partner.id,
          status
        );

        setSuccess(
          "Delivery partner status updated successfully."
        );

        await loadPartners();
      } catch (err) {
        setError(
          err.message ||
            "Delivery partner status could not be updated"
        );
      } finally {
        setProcessingPartnerId(
          null
        );
      }
    };

  // ==========================================
  // BADGE
  // ==========================================

  const statusClass = (status) => {
    if (status === "AVAILABLE") {
      return "super-admin-badge super-admin-badge-success";
    }

    if (status === "BUSY") {
      return "super-admin-badge super-admin-badge-warning";
    }

    return "super-admin-badge super-admin-badge-neutral";
  };

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="super-admin-page">
      <div className="super-admin-page-header">
        <div>
          <h1>
            Delivery Partners
          </h1>

          <p>
            Manage delivery partner
            accounts, availability and
            work status.
          </p>
        </div>

        <button
          type="button"
          className="super-admin-button super-admin-button-primary"
          onClick={
            openCreateModal
          }
        >
          Add Delivery Partner
        </button>
      </div>

      <div className="super-admin-stat-grid">
        <div className="super-admin-stat-card">
          <span>
            Visible Partners
          </span>

          <strong>
            {counts.total}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Available
          </span>

          <strong>
            {counts.available}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Busy
          </span>

          <strong>
            {counts.busy}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Offline
          </span>

          <strong>
            {counts.offline}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Active Accounts
          </span>

          <strong>
            {counts.active}
          </strong>
        </div>
      </div>

      <div className="super-admin-section">
        <div className="super-admin-filter-bar">
          <div className="super-admin-filter-field">
            <label>
              Work Status
            </label>

            <select
              value={
                statusFilter
              }
              onChange={(event) =>
                setStatusFilter(
                  event.target.value
                )
              }
            >
              <option value="">
                All Statuses
              </option>

              <option value="AVAILABLE">
                Available
              </option>

              <option value="BUSY">
                Busy
              </option>

              <option value="OFFLINE">
                Offline
              </option>
            </select>
          </div>

          <button
            type="button"
            className="super-admin-button super-admin-button-secondary"
            onClick={
              loadPartners
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
            Loading delivery
            partners...
          </div>
        ) : partners.length === 0 ? (
          <div className="super-admin-empty-card">
            No delivery partners found.
          </div>
        ) : (
          <div className="super-admin-table-wrapper">
            <table className="super-admin-table">
              <thead>
                <tr>
                  <th>
                    Partner
                  </th>

                  <th>
                    Vehicle
                  </th>

                  <th>
                    Work Status
                  </th>

                  <th>
                    Account
                  </th>

                  <th>
                    Assignments
                  </th>

                  <th>
                    Delivered
                  </th>

                  <th>
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody>
                {partners.map(
                  (partner) => {
                    const processing =
                      processingPartnerId ===
                      partner.id;

                    return (
                      <tr
                        key={
                          partner.id
                        }
                      >
                        <td>
                          <strong>
                            {
                              partner.fullName
                            }
                          </strong>

                          <div>
                            {
                              partner.email
                            }
                          </div>

                          <small>
                            {
                              partner.phone
                            }
                          </small>
                        </td>

                        <td>
                          <strong>
                            {
                              partner.vehicleType ||
                              "-"
                            }
                          </strong>

                          <div>
                            <small>
                              {
                                partner.vehicleNumber ||
                                "-"
                              }
                            </small>
                          </div>
                        </td>

                        <td>
                          <div
                            style={{
                              display:
                                "flex",
                              flexDirection:
                                "column",
                              gap:
                                "7px",
                              minWidth:
                                "135px",
                            }}
                          >
                            <span
                              className={statusClass(
                                partner.status
                              )}
                            >
                              {
                                partner.status
                              }
                            </span>

                            <select
                              value={
                                partner.status
                              }
                              disabled={
                                processing ||
                                !partner.active
                              }
                              onChange={(
                                event
                              ) =>
                                handleStatusChange(
                                  partner,
                                  event
                                    .target
                                    .value
                                )
                              }
                              style={{
                                height:
                                  "34px",
                                border:
                                  "1px solid #d1d5db",
                                borderRadius:
                                  "7px",
                                background:
                                  "#fff",
                              }}
                            >
                              <option value="AVAILABLE">
                                Available
                              </option>

                              <option value="BUSY">
                                Busy
                              </option>

                              <option value="OFFLINE">
                                Offline
                              </option>
                            </select>
                          </div>
                        </td>

                        <td>
                          <span
                            className={
                              partner.active
                                ? "super-admin-badge super-admin-badge-success"
                                : "super-admin-badge super-admin-badge-neutral"
                            }
                          >
                            {partner.active
                              ? "ACTIVE"
                              : "INACTIVE"}
                          </span>
                        </td>

                        <td>
                          {
                            partner.totalAssignments ??
                            0
                          }
                        </td>

                        <td>
                          {
                            partner.deliveredAssignments ??
                            0
                          }
                        </td>

                        <td>
                          <div
                            style={{
                              display:
                                "flex",
                              gap:
                                "7px",
                              flexWrap:
                                "wrap",
                            }}
                          >
                            <button
                              type="button"
                              disabled={
                                processing
                              }
                              className="super-admin-button super-admin-button-secondary"
                              onClick={() =>
                                openEditModal(
                                  partner
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
                                partner.active
                                  ? "super-admin-button super-admin-button-danger"
                                  : "super-admin-button super-admin-button-success"
                              }
                              onClick={() =>
                                handleActiveToggle(
                                  partner
                                )
                              }
                            >
                              {partner.active
                                ? "Deactivate"
                                : "Activate"}
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
                Add Delivery Partner
              </h2>

              <button
                type="button"
                className="super-admin-button super-admin-button-secondary"
                onClick={() =>
                  setCreateModalOpen(
                    false
                  )
                }
              >
                Close
              </button>
            </div>

            <PartnerForm
              form={form}
              setForm={setForm}
              onSubmit={
                handleCreate
              }
              submitLabel="Create Partner"
              onCancel={() =>
                setCreateModalOpen(
                  false
                )
              }
            />
          </div>
        </div>
      )}

      {/* =====================================
          EDIT MODAL
      ===================================== */}

      {editPartner && (
        <div className="super-admin-modal-backdrop">
          <div className="super-admin-modal">
            <div className="super-admin-modal-header">
              <h2>
                Edit Delivery Partner
              </h2>

              <button
                type="button"
                className="super-admin-button super-admin-button-secondary"
                onClick={() =>
                  setEditPartner(
                    null
                  )
                }
              >
                Close
              </button>
            </div>

            <PartnerForm
              form={form}
              setForm={setForm}
              onSubmit={
                handleEdit
              }
              submitLabel="Save Changes"
              disabled={
                processingPartnerId ===
                editPartner.id
              }
              onCancel={() =>
                setEditPartner(
                  null
                )
              }
            />
          </div>
        </div>
      )}
    </div>
  );
}

// ==========================================
// PARTNER FORM
// ==========================================

function PartnerForm({
  form,
  setForm,
  onSubmit,
  submitLabel,
  onCancel,
  disabled = false,
}) {
  const updateField = (
    field,
    value
  ) => {
    setForm((current) => ({
      ...current,
      [field]: value,
    }));
  };

  return (
    <form onSubmit={onSubmit}>
      <div className="super-admin-modal-body">
        <div className="super-admin-login-form">
          <div className="super-admin-form-group">
            <label>
              Full Name
            </label>

            <input
              required
              minLength="2"
              maxLength="100"
              value={
                form.fullName
              }
              onChange={(event) =>
                updateField(
                  "fullName",
                  event.target.value
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
                form.email
              }
              onChange={(event) =>
                updateField(
                  "email",
                  event.target.value
                )
              }
            />
          </div>

          <div className="super-admin-form-group">
            <label>
              Phone
            </label>

            <input
              required
              minLength="10"
              maxLength="15"
              value={
                form.phone
              }
              onChange={(event) =>
                updateField(
                  "phone",
                  event.target.value
                )
              }
            />
          </div>

          <div className="super-admin-form-group">
            <label>
              Vehicle Type
            </label>

            <input
              value={
                form.vehicleType
              }
              onChange={(event) =>
                updateField(
                  "vehicleType",
                  event.target.value
                )
              }
              placeholder="Bike, Scooter, etc."
            />
          </div>

          <div className="super-admin-form-group">
            <label>
              Vehicle Number
            </label>

            <input
              value={
                form.vehicleNumber
              }
              onChange={(event) =>
                updateField(
                  "vehicleNumber",
                  event.target.value
                )
              }
              placeholder="PB12AB1234"
            />
          </div>
        </div>
      </div>

      <div className="super-admin-modal-footer">
        <button
          type="button"
          className="super-admin-button super-admin-button-secondary"
          onClick={
            onCancel
          }
        >
          Cancel
        </button>

        <button
          type="submit"
          disabled={
            disabled
          }
          className="super-admin-button super-admin-button-primary"
        >
          {submitLabel}
        </button>
      </div>
    </form>
  );
}

export default SuperAdminDeliveryPartners;