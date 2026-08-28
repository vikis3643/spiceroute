import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  getRestaurants,
  getSupportTicket,
  getSupportTickets,
  updateSupportTicketStatus,
} from "../../services/superAdminApi";

function SuperAdminSupport() {
  const [tickets, setTickets] =
    useState([]);

  const [restaurants, setRestaurants] =
    useState([]);

  const [
    selectedTicket,
    setSelectedTicket,
  ] = useState(null);

  const [loading, setLoading] =
    useState(true);

  const [
    detailLoading,
    setDetailLoading,
  ] = useState(false);

  const [
    processingTicketId,
    setProcessingTicketId,
  ] = useState(null);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  const [filters, setFilters] =
    useState({
      restaurantId: "",
      status: "",
      priority: "",
    });

  // ==========================================
  // INITIAL LOAD
  // ==========================================

  useEffect(() => {
    loadRestaurants();
  }, []);

  useEffect(() => {
    loadTickets();
  }, [
    filters.restaurantId,
    filters.status,
    filters.priority,
  ]);

  // ==========================================
  // LOAD RESTAURANTS
  // ==========================================

  const loadRestaurants =
    async () => {
      try {
        const data =
          await getRestaurants();

        setRestaurants(
          Array.isArray(data)
            ? data
            : []
        );
      } catch {
        // Support page can still work
        // without restaurant filter data.
      }
    };

  // ==========================================
  // LOAD TICKETS
  // ==========================================

  const loadTickets =
    async () => {
      setLoading(true);
      setError("");

      try {
        const data =
          await getSupportTickets({
            restaurantId:
              filters.restaurantId ||
              null,

            status:
              filters.status ||
              null,

            priority:
              filters.priority ||
              null,
          });

        setTickets(
          Array.isArray(data)
            ? data
            : []
        );
      } catch (err) {
        setError(
          err.message ||
            "Unable to load support tickets"
        );
      } finally {
        setLoading(false);
      }
    };

  // ==========================================
  // COUNTS
  // ==========================================

  const counts =
    useMemo(() => {
      const result = {
        total:
          tickets.length,

        open: 0,

        inProgress: 0,

        resolved: 0,

        closed: 0,

        urgent: 0,
      };

      tickets.forEach(
        (ticket) => {
          switch (
            ticket.status
          ) {
            case "OPEN":
              result.open += 1;
              break;

            case "IN_PROGRESS":
              result.inProgress +=
                1;
              break;

            case "RESOLVED":
              result.resolved +=
                1;
              break;

            case "CLOSED":
              result.closed +=
                1;
              break;

            default:
              break;
          }

          if (
            ticket.priority ===
            "URGENT"
          ) {
            result.urgent +=
              1;
          }
        }
      );

      return result;
    }, [tickets]);

  // ==========================================
  // OPEN TICKET DETAILS
  // ==========================================

  const openTicketDetails =
    async (ticketId) => {
      setDetailLoading(true);
      setError("");
      setSuccess("");

      try {
        const data =
          await getSupportTicket(
            ticketId
          );

        setSelectedTicket(
          data
        );
      } catch (err) {
        setError(
          err.message ||
            "Unable to load ticket details"
        );
      } finally {
        setDetailLoading(false);
      }
    };

  // ==========================================
  // UPDATE STATUS
  // ==========================================

  const handleStatusChange =
    async (
      ticket,
      newStatus
    ) => {
      if (
        !newStatus ||
        newStatus ===
          ticket.status
      ) {
        return;
      }

      setProcessingTicketId(
        ticket.id
      );

      setError("");
      setSuccess("");

      try {
        const updated =
          await updateSupportTicketStatus(
            ticket.id,
            newStatus
          );

        setSuccess(
          `Ticket #${ticket.id} status updated successfully.`
        );

        if (
          selectedTicket?.id ===
          ticket.id
        ) {
          setSelectedTicket(
            updated
          );
        }

        await loadTickets();
      } catch (err) {
        setError(
          err.message ||
            "Ticket status could not be updated"
        );
      } finally {
        setProcessingTicketId(
          null
        );
      }
    };

  // ==========================================
  // CLEAR FILTERS
  // ==========================================

  const clearFilters =
    () => {
      setFilters({
        restaurantId: "",
        status: "",
        priority: "",
      });
    };

  // ==========================================
  // HELPERS
  // ==========================================

  const formatDate =
    (value) => {
      if (!value) {
        return "-";
      }

      return new Date(
        value
      ).toLocaleString(
        "en-IN"
      );
    };

  const formatLabel =
    (value) => {
      if (!value) {
        return "-";
      }

      return String(value)
        .replaceAll(
          "_",
          " "
        )
        .toLowerCase()
        .replace(
          /\b\w/g,
          (character) =>
            character.toUpperCase()
        );
    };

  const statusClass =
    (status) => {
      switch (status) {
        case "RESOLVED":
          return "super-admin-badge super-admin-badge-success";

        case "CLOSED":
          return "super-admin-badge super-admin-badge-neutral";

        case "OPEN":
          return "super-admin-badge super-admin-badge-warning";

        case "IN_PROGRESS":
          return "super-admin-badge super-admin-badge-info";

        default:
          return "super-admin-badge super-admin-badge-neutral";
      }
    };

  const priorityClass =
    (priority) => {
      switch (
        priority
      ) {
        case "URGENT":
          return "super-admin-badge super-admin-badge-danger";

        case "HIGH":
          return "super-admin-badge super-admin-badge-warning";

        case "MEDIUM":
          return "super-admin-badge super-admin-badge-info";

        default:
          return "super-admin-badge super-admin-badge-neutral";
      }
    };

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <div className="super-admin-page">
      {/* HEADER */}

      <div className="super-admin-page-header">
        <div>
          <h1>
            Support & Complaints
          </h1>

          <p>
            Monitor customer support
            tickets across all
            restaurants and manage
            resolution status.
          </p>
        </div>

        <button
          type="button"
          className="super-admin-button super-admin-button-secondary"
          onClick={
            loadTickets
          }
        >
          Refresh
        </button>
      </div>

      {/* STATS */}

      <div className="super-admin-stat-grid">
        <div className="super-admin-stat-card">
          <span>
            Visible Tickets
          </span>

          <strong>
            {counts.total}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Open
          </span>

          <strong>
            {counts.open}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            In Progress
          </span>

          <strong>
            {
              counts.inProgress
            }
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Resolved
          </span>

          <strong>
            {
              counts.resolved
            }
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Closed
          </span>

          <strong>
            {counts.closed}
          </strong>
        </div>

        <div className="super-admin-stat-card">
          <span>
            Urgent
          </span>

          <strong>
            {counts.urgent}
          </strong>
        </div>
      </div>

      {/* FILTERS */}

      <div className="super-admin-section">
        <div className="super-admin-filter-bar">
          <div className="super-admin-filter-field">
            <label>
              Restaurant
            </label>

            <select
              value={
                filters.restaurantId
              }
              onChange={(event) =>
                setFilters(
                  (current) => ({
                    ...current,
                    restaurantId:
                      event
                        .target
                        .value,
                  })
                )
              }
            >
              <option value="">
                All Restaurants
              </option>

              {restaurants.map(
                (restaurant) => (
                  <option
                    key={
                      restaurant.id
                    }
                    value={
                      restaurant.id
                    }
                  >
                    {
                      restaurant.name
                    }
                  </option>
                )
              )}
            </select>
          </div>

          <div className="super-admin-filter-field">
            <label>
              Status
            </label>

            <select
              value={
                filters.status
              }
              onChange={(event) =>
                setFilters(
                  (current) => ({
                    ...current,
                    status:
                      event
                        .target
                        .value,
                  })
                )
              }
            >
              <option value="">
                All Statuses
              </option>

              <option value="OPEN">
                Open
              </option>

              <option value="IN_PROGRESS">
                In Progress
              </option>

              <option value="RESOLVED">
                Resolved
              </option>

              <option value="CLOSED">
                Closed
              </option>
            </select>
          </div>

          <div className="super-admin-filter-field">
            <label>
              Priority
            </label>

            <select
              value={
                filters.priority
              }
              onChange={(event) =>
                setFilters(
                  (current) => ({
                    ...current,
                    priority:
                      event
                        .target
                        .value,
                  })
                )
              }
            >
              <option value="">
                All Priorities
              </option>

              <option value="LOW">
                Low
              </option>

              <option value="MEDIUM">
                Medium
              </option>

              <option value="HIGH">
                High
              </option>

              <option value="URGENT">
                Urgent
              </option>
            </select>
          </div>

          <button
            type="button"
            className="super-admin-button super-admin-button-secondary"
            onClick={
              clearFilters
            }
          >
            Clear Filters
          </button>
        </div>

        {/* SUCCESS */}

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

        {/* ERROR */}

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

        {/* TABLE */}

        {loading ? (
          <div className="super-admin-loading-card">
            Loading support
            tickets...
          </div>
        ) : tickets.length ===
          0 ? (
          <div className="super-admin-empty-card">
            No support tickets
            found.
          </div>
        ) : (
          <div className="super-admin-table-wrapper">
            <table className="super-admin-table">
              <thead>
                <tr>
                  <th>
                    Ticket
                  </th>

                  <th>
                    Customer
                  </th>

                  <th>
                    Restaurant
                  </th>

                  <th>
                    Category
                  </th>

                  <th>
                    Priority
                  </th>

                  <th>
                    Status
                  </th>

                  <th>
                    Created
                  </th>

                  <th>
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody>
                {tickets.map(
                  (ticket) => {
                    const processing =
                      processingTicketId ===
                      ticket.id;

                    return (
                      <tr
                        key={
                          ticket.id
                        }
                      >
                        <td>
                          <strong>
                            #
                            {
                              ticket.id
                            }
                          </strong>

                          <div>
                            {
                              ticket.subject
                            }
                          </div>

                          {ticket.orderId && (
                            <small>
                              Order #
                              {
                                ticket.orderId
                              }
                            </small>
                          )}
                        </td>

                        <td>
                          <strong>
                            {
                              ticket.customerName
                            }
                          </strong>

                          <div>
                            {
                              ticket.customerEmail
                            }
                          </div>

                          <small>
                            Customer ID:{" "}
                            {
                              ticket.customerId
                            }
                          </small>
                        </td>

                        <td>
                          {ticket.restaurantId
                            ? (
                              <>
                                <strong>
                                  {
                                    ticket.restaurantName
                                  }
                                </strong>

                                <div>
                                  <small>
                                    ID:{" "}
                                    {
                                      ticket.restaurantId
                                    }
                                  </small>
                                </div>
                              </>
                            )
                            : "-"}
                        </td>

                        <td>
                          {formatLabel(
                            ticket.category
                          )}
                        </td>

                        <td>
                          <span
                            className={priorityClass(
                              ticket.priority
                            )}
                          >
                            {
                              ticket.priority
                            }
                          </span>
                        </td>

                        <td>
                          <span
                            className={statusClass(
                              ticket.status
                            )}
                          >
                            {formatLabel(
                              ticket.status
                            )}
                          </span>
                        </td>

                        <td>
                          {formatDate(
                            ticket.createdAt
                          )}
                        </td>

                        <td>
                          <div
                            style={{
                              display:
                                "flex",

                              gap:
                                "8px",

                              flexWrap:
                                "wrap",
                            }}
                          >
                            <button
                              type="button"
                              disabled={
                                detailLoading
                              }
                              className="super-admin-button super-admin-button-secondary"
                              onClick={() =>
                                openTicketDetails(
                                  ticket.id
                                )
                              }
                            >
                              View
                            </button>

                            <select
                              value={
                                ticket.status
                              }
                              disabled={
                                processing
                              }
                              onChange={(
                                event
                              ) =>
                                handleStatusChange(
                                  ticket,
                                  event
                                    .target
                                    .value
                                )
                              }
                              style={{
                                minWidth:
                                  "140px",

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
                              <option value="OPEN">
                                Open
                              </option>

                              <option value="IN_PROGRESS">
                                In Progress
                              </option>

                              <option value="RESOLVED">
                                Resolved
                              </option>

                              <option value="CLOSED">
                                Closed
                              </option>
                            </select>
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
          TICKET DETAILS MODAL
      ===================================== */}

      {selectedTicket && (
        <div className="super-admin-modal-backdrop">
          <div
            className="super-admin-modal"
            style={{
              maxWidth:
                "900px",
            }}
          >
            <div className="super-admin-modal-header">
              <div>
                <h2>
                  Ticket #
                  {
                    selectedTicket.id
                  }
                </h2>

                <small>
                  {formatDate(
                    selectedTicket.createdAt
                  )}
                </small>
              </div>

              <button
                type="button"
                className="super-admin-button super-admin-button-secondary"
                onClick={() =>
                  setSelectedTicket(
                    null
                  )
                }
              >
                Close
              </button>
            </div>

            <div className="super-admin-modal-body">
              {/* INFO CARDS */}

              <div
                style={{
                  display:
                    "grid",

                  gridTemplateColumns:
                    "repeat(auto-fit, minmax(200px, 1fr))",

                  gap:
                    "14px",

                  marginBottom:
                    "18px",
                }}
              >
                <div className="super-admin-card">
                  <strong>
                    Customer
                  </strong>

                  <p>
                    {
                      selectedTicket.customerName
                    }
                  </p>

                  <small>
                    {
                      selectedTicket.customerEmail
                    }
                  </small>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Restaurant
                  </strong>

                  <p>
                    {
                      selectedTicket.restaurantName ||
                      "Platform Support"
                    }
                  </p>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Category
                  </strong>

                  <p>
                    {formatLabel(
                      selectedTicket.category
                    )}
                  </p>
                </div>

                <div className="super-admin-card">
                  <strong>
                    Priority
                  </strong>

                  <p>
                    <span
                      className={priorityClass(
                        selectedTicket.priority
                      )}
                    >
                      {
                        selectedTicket.priority
                      }
                    </span>
                  </p>
                </div>
              </div>

              {/* SUBJECT */}

              <div
                className="super-admin-card"
                style={{
                  marginBottom:
                    "18px",
                }}
              >
                <strong>
                  Subject
                </strong>

                <p>
                  {
                    selectedTicket.subject
                  }
                </p>

                {selectedTicket.orderId && (
                  <small>
                    Related Order: #
                    {
                      selectedTicket.orderId
                    }
                  </small>
                )}
              </div>

              {/* STATUS */}

              <div
                className="super-admin-card"
                style={{
                  marginBottom:
                    "18px",
                }}
              >
                <strong>
                  Ticket Status
                </strong>

                <div
                  style={{
                    marginTop:
                      "10px",
                  }}
                >
                  <select
                    value={
                      selectedTicket.status
                    }
                    disabled={
                      processingTicketId ===
                      selectedTicket.id
                    }
                    onChange={(event) =>
                      handleStatusChange(
                        selectedTicket,
                        event.target.value
                      )
                    }
                    style={{
                      minWidth:
                        "180px",

                      height:
                        "38px",

                      border:
                        "1px solid #d1d5db",

                      borderRadius:
                        "8px",

                      background:
                        "#fff",

                      padding:
                        "0 10px",
                    }}
                  >
                    <option value="OPEN">
                      Open
                    </option>

                    <option value="IN_PROGRESS">
                      In Progress
                    </option>

                    <option value="RESOLVED">
                      Resolved
                    </option>

                    <option value="CLOSED">
                      Closed
                    </option>
                  </select>
                </div>
              </div>

              {/* CONVERSATION */}

              <div className="super-admin-section">
                <h3 className="super-admin-section-title">
                  Conversation
                </h3>

                {!selectedTicket.messages
                  ?.length ? (
                  <div className="super-admin-empty-card">
                    No messages found.
                  </div>
                ) : (
                  <div
                    style={{
                      display:
                        "flex",

                      flexDirection:
                        "column",

                      gap:
                        "12px",
                    }}
                  >
                    {selectedTicket.messages.map(
                      (
                        message,
                        index
                      ) => (
                        <div
                          className="super-admin-card"
                          key={
                            message.id ??
                            index
                          }
                        >
                          <div
                            style={{
                              display:
                                "flex",

                              justifyContent:
                                "space-between",

                              gap:
                                "12px",

                              flexWrap:
                                "wrap",

                              marginBottom:
                                "8px",
                            }}
                          >
                            <strong>
                              {
                                message.senderName ||
                                message.senderType ||
                                "Support"
                              }
                            </strong>

                            <small>
                              {formatDate(
                                message.createdAt
                              )}
                            </small>
                          </div>

                          <p
                            style={{
                              margin:
                                0,

                              whiteSpace:
                                "pre-wrap",
                            }}
                          >
                            {
                              message.message
                            }
                          </p>
                        </div>
                      )
                    )}
                  </div>
                )}
              </div>

              <div
                style={{
                  marginTop:
                    "18px",

                  fontSize:
                    "13px",

                  color:
                    "#6b7280",
                }}
              >
                Last updated:{" "}
                {formatDate(
                  selectedTicket.updatedAt
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default SuperAdminSupport;