import { useEffect, useState } from "react";

const SUPPORT_API =
  `${import.meta.env.VITE_API_BASE_URL}/support`;

const ORDERS_API =
  `${import.meta.env.VITE_API_BASE_URL}/orders/my-orders`;

const EMPTY_FORM = {
  subject: "",
  category: "ORDER_ISSUE",
  priority: "MEDIUM",
  orderId: "",
  message: "",
};

const CATEGORIES = [
  "ORDER_ISSUE",
  "PAYMENT_ISSUE",
  "DELIVERY_ISSUE",
  "REFUND_REQUEST",
  "ACCOUNT_ISSUE",
  "FOOD_QUALITY",
  "OTHER",
];

const PRIORITIES = [
  "LOW",
  "MEDIUM",
  "HIGH",
  "URGENT",
];

function getToken() {
  return sessionStorage.getItem(
    "customerToken",
  );
}

function formatLabel(value) {
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
  return new Intl.DateTimeFormat("en-IN", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

function CustomerSupport({ onBack }) {
  const [tickets, setTickets] = useState([]);
  const [orders, setOrders] = useState([]);
  const [selectedTicketId, setSelectedTicketId] =
    useState(null);

  const [formData, setFormData] =
    useState(EMPTY_FORM);

  const [replyMessage, setReplyMessage] =
    useState("");

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] =
    useState(false);

  const [replying, setReplying] =
    useState(false);

  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] =
    useState("");

  const selectedTicket =
    tickets.find(
      (ticket) =>
        ticket.id === selectedTicketId,
    ) ?? null;
  const selectedOrder =
  formData.orderId
    ? orders.find(
        (order) =>
          order.id ===
          Number(formData.orderId),
      ) ?? null
    : null;
  useEffect(() => {
    let requestIsActive = true;

    async function loadSupportData() {
      const token = getToken();

      if (!token) {
        window.location.href =
          "/customer-login";
        return;
      }

      try {
        const headers = {
          Authorization: `Bearer ${token}`,
        };

        const [
          ticketsResponse,
          ordersResponse,
        ] = await Promise.all([
          fetch(
            `${SUPPORT_API}/tickets/my-tickets`,
            { headers },
          ),
          fetch(ORDERS_API, { headers }),
        ]);

        if (
          ticketsResponse.status === 401 ||
          ordersResponse.status === 401
        ) {
          sessionStorage.removeItem(
            "customerToken",
          );

          window.location.href =
            "/customer-login";

          return;
        }

        if (!ticketsResponse.ok) {
          throw new Error(
            "Unable to load support tickets",
          );
        }

        const ticketData =
          await ticketsResponse.json();

        const orderData = ordersResponse.ok
          ? await ordersResponse.json()
          : [];

        if (requestIsActive) {
          setTickets(ticketData);
          setOrders(orderData);

          if (ticketData.length > 0) {
            setSelectedTicketId(
              ticketData[0].id,
            );
          }

          setError("");
        }
      } catch {
        if (requestIsActive) {
          setError(
            "Could not load customer support. Make sure the backend is running.",
          );
        }
      } finally {
        if (requestIsActive) {
          setLoading(false);
        }
      }
    }

    loadSupportData();

    return () => {
      requestIsActive = false;
    };
  }, []);

  function handleInputChange(event) {
    const { name, value } = event.target;

    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }));
  }

  async function createTicket(event) {
    event.preventDefault();

    const token = getToken();

 if (
  formData.orderId &&
  !selectedOrder
) {
  setError(
    "Selected order could not be found.",
  );
  return;
}

const requestBody = {
  subject:
    formData.subject.trim(),

  category:
    formData.category,

  priority:
    formData.priority,

  restaurantId:
    selectedOrder?.restaurantId ??
    null,

  orderId:
    selectedOrder?.id ??
    null,

  message:
    formData.message.trim(),
};

    try {
      setSubmitting(true);
      setError("");
      setSuccessMessage("");

      const response = await fetch(
        `${SUPPORT_API}/tickets`,
        {
          method: "POST",
          headers: {
            "Content-Type":
              "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(requestBody),
        },
      );

      const responseData =
        await response.json();

      if (!response.ok) {
        throw new Error(
          responseData.message ||
            "Unable to create support ticket",
        );
      }

      setTickets((currentTickets) => [
        responseData,
        ...currentTickets,
      ]);

      setSelectedTicketId(responseData.id);
      setFormData(EMPTY_FORM);

      setSuccessMessage(
        `Support ticket #${responseData.id} created successfully.`,
      );
    } catch (requestError) {
      setError(
        requestError.message ||
          "The support ticket could not be created.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  async function submitReply(event) {
    event.preventDefault();

    if (
      !selectedTicket ||
      !replyMessage.trim()
    ) {
      return;
    }

    const token = getToken();

    try {
      setReplying(true);
      setError("");
      setSuccessMessage("");

      const response = await fetch(
        `${SUPPORT_API}/tickets/my-tickets/${selectedTicket.id}/replies`,
        {
          method: "POST",
          headers: {
            "Content-Type":
              "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            message: replyMessage.trim(),
          }),
        },
      );

      const responseData =
        await response.json();

      if (!response.ok) {
        throw new Error(
          responseData.message ||
            "Unable to send reply",
        );
      }

      setTickets((currentTickets) =>
        currentTickets.map((ticket) =>
          ticket.id === responseData.id
            ? responseData
            : ticket,
        ),
      );

      setReplyMessage("");

      setSuccessMessage(
        "Your reply was sent successfully.",
      );
    } catch (requestError) {
      setError(
        requestError.message ||
          "Your reply could not be sent.",
      );
    } finally {
      setReplying(false);
    }
  }

  return (
    <main className="support-page">
      <header className="support-header">
        <div>
          <span className="eyebrow">
            Customer assistance
          </span>

          <h1>Customer Support</h1>

         <p>
  Create a support ticket for your
  order and connect with the correct
  restaurant team.
</p>
        </div>

        <button
          type="button"
          onClick={onBack}
        >
          Back to restaurant
        </button>
      </header>

      {loading && (
        <div className="status-message">
          Loading customer support...
        </div>
      )}

      {error && (
        <div className="status-message error-message">
          {error}
        </div>
      )}

      {successMessage && (
        <div className="support-success-message">
          {successMessage}
        </div>
      )}

      {!loading && (
        <div className="support-layout">
          <section className="support-create-card">
            <span className="eyebrow">
              Need help?
            </span>

            <h2>Create a support ticket</h2>

            <form onSubmit={createTicket}>
              <label>
                Subject
                <input
                  type="text"
                  name="subject"
                  value={formData.subject}
                  onChange={handleInputChange}
                  placeholder="Briefly describe your issue"
                  minLength="5"
                  maxLength="150"
                  required
                />
              </label>

              <label>
                Category
                <select
                  name="category"
                  value={formData.category}
                  onChange={handleInputChange}
                  required
                >
                  {CATEGORIES.map(
                    (category) => (
                      <option
                        key={category}
                        value={category}
                      >
                        {formatLabel(category)}
                      </option>
                    ),
                  )}
                </select>
              </label>

              <label>
                Priority
                <select
                  name="priority"
                  value={formData.priority}
                  onChange={handleInputChange}
                  required
                >
                  {PRIORITIES.map(
                    (priority) => (
                      <option
                        key={priority}
                        value={priority}
                      >
                        {formatLabel(priority)}
                      </option>
                    ),
                  )}
                </select>
              </label>

              <label>
                Related order (optional)
                <select
                  name="orderId"
                  value={formData.orderId}
                  onChange={handleInputChange}
                >
                  <option value="">
                    No related order
                  </option>

                  {orders.map((order) => (
                    <option
                      key={order.id}
                      value={order.id}
                    >
                     Order #{order.id}
{" — "}
{order.restaurantName}
{" — "}
{formatDate(
  order.createdAt,
)}
                    </option>
                  ))}
                </select>
              </label>

              <label>
                Describe your problem
                <textarea
                  name="message"
                  value={formData.message}
                  onChange={handleInputChange}
                  placeholder="Provide complete details so we can help you quickly"
                  minLength="10"
                  maxLength="2000"
                  rows="6"
                  required
                />
              </label>

              <button
                className="support-submit-button"
                type="submit"
                disabled={submitting}
              >
                {submitting
                  ? "Creating ticket..."
                  : "Submit support ticket"}
              </button>
            </form>
          </section>

          <section className="support-tickets-card">
            <div className="support-section-heading">
              <div>
                <span className="eyebrow">
                  Your requests
                </span>

                <h2>My support tickets</h2>
              </div>

              <strong>
                {tickets.length}
              </strong>
            </div>

            {tickets.length === 0 ? (
              <div className="support-empty">
                <span>🎧</span>
                <h3>No support tickets</h3>
                <p>
                  Your submitted tickets will
                  appear here.
                </p>
              </div>
            ) : (
              <div className="support-ticket-list">
                {tickets.map((ticket) => (
                  <button
                    className={
                      selectedTicketId ===
                      ticket.id
                        ? "support-ticket-item active"
                        : "support-ticket-item"
                    }
                    type="button"
                    key={ticket.id}
                    onClick={() =>
                      setSelectedTicketId(
                        ticket.id,
                      )
                    }
                  >
                    <div>
                      <strong>
                        Ticket #{ticket.id}
                      </strong>

                      <span
                        className={`support-status support-status-${ticket.status.toLowerCase()}`}
                      >
                        {formatLabel(
                          ticket.status,
                        )}
                      </span>
                    </div>

                    <h3>{ticket.subject}</h3>

                    <small>
                      {formatLabel(
                        ticket.category,
                      )}
                      {" · "}
                      {formatLabel(
                        ticket.priority,
                      )}
                    </small>
                  </button>
                ))}
              </div>
            )}
          </section>

          {selectedTicket && (
            <section className="support-conversation-card">
              <div className="support-conversation-heading">
                <div>
                  <span className="eyebrow">
                    Ticket #
                    {selectedTicket.id}
                  </span>

                  <h2>
                    {selectedTicket.subject}
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

              <div className="support-ticket-details">
                <span>
                  Category:{" "}
                  <strong>
                    {formatLabel(
                      selectedTicket.category,
                    )}
                  </strong>
                </span>

                <span>
                  Priority:{" "}
                  <strong>
                    {formatLabel(
                      selectedTicket.priority,
                    )}
                  </strong>
                </span>

                {selectedTicket.orderId && (
                  <span>
                    Related order:{" "}
                    <strong>
                      #{selectedTicket.orderId}
                    </strong>
                  </span>
                )}
              </div>

              <div className="support-messages">
                {selectedTicket.messages.map(
                  (message) => (
                    <article
                      className={
                        message.senderType ===
                        "CUSTOMER"
                          ? "support-message customer-message"
                          : "support-message admin-message"
                      }
                      key={message.id}
                    >
                      <div>
                        <strong>
                          {message.senderName}
                        </strong>

                        <small>
                          {formatDate(
                            message.createdAt,
                          )}
                        </small>
                      </div>

                      <p>{message.message}</p>
                    </article>
                  ),
                )}
              </div>

              {selectedTicket.status !==
              "CLOSED" ? (
                <form
                  className="support-reply-form"
                  onSubmit={submitReply}
                >
                  <label>
                    Reply to support
                    <textarea
                      value={replyMessage}
                      onChange={(event) =>
                        setReplyMessage(
                          event.target.value,
                        )
                      }
                      placeholder="Write your reply"
                      minLength="2"
                      maxLength="2000"
                      rows="4"
                      required
                    />
                  </label>

                  <button
                    type="submit"
                    disabled={replying}
                  >
                    {replying
                      ? "Sending reply..."
                      : "Send reply"}
                  </button>
                </form>
              ) : (
                <p className="support-closed-message">
                  This support ticket is closed.
                </p>
              )}
            </section>
          )}
        </div>
      )}
    </main>
  );
}

export default CustomerSupport;