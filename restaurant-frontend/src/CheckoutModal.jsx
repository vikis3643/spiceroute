import { useEffect, useState } from "react";

const ORDERS_API_URL =
  "http://localhost:8080/api/orders";

const ORDER_QUOTE_API_URL =
  "http://localhost:8080/api/orders/quote";

const PROFILE_API_URL =
  "http://localhost:8080/api/customer/profile";

function minimumScheduleValue() {
  const minimumDate = new Date(
    Date.now() + 60 * 60 * 1000,
  );

  const pad = (value) =>
    String(value).padStart(2, "0");

  return `${minimumDate.getFullYear()}-${pad(
    minimumDate.getMonth() + 1,
  )}-${pad(minimumDate.getDate())}T${pad(
    minimumDate.getHours(),
  )}:${pad(minimumDate.getMinutes())}`;
}

function formatScheduledDate(dateValue) {
  if (!dateValue) {
    return "Not scheduled";
  }

  return new Intl.DateTimeFormat("en-IN", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(dateValue));
}

function CheckoutModal({
  isOpen,
  cart,
  onClose,
  onOrderPlaced,
  formatPrice,
}) {
  const [formData, setFormData] = useState({
    customerName: "",
    phone: "",
    deliveryAddress: "",
  });

  const [paymentMethod, setPaymentMethod] =
    useState("CASH_ON_DELIVERY");

  const [demoPaymentType, setDemoPaymentType] =
    useState("UPI");

  const [orderTiming, setOrderTiming] =
    useState("NOW");

  const [mealSlot, setMealSlot] =
    useState("LUNCH");

  const [scheduledFor, setScheduledFor] =
    useState("");

  const [deliveryLocation, setDeliveryLocation] =
    useState(null);

  const [locationLoading, setLocationLoading] =
    useState(false);

  const [locationMessage, setLocationMessage] =
    useState("");

  const [submitting, setSubmitting] =
    useState(false);

  const [error, setError] = useState("");

  const [completedOrder, setCompletedOrder] =
    useState(null);

  const [priceQuote, setPriceQuote] =
    useState(null);

  useEffect(() => {
    if (!isOpen) {
      return undefined;
    }

    const token =
      sessionStorage.getItem("customerToken");

    if (!token) {
      return undefined;
    }

    let requestIsActive = true;

    fetch(PROFILE_API_URL, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            "Unable to load customer profile",
          );
        }

        return response.json();
      })
      .then((profile) => {
        if (requestIsActive) {
          setFormData({
            customerName:
              profile.fullName ?? "",
            phone: profile.phone ?? "",
            deliveryAddress:
              profile.defaultDeliveryAddress ??
              "",
          });
        }
      })
      .catch(() => {
        // Checkout remains usable without a saved profile.
      });

    return () => {
      requestIsActive = false;
    };
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen || cart.length === 0) {
      return undefined;
    }

    const token =
      sessionStorage.getItem("customerToken");

    if (!token) {
      return undefined;
    }

    let requestIsActive = true;

    fetch(ORDER_QUOTE_API_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        items: cart.map((item) => ({
          menuItemId: item.id,
          quantity: item.quantity,
        })),
      }),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            "Unable to calculate discount quote",
          );
        }

        return response.json();
      })
      .then((quote) => {
        if (requestIsActive) {
          setPriceQuote(quote);
        }
      })
      .catch(() => {
        if (requestIsActive) {
          setPriceQuote(null);
        }
      });

    return () => {
      requestIsActive = false;
    };
  }, [isOpen, cart]);

  if (!isOpen) {
    return null;
  }

  const subtotal = cart.reduce(
    (total, item) =>
      total +
      Number(item.price) * item.quantity,
    0,
  );

  const calculatedDeliveryFee =
    subtotal >= 500 ? 0 : 40;

  const discountAmount = Number(
    priceQuote?.discountAmount ?? 0,
  );

  const deliveryFee = Number(
    priceQuote?.deliveryFee ??
      calculatedDeliveryFee,
  );

  const finalTotal = Number(
    priceQuote?.totalAmount ??
      subtotal + deliveryFee,
  );

  function handleInputChange(event) {
    const { name, value } = event.target;

    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }));
  }

  function getCurrentLocation() {
    if (!navigator.geolocation) {
      setLocationMessage(
        "Location is not supported by this browser. Please enter your address manually.",
      );
      return;
    }

    setLocationLoading(true);
    setLocationMessage("");
    setError("");

    navigator.geolocation.getCurrentPosition(
      (position) => {
        setDeliveryLocation({
          latitude:
            position.coords.latitude,
          longitude:
            position.coords.longitude,
          accuracy:
            position.coords.accuracy,
        });

        setLocationMessage(
          "Your delivery location has been added successfully.",
        );

        setLocationLoading(false);
      },
      (locationError) => {
        setDeliveryLocation(null);

        if (
          locationError.code ===
          locationError.PERMISSION_DENIED
        ) {
          setLocationMessage(
            "Location permission was denied. You can allow it in your browser settings or continue using your written address.",
          );
        } else if (
          locationError.code ===
          locationError.POSITION_UNAVAILABLE
        ) {
          setLocationMessage(
            "Your location is currently unavailable. Please try again or use your written address.",
          );
        } else {
          setLocationMessage(
            "Location request timed out. Please try again or use your written address.",
          );
        }

        setLocationLoading(false);
      },
      {
        enableHighAccuracy: true,
        timeout: 15000,
        maximumAge: 0,
      },
    );
  }

  function removeCurrentLocation() {
    setDeliveryLocation(null);
    setLocationMessage(
      "GPS delivery location removed.",
    );
  }

  function waitForDemoPayment() {
    return new Promise((resolve) => {
      window.setTimeout(resolve, 1800);
    });
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const token =
      sessionStorage.getItem("customerToken");

    if (!token) {
      setError(
        "Please sign in as a customer before placing an order.",
      );
      return;
    }

    if (!/^[0-9]{10}$/.test(formData.phone)) {
      setError(
        "Please enter a valid 10-digit phone number.",
      );
      return;
    }

    if (cart.length === 0) {
      setError(
        "Your cart is empty. Please add a food item.",
      );
      return;
    }

    if (orderTiming === "SCHEDULED") {
      if (!scheduledFor) {
        setError(
          "Please select a future delivery date and time.",
        );
        return;
      }

      const selectedTime =
        new Date(scheduledFor).getTime();

      const minimumTime =
        Date.now() + 60 * 60 * 1000;

      if (selectedTime < minimumTime) {
        setError(
          "Scheduled orders require at least one hour of advance notice.",
        );
        return;
      }
    }

    const orderRequest = {
      customerName:
        formData.customerName.trim(),
      phone: formData.phone.trim(),
      deliveryAddress:
        formData.deliveryAddress.trim(),
      deliveryLatitude:
        deliveryLocation?.latitude ?? null,
      deliveryLongitude:
        deliveryLocation?.longitude ?? null,
      paymentMethod,
      orderTiming,
      mealSlot:
        orderTiming === "SCHEDULED"
          ? mealSlot
          : null,
      scheduledFor:
        orderTiming === "SCHEDULED"
          ? scheduledFor
          : null,
      items: cart.map((item) => ({
        menuItemId: item.id,
        quantity: item.quantity,
      })),
    };

    try {
      setSubmitting(true);
      setError("");

      if (
        paymentMethod === "DEMO_RAZORPAY"
      ) {
        await waitForDemoPayment();
      }

      const response = await fetch(
        ORDERS_API_URL,
        {
          method: "POST",
          headers: {
            "Content-Type":
              "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(orderRequest),
        },
      );

      if (!response.ok) {
        throw new Error(
          "Could not place the order",
        );
      }

      const savedOrder =
        await response.json();

      setCompletedOrder(savedOrder);
      onOrderPlaced();
    } catch {
      setError(
        "Your order could not be placed. Please check the backend and try again.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  function closeModal() {
    setFormData({
      customerName: "",
      phone: "",
      deliveryAddress: "",
    });

    setPaymentMethod(
      "CASH_ON_DELIVERY",
    );

    setDemoPaymentType("UPI");
    setOrderTiming("NOW");
    setMealSlot("LUNCH");
    setScheduledFor("");
    setDeliveryLocation(null);
    setLocationMessage("");
    setLocationLoading(false);
    setError("");
    setCompletedOrder(null);
    setPriceQuote(null);
    setSubmitting(false);

    onClose();
  }

  function paymentName(method) {
    if (method === "DEMO_RAZORPAY") {
      return "Demo Razorpay";
    }

    return "Cash on delivery";
  }

  function locationMapUrl(
    latitude,
    longitude,
  ) {
    return `https://www.google.com/maps?q=${latitude},${longitude}`;
  }

  return (
    <div className="modal-backdrop">
      <section className="checkout-modal">
        <button
          className="modal-close"
          type="button"
          onClick={closeModal}
          aria-label="Close checkout"
          disabled={submitting}
        >
          ×
        </button>

        {completedOrder ? (
          <div className="order-success">
            <span className="success-icon">
              ✓
            </span>

            <span className="eyebrow">
              Order confirmed
            </span>

            <h2>
              Thank you for your order!
            </h2>

            <p>
              Your order number is{" "}
              <strong>
                #{completedOrder.id}
              </strong>
              .
            </p>

            <div className="success-details">
              <div>
                <span>Order status</span>
                <strong>
                  {completedOrder.status}
                </strong>
              </div>

              <div>
                <span>Payment method</span>
                <strong>
                  {paymentName(
                    completedOrder.paymentMethod,
                  )}
                </strong>
              </div>

              <div>
                <span>Payment status</span>
                <strong>
                  {completedOrder.paymentStatus ===
                  "PAID"
                    ? "Paid (Demo)"
                    : "Pay on delivery"}
                </strong>
              </div>

              <div>
                <span>Total</span>
                <strong>
                  {formatPrice(
                    completedOrder.totalAmount,
                  )}
                </strong>
              </div>

              {Number(
                completedOrder.discountAmount ?? 0,
              ) > 0 && (
                <div>
                  <span>Discount saved</span>
                  <strong>
                    {formatPrice(
                      completedOrder.discountAmount,
                    )}
                  </strong>
                </div>
              )}

              <div>
                <span>Order timing</span>
                <strong>
                  {completedOrder.orderTiming ===
                  "SCHEDULED"
                    ? "Scheduled"
                    : "Order now"}
                </strong>
              </div>
            </div>

            {completedOrder.appliedDiscountNames && (
              <div className="applied-discount-message">
                🎉 Applied offer: {completedOrder.appliedDiscountNames}
              </div>
            )}

            {completedOrder.orderTiming ===
              "SCHEDULED" && (
              <div className="scheduled-order-confirmation">
                <span>
                  🗓️ Scheduled delivery
                </span>

                <strong>
                  {formatScheduledDate(
                    completedOrder.scheduledFor,
                  )}
                </strong>

                <small>
                  Meal: {completedOrder.mealSlot}
                </small>
              </div>
            )}

            {completedOrder.deliveryLatitude &&
              completedOrder.deliveryLongitude && (
                <a
                  className="checkout-map-link"
                  href={locationMapUrl(
                    completedOrder.deliveryLatitude,
                    completedOrder.deliveryLongitude,
                  )}
                  target="_blank"
                  rel="noreferrer"
                >
                  📍 View saved delivery location
                </a>
              )}

            {completedOrder.transactionId && (
              <div className="demo-transaction">
                <span>
                  Demo transaction ID
                </span>

                <strong>
                  {
                    completedOrder.transactionId
                  }
                </strong>
              </div>
            )}

            <button
              className="checkout-submit"
              type="button"
              onClick={closeModal}
            >
              Continue browsing
            </button>
          </div>
        ) : (
          <>
            <div className="checkout-heading">
              <span className="eyebrow">
                Complete your order
              </span>

              <h2>Checkout</h2>

              <p>
                Enter your delivery details,
                optionally add your GPS
                location and select a payment
                method.
              </p>
            </div>

            <div className="checkout-layout">
              <form
                className="checkout-form"
                onSubmit={handleSubmit}
              >
                <label>
                  Full name

                  <input
                    type="text"
                    name="customerName"
                    placeholder="Enter your full name"
                    value={
                      formData.customerName
                    }
                    onChange={
                      handleInputChange
                    }
                    maxLength="100"
                    required
                  />
                </label>

                <label>
                  Phone number

                  <input
                    type="tel"
                    name="phone"
                    placeholder="10-digit phone number"
                    value={formData.phone}
                    onChange={
                      handleInputChange
                    }
                    pattern="[0-9]{10}"
                    maxLength="10"
                    required
                  />
                </label>

                <label>
                  Delivery address

                  <textarea
                    name="deliveryAddress"
                    placeholder="House number, street, city and PIN code"
                    value={
                      formData.deliveryAddress
                    }
                    onChange={
                      handleInputChange
                    }
                    minLength="10"
                    maxLength="1000"
                    rows="4"
                    required
                  />
                </label>

                <div className="delivery-location-box">
                  <div className="delivery-location-heading">
                    <div>
                      <strong>
                        📍 GPS delivery location
                      </strong>

                      <small>
                        Optional — used only
                        to help locate your
                        delivery address.
                      </small>
                    </div>

                    {deliveryLocation && (
                      <span className="location-added-badge">
                        ✓ Added
                      </span>
                    )}
                  </div>

                  {!deliveryLocation ? (
                    <button
                      className="location-button"
                      type="button"
                      onClick={
                        getCurrentLocation
                      }
                      disabled={
                        locationLoading ||
                        submitting
                      }
                    >
                      {locationLoading
                        ? "Getting your location..."
                        : "Use my current location"}
                    </button>
                  ) : (
                    <div className="location-actions">
                      <a
                        href={locationMapUrl(
                          deliveryLocation.latitude,
                          deliveryLocation.longitude,
                        )}
                        target="_blank"
                        rel="noreferrer"
                      >
                        View on map
                      </a>

                      <button
                        type="button"
                        onClick={
                          removeCurrentLocation
                        }
                        disabled={submitting}
                      >
                        Remove location
                      </button>
                    </div>
                  )}

                  {locationMessage && (
                    <p
                      className={
                        deliveryLocation
                          ? "location-message location-success"
                          : "location-message"
                      }
                    >
                      {locationMessage}
                    </p>
                  )}
                </div>

                <div className="order-schedule-box">
                  <div className="order-schedule-heading">
                    <strong>
                      🗓️ When should we deliver?
                    </strong>

                    <small>
                      Order now or select a future time.
                    </small>
                  </div>

                  <div className="order-timing-options">
                    <button
                      className={
                        orderTiming === "NOW"
                          ? "order-timing-active"
                          : ""
                      }
                      type="button"
                      onClick={() => {
                        setOrderTiming("NOW");
                        setScheduledFor("");
                        setError("");
                      }}
                    >
                      Order now
                    </button>

                    <button
                      className={
                        orderTiming === "SCHEDULED"
                          ? "order-timing-active"
                          : ""
                      }
                      type="button"
                      onClick={() => {
                        setOrderTiming("SCHEDULED");
                        setError("");
                      }}
                    >
                      Schedule for later
                    </button>
                  </div>

                  {orderTiming === "SCHEDULED" && (
                    <div className="scheduled-order-fields">
                      <label>
                        Meal
                        <select
                          value={mealSlot}
                          onChange={(event) =>
                            setMealSlot(
                              event.target.value,
                            )
                          }
                        >
                          <option value="BREAKFAST">
                            Breakfast
                          </option>
                          <option value="LUNCH">
                            Lunch
                          </option>
                          <option value="DINNER">
                            Dinner
                          </option>
                        </select>
                      </label>

                      <label>
                        Delivery date and time
                        <input
                          type="datetime-local"
                          value={scheduledFor}
                          min={minimumScheduleValue()}
                          onChange={(event) =>
                            setScheduledFor(
                              event.target.value,
                            )
                          }
                          required
                        />
                      </label>

                      <p>
                        Please schedule at least one hour in advance.
                        The restaurant will begin preparation 30 minutes
                        before delivery.
                      </p>
                    </div>
                  )}
                </div>

                <div className="payment-heading">
                  <strong>
                    Select payment method
                  </strong>

                  <small>
                    Choose how you want to
                    pay
                  </small>
                </div>

                <button
                  className={`payment-choice ${
                    paymentMethod ===
                    "CASH_ON_DELIVERY"
                      ? "payment-choice-active"
                      : ""
                  }`}
                  type="button"
                  onClick={() =>
                    setPaymentMethod(
                      "CASH_ON_DELIVERY",
                    )
                  }
                >
                  <span className="payment-icon">
                    💵
                  </span>

                  <span>
                    <strong>
                      Cash on delivery
                    </strong>

                    <small>
                      Pay when your order
                      arrives
                    </small>
                  </span>

                  <span className="payment-check">
                    {paymentMethod ===
                    "CASH_ON_DELIVERY"
                      ? "✓"
                      : ""}
                  </span>
                </button>

                <button
                  className={`payment-choice ${
                    paymentMethod ===
                    "DEMO_RAZORPAY"
                      ? "payment-choice-active"
                      : ""
                  }`}
                  type="button"
                  onClick={() =>
                    setPaymentMethod(
                      "DEMO_RAZORPAY",
                    )
                  }
                >
                  <span className="payment-icon">
                    ⚡
                  </span>

                  <span>
                    <strong>
                      Razorpay Demo
                    </strong>

                    <small>
                      Simulated online
                      payment
                    </small>
                  </span>

                  <span className="payment-check">
                    {paymentMethod ===
                    "DEMO_RAZORPAY"
                      ? "✓"
                      : ""}
                  </span>
                </button>

                {paymentMethod ===
                  "DEMO_RAZORPAY" && (
                  <div className="razorpay-demo-box">
                    <div className="razorpay-demo-header">
                      <div>
                        <span className="razorpay-demo-logo">
                          R
                        </span>

                        <strong>
                          Razorpay Demo Checkout
                        </strong>
                      </div>

                      <span>
                        {formatPrice(
                          finalTotal,
                        )}
                      </span>
                    </div>

                    <p className="demo-warning">
                      Demo payment only — no
                      real money will be
                      charged.
                    </p>

                    <div className="demo-payment-types">
                      {[
                        "UPI",
                        "CARD",
                        "NET_BANKING",
                      ].map((type) => (
                        <button
                          className={
                            demoPaymentType ===
                            type
                              ? "demo-payment-type-active"
                              : ""
                          }
                          type="button"
                          key={type}
                          onClick={() =>
                            setDemoPaymentType(
                              type,
                            )
                          }
                        >
                          {type ===
                          "NET_BANKING"
                            ? "Net Banking"
                            : type === "CARD"
                              ? "Card"
                              : "UPI"}
                        </button>
                      ))}
                    </div>

                    <div className="demo-payment-message">
                      <span>🔒</span>

                      <div>
                        <strong>
                          {demoPaymentType ===
                          "UPI"
                            ? "Demo UPI payment"
                            : demoPaymentType ===
                                "CARD"
                              ? "Demo card payment"
                              : "Demo net banking payment"}
                        </strong>

                        <small>
                          No UPI PIN, card,
                          CVV, OTP or bank
                          password is required.
                        </small>
                      </div>
                    </div>
                  </div>
                )}

                {error && (
                  <p className="checkout-error">
                    {error}
                  </p>
                )}

                <button
                  className="checkout-submit"
                  type="submit"
                  disabled={submitting}
                >
                  {submitting
                    ? paymentMethod ===
                      "DEMO_RAZORPAY"
                      ? "Processing demo payment..."
                      : "Placing order..."
                    : paymentMethod ===
                        "DEMO_RAZORPAY"
                      ? `Pay demo · ${formatPrice(
                          finalTotal,
                        )}`
                      : `Place order · ${formatPrice(
                          finalTotal,
                        )}`}
                </button>
              </form>

              <aside className="order-summary">
                <h3>Order summary</h3>

                <div className="summary-items">
                  {cart.map((item) => (
                    <div key={item.id}>
                      <span>
                        {item.quantity} ×{" "}
                        {item.name}
                      </span>

                      <strong>
                        {formatPrice(
                          Number(
                            item.price,
                          ) *
                            item.quantity,
                        )}
                      </strong>
                    </div>
                  ))}
                </div>

                <div className="summary-price">
                  <div>
                    <span>Subtotal</span>

                    <strong>
                      {formatPrice(subtotal)}
                    </strong>
                  </div>

                  <div>
                    <span>
                      Delivery fee
                    </span>

                    <strong>
                      {deliveryFee === 0
                        ? "Free"
                        : formatPrice(
                            deliveryFee,
                          )}
                    </strong>
                  </div>

                  {discountAmount > 0 && (
                    <div className="summary-discount">
                      <span>
                        Discount
                        {priceQuote
                          ?.appliedDiscountNames
                          ? ` (${priceQuote.appliedDiscountNames})`
                          : ""}
                      </span>

                      <strong>
                        −{formatPrice(
                          discountAmount,
                        )}
                      </strong>
                    </div>
                  )}

                  <div className="summary-total">
                    <span>Total</span>

                    <strong>
                      {formatPrice(
                        finalTotal,
                      )}
                    </strong>
                  </div>
                </div>
              </aside>
            </div>
          </>
        )}
      </section>
    </div>
  );
}

export default CheckoutModal;