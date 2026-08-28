function AdminNav({
  activePage,
}) {
  const navItems = [
    {
      label: "Dashboard",
      path: "/admin",
      key: "dashboard",
    },
    {
      label: "Orders",
      path: "/admin/orders",
      key: "orders",
    },
    {
      label: "Menu",
      path: "/admin/menu",
      key: "menu",
    },
    {
      label: "Discounts",
      path: "/admin/discounts",
      key: "discounts",
    },
    {
      label: "Reviews",
      path: "/admin/reviews",
      key: "reviews",
    },
    {
      label: "Sales",
      path: "/admin/sales",
      key: "sales",
    },
    {
      label: "Support",
      path: "/admin/support",
      key: "support",
    },
    {
      label: "Profile",
      path: "/admin/profile",
      key: "profile",
    },
  ];

  function goTo(path) {
    window.location.href = path;
  }

  function logout() {
    sessionStorage.removeItem(
      "adminToken",
    );

    sessionStorage.removeItem(
      "adminEmail",
    );

    window.location.href = "/admin";
  }

  return (
    <nav className="admin-main-nav">
      <div className="admin-main-nav-links">
        {navItems.map(
          (item) => (
            <button
              key={item.key}
              type="button"
              className={
                activePage ===
                item.key
                  ? "admin-nav-button active"
                  : "admin-nav-button"
              }
              onClick={() =>
                goTo(item.path)
              }
            >
              {item.label}
            </button>
          ),
        )}
      </div>

      <button
        type="button"
        className="admin-nav-logout"
        onClick={logout}
      >
        Logout
      </button>
    </nav>
  );
}

export default AdminNav;