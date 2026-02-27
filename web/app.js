const state = {
  customers: [],
  products: [],
  cart: new Map(),
  latestQuote: null,
  dashboard: null,
};

const els = {
  statusPill: document.getElementById("status-pill"),
  startedAt: document.getElementById("started-at"),
  footerTs: document.getElementById("footer-timestamp"),
  customerSelect: document.getElementById("customer-select"),
  catalog: document.getElementById("catalog"),
  cartEmpty: document.getElementById("cart-empty"),
  cartList: document.getElementById("cart-list"),
  quoteBox: document.getElementById("quote-box"),
  quoteLines: document.getElementById("quote-lines"),
  quoteTotals: document.getElementById("quote-totals"),
  metricCards: document.getElementById("metric-cards"),
  topProducts: document.getElementById("top-products"),
  recentSales: document.getElementById("recent-sales"),
  feedback: document.getElementById("feedback"),
  btnQuote: document.getElementById("btn-quote"),
  btnRegister: document.getElementById("btn-register"),
  btnReset: document.getElementById("btn-reset"),
};

async function request(path, options = {}) {
  const response = await fetch(path, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  let payload;
  try {
    payload = await response.json();
  } catch {
    payload = {};
  }

  if (!response.ok) {
    const message = payload.error || `Request failed (${response.status})`;
    throw new Error(message);
  }

  return payload;
}

function setFeedback(message, isError = false) {
  els.feedback.textContent = message;
  els.feedback.classList.toggle("error", isError);
}

function money(value) {
  const amount = Number(value || 0);
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "EUR",
    maximumFractionDigits: 2,
  }).format(amount);
}

function parseIso(value) {
  if (!value) {
    return "--";
  }
  const date = new Date(value);
  return date.toLocaleString();
}

function currentPayload() {
  const customerId = Number(els.customerSelect.value);
  const items = Array.from(state.cart.entries()).map(([itemId, quantity]) => ({
    itemId,
    quantity,
  }));
  return { customerId, items };
}

function renderCustomers() {
  if (!state.customers.length) {
    els.customerSelect.innerHTML = "<option value=''>No customer found</option>";
    return;
  }

  els.customerSelect.innerHTML = state.customers
    .map(
      (customer) =>
        `<option value="${customer.id}">${customer.name} (${customer.district}, ${customer.loyaltyYears}y loyalty)</option>`
    )
    .join("");
}

function renderCatalog() {
  if (!state.products.length) {
    els.catalog.innerHTML = "<div class='empty-state'>No products available.</div>";
    return;
  }

  els.catalog.innerHTML = state.products
    .map((product) => {
      const stockClass = product.lowStock ? "stock-pill low" : "stock-pill";
      const disabled = product.stock <= 0 ? "disabled" : "";
      return `
        <article class="product-card">
          <h3>${product.name}</h3>
          <div class="product-meta">#${product.id} · ${product.category}</div>
          <div class="product-row">
            <strong>${money(product.price)}</strong>
            <span class="${stockClass}">Stock ${product.stock}</span>
          </div>
          <div class="product-meta">Category discount: ${(Number(product.categoryDiscount) * 100).toFixed(0)}%</div>
          <button data-add-item="${product.id}" ${disabled}>Add to cart</button>
        </article>
      `;
    })
    .join("");
}

function renderCart() {
  const cartEntries = Array.from(state.cart.entries());
  if (!cartEntries.length) {
    els.cartEmpty.classList.remove("hidden");
    els.cartList.innerHTML = "";
    return;
  }

  els.cartEmpty.classList.add("hidden");

  els.cartList.innerHTML = cartEntries
    .map(([itemId, quantity]) => {
      const product = state.products.find((item) => item.id === itemId);
      if (!product) {
        return "";
      }

      return `
        <div class="cart-item">
          <div>
            <h4>${product.name}</h4>
            <div class="product-meta">${money(product.price)} each</div>
          </div>
          <div class="cart-controls">
            <button class="qty-btn" data-dec-item="${itemId}">-</button>
            <strong>${quantity}</strong>
            <button class="qty-btn" data-inc-item="${itemId}">+</button>
            <button class="remove-btn" data-remove-item="${itemId}">Remove</button>
          </div>
        </div>
      `;
    })
    .join("");
}

function renderQuote(quote) {
  if (!quote) {
    els.quoteBox.classList.add("hidden");
    return;
  }

  els.quoteBox.classList.remove("hidden");
  els.quoteLines.innerHTML = quote.lines
    .map(
      (line) =>
        `<div class="quote-line"><span>${line.itemName} x${line.quantity}</span><span>${money(line.lineSubtotal)}</span></div>`
    )
    .join("");

  els.quoteTotals.innerHTML = `
    <div class="quote-total"><span>Subtotal</span><span>${money(quote.itemSubtotal)}</span></div>
    <div class="quote-total"><span>Category discount</span><span>-${money(quote.categoryDiscount)}</span></div>
    <div class="quote-total"><span>Loyalty discount</span><span>-${money(quote.loyaltyDiscount)}</span></div>
    <div class="quote-total"><span>Shipping</span><span>${money(quote.shippingCost)}</span></div>
    <div class="quote-total"><strong>Total</strong><strong>${money(quote.total)}</strong></div>
  `;
}

function renderMetrics() {
  const metrics = state.dashboard?.metrics;
  if (!metrics) {
    els.metricCards.innerHTML = "";
    return;
  }

  const cards = [
    ["Revenue", money(metrics.revenue)],
    ["Registered Sales", metrics.salesRegistered],
    ["Quotes", metrics.quotesGenerated],
    ["Inventory Value", money(metrics.inventoryValue)],
    ["Low Stock Products", metrics.lowStockProducts],
    ["Conversion Rate", `${Math.round(Number(metrics.quoteConversionRate || 0) * 100)}%`],
  ];

  els.metricCards.innerHTML = cards
    .map(([label, value]) => `<article class="metric-card"><span>${label}</span><strong>${value}</strong></article>`)
    .join("");

  els.startedAt.textContent = `Session: ${parseIso(metrics.startedAt)}`;
  els.footerTs.textContent = `Updated: ${parseIso(metrics.timestamp)}`;
}

function renderTopProducts() {
  const topProducts = state.dashboard?.topProducts || [];
  if (!topProducts.length) {
    els.topProducts.innerHTML = "<li>No sales yet.</li>";
    return;
  }

  els.topProducts.innerHTML = topProducts
    .map((product) => `<li>${product.itemName} · ${product.unitsSold} units sold</li>`)
    .join("");
}

function renderRecentSales() {
  const sales = state.dashboard?.recentSales || [];
  if (!sales.length) {
    els.recentSales.innerHTML = "<li>No sales registered.</li>";
    return;
  }

  els.recentSales.innerHTML = sales
    .map(
      (sale) =>
        `<li><strong>${sale.saleId}</strong><br>${sale.customer.name} · ${money(sale.quote.total)}<br><span class="product-meta">${parseIso(
          sale.timestampUtc
        )}</span></li>`
    )
    .join("");
}

function refreshUI() {
  renderCustomers();
  renderCatalog();
  renderCart();
  renderQuote(state.latestQuote);
  renderMetrics();
  renderTopProducts();
  renderRecentSales();
}

function applyInventoryFromPayload(payload) {
  if (Array.isArray(payload?.inventory)) {
    state.products = payload.inventory;
  }
}

async function loadHealth() {
  try {
    const health = await request("/health");
    els.statusPill.textContent = `Service ${health.status}`;
  } catch (error) {
    els.statusPill.textContent = "Service unreachable";
    setFeedback(error.message, true);
  }
}

async function loadCustomers() {
  const payload = await request("/api/customers");
  state.customers = payload.customers || [];
}

async function loadDashboard() {
  const payload = await request("/api/dashboard");
  state.dashboard = payload;
  if (Array.isArray(payload.inventory)) {
    state.products = payload.inventory;
  }
}

function validateCart() {
  if (!state.cart.size) {
    throw new Error("Cart is empty.");
  }

  if (!els.customerSelect.value) {
    throw new Error("Choose a customer first.");
  }
}

async function generateQuote() {
  validateCart();
  const payload = currentPayload();
  const response = await request("/api/sales/quote", {
    method: "POST",
    body: JSON.stringify(payload),
  });

  state.latestQuote = response.quote;
  applyInventoryFromPayload(response);
  await loadDashboard();
  setFeedback("Quote generated successfully.");
}

async function registerSale() {
  validateCart();
  const payload = currentPayload();
  const response = await request("/api/sales/register", {
    method: "POST",
    body: JSON.stringify(payload),
  });

  state.latestQuote = response.sale?.quote || null;
  applyInventoryFromPayload(response);
  state.cart.clear();
  await loadDashboard();
  setFeedback(`Sale ${response.sale?.saleId || "created"} registered.`);
}

async function resetSession() {
  await request("/api/reset", { method: "POST" });
  state.cart.clear();
  state.latestQuote = null;
  await loadDashboard();
  setFeedback("Session reset completed.");
}

function wireEvents() {
  els.catalog.addEventListener("click", (event) => {
    const button = event.target.closest("button[data-add-item]");
    if (!button) {
      return;
    }

    const itemId = Number(button.dataset.addItem);
    const current = state.cart.get(itemId) || 0;
    state.cart.set(itemId, current + 1);
    renderCart();
  });

  els.cartList.addEventListener("click", (event) => {
    const inc = event.target.closest("button[data-inc-item]");
    if (inc) {
      const itemId = Number(inc.dataset.incItem);
      const product = state.products.find((item) => item.id === itemId);
      const current = state.cart.get(itemId) || 0;
      if (!product || current >= product.stock) {
        setFeedback("Cannot exceed available stock.", true);
        return;
      }
      state.cart.set(itemId, current + 1);
      renderCart();
      return;
    }

    const dec = event.target.closest("button[data-dec-item]");
    if (dec) {
      const itemId = Number(dec.dataset.decItem);
      const current = state.cart.get(itemId) || 0;
      if (current <= 1) {
        state.cart.delete(itemId);
      } else {
        state.cart.set(itemId, current - 1);
      }
      renderCart();
      return;
    }

    const remove = event.target.closest("button[data-remove-item]");
    if (remove) {
      const itemId = Number(remove.dataset.removeItem);
      state.cart.delete(itemId);
      renderCart();
    }
  });

  els.btnQuote.addEventListener("click", async () => {
    try {
      await generateQuote();
      refreshUI();
    } catch (error) {
      setFeedback(error.message, true);
    }
  });

  els.btnRegister.addEventListener("click", async () => {
    try {
      await registerSale();
      refreshUI();
    } catch (error) {
      setFeedback(error.message, true);
    }
  });

  els.btnReset.addEventListener("click", async () => {
    try {
      await resetSession();
      refreshUI();
    } catch (error) {
      setFeedback(error.message, true);
    }
  });
}

async function bootstrap() {
  wireEvents();

  try {
    await Promise.all([loadHealth(), loadCustomers(), loadDashboard()]);
    refreshUI();
    setFeedback("Dashboard loaded.");
  } catch (error) {
    setFeedback(error.message, true);
  }

  setInterval(async () => {
    try {
      await loadDashboard();
      refreshUI();
    } catch {
      // Keep current UI state if periodic refresh fails.
    }
  }, 10000);
}

bootstrap();
