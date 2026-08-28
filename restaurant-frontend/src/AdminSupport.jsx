import { useEffect, useState } from "react";
import AdminNav from "./AdminNav.jsx";

const ADMIN_SUPPORT_API =
  "http://localhost:8080/api/support/restaurant-admin/tickets";

const TICKET_STATUSES = [
  "OPEN",
  "IN_PROGRESS",
  "RESOLVED",
  "CLOSED",
];

function getAdminToken() {
  return sessionStorage.getItem("adminToken");
}

function handleUnauthorized(response) {
  if (
    response.status === 401 ||
    response.status === 403
  ) {
    sessionStorage.removeItem(
      "adminToken",
    );

    sessionStorage.removeItem(
      "adminEmail",
    );

    window.location.href = "/admin";

    throw new Error(
      "Admin session expired",
    );
  }
}

function formatLabel(value) {
  if (!value) {
    return "Not specified";
  }

  return value
    .toLowerCase()
    .split("_")
    .map(
      (word) =>
        word.charAt(0).toUpperCase() +
        word.slice(1),
    )
    .join(" ");
}

function formatDate(value) {
  if (!value) {
    return "Not available";
  }

  return new Intl.DateTimeFormat(
    "en-IN",
    {
      dateStyle: "medium",
      timeStyle: "short",
    },
  ).format(new Date(value));
}

function AdminSupport() {
  const [tickets, setTickets] =
    useState([]);

  const [
    selectedTicketId,
    setSelectedTicketId,
  ] = useState(null);

  const [statusFilter, setStatusFilter] =
    useState("ALL");

  const [replyMessage, setReplyMessage] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [replying, setReplying] =
    useState(false);

  const [
    updatingStatus,
    setUpdatingStatus,
  ] = useState(false);

  const [error, setError] =
    useState("");

  const [message, setMessage] =
    useState("");

  const selectedTicket =
    tickets.find(
      (ticket) =>
        ticket.id === selectedTicketId,
    ) ?? null;

  const filteredTickets =
    statusFilter === "ALL"
      ? tickets
      : tickets.filter(
          (ticket) =>
            ticket.status ===
            statusFilter,
        );

  useEffect(() => {
    let requestIsActive = true;

    async function loadTickets() {
      const token =
        getAdminToken();

      if (!token) {
        window.location.href =
          "/admin";

        return;
      }

      try {
        const response = await fetch(
          ADMIN_SUPPORT_API,
          {
            headers: {
              Authorization:
                `Bearer ${token}`,
            },
          },
        );

        handleUnauthorized(
          response,
        );

        if (!response.ok) {
          throw new Error(
            "Unable to load support tickets",
          );
        }

        const data =
          await response.json();

        if (requestIsActive) {
          setTickets(data);

          if (data.length > 0) {
            setSelectedTicketId(
              data[0].id,
            );
          }

          setError("");
        }
      } catch (requestError) {
        if (
          requestIsActive &&
          requestError.message !==
            "Admin session expired"
        ) {
          setError(
            "Could not load support tickets. Make sure the backend is running.",
          );
        }
      } finally {
        if (requestIsActive) {
          setLoading(false);
        }
      }
    }

    loadTickets();

    return () => {
      requestIsActive = false;
    };
  }, []);

  async function refreshTickets() {
    const token =
      getAdminToken();

    if (!token) {
      window.location.href =
        "/admin";

      return;
    }

    try {
      setLoading(true);
      setError("");
      setMessage("");

      const response = await fetch(
        ADMIN_SUPPORT_API,
        {
          headers: {
            Authorization:
              `Bearer ${token}`,
          },
        },
      );

      handleUnauthorized(
        response,
      );

      if (!response.ok) {
        throw new Error(
          "Unable to refresh support tickets",
        );
      }

      const data =
        await response.json();

      setTickets(data);

      if (
        selectedTicketId &&
        !data.some(
          (ticket) =>
            ticket.id ===
            selectedTicketId,
        )
      ) {
        setSelectedTicketId(
          data[0]?.id ?? null,
        );
      }

      if (
        !selectedTicketId &&
        data.length > 0
      ) {
        setSelectedTicketId(
          data[0].id,
        );
      }

      setMessage(
        "Support tickets refreshed.",
      );
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          "Support tickets could not be refreshed.",
        );
      }
    } finally {
      setLoading(false);
    }
  }

  async function updateStatus(
    newStatus,
  ) {
    if (!selectedTicket) {
      return;
    }

    const token =
      getAdminToken();

    if (!token) {
      window.location.href =
        "/admin";

      return;
    }

    try {
      setUpdatingStatus(true);
      setError("");
      setMessage("");

      const response = await fetch(
        `${ADMIN_SUPPORT_API}/${selectedTicket.id}/status?status=${newStatus}`,
        {
          method: "PATCH",

          headers: {
            Authorization:
              `Bearer ${token}`,
          },
        },
      );

      handleUnauthorized(
        response,
      );

      let responseData = null;

      try {
        responseData =
          await response.json();
      } catch {
        // Response may not contain JSON.
      }

      if (!response.ok) {
        throw new Error(
          responseData?.message ||
            "Unable to update ticket status",
        );
      }

      setTickets(
        (currentTickets) =>
          currentTickets.map(
            (ticket) =>
              ticket.id ===
              responseData.id
                ? responseData
                : ticket,
          ),
      );

      setMessage(
        `Ticket #${responseData.id} status updated.`,
      );
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          requestError.message ||
            "Ticket status could not be updated.",
        );
      }
    } finally {
      setUpdatingStatus(false);
    }
  }

  async function submitReply(
    event,
  ) {
    event.preventDefault();

    if (
      !selectedTicket ||
      !replyMessage.trim()
    ) {
      return;
    }

    const token =
      getAdminToken();

    if (!token) {
      window.location.href =
        "/admin";

      return;
    }

    try {
      setReplying(true);
      setError("");
      setMessage("");

      const response = await fetch(
        `${ADMIN_SUPPORT_API}/${selectedTicket.id}/replies`,
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json",

            Authorization:
              `Bearer ${token}`,
          },

          body: JSON.stringify({
            message:
              replyMessage.trim(),
          }),
        },
      );

      handleUnauthorized(
        response,
      );

      let responseData = null;

      try {
        responseData =
          await response.json();
      } catch {
        // Response may not contain JSON.
      }

      if (!response.ok) {
        throw new Error(
          responseData?.message ||
            "Unable to send reply",
        );
      }

      setTickets(
        (currentTickets) =>
          currentTickets.map(
            (ticket) =>
              ticket.id ===
              responseData.id
                ? responseData
                : ticket,
          ),
      );

      setReplyMessage("");

      setMessage(
        "Admin reply sent.",
      );
    } catch (requestError) {
      if (
        requestError.message !==
        "Admin session expired"
      ) {
        setError(
          requestError.message ||
            "The reply could not be sent.",
        );
      }
    } finally {
      setReplying(false);
    }
  }

  const openCount =
    tickets.filter(
      (ticket) =>
        ticket.status === "OPEN",
    ).length;

  const activeCount =
    tickets.filter(
      (ticket) =>
        ticket.status === "OPEN" ||
        ticket.status ===
          "IN_PROGRESS",
    ).length;

  const resolvedCount =
    tickets.filter(
      (ticket) =>
        ticket.status ===
          "RESOLVED" ||
        ticket.status === "CLOSED",
    ).length;

  return (
    <main className="admin-support-page">
      <AdminNav
        activePage="support"
      />

      <header className="admin-support-header">
        <div>
          <span className="eyebrow">
            Restaurant management
          </span>

          <h1>
            Customer Support
          </h1>

          <p>
            View customer problems,
            send replies and manage
            ticket progress.
          </p>
        </div>

        <div className="admin-support-actions">
          <button
            type="button"
            onClick={
              refreshTickets
            }
          >
            Refresh
          </button>
        </div>
      </header>

      <section className="admin-support-stats">
        <article>
          <span>
            Total tickets
          </span>

          <strong>
            {tickets.length}
          </strong>
        </article>

        <article>
          <span>
            New tickets
          </span>

          <strong>
            {openCount}
          </strong>
        </article>

        <article>
          <span>
            Active tickets
          </span>

          <strong>
            {activeCount}
          </strong>
        </article>

        <article>
          <span>
            Resolved or closed
          </span>

          <strong>
            {resolvedCount}
          </strong>
        </article>
      </section>

      {loading && (
        <div className="status-message">
          Loading support
          tickets...
        </div>
      )}

      {error && (
        <div className="status-message error-message">
          {error}
        </div>
      )}

      {message && (
        <div className="support-success-message">
          {message}
        </div>
      )}

      {!loading && (
        <div className="admin-support-layout">
          <section className="admin-support-list-card">
            <div className="admin-support-list-heading">
              <h2>
                Support tickets
              </h2>

              <select
                value={
                  statusFilter
                }
                onChange={(
                  event,
                ) =>
                  setStatusFilter(
                    event.target
                      .value,
                  )
                }
              >
                <option value="ALL">
                  All statuses
                </option>

                {TICKET_STATUSES.map(
                  (status) => (
                    <option
                      key={
                        status
                      }
                      value={
                        status
                      }
                    >
                      {formatLabel(
                        status,
                      )}
                    </option>
                  ),
                )}
              </select>
            </div>

            {filteredTickets.length ===
            0 ? (
              <div className="support-empty">
                <span>🎧</span>

                <h3>
                  No support
                  tickets
                </h3>

                <p>
                  No tickets match
                  the selected
                  status.
                </p>
              </div>
            ) : (
              <div className="admin-support-ticket-list">
                {filteredTickets.map(
                  (ticket) => (
                    <button
                      className={
                        selectedTicketId ===
                        ticket.id
                          ? "admin-support-ticket active"
                          : "admin-support-ticket"
                      }
                      type="button"
                      key={
                        ticket.id
                      }
                      onClick={() =>
                        setSelectedTicketId(
                          ticket.id,
                        )
                      }
                    >
                      <div>
                        <strong>
                          Ticket #
                          {
                            ticket.id
                          }
                        </strong>

                        <span
                          className={`support-status support-status-${ticket.status.toLowerCase()}`}
                        >
                          {formatLabel(
                            ticket.status,
                          )}
                        </span>
                      </div>

                      <h3>
                        {
                          ticket.subject
                        }
                      </h3>

                      <p>
                        {
                          ticket.customerName
                        }
                      </p>

                      <small>
                        {formatLabel(
                          ticket.priority,
                        )}
                        {" · "}
                        {formatDate(
                          ticket.updatedAt,
                        )}
                      </small>
                    </button>
                  ),
                )}
              </div>
            )}
          </section>

          <section className="admin-support-conversation">
            {!selectedTicket ? (
              <div className="support-empty">
                <span>💬</span>

                <h3>
                  Select a ticket
                </h3>

                <p>
                  Choose a customer
                  ticket to view its
                  conversation.
                </p>
              </div>
            ) : (
              <>
                <div className="support-conversation-heading">
                  <div>
                    <span className="eyebrow">
                      Ticket #
                      {
                        selectedTicket.id
                      }
                    </span>

                    <h2>
                      {
                        selectedTicket.subject
                      }
                    </h2>
                  </div>

                  <span
                    className={`support-status support-status-${selectedTicket.status.toLowerCase()}`}
                  >
                    {formatLabel(
                      selectedTicket.status,
                    )}
                  </span>
                </div>

                <div className="admin-support-customer">
                  <div>
                    <span>
                      Customer
                    </span>

                    <strong>
                      {
                        selectedTicket.customerName
                      }
                    </strong>

                    <small>
                      {
                        selectedTicket.customerEmail
                      }
                    </small>
                  </div>

                  <div>
                    <span>
                      Category
                    </span>

                    <strong>
                      {formatLabel(
                        selectedTicket.category,
                      )}
                    </strong>
                  </div>

                  <div>
                    <span>
                      Priority
                    </span>

                    <strong>
                      {formatLabel(
                        selectedTicket.priority,
                      )}
                    </strong>
                  </div>

                  <div>
                    <span>
                      Related order
                    </span>

                    <strong>
                      {selectedTicket.orderId
                        ? `#${selectedTicket.orderId}`
                        : "Not provided"}
                    </strong>
                  </div>
                </div>

                <label className="admin-support-status-select">
                  Update ticket
                  status

                  <select
                    value={
                      selectedTicket.status
                    }
                    disabled={
                      updatingStatus
                    }
                    onChange={(
                      event,
                    ) =>
                      updateStatus(
                        event.target
                          .value,
                      )
                    }
                  >
                    {TICKET_STATUSES.map(
                      (
                        status,
                      ) => (
                        <option
                          key={
                            status
                          }
                          value={
                            status
                          }
                        >
                          {formatLabel(
                            status,
                          )}
                        </option>
                      ),
                    )}
                  </select>
                </label>

                <div className="support-messages">
                  {selectedTicket.messages.map(
                    (
                      supportMessage,
                    ) => (
                      <article
                        className={
                          supportMessage.senderType ===
                          "ADMIN"
                            ? "support-message customer-message"
                            : "support-message admin-message"
                        }
                        key={
                          supportMessage.id
                        }
                      >
                        <div>
                          <strong>
                            {
                              supportMessage.senderName
                            }
                          </strong>

                          <small>
                            {formatDate(
                              supportMessage.createdAt,
                            )}
                          </small>
                        </div>

                        <p>
                          {
                            supportMessage.message
                          }
                        </p>
                      </article>
                    ),
                  )}
                </div>

                {selectedTicket.status !==
                "CLOSED" ? (
                  <form
                    className="support-reply-form"
                    onSubmit={
                      submitReply
                    }
                  >
                    <label>
                      Reply to
                      customer

                      <textarea
                        value={
                          replyMessage
                        }
                        onChange={(
                          event,
                        ) =>
                          setReplyMessage(
                            event
                              .target
                              .value,
                          )
                        }
                        placeholder="Write a helpful response"
                        minLength="2"
                        maxLength="2000"
                        rows="4"
                        required
                      />
                    </label>

                    <button
                      type="submit"
                      disabled={
                        replying
                      }
                    >
                      {replying
                        ? "Sending reply..."
                        : "Send admin reply"}
                    </button>
                  </form>
                ) : (
                  <p className="support-closed-message">
                    This ticket is
                    closed. Change its
                    status to reply
                    again.
                  </p>
                )}
              </>
            )}
          </section>
        </div>
      )}
    </main>
  );
}

export default AdminSupport;