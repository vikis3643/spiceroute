import { useEffect, useState } from "react";
import AdminNav from "./AdminNav.jsx";

const API_BASE_URL =
  "http://localhost:8080/api/restaurant-admin";

const EMPTY_FORM = {
  name: "",
  description: "",
  discountType: "PERCENTAGE",
  discountScope: "ENTIRE_ORDER",
  discountValue: "",
  minimumOrderAmount: "0",
  maximumDiscountAmount: "",
  startsAt: "",
  endsAt: "",
  active: true,
  menuItemId: "",
  categoryId: "",
};

function getAdminHeaders(
  includeJson = false,
) {
  const headers = {
    Authorization:
      `Bearer ${sessionStorage.getItem(
        "adminToken",
      )}`,
  };

  if (includeJson) {
    headers["Content-Type"] =
      "application/json";
  }

  return headers;
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

    window.location.href =
      "/admin";

    throw new Error(
      "Admin session expired",
    );
  }
}

async function requestDiscountData() {
  const headers = getAdminHeaders();

  const [
    discountResponse,
    categoryResponse,
    menuResponse,
  ] = await Promise.all([
    fetch(
      `${API_BASE_URL}/discounts`,
      { headers },
    ),
    fetch(
      `${API_BASE_URL}/categories`,
      { headers },
    ),
    fetch(
      `${API_BASE_URL}/menu-items`,
      { headers },
    ),
  ]);

  handleUnauthorized(
    discountResponse,
  );
  handleUnauthorized(
    categoryResponse,
  );
  handleUnauthorized(
    menuResponse,
  );

  if (
    !discountResponse.ok ||
    !categoryResponse.ok ||
    !menuResponse.ok
  ) {
    throw new Error(
      "Unable to load discount data",
    );
  }

  return {
    discounts:
      await discountResponse.json(),

    categories:
      await categoryResponse.json(),

    menuItems:
      await menuResponse.json(),
  };
}

function toDateTimeInput(value) {
  return value
    ? String(value).slice(0, 16)
    : "";
}

function readableName(value) {
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

function AdminDiscounts({
  formatPrice,
}) {
  const [discounts, setDiscounts] =
    useState([]);

  const [categories, setCategories] =
    useState([]);

  const [menuItems, setMenuItems] =
    useState([]);

  const [formData, setFormData] =
    useState(EMPTY_FORM);

  const [
    editingDiscountId,
    setEditingDiscountId,
  ] = useState(null);

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

    requestDiscountData()
      .then((data) => {
        if (requestIsActive) {
          setDiscounts(
            data.discounts,
          );

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
            "Could not load discount data. Make sure Spring Boot is running.",
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

  async function refreshDiscountData() {
    try {
      const data =
        await requestDiscountData();

      setDiscounts(
        data.discounts,
      );

      setCategories(
        data.categories,
      );

      setMenuItems(
        data.menuItems,
      );

      setError("");
    } catch {
      setError(
        "Could not refresh discount data.",
      );
    }
  }

  function handleInputChange(event) {
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

  function resetForm() {
    setEditingDiscountId(null);
    setFormData(EMPTY_FORM);
  }

  function startEditing(discount) {
    setEditingDiscountId(
      discount.id,
    );

    setFormData({
      name:
        discount.name,

      description:
        discount.description ?? "",

      discountType:
        discount.discountType,

      discountScope:
        discount.discountScope,

      discountValue:
        String(
          discount.discountValue,
        ),

      minimumOrderAmount:
        String(
          discount.minimumOrderAmount ??
            0,
        ),

      maximumDiscountAmount:
        discount.maximumDiscountAmount ==
        null
          ? ""
          : String(
              discount.maximumDiscountAmount,
            ),

      startsAt:
        toDateTimeInput(
          discount.startsAt,
        ),

      endsAt:
        toDateTimeInput(
          discount.endsAt,
        ),

      active:
        discount.active,

      menuItemId:
        discount.menuItem?.id ==
        null
          ? ""
          : String(
              discount.menuItem.id,
            ),

      categoryId:
        discount.category?.id ==
        null
          ? ""
          : String(
              discount.category.id,
            ),
    });

    setError("");
    setSuccessMessage("");

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }

  function buildRequestBody() {
    return {
      name:
        formData.name.trim(),

      description:
        formData.description.trim(),

      discountType:
        formData.discountType,

      discountScope:
        formData.discountScope,

      discountValue:
        Number(
          formData.discountValue,
        ),

      minimumOrderAmount:
        Number(
          formData.minimumOrderAmount ||
            0,
        ),

      maximumDiscountAmount:
        formData.maximumDiscountAmount
          ? Number(
              formData
                .maximumDiscountAmount,
            )
          : null,

      startsAt:
        formData.startsAt || null,

      endsAt:
        formData.endsAt || null,

      active:
        formData.active,

      menuItem:
        formData.discountScope ===
        "MENU_ITEM"
          ? {
              id: Number(
                formData.menuItemId,
              ),
            }
          : null,

      category:
        formData.discountScope ===
        "CATEGORY"
          ? {
              id: Number(
                formData.categoryId,
              ),
            }
          : null,
    };
  }

  async function saveDiscount(event) {
    event.preventDefault();

    if (
      formData.discountScope ===
        "MENU_ITEM" &&
      !formData.menuItemId
    ) {
      setError(
        "Please select a menu item.",
      );

      return;
    }

    if (
      formData.discountScope ===
        "CATEGORY" &&
      !formData.categoryId
    ) {
      setError(
        "Please select a category.",
      );

      return;
    }

    const requestUrl =
      editingDiscountId
        ? `${API_BASE_URL}/discounts/${editingDiscountId}`
        : `${API_BASE_URL}/discounts`;

    try {
      setSaving(true);
      setError("");
      setSuccessMessage("");

      const response =
        await fetch(
          requestUrl,
          {
            method:
              editingDiscountId
                ? "PUT"
                : "POST",

            headers:
              getAdminHeaders(
                true,
              ),

            body:
              JSON.stringify(
                buildRequestBody(),
              ),
          },
        );

      handleUnauthorized(
        response,
      );

      if (!response.ok) {
        const responseData =
          await response.json();

        throw new Error(
          responseData.message ||
            "Unable to save discount",
        );
      }

      setSuccessMessage(
        editingDiscountId
          ? "Discount updated successfully."
          : "Discount created successfully.",
      );

      resetForm();

      await refreshDiscountData();
    } catch (requestError) {
      setError(
        requestError.message ||
          "The discount could not be saved.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function deleteDiscount(
    discount,
  ) {
    const confirmed =
      window.confirm(
        `Delete discount "${discount.name}"?`,
      );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccessMessage("");

      const response =
        await fetch(
          `${API_BASE_URL}/discounts/${discount.id}`,
          {
            method: "DELETE",

            headers:
              getAdminHeaders(),
          },
        );

      handleUnauthorized(
        response,
      );

      if (!response.ok) {
        throw new Error(
          "Unable to delete discount",
        );
      }

      setSuccessMessage(
        "Discount deleted successfully.",
      );

      await refreshDiscountData();
    } catch {
      setError(
        "The discount could not be deleted.",
      );
    }
  }

  function formatDiscountValue(
    discount,
  ) {
    if (
      discount.discountType ===
      "PERCENTAGE"
    ) {
      return `${Number(
        discount.discountValue,
      )}%`;
    }

    return formatPrice(
      discount.discountValue,
    );
  }

  function targetName(discount) {
    if (
      discount.discountScope ===
      "MENU_ITEM"
    ) {
      return (
        discount.menuItem?.name ||
        "Menu item"
      );
    }

    if (
      discount.discountScope ===
      "CATEGORY"
    ) {
      return (
        discount.category?.name ||
        "Category"
      );
    }

    return "Entire order";
  }

  return (
    <div className="admin-page">
      <AdminNav
        activePage="discounts"
      />

      <header className="admin-header">
        <div>
          <span className="eyebrow">
            Restaurant management
          </span>

          <h1>
            Discount Management
          </h1>

          <p>
            Create and manage
            automatic menu and order
            discounts.
          </p>
        </div>
      </header>

      <main className="discount-admin-content">
        {error && (
          <div className="status-message error-message">
            {error}
          </div>
        )}

        {successMessage && (
          <div className="admin-success-message">
            {successMessage}
          </div>
        )}

        <section className="discount-admin-layout">
          <div className="discount-form-card">
            <span className="eyebrow">
              {editingDiscountId
                ? "Edit discount"
                : "New discount"}
            </span>

            <h2>
              {editingDiscountId
                ? "Update discount"
                : "Create discount"}
            </h2>

            <form
              className="discount-admin-form"
              onSubmit={
                saveDiscount
              }
            >
              <label>
                Discount name

                <input
                  type="text"
                  name="name"
                  value={
                    formData.name
                  }
                  onChange={
                    handleInputChange
                  }
                  maxLength="100"
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
                  rows="3"
                  maxLength="500"
                />
              </label>

              <div className="discount-form-row">
                <label>
                  Discount type

                  <select
                    name="discountType"
                    value={
                      formData.discountType
                    }
                    onChange={
                      handleInputChange
                    }
                  >
                    <option value="PERCENTAGE">
                      Percentage
                    </option>

                    <option value="FIXED_AMOUNT">
                      Fixed amount
                    </option>
                  </select>
                </label>

                <label>
                  Discount value

                  <input
                    type="number"
                    name="discountValue"
                    value={
                      formData.discountValue
                    }
                    onChange={
                      handleInputChange
                    }
                    min="0.01"
                    max={
                      formData.discountType ===
                      "PERCENTAGE"
                        ? "100"
                        : undefined
                    }
                    step="0.01"
                    required
                  />
                </label>
              </div>

              <label>
                Apply discount to

                <select
                  name="discountScope"
                  value={
                    formData.discountScope
                  }
                  onChange={
                    handleInputChange
                  }
                >
                  <option value="ENTIRE_ORDER">
                    Entire order
                  </option>

                  <option value="CATEGORY">
                    Category
                  </option>

                  <option value="MENU_ITEM">
                    Menu item
                  </option>
                </select>
              </label>

              {formData.discountScope ===
                "MENU_ITEM" && (
                <label>
                  Menu item

                  <select
                    name="menuItemId"
                    value={
                      formData.menuItemId
                    }
                    onChange={
                      handleInputChange
                    }
                    required
                  >
                    <option value="">
                      Select menu item
                    </option>

                    {menuItems.map(
                      (
                        menuItem,
                      ) => (
                        <option
                          key={
                            menuItem.id
                          }
                          value={
                            menuItem.id
                          }
                        >
                          {
                            menuItem.name
                          }
                        </option>
                      ),
                    )}
                  </select>
                </label>
              )}

              {formData.discountScope ===
                "CATEGORY" && (
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
                      Select category
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
              )}

              <div className="discount-form-row">
                <label>
                  Minimum order amount

                  <input
                    type="number"
                    name="minimumOrderAmount"
                    value={
                      formData.minimumOrderAmount
                    }
                    onChange={
                      handleInputChange
                    }
                    min="0"
                    step="0.01"
                  />
                </label>

                <label>
                  Maximum discount

                  <input
                    type="number"
                    name="maximumDiscountAmount"
                    value={
                      formData
                        .maximumDiscountAmount
                    }
                    onChange={
                      handleInputChange
                    }
                    min="0.01"
                    step="0.01"
                    placeholder="Optional"
                  />
                </label>
              </div>

              <div className="discount-form-row">
                <label>
                  Starts at

                  <input
                    type="datetime-local"
                    name="startsAt"
                    value={
                      formData.startsAt
                    }
                    onChange={
                      handleInputChange
                    }
                  />
                </label>

                <label>
                  Ends at

                  <input
                    type="datetime-local"
                    name="endsAt"
                    value={
                      formData.endsAt
                    }
                    onChange={
                      handleInputChange
                    }
                  />
                </label>
              </div>

              <label className="discount-active-field">
                <input
                  type="checkbox"
                  name="active"
                  checked={
                    formData.active
                  }
                  onChange={
                    handleInputChange
                  }
                />

                Discount active
              </label>

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
                    : editingDiscountId
                      ? "Update discount"
                      : "Create discount"}
                </button>

                {editingDiscountId && (
                  <button
                    className="cancel-edit-button"
                    type="button"
                    onClick={
                      resetForm
                    }
                  >
                    Cancel editing
                  </button>
                )}
              </div>
            </form>
          </div>

          <section className="discount-management-list">
            <div className="management-list-heading">
              <div>
                <span className="eyebrow">
                  Current offers
                </span>

                <h2>
                  Active and saved
                  discounts
                </h2>
              </div>

              <span>
                {discounts.length}{" "}
                discounts
              </span>
            </div>

            {loading && (
              <div className="status-message">
                Loading
                discounts...
              </div>
            )}

            {!loading &&
              discounts.length ===
                0 && (
                <div className="status-message">
                  No discounts
                  created yet.
                </div>
              )}

            <div className="discount-card-grid">
              {discounts.map(
                (discount) => (
                  <article
                    className="discount-card"
                    key={
                      discount.id
                    }
                  >
                    <div className="discount-card-heading">
                      <span
                        className={
                          discount.active
                            ? "discount-active"
                            : "discount-inactive"
                        }
                      >
                        {discount.active
                          ? "Active"
                          : "Inactive"}
                      </span>

                      <strong>
                        {formatDiscountValue(
                          discount,
                        )}
                      </strong>
                    </div>

                    <h3>
                      {
                        discount.name
                      }
                    </h3>

                    <p>
                      {discount.description ||
                        "No description provided."}
                    </p>

                    <div className="discount-card-details">
                      <span>
                        Scope:{" "}
                        {readableName(
                          discount.discountScope,
                        )}
                      </span>

                      <span>
                        Target:{" "}
                        {targetName(
                          discount,
                        )}
                      </span>

                      <span>
                        Minimum:{" "}
                        {formatPrice(
                          discount.minimumOrderAmount ||
                            0,
                        )}
                      </span>

                      {discount.maximumDiscountAmount !=
                        null && (
                        <span>
                          Maximum
                          discount:{" "}
                          {formatPrice(
                            discount.maximumDiscountAmount,
                          )}
                        </span>
                      )}
                    </div>

                    <div className="discount-card-actions">
                      <button
                        type="button"
                        onClick={() =>
                          startEditing(
                            discount,
                          )
                        }
                      >
                        Edit
                      </button>

                      <button
                        className="delete-menu-button"
                        type="button"
                        onClick={() =>
                          deleteDiscount(
                            discount,
                          )
                        }
                      >
                        Delete
                      </button>
                    </div>
                  </article>
                ),
              )}
            </div>
          </section>
        </section>
      </main>
    </div>
  );
}

export default AdminDiscounts;