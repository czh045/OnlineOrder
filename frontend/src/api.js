const parseResponse = async (response) => {
  const contentType = response.headers.get("content-type") || "";
  const data = contentType.includes("application/json") ? await response.json() : null;

  if (!response.ok) {
    throw new Error(data?.message || `Request failed with status ${response.status}`);
  }
  return data;
};

const request = (path, options = {}) =>
  fetch(path, {
    credentials: "include",
    ...options,
    headers: {
      ...(options.body ? { "Content-Type": "application/json" } : {}),
      ...options.headers
    }
  }).then(parseResponse);

export const login = (email, password) =>
  fetch("/login", {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({ username: email, password })
  }).then(parseResponse);

export const logout = () => request("/logout", { method: "POST" });
export const signup = (payload) =>
  request("/signup", { method: "POST", body: JSON.stringify(payload) });

export const getCurrentUser = () => request("/me");
export const getRestaurants = () => request("/restaurants/menu");
export const getCart = () => request("/cart");
export const getOrders = () => request("/orders");
export const getPaymentMethods = () => request("/payment-methods");

export const addToCart = (menuId, quantity = 1) =>
  request("/cart", {
    method: "POST",
    body: JSON.stringify({ menu_id: menuId, quantity })
  });

export const updateCartItem = (orderItemId, quantity) =>
  request(`/cart/items/${orderItemId}`, {
    method: "PATCH",
    body: JSON.stringify({ quantity })
  });

export const removeCartItem = (orderItemId) =>
  request(`/cart/items/${orderItemId}`, { method: "DELETE" });

export const addPaymentMethod = (payload) =>
  request("/payment-methods", { method: "POST", body: JSON.stringify(payload) });

export const checkout = (paymentMethodId) =>
  request("/cart/checkout", {
    method: "POST",
    body: JSON.stringify({ payment_method_id: paymentMethodId })
  });

export const getRecommendations = (message) =>
  request("/recommendations", {
    method: "POST",
    body: JSON.stringify({ message })
  });

export const createRestaurant = (payload) =>
  request("/restaurants", { method: "POST", body: JSON.stringify(payload) });

export const updateRestaurant = (id, payload) =>
  request(`/restaurant/${id}`, { method: "PUT", body: JSON.stringify(payload) });

export const deleteRestaurant = (id) =>
  request(`/restaurant/${id}`, { method: "DELETE" });

export const createMenuItem = (restaurantId, payload) =>
  request(`/restaurant/${restaurantId}/menu`, {
    method: "POST",
    body: JSON.stringify(payload)
  });

export const updateMenuItem = (id, payload) =>
  request(`/menu/${id}`, { method: "PUT", body: JSON.stringify(payload) });

export const deleteMenuItem = (id) =>
  request(`/menu/${id}`, { method: "DELETE" });
