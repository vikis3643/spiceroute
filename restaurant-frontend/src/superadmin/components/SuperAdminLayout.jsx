import {
  clearSuperAdminToken,
} from "../../services/superAdminApi";

function SuperAdminLayout({
  activePage,
  onNavigate,
  onLogout,
  children,
}) {
  const menuItems = [
    {
      key: "dashboard",
      label: "Dashboard",
    },
    {
      key: "restaurants",
      label: "Restaurants",
    },
    {
      key: "restaurant-admins",
      label: "Restaurant Admins",
    },
    {
      key: "customers",
      label: "Customers",
    },
    {
      key: "orders",
      label: "Orders",
    },
    {
      key: "payments",
      label: "Payments & Earnings",
    },
    {
      key: "delivery-partners",
      label: "Delivery Partners",
    },
    {
      key: "delivery-assignments",
      label: "Delivery Assignments",
    },
    {
      key: "support",
      label: "Support",
    },
    {
      key: "reports",
      label: "Reports",
    },
    {
      key: "settings",
      label: "Platform Settings",
    },
  ];

  const handleLogout = () => {
    clearSuperAdminToken();

    if (onLogout) {
      onLogout();
    }
  };

  return (
    <div className="super-admin-layout">
      <aside className="super-admin-sidebar">
        <div className="super-admin-brand">
          <h2>SpiceRoute</h2>
          <span>Super Admin</span>
        </div>

        <nav className="super-admin-nav">
          {menuItems.map((item) => (
            <button
              key={item.key}
              type="button"
              className={
                activePage === item.key
                  ? "super-admin-nav-item active"
                  : "super-admin-nav-item"
              }
              onClick={() =>
                onNavigate?.(item.key)
              }
            >
              {item.label}
            </button>
          ))}
        </nav>

        <div className="super-admin-sidebar-footer">
          <button
            type="button"
            className="super-admin-logout-button"
            onClick={handleLogout}
          >
            Logout
          </button>
        </div>
      </aside>

      <main className="super-admin-main">
        {children}
      </main>
    </div>
  );
}

export default SuperAdminLayout;