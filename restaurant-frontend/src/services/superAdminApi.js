const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const TOKEN_KEY = "superAdminToken";

// ==========================================
// TOKEN HELPERS
// ==========================================

export function getSuperAdminToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setSuperAdminToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearSuperAdminToken() {
  localStorage.removeItem(TOKEN_KEY);
}

// ==========================================
// BASE REQUEST
// ==========================================

async function request(
  endpoint,
  {
    method = "GET",
    body = null,
    auth = true,
    headers = {},
  } = {}
) {
  const requestHeaders = {
    "Content-Type": "application/json",
    ...headers,
  };

  if (auth) {
    const token = getSuperAdminToken();

    if (token) {
      requestHeaders.Authorization = `Bearer ${token}`;
    }
  }

  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      method,
      headers: requestHeaders,
      body:
        body === null
          ? null
          : JSON.stringify(body),
    }
  );

  if (response.status === 204) {
    return null;
  }

  let data = null;

  try {
    data = await response.json();
  } catch {
    data = null;
  }

  if (!response.ok) {
    const message =
      data?.message ||
      data?.detail ||
      data?.error ||
      `Request failed with status ${response.status}`;

    const error = new Error(message);
    error.status = response.status;
    error.data = data;

    if (response.status === 401) {
      clearSuperAdminToken();
    }

    throw error;
  }

  return data;
}

// ==========================================
// AUTH
// ==========================================

export async function superAdminLogin(
  email,
  password
) {
  const response = await request(
    "/super-admin/auth/login",
    {
      method: "POST",
      auth: false,
      body: {
        email,
        password,
      },
    }
  );

  if (response?.token) {
    setSuperAdminToken(response.token);
  }

  return response;
}

// ==========================================
// DASHBOARD
// ==========================================

export function getSuperAdminDashboard() {
  return request(
    "/super-admin/dashboard/summary"
  );
}

// ==========================================
// RESTAURANTS
// ==========================================

export function getRestaurants(status = null) {
  const query = status
    ? `?status=${encodeURIComponent(status)}`
    : "";

  return request(
    `/super-admin/restaurants${query}`
  );
}

export function getRestaurant(restaurantId) {
  return request(
    `/super-admin/restaurants/${restaurantId}`
  );
}

export function updateRestaurantApproval(
  restaurantId,
  approvalStatus
) {
  return request(
    `/super-admin/restaurants/${restaurantId}/approval`,
    {
      method: "PATCH",
      body: {
        approvalStatus,
      },
    }
  );
}

export function updateRestaurantActive(
  restaurantId,
  active
) {
  return request(
    `/super-admin/restaurants/${restaurantId}/active`,
    {
      method: "PATCH",
      body: {
        active,
      },
    }
  );
}

export function updateRestaurantCommission(
  restaurantId,
  commissionPercentage
) {
  return request(
    `/super-admin/restaurants/${restaurantId}/commission`,
    {
      method: "PATCH",
      body: {
        commissionPercentage,
      },
    }
  );
}
// ==========================================
// RESTAURANT ADMINS
// ==========================================

export function getRestaurantAdmins(
  restaurantId = null
) {
  const query =
    restaurantId !== null &&
    restaurantId !== ""
      ? `?restaurantId=${encodeURIComponent(
          restaurantId
        )}`
      : "";

  return request(
    `/super-admin/restaurant-admins${query}`
  );
}

export function getRestaurantAdmin(
  adminId
) {
  return request(
    `/super-admin/restaurant-admins/${adminId}`
  );
}

export function createRestaurantAdmin(
  {
    restaurantId,
    fullName,
    email,
    password,
  }
) {
  return request(
    "/super-admin/restaurant-admins",
    {
      method: "POST",
      body: {
        restaurantId,
        fullName,
        email,
        password,
      },
    }
  );
}

export function updateRestaurantAdmin(
  adminId,
  {
    fullName,
    email,
  }
) {
  return request(
    `/super-admin/restaurant-admins/${adminId}`,
    {
      method: "PUT",
      body: {
        fullName,
        email,
      },
    }
  );
}

export function updateRestaurantAdminActive(
  adminId,
  active
) {
  return request(
    `/super-admin/restaurant-admins/${adminId}/active`,
    {
      method: "PATCH",
      body: {
        active,
      },
    }
  );
}

export function resetRestaurantAdminPassword(
  adminId,
  newPassword
) {
  return request(
    `/super-admin/restaurant-admins/${adminId}/password`,
    {
      method: "PATCH",
      body: {
        newPassword,
      },
    }
  );
}

// ==========================================
// CUSTOMERS
// ==========================================

export function getCustomers() {
  return request(
    "/super-admin/customers"
  );
}

export function getCustomer(customerId) {
  return request(
    `/super-admin/customers/${customerId}`
  );
}

export function getCustomerOrders(customerId) {
  return request(
    `/super-admin/customers/${customerId}/orders`
  );
}

export function updateCustomerActive(
  customerId,
  active
) {
  return request(
    `/super-admin/customers/${customerId}/active`,
    {
      method: "PATCH",
      body: {
        active,
      },
    }
  );
}

// ==========================================
// ORDERS
// ==========================================

export function getSuperAdminOrders(filters = {}) {
  const params =
    new URLSearchParams();

  if (filters.restaurantId) {
    params.set(
      "restaurantId",
      filters.restaurantId
    );
  }

  if (filters.status) {
    params.set(
      "status",
      filters.status
    );
  }

  if (filters.paymentMethod) {
    params.set(
      "paymentMethod",
      filters.paymentMethod
    );
  }

  if (filters.paymentStatus) {
    params.set(
      "paymentStatus",
      filters.paymentStatus
    );
  }

  const query =
    params.toString()
      ? `?${params.toString()}`
      : "";

  return request(
    `/super-admin/orders${query}`
  );
}

export function getSuperAdminOrder(orderId) {
  return request(
    `/super-admin/orders/${orderId}`
  );
}

export function getPaymentSummary() {
  return request(
    "/super-admin/orders/payments/summary"
  );
}

// ==========================================
// EARNINGS
// ==========================================

export function getPlatformEarnings() {
  return request(
    "/super-admin/earnings"
  );
}

export function getEarningsByRange(
  startDate,
  endDate
) {
  return request(
    `/super-admin/earnings/range?startDate=${startDate}&endDate=${endDate}`
  );
}

// ==========================================
// DELIVERY PARTNERS
// ==========================================

export function getDeliveryPartners(
  status = null
) {
  const query = status
    ? `?status=${encodeURIComponent(status)}`
    : "";

  return request(
    `/super-admin/delivery-partners${query}`
  );
}

export function getAvailableDeliveryPartners() {
  return request(
    "/super-admin/delivery-partners/available"
  );
}

export function getDeliveryPartner(partnerId) {
  return request(
    `/super-admin/delivery-partners/${partnerId}`
  );
}

export function createDeliveryPartner(body) {
  return request(
    "/super-admin/delivery-partners",
    {
      method: "POST",
      body,
    }
  );
}

export function updateDeliveryPartner(
  partnerId,
  body
) {
  return request(
    `/super-admin/delivery-partners/${partnerId}`,
    {
      method: "PUT",
      body,
    }
  );
}

export function updateDeliveryPartnerActive(
  partnerId,
  active
) {
  return request(
    `/super-admin/delivery-partners/${partnerId}/active`,
    {
      method: "PATCH",
      body: {
        active,
      },
    }
  );
}

export function updateDeliveryPartnerStatus(
  partnerId,
  status
) {
  return request(
    `/super-admin/delivery-partners/${partnerId}/status`,
    {
      method: "PATCH",
      body: {
        status,
      },
    }
  );
}

// ==========================================
// DELIVERY ASSIGNMENTS
// ==========================================

export function getDeliveryAssignments() {
  return request(
    "/super-admin/delivery-assignments"
  );
}

export function getDeliveryAssignment(
  assignmentId
) {
  return request(
    `/super-admin/delivery-assignments/${assignmentId}`
  );
}

export function getDeliveryAssignmentByOrder(
  orderId
) {
  return request(
    `/super-admin/delivery-assignments/order/${orderId}`
  );
}

export function createDeliveryAssignment(
  orderId,
  deliveryPartnerId
) {
  return request(
    "/super-admin/delivery-assignments",
    {
      method: "POST",
      body: {
        orderId,
        deliveryPartnerId,
      },
    }
  );
}

export function updateDeliveryAssignmentStatus(
  assignmentId,
  status
) {
  return request(
    `/super-admin/delivery-assignments/${assignmentId}/status`,
    {
      method: "PATCH",
      body: {
        status,
      },
    }
  );
}

// ==========================================
// SUPPORT / COMPLAINTS
// ==========================================

export function getSupportTickets(
  filters = {}
) {
  const params =
    new URLSearchParams();

  if (filters.restaurantId) {
    params.set(
      "restaurantId",
      filters.restaurantId
    );
  }

  if (filters.status) {
    params.set(
      "status",
      filters.status
    );
  }

  if (filters.priority) {
    params.set(
      "priority",
      filters.priority
    );
  }

  const query =
    params.toString()
      ? `?${params.toString()}`
      : "";

  return request(
    `/super-admin/support/tickets${query}`
  );
}

export function getSupportTicket(ticketId) {
  return request(
    `/super-admin/support/tickets/${ticketId}`
  );
}

export function updateSupportTicketStatus(
  ticketId,
  status
) {
  return request(
    `/super-admin/support/tickets/${ticketId}/status`,
    {
      method: "PATCH",
      body: {
        status,
      },
    }
  );
}

// ==========================================
// REPORTS
// ==========================================

export function getSuperAdminReport(
  startDate,
  endDate
) {
  return request(
    `/super-admin/reports?startDate=${startDate}&endDate=${endDate}`
  );
}

// ==========================================
// PLATFORM SETTINGS
// ==========================================

export function getPlatformSettings() {
  return request(
    "/super-admin/settings"
  );
}

export function updatePlatformSettings(body) {
  return request(
    "/super-admin/settings",
    {
      method: "PUT",
      body,
    }
  );
}
