import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  createDeliveryAssignment,
  getAvailableDeliveryPartners,
  getDeliveryAssignments,
  getSuperAdminOrders,
  updateDeliveryAssignmentStatus,
} from "../../services/superAdminApi";

function SuperAdminDeliveryAssignments() {
  const [assignments, setAssignments] =
    useState([]);

  const [availablePartners, setAvailablePartners] =
    useState([]);

  const [readyOrders, setReadyOrders] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  const [
    processingAssignmentId,
    setProcessingAssignmentId,
  ] = useState(null);

  const [
    createModalOpen,
    setCreateModalOpen,
  ] = useState(false);

  const [selectedOrderId, setSelectedOrderId] =
    useState("");

  const [
    selectedPartnerId,
    setSelectedPartnerId,
  ] = useState("");

  // ==========================================
  // INITIAL LOAD
  // ==========================================

  useEffect(() => {
    loadAssignments();
  }, []);

  const loadAssignments = async () => {
    setLoading(true);
    setError("");

    try {
      const data =
        await getDeliveryAssignments();

      setAssignments(
        Array.isArray(data)
          ? data
          : []
      );
    } catch (err) {
      setError(
        err.message ||
          "Unable to load delivery assignments"
      );
    } finally {
      setLoading(false);
    }
  };

  // ==========================================
  // LOAD CREATE MODAL DATA
  // ==========================================

  const openCreateModal = async () => {
    setError("");
    setSuccess("");

    setSelectedOrderId("");
    setSelectedPartnerId("");

    try {
      const [
        partnerData,
        orderData,
      ] = await Promise.all([
        getAvailableDeliveryPartners(),

        getSuperAdminOrders({
          status: "READY",
        }),
      ]);

      setAvailablePartners(
        Array.isArray(partnerData)
          ? partnerData
          : []
      );

      setReadyOrders(
        Array.isArray(orderData)
          ? orderData
          : []
      );

      setCreateModalOpen(true);
    } catch (err) {
      setError(
        err.message ||
          "Unable to load available orders or delivery partners"
      );
    }
  };

  // ==========================================
  // CREATE ASSIGNMENT
  // ==========================================

  const handleCreateAssignment =
    async (event) => {
      event.preventDefault();

      setError("");
      setSuccess("");

      if (
        !selectedOrderId ||
        !selectedPartnerId
      ) {
        setError(
          "Please select an order and delivery partner"
        );
        return;
      }

      try {
        await createDeliveryAssignment(
          Number(selectedOrderId),
          Number(selectedPartnerId)
        );

        setCreateModalOpen(false);

        setSelectedOrderId("");
        setSelectedPartnerId("");

        setSuccess(
          "Delivery assignment created successfully."
        );

        await loadAssignments();
      } catch (err) {
        setError(
          err.message ||
            "Delivery assignment could not be created"
        );
      }
    };

  // ==========================================
  // UPDATE STATUS
  // ==========================================

  const handleStatusChange =
    async (
      assignment,
      status
    ) => {
      setProcessingAssignmentId(
        assignment.id
      );

      setError("");
      setSuccess("");

      try {
        await updateDeliveryAssignmentStatus(
          assignment.id,
          status
        );

        setSuccess(
          "Delivery assignment status updated successfully."
        );

        await loadAssignments();
      } catch (err) {
        setError(
          err.message ||
            "Delivery assignment status could not be updated"
        );
      } finally {
        setProcessingAssignmentId(
          null
        );
      }
    };

  // ==========================================
  // COUNTS
  // ==========================================

  const counts = useMemo(() => {
    const result = {
      total: assignments.length,
      assigned: 0,
      accepted: 0,
      pickedUp: 0,
      delivered: 0,
      cancelled: 0,
    };

    assignments.forEach(
      (assignment) => {
        switch (
          assignment.status
        ) {
          case "ASSIGNED":
            result.assigned += 1;
            break;

          case "ACCEPTED":
            result.accepted += 1;
            break;

          case "PICKED_UP":
            result.pickedUp += 1;
            break;

          case "DELIVERED":
            result.delivered += 1;
            break;

          case "CANCELLED":
            result.cancelled += 1;
            break;

          default:
            break;
        }
      }
    );

    return result;
  }, [assignments]);

  // ==========================================
  // HELPERS
  // ==========================================

  const formatDate = (value) => {
    if (!value) {
      return "-";
    }

    return new Date(
      value
    ).toLocaleString(
      "en-IN"
    );
  };

  const assignmentStatusClass = (
    status
  ) => {
    if (status === "DELIVERED") {
      return "super-admin-badge super-admin-badge-success";
    }

    if (status === "CANCELLED") {
      return "super-admin-badge super-admin-badge-danger";
    }

    if (
      status === "ASSIGNED" ||
      status === "ACCEPTED"
    ) {
      return "super-admin-badge super-admin-badge-warning";
    }

    return "super-admin-badge super-admin-badge-info";
  };

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="super-admin-page">
      <div className="super-admin-page-header">
        <div>
          <h1>
            Delivery Assignments
          </h1>

          <p>
            Assign ready orders to
            available delivery partners
            and track delivery progress.
          </p>
        </div>

        <button
          type="button"
          className="super-admin-button super-admin-button-primary"
          onClick={
            openCreateModal
          }
        >
          Create Assignment
        </button>
      </div>

      <div className="super-admin-stat-grid">
        <div className="super-admin-stat-card">
          <span>
            Total Assignments
          </span>

          <strong>
            {counts.total}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Assigned
          </span>

          <strong>
            {counts.assigned}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Accepted
          </span>

          <strong>
            {counts.accepted}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Picked Up
          </span>

          <strong>
            {counts.pickedUp}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Delivered
          </span>

          <strong>
            {counts.delivered}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Cancelled
          </span>

          <strong>
            {counts.cancelled}
          </strong>
        </div>
      </div>

      <div className="super-admin-section">
        <div className="super-admin-filter-bar">
          <button
            type="button"
            className="super-admin-button super-admin-button-secondary"
            onClick={
              loadAssignments
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
            assignments...
          </div>
        ) : assignments.length === 0 ? (
          <div className="super-admin-empty-card">
            No delivery assignments
            found.
          </div>
        ) : (
          <div className="super-admin-table-wrapper">
            <table className="super-admin-table">
              <thead>
                <tr>
                  <th>
                    Assignment
                  </th>

                  <th>
                    Order
                  </th>

                  <th>
                    Restaurant
                  </th>

                  <th>
                    Customer
                  </th>

                  <th>
                    Delivery Partner
                  </th>

                  <th>
                    Status
                  </th>

                  <th>
                    Assigned At
                  </th>

                  <th>
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody>
                {assignments.map(
                  (assignment) => {
                    const processing =
                      processingAssignmentId ===
                      assignment.id;

                    return (
                      <tr
                        key={
                          assignment.id
                        }
                      >
                        <td>
                          <strong>
                            #
                            {
                              assignment.id
                            }
                          </strong>
                        </td>

                        <td>
                          <strong>
                            Order #
                            {
                              assignment.orderId
                            }
                          </strong>
                        </td>

                        <td>
                          <strong>
                            {
                              assignment.restaurantName
                            }
                          </strong>

                          <div>
                            <small>
                              ID:{" "}
                              {
                                assignment.restaurantId
                              }
                            </small>
                          </div>
                        </td>

                        <td>
                          <strong>
                            {
                              assignment.customerName
                            }
                          </strong>

                          <div>
                            {
                              assignment.customerPhone
                            }
                          </div>

                          <small>
                            {
                              assignment.deliveryAddress
                            }
                          </small>
                        </td>

                        <td>
                          <strong>
                            {
                              assignment.deliveryPartnerName
                            }
                          </strong>

                          <div>
                            {
                              assignment.deliveryPartnerPhone
                            }
                          </div>

                          <small>
                            Partner ID:{" "}
                            {
                              assignment.deliveryPartnerId
                            }
                          </small>
                        </td>

                        <td>
                          <span
                            className={assignmentStatusClass(
                              assignment.status
                            )}
                          >
                            {
                              assignment.status
                            }
                          </span>
                        </td>

                        <td>
                          {formatDate(
                            assignment.assignedAt
                          )}
                        </td>

                        <td>
                          <select
                            value={
                              assignment.status
                            }
                            disabled={
                              processing ||
                              assignment.status ===
                                "DELIVERED" ||
                              assignment.status ===
                                "CANCELLED"
                            }
                            onChange={(
                              event
                            ) =>
                              handleStatusChange(
                                assignment,
                                event
                                  .target
                                  .value
                              )
                            }
                            style={{
                              minWidth:
                                "145px",
                              height:
                                "36px",
                              border:
                                "1px solid #d1d5db",
                              borderRadius:
                                "8px",
                              background:
                                "#fff",
                              padding:
                                "0 8px",
                            }}
                          >
                            <option value="ASSIGNED">
                              Assigned
                            </option>

                            <option value="ACCEPTED">
                              Accepted
                            </option>

                            <option value="PICKED_UP">
                              Picked Up
                            </option>

                            <option value="DELIVERED">
                              Delivered
                            </option>

                            <option value="CANCELLED">
                              Cancelled
                            </option>
                          </select>
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
          CREATE ASSIGNMENT MODAL
      ===================================== */}

      {createModalOpen && (
        <div className="super-admin-modal-backdrop">
          <div className="super-admin-modal">
            <div className="super-admin-modal-header">
              <h2>
                Create Delivery Assignment
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

            <form
              onSubmit={
                handleCreateAssignment
              }
            >
              <div className="super-admin-modal-body">
                <div className="super-admin-login-form">

                  <div className="super-admin-form-group">
                    <label>
                      Ready Order
                    </label>

                    <select
                      required
                      value={
                        selectedOrderId
                      }
                      onChange={(event) =>
                        setSelectedOrderId(
                          event.target.value
                        )
                      }
                    >
                      <option value="">
                        Select Ready Order
                      </option>

                      {readyOrders.map(
                        (order) => (
                          <option
                            key={
                              order.id
                            }
                            value={
                              order.id
                            }
                          >
                            Order #
                            {
                              order.id
                            }
                            {" - "}
                            {
                              order.restaurantName
                            }
                            {" - "}
                            {
                              order.customerName
                            }
                          </option>
                        )
                      )}
                    </select>

                    {readyOrders.length ===
                      0 && (
                      <small>
                        No READY orders are
                        currently available.
                      </small>
                    )}
                  </div>

                  <div className="super-admin-form-group">
                    <label>
                      Available Delivery
                      Partner
                    </label>

                    <select
                      required
                      value={
                        selectedPartnerId
                      }
                      onChange={(event) =>
                        setSelectedPartnerId(
                          event.target.value
                        )
                      }
                    >
                      <option value="">
                        Select Delivery Partner
                      </option>

                      {availablePartners.map(
                        (partner) => (
                          <option
                            key={
                              partner.id
                            }
                            value={
                              partner.id
                            }
                          >
                            {
                              partner.fullName
                            }
                            {" - "}
                            {
                              partner.phone
                            }
                            {partner.vehicleType
                              ? ` - ${partner.vehicleType}`
                              : ""}
                          </option>
                        )
                      )}
                    </select>

                    {availablePartners.length ===
                      0 && (
                      <small>
                        No AVAILABLE delivery
                        partners are currently
                        available.
                      </small>
                    )}
                  </div>
                </div>
              </div>

              <div className="super-admin-modal-footer">
                <button
                  type="button"
                  className="super-admin-button super-admin-button-secondary"
                  onClick={() =>
                    setCreateModalOpen(
                      false
                    )
                  }
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  disabled={
                    !selectedOrderId ||
                    !selectedPartnerId
                  }
                  className="super-admin-button super-admin-button-primary"
                >
                  Assign Delivery Partner
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default SuperAdminDeliveryAssignments;