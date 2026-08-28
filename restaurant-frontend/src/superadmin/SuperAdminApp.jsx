import "./superAdmin.css";

import SuperAdminLayout
  from "./components/SuperAdminLayout";

import SuperAdminDashboard
  from "./pages/SuperAdminDashboard";

import SuperAdminRestaurants
  from "./pages/SuperAdminRestaurants";

import SuperAdminRestaurantAdmins
  from "./pages/SuperAdminRestaurantAdmins";

import SuperAdminCustomers
  from "./pages/SuperAdminCustomers";

import SuperAdminOrders
  from "./pages/SuperAdminOrders";

import SuperAdminPayments
  from "./pages/SuperAdminPayments";

import SuperAdminDeliveryPartners
  from "./pages/SuperAdminDeliveryPartners";

import SuperAdminDeliveryAssignments
  from "./pages/SuperAdminDeliveryAssignments";

import SuperAdminSupport
  from "./pages/SuperAdminSupport";

import SuperAdminReports
  from "./pages/SuperAdminReports";

import SuperAdminSettings
  from "./pages/SuperAdminSettings";

function SuperAdminApp() {
  const currentPath =
    window.location.pathname;

  // ==========================================
  // ACTIVE PAGE
  // ==========================================

  const getActivePage = () => {
    if (
      currentPath === "/super-admin" ||
      currentPath === "/super-admin/" ||
      currentPath === "/super-admin/dashboard"
    ) {
      return "dashboard";
    }

    if (
      currentPath.startsWith(
        "/super-admin/restaurants"
      )
    ) {
      return "restaurants";
    }

    if (
      currentPath.startsWith(
        "/super-admin/restaurant-admins"
      )
    ) {
      return "restaurant-admins";
    }

    if (
      currentPath.startsWith(
        "/super-admin/customers"
      )
    ) {
      return "customers";
    }

    if (
      currentPath.startsWith(
        "/super-admin/orders"
      )
    ) {
      return "orders";
    }

    if (
      currentPath.startsWith(
        "/super-admin/payments"
      )
    ) {
      return "payments";
    }

    if (
      currentPath.startsWith(
        "/super-admin/delivery-partners"
      )
    ) {
      return "delivery-partners";
    }

    if (
      currentPath.startsWith(
        "/super-admin/delivery-assignments"
      )
    ) {
      return "delivery-assignments";
    }

    if (
      currentPath.startsWith(
        "/super-admin/support"
      )
    ) {
      return "support";
    }

    if (
      currentPath.startsWith(
        "/super-admin/reports"
      )
    ) {
      return "reports";
    }

    if (
      currentPath.startsWith(
        "/super-admin/settings"
      )
    ) {
      return "settings";
    }

    return "dashboard";
  };

  const activePage =
    getActivePage();

  // ==========================================
  // NAVIGATION
  // ==========================================

  const handleNavigate = (
    page
  ) => {
    const routes = {
      dashboard:
        "/super-admin/dashboard",

      restaurants:
        "/super-admin/restaurants",

      "restaurant-admins":
        "/super-admin/restaurant-admins",

      customers:
        "/super-admin/customers",

      orders:
        "/super-admin/orders",

      payments:
        "/super-admin/payments",

      "delivery-partners":
        "/super-admin/delivery-partners",

      "delivery-assignments":
        "/super-admin/delivery-assignments",

      support:
        "/super-admin/support",

      reports:
        "/super-admin/reports",

      settings:
        "/super-admin/settings",
    };

    const destination =
      routes[page];

    if (destination) {
      window.location.href =
        destination;
    }
  };

  // ==========================================
  // LOGOUT
  // ==========================================

  const handleLogout = () => {
    window.location.href =
      "/super-admin";
  };

  // ==========================================
  // PAGE CONTENT
  // ==========================================

  let pageContent;

  switch (activePage) {
    case "dashboard":
      pageContent =
        <SuperAdminDashboard />;
      break;

    case "restaurants":
      pageContent =
        <SuperAdminRestaurants />;
      break;

    case "restaurant-admins":
      pageContent =
        <SuperAdminRestaurantAdmins />;
      break;

    case "customers":
      pageContent =
        <SuperAdminCustomers />;
      break;

    case "orders":
      pageContent =
        <SuperAdminOrders />;
      break;

    case "payments":
      pageContent =
        <SuperAdminPayments />;
      break;

    case "delivery-partners":
      pageContent =
        <SuperAdminDeliveryPartners />;
      break;

    case "delivery-assignments":
      pageContent =
        <SuperAdminDeliveryAssignments />;
      break;

    case "support":
      pageContent =
        <SuperAdminSupport />;
      break;

    case "reports":
      pageContent =
        <SuperAdminReports />;
      break;

    case "settings":
      pageContent =
        <SuperAdminSettings />;
      break;

    default:
      pageContent =
        <SuperAdminDashboard />;
  }

  // ==========================================
  // RENDER
  // ==========================================

  return (
    <SuperAdminLayout
      activePage={activePage}
      onNavigate={handleNavigate}
      onLogout={handleLogout}
    >
      {pageContent}
    </SuperAdminLayout>
  );
}

export default SuperAdminApp;