import { useEffect, useState } from "react";
import AdminNav from "./AdminNav.jsx";

const API_BASE_URL =
  `${import.meta.env.VITE_API_BASE_URL}/restaurant-admin`;

const EMPTY_FORM = {
  name: "",
  description: "",
  price: "",
  imageUrl: "",
  vegetarian: true,
  available: true,
  spiceLevel: "MEDIUM",
  tasteType: "SAVOURY",
  proteinLevel: "NORMAL",
  categoryId: "",
};

function getAuthorizationHeaders(
  includeJson = false,
) {
  const token =
    sessionStorage.getItem(
      "adminToken",
    );

  const headers = {
    Authorization: `Bearer ${token}`,
  };

  if (includeJson) {
    headers["Content-Type"] =
      "application/json";
  }

  return headers;
}

function handleUnauthorizedResponse(
  response,
) {
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

    window.location.href =
      "/admin";

    throw new Error(
      "Admin session expired",
    );
  }
}

async function requestMenuData() {
  const headers =
    getAuthorizationHeaders();

  const [
    categoryResponse,
    menuResponse,
  ] = await Promise.all([
    fetch(
      `${API_BASE_URL}/categories`,
      {
        headers,
      },
    ),

    fetch(
      `${API_BASE_URL}/menu-items`,
      {
        headers,
      },
    ),
  ]);

  handleUnauthorizedResponse(
    categoryResponse,
  );

  handleUnauthorizedResponse(
    menuResponse,
  );

  if (
    !categoryResponse.ok ||
    !menuResponse.ok
  ) {
    throw new Error(
      "Unable to load menu data",
    );
  }

  return {
    categories:
      await categoryResponse.json(),

    menuItems:
      await menuResponse.json(),
  };
}

function AdminMenu({
  formatPrice,
}) {
  const [categories, setCategories] =
    useState([]);

  const [menuItems, setMenuItems] =
    useState([]);

  const [formData, setFormData] =
    useState(EMPTY_FORM);

  const [
    editingItemId,
    setEditingItemId,
  ] = useState(null);

  const [
    newCategoryName,
    setNewCategoryName,
  ] = useState("");

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  const [
    successMessage,
    setSuccessMessage,
  ] = useState("");

  useEffect(() => {
    let requestIsActive = true;

    requestMenuData()
      .then((data) => {
        if (requestIsActive) {
          setCategories(
            data.categories,
          );

          setMenuItems(
            data.menuItems,
          );

          setError("");
        }
      })
      .catch(() => {
        if (requestIsActive) {
          setError(
            "Could not load menu data. Make sure Spring Boot is running.",
          );
        }
      })
      .finally(() => {
        if (requestIsActive) {
          setLoading(false);
        }
      });

    return () => {
      requestIsActive = false;
    };
  }, []);

  async function refreshMenuData() {
    try {
      const data =
        await requestMenuData();

      setCategories(
        data.categories,
      );

      setMenuItems(
        data.menuItems,
      );

      setError("");
    } catch {
      setError(
        "Could not refresh menu data.",
      );
    }
  }

  function handleInputChange(
    event,
  ) {
    const {
      name,
      value,
      type,
      checked,
    } = event.target;

    setFormData(
      (currentData) => ({
        ...currentData,

        [name]:
          type === "checkbox"
            ? checked
            : value,
      }),
    );
  }

  function startEditing(
    menuItem,
  ) {
    setEditingItemId(
      menuItem.id,
    );

    setFormData({
      name:
        menuItem.name,

      description:
        menuItem.description ??
        "",

      price:
        String(
          menuItem.price,
        ),

      imageUrl:
        menuItem.imageUrl ??
        "",

      vegetarian:
        menuItem.vegetarian,

      available:
        menuItem.available,

      spiceLevel:
        menuItem.spiceLevel ??
        "MEDIUM",

      tasteType:
        menuItem.tasteType ??
        "SAVOURY",

      proteinLevel:
        menuItem.proteinLevel ??
        "NORMAL",

      categoryId:
        String(
          menuItem.category?.id ??
            "",
        ),
    });

    setSuccessMessage("");
    setError("");

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }

  function resetForm() {
    setEditingItemId(null);
    setFormData(EMPTY_FORM);
  }

  async function saveMenuItem(
    event,
  ) {
    event.preventDefault();

    if (!formData.categoryId) {
      setError(
        "Please select a category.",
      );

      return;
    }

    const requestBody = {
      name:
        formData.name,

      description:
        formData.description,

      price:
        Number(
          formData.price,
        ),

      imageUrl:
        formData.imageUrl,

      vegetarian:
        formData.vegetarian,

      available:
        formData.available,

      spiceLevel:
        formData.spiceLevel,

      tasteType:
        formData.tasteType,

      proteinLevel:
        formData.proteinLevel,

      category: {
        id: Number(
          formData.categoryId,
        ),
      },
    };

    const requestUrl =
      editingItemId
        ? `${API_BASE_URL}/menu-items/${editingItemId}`
        : `${API_BASE_URL}/menu-items`;

    try {
      setSaving(true);
      setError("");
      setSuccessMessage("");

      const response =
        await fetch(
          requestUrl,
          {
            method:
              editingItemId
                ? "PUT"
                : "POST",

            headers:
              getAuthorizationHeaders(
                true,
              ),

            body:
              JSON.stringify(
                requestBody,
              ),
          },
        );

      handleUnauthorizedResponse(
        response,
      );

      if (!response.ok) {
        throw new Error(
          "Unable to save menu item",
        );
      }

      setSuccessMessage(
        editingItemId
          ? "Menu item updated successfully."
          : "Menu item added successfully.",
      );

      resetForm();

      await refreshMenuData();
    } catch {
      setError(
        "The menu item could not be saved. Check all fields and try again.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function deleteMenuItem(
    menuItem,
  ) {
    const confirmed =
      window.confirm(
        `Delete "${menuItem.name}" from the menu?`,
      );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccessMessage("");

      const response =
        await fetch(
          `${API_BASE_URL}/menu-items/${menuItem.id}`,
          {
            method:
              "DELETE",

            headers:
              getAuthorizationHeaders(),
          },
        );

      handleUnauthorizedResponse(
        response,
      );

      if (!response.ok) {
        throw new Error(
          "Unable to delete menu item",
        );
      }

      setSuccessMessage(
        "Menu item deleted successfully.",
      );

      await refreshMenuData();
    } catch {
      setError(
        "The menu item could not be deleted.",
      );
    }
  }

  async function toggleAvailability(
    menuItem,
  ) {
    const requestBody = {
      name:
        menuItem.name,

      description:
        menuItem.description,

      price:
        Number(
          menuItem.price,
        ),

      imageUrl:
        menuItem.imageUrl,

      vegetarian:
        menuItem.vegetarian,

      available:
        !menuItem.available,

      spiceLevel:
        menuItem.spiceLevel ??
        "MEDIUM",

      tasteType:
        menuItem.tasteType ??
        "SAVOURY",

      proteinLevel:
        menuItem.proteinLevel ??
        "NORMAL",

      category: {
        id:
          menuItem.category.id,
      },
    };

    try {
      setError("");

      const response =
        await fetch(
          `${API_BASE_URL}/menu-items/${menuItem.id}`,
          {
            method: "PUT",

            headers:
              getAuthorizationHeaders(
                true,
              ),

            body:
              JSON.stringify(
                requestBody,
              ),
          },
        );

      handleUnauthorizedResponse(
        response,
      );

      if (!response.ok) {
        throw new Error(
          "Unable to update availability",
        );
      }

      await refreshMenuData();
    } catch {
      setError(
        "Availability could not be updated.",
      );
    }
  }

  async function addCategory(
    event,
  ) {
    event.preventDefault();

    try {
      setError("");
      setSuccessMessage("");

      const response =
        await fetch(
          `${API_BASE_URL}/categories`,
          {
            method: "POST",

            headers:
              getAuthorizationHeaders(
                true,
              ),

            body:
              JSON.stringify({
                name:
                  newCategoryName,

                description:
                  "",

                active:
                  true,
              }),
          },
        );

      handleUnauthorizedResponse(
        response,
      );

      if (!response.ok) {
        throw new Error(
          "Unable to create category",
        );
      }

      setNewCategoryName("");

      setSuccessMessage(
        "Category added successfully.",
      );

      await refreshMenuData();
    } catch {
      setError(
        "The category could not be added. It may already exist.",
      );
    }
  }

  return (
    <div className="admin-page">
      <AdminNav
        activePage="menu"
      />

      <header className="admin-header">
        <div>
          <span className="eyebrow">
            Restaurant management
          </span>

          <h1>
            Menu Management
          </h1>

          <p>
            Add dishes, update
            prices and control
            availability.
          </p>
        </div>
      </header>

      <main className="menu-admin-content">
        {error && (
          <div className="status-message error-message">
            {error}
          </div>
        )}

        {successMessage && (
          <div className="admin-success-message">
            {
              successMessage
            }
          </div>
        )}

        <section className="menu-admin-layout">
          <div className="menu-form-card">
            <span className="eyebrow">
              {editingItemId
                ? "Edit dish"
                : "New dish"}
            </span>

            <h2>
              {editingItemId
                ? "Update menu item"
                : "Add menu item"}
            </h2>

            <form
              className="menu-admin-form"
              onSubmit={
                saveMenuItem
              }
            >
              <label>
                Food name

                <input
                  type="text"
                  name="name"
                  value={
                    formData.name
                  }
                  onChange={
                    handleInputChange
                  }
                  minLength="2"
                  maxLength="150"
                  required
                />
              </label>

              <label>
                Description

                <textarea
                  name="description"
                  value={
                    formData.description
                  }
                  onChange={
                    handleInputChange
                  }
                  rows="4"
                  maxLength="1000"
                />
              </label>

              <div className="form-row">
                <label>
                  Price

                  <input
                    type="number"
                    name="price"
                    value={
                      formData.price
                    }
                    onChange={
                      handleInputChange
                    }
                    min="0.01"
                    step="0.01"
                    required
                  />
                </label>

                <label>
                  Category

                  <select
                    name="categoryId"
                    value={
                      formData.categoryId
                    }
                    onChange={
                      handleInputChange
                    }
                    required
                  >
                    <option value="">
                      Select
                      category
                    </option>

                    {categories.map(
                      (
                        category,
                      ) => (
                        <option
                          key={
                            category.id
                          }
                          value={
                            category.id
                          }
                        >
                          {
                            category.name
                          }
                        </option>
                      ),
                    )}
                  </select>
                </label>
              </div>

              <label>
                Image URL

                <input
                  type="url"
                  name="imageUrl"
                  value={
                    formData.imageUrl
                  }
                  onChange={
                    handleInputChange
                  }
                  placeholder="https://example.com/food.jpg"
                />
              </label>

              <div className="recommendation-field-grid">
                <label>
                  Spice level

                  <select
                    name="spiceLevel"
                    value={
                      formData.spiceLevel
                    }
                    onChange={
                      handleInputChange
                    }
                  >
                    <option value="MILD">
                      Mild
                    </option>

                    <option value="MEDIUM">
                      Medium
                    </option>

                    <option value="HOT">
                      Hot
                    </option>
                  </select>
                </label>

                <label>
                  Taste type

                  <select
                    name="tasteType"
                    value={
                      formData.tasteType
                    }
                    onChange={
                      handleInputChange
                    }
                  >
                    <option value="SPICY">
                      Spicy
                    </option>

                    <option value="SWEET">
                      Sweet
                    </option>

                    <option value="CREAMY">
                      Creamy
                    </option>

                    <option value="TANGY">
                      Tangy
                    </option>

                    <option value="SAVOURY">
                      Savoury
                    </option>
                  </select>
                </label>

                <label>
                  Protein level

                  <select
                    name="proteinLevel"
                    value={
                      formData.proteinLevel
                    }
                    onChange={
                      handleInputChange
                    }
                  >
                    <option value="NORMAL">
                      Normal
                    </option>

                    <option value="HIGH">
                      High protein
                    </option>
                  </select>
                </label>
              </div>

              <div className="checkbox-row">
                <label>
                  <input
                    type="checkbox"
                    name="vegetarian"
                    checked={
                      formData.vegetarian
                    }
                    onChange={
                      handleInputChange
                    }
                  />

                  Vegetarian
                </label>

                <label>
                  <input
                    type="checkbox"
                    name="available"
                    checked={
                      formData.available
                    }
                    onChange={
                      handleInputChange
                    }
                  />

                  Available
                </label>
              </div>

              <div className="form-actions">
                <button
                  className="checkout-submit"
                  type="submit"
                  disabled={
                    saving
                  }
                >
                  {saving
                    ? "Saving..."
                    : editingItemId
                      ? "Update item"
                      : "Add item"}
                </button>

                {editingItemId && (
                  <button
                    className="cancel-edit-button"
                    type="button"
                    onClick={
                      resetForm
                    }
                  >
                    Cancel
                    editing
                  </button>
                )}
              </div>
            </form>

            <form
              className="quick-category-form"
              onSubmit={
                addCategory
              }
            >
              <h3>
                Add a category
              </h3>

              <div>
                <input
                  type="text"
                  value={
                    newCategoryName
                  }
                  onChange={(
                    event,
                  ) =>
                    setNewCategoryName(
                      event.target
                        .value,
                    )
                  }
                  placeholder="Example: Desserts"
                  minLength="2"
                  maxLength="100"
                  required
                />

                <button type="submit">
                  Add
                </button>
              </div>
            </form>
          </div>

          <section className="menu-management-list">
            <div className="management-list-heading">
              <div>
                <span className="eyebrow">
                  Current dishes
                </span>

                <h2>
                  Restaurant menu
                </h2>
              </div>

              <span>
                {menuItems.length}{" "}
                items
              </span>
            </div>

            {loading && (
              <div className="status-message">
                Loading menu...
              </div>
            )}

            {!loading &&
              menuItems.map(
                (item) => (
                  <article
                    className="management-menu-item"
                    key={
                      item.id
                    }
                  >
                    <div className="management-item-image">
                      {item.imageUrl ? (
                        <img
                          src={
                            item.imageUrl
                          }
                          alt={
                            item.name
                          }
                        />
                      ) : (
                        <span>
                          🍲
                        </span>
                      )}
                    </div>

                    <div className="management-item-details">
                      <span>
                        {
                          item.category
                            ?.name
                        }
                      </span>

                      <h3>
                        {
                          item.name
                        }
                      </h3>

                      <p>
                        {
                          item.description
                        }
                      </p>

                      <div className="recommendation-tags">
                        <span>
                          Spice:{" "}
                          {item.spiceLevel ??
                            "Not set"}
                        </span>

                        <span>
                          Taste:{" "}
                          {item.tasteType ??
                            "Not set"}
                        </span>

                        <span>
                          Protein:{" "}
                          {item.proteinLevel ??
                            "Not set"}
                        </span>
                      </div>

                      <strong>
                        {formatPrice(
                          item.price,
                        )}
                      </strong>
                    </div>

                    <div className="management-item-actions">
                      <button
                        className={
                          item.available
                            ? "availability-on"
                            : "availability-off"
                        }
                        type="button"
                        onClick={() =>
                          toggleAvailability(
                            item,
                          )
                        }
                      >
                        {item.available
                          ? "Available"
                          : "Unavailable"}
                      </button>

                      <button
                        type="button"
                        onClick={() =>
                          startEditing(
                            item,
                          )
                        }
                      >
                        Edit
                      </button>

                      <button
                        className="delete-menu-button"
                        type="button"
                        onClick={() =>
                          deleteMenuItem(
                            item,
                          )
                        }
                      >
                        Delete
                      </button>
                    </div>
                  </article>
                ),
              )}
          </section>
        </section>
      </main>
    </div>
  );
}

export default AdminMenu;