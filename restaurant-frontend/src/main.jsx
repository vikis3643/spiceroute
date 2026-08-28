import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";

import App from "./App.jsx";

import RestaurantPartnerRegistration
  from "./RestaurantPartnerRegistration.jsx";

import AdminDashboard
  from "./AdminDashboard.jsx";
import AdminLogin
  from "./AdminLogin.jsx";
import AdminChangePassword
  from "./AdminChangePassword.jsx";
import AdminOrders
  from "./AdminOrders.jsx";
import AdminMenu
  from "./AdminMenu.jsx";
import AdminReviews
  from "./AdminReviews.jsx";
import AdminSales
  from "./AdminSales.jsx";
import AdminProfile
  from "./AdminProfile.jsx";
import AdminDiscounts
  from "./AdminDiscounts.jsx";
import AdminSupport
  from "./AdminSupport.jsx";

import CustomerAuth
  from "./CustomerAuth.jsx";
import ResetPassword
  from "./ResetPassword.jsx";
import MyOrders
  from "./MyOrders.jsx";
import CustomerProfile
  from "./CustomerProfile.jsx";
import Wishlist
  from "./Wishlist.jsx";
import OrderReview
  from "./OrderReview.jsx";
import FoodRecommendation
  from "./FoodRecommendation.jsx";
import CustomerSupport
  from "./CustomerSupport.jsx";

import SuperAdminLogin
  from "./superadmin/pages/SuperAdminLogin.jsx";

import SuperAdminApp
  from "./superadmin/SuperAdminApp.jsx";

import {
  getSuperAdminToken,
} from "./services/superAdminApi.js";

// ==========================================
// HELPERS
// ==========================================

function formatPrice(price) {
  return new Intl.NumberFormat(
    "en-IN",
    {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }
  ).format(price);
}

function goTo(path) {
  window.location.href = path;
}

// ==========================================
// CURRENT PATH
// ==========================================

const currentPath =
  window.location.pathname;

// ==========================================
// TOKENS
// ==========================================

const adminToken =
  sessionStorage.getItem(
    "adminToken"
  );

const adminMustChangePassword =
  sessionStorage.getItem(
    "adminMustChangePassword"
  ) === "true";

const customerToken =
  sessionStorage.getItem(
    "customerToken"
  );

const superAdminToken =
  getSuperAdminToken();

// ==========================================
// PAGE GROUPS
// ==========================================

const isAdminPage =
  currentPath.startsWith(
    "/admin"
  );

const isSuperAdminPage =
  currentPath.startsWith(
    "/super-admin"
  );

const isProtectedCustomerPage =
  currentPath === "/my-orders" ||
  currentPath === "/customer-profile" ||
  currentPath === "/wishlist" ||
  currentPath === "/review" ||
  currentPath === "/recommend-food" ||
  currentPath === "/support";

// ==========================================
// PAGE ROUTING
// ==========================================

let pageContent;

// ==========================================
// RESTAURANT PARTNER REGISTRATION
// ==========================================

if (
  currentPath ===
  "/partner-with-us"
) {

  pageContent =
    <RestaurantPartnerRegistration />;

// ==========================================
// PASSWORD RESET
// ==========================================

} else if (
  currentPath ===
  "/reset-password"
) {

  pageContent =
    <ResetPassword />;

// ==========================================
// SUPER ADMIN LOGIN
// ==========================================

} else if (
  isSuperAdminPage &&
  !superAdminToken
) {

  pageContent = (
    <SuperAdminLogin
      onLoginSuccess={() =>
        goTo(
          "/super-admin/dashboard"
        )
      }
    />
  );

// ==========================================
// SUPER ADMIN PROTECTED AREA
// ==========================================

} else if (
  isSuperAdminPage &&
  superAdminToken
) {

  pageContent =
    <SuperAdminApp />;

// ==========================================
// PROTECTED CUSTOMER PAGE WITHOUT TOKEN
// ==========================================

} else if (
  isProtectedCustomerPage &&
  !customerToken
) {

  pageContent = (
    <CustomerAuth
      onSuccess={() =>
        goTo(
          currentPath
        )
      }
    />
  );

// ==========================================
// CUSTOMER ORDERS
// ==========================================

} else if (
  currentPath ===
  "/my-orders"
) {

  pageContent = (
    <MyOrders
      formatPrice={
        formatPrice
      }
      onBack={() =>
        goTo("/")
      }
    />
  );

// ==========================================
// CUSTOMER PROFILE
// ==========================================

} else if (
  currentPath ===
  "/customer-profile"
) {

  pageContent = (
    <CustomerProfile
      onBack={() =>
        goTo("/")
      }
      onOrders={() =>
        goTo(
          "/my-orders"
        )
      }
    />
  );

// ==========================================
// CUSTOMER SUPPORT
// ==========================================

} else if (
  currentPath ===
  "/support"
) {

  pageContent = (
    <CustomerSupport
      onBack={() =>
        goTo("/")
      }
    />
  );

// ==========================================
// CUSTOMER WISHLIST
// ==========================================

} else if (
  currentPath ===
  "/wishlist"
) {

  pageContent = (
    <Wishlist
      formatPrice={
        formatPrice
      }
      onBack={() =>
        goTo("/")
      }
    />
  );

// ==========================================
// CUSTOMER REVIEW
// ==========================================

} else if (
  currentPath ===
  "/review"
) {

  const orderId =
    new URLSearchParams(
      window.location.search
    ).get(
      "orderId"
    );

  pageContent = (
    <OrderReview
      orderId={orderId}
      onBack={() =>
        goTo(
          "/my-orders"
        )
      }
    />
  );

// ==========================================
// FOOD RECOMMENDATION
// ==========================================

} else if (
  currentPath ===
  "/recommend-food"
) {

  pageContent = (
    <FoodRecommendation
      formatPrice={
        formatPrice
      }
      onBack={() =>
        goTo("/")
      }
    />
  );

// ==========================================
// CUSTOMER LOGIN
// ==========================================

} else if (
  currentPath ===
  "/customer-login"
) {

  pageContent = (
    <CustomerAuth
      onSuccess={() =>
        goTo("/")
      }
    />
  );

// ==========================================
// RESTAURANT ADMIN LOGIN
// ==========================================

} else if (
  isAdminPage &&
  !adminToken
) {

  pageContent = (
    <AdminLogin
      onLogin={() =>
        goTo("/admin")
      }
    />
  );

// ==========================================
// RESTAURANT ADMIN MUST CHANGE PASSWORD
// ==========================================

} else if (
  isAdminPage &&
  adminToken &&
  adminMustChangePassword &&
  currentPath !==
    "/admin/change-password"
) {

  window.location.replace(
    "/admin/change-password"
  );

  pageContent = null;

// ==========================================
// RESTAURANT ADMIN CHANGE PASSWORD
// ==========================================

} else if (
  currentPath ===
  "/admin/change-password"
) {

  pageContent =
    <AdminChangePassword />;

// ==========================================
// RESTAURANT ADMIN SUPPORT
// ==========================================

} else if (
  currentPath ===
  "/admin/support"
) {

  pageContent = (
    <AdminSupport
      onBack={() =>
        goTo("/")
      }
      onOrders={() =>
        goTo(
          "/admin"
        )
      }
      onMenu={() =>
        goTo(
          "/admin/menu"
        )
      }
    />
  );

// ==========================================
// RESTAURANT ADMIN REVIEWS
// ==========================================

} else if (
  currentPath ===
  "/admin/reviews"
) {

  pageContent =
    <AdminReviews />;

// ==========================================
// RESTAURANT ADMIN SALES
// ==========================================

} else if (
  currentPath ===
  "/admin/sales"
) {

  pageContent = (
    <AdminSales
      formatPrice={
        formatPrice
      }
    />
  );

// ==========================================
// RESTAURANT ADMIN PROFILE
// ==========================================

} else if (
  currentPath ===
  "/admin/profile"
) {

  pageContent =
    <AdminProfile />;

// ==========================================
// RESTAURANT ADMIN DISCOUNTS
// ==========================================

} else if (
  currentPath ===
  "/admin/discounts"
) {

  pageContent = (
    <AdminDiscounts
      formatPrice={
        formatPrice
      }
      onBack={() =>
        goTo("/")
      }
      onOrders={() =>
        goTo(
          "/admin"
        )
      }
      onMenu={() =>
        goTo(
          "/admin/menu"
        )
      }
    />
  );

// ==========================================
// RESTAURANT ADMIN MENU
// ==========================================

} else if (
  currentPath ===
  "/admin/menu"
) {

  pageContent = (
    <AdminMenu
      formatPrice={
        formatPrice
      }
      onBack={() =>
        goTo("/")
      }
      onOrders={() =>
        goTo(
          "/admin"
        )
      }
    />
  );

// ==========================================
// RESTAURANT ADMIN ORDERS
// ==========================================

} else if (
  currentPath ===
  "/admin/orders"
) {

  pageContent = (
    <AdminOrders
      formatPrice={
        formatPrice
      }
      onBack={() =>
        goTo(
          "/admin"
        )
      }
    />
  );

// ==========================================
// RESTAURANT ADMIN DASHBOARD
// ==========================================

} else if (
  currentPath ===
  "/admin"
) {

  pageContent = (
    <AdminDashboard
      formatPrice={
        formatPrice
      }
    />
  );

// ==========================================
// CUSTOMER HOME
// ==========================================

} else {

  pageContent =
    <App />;
}

// ==========================================
// RENDER
// ==========================================

createRoot(
  document.getElementById(
    "root"
  )
).render(
  <StrictMode>
    {pageContent}
  </StrictMode>
);