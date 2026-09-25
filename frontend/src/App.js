import {
  Badge,
  Button,
  Card,
  Collapse,
  ConfigProvider,
  Descriptions,
  Divider,
  Drawer,
  Empty,
  Form,
  Input,
  InputNumber,
  Layout,
  List,
  Modal,
  Popconfirm,
  Select,
  Space,
  Spin,
  Statistic,
  Table,
  Tabs,
  Tag,
  Typography,
  message
} from "antd";
import {
  DeleteOutlined,
  EditOutlined,
  LogoutOutlined,
  PlusOutlined,
  RobotOutlined,
  ShoppingCartOutlined
} from "@ant-design/icons";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  addPaymentMethod,
  addToCart,
  checkout,
  createMenuItem,
  createRestaurant,
  deleteMenuItem,
  deleteRestaurant,
  getCart,
  getCurrentUser,
  getOrders,
  getPaymentMethods,
  getRecommendations,
  getRestaurants,
  login,
  logout,
  removeCartItem,
  signup,
  updateCartItem,
  updateMenuItem,
  updateRestaurant
} from "./api";

const { Header, Content } = Layout;
const { Title, Text, Paragraph } = Typography;

const money = (value) => `$${Number(value || 0).toFixed(2)}`;

function AuthPanel({ onAuthenticated }) {
  const [mode, setMode] = useState("login");
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      if (mode === "login") {
        await login(values.email, values.password);
      } else {
        await signup({
          email: values.email,
          password: values.password,
          first_name: values.firstName,
          last_name: values.lastName
        });
        await login(values.email, values.password);
      }
      await onAuthenticated();
    } catch (error) {
      message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  const switchMode = (nextMode) => {
    setMode(nextMode);
    form.resetFields();
  };

  return (
    <main className="auth-page">
      <section className="auth-intro">
        <Text className="eyebrow">FULL-STACK FOOD ORDERING</Text>
        <Title>OnlineOrder</Title>
        <Paragraph>
          Browse menus, manage a live cart, save demo payment methods, and review completed orders.
        </Paragraph>
        <div className="auth-demo-note">
          Demo administrator: <code>foo@mail.com</code> / <code>123456</code>
        </div>
      </section>
      <section className="auth-form-area">
        <Card className="auth-card" bordered={false}>
          <Tabs
            activeKey={mode}
            onChange={switchMode}
            items={[
              { key: "login", label: "Sign in" },
              { key: "signup", label: "Create account" }
            ]}
          />
          <Form form={form} layout="vertical" onFinish={onFinish} requiredMark={false}>
            {mode === "signup" && (
              <div className="form-two-columns">
                <Form.Item
                  name="firstName"
                  label="First name"
                  rules={[{ required: true, message: "Enter your first name" }]}
                >
                  <Input autoComplete="given-name" />
                </Form.Item>
                <Form.Item
                  name="lastName"
                  label="Last name"
                  rules={[{ required: true, message: "Enter your last name" }]}
                >
                  <Input autoComplete="family-name" />
                </Form.Item>
              </div>
            )}
            <Form.Item
              name="email"
              label="Email"
              rules={[
                { required: true, message: "Enter your email" },
                { type: "email", message: "Enter a valid email" }
              ]}
            >
              <Input autoComplete="email" />
            </Form.Item>
            <Form.Item
              name="password"
              label="Password"
              rules={[{ required: true, min: 3, message: "Use at least 3 characters" }]}
            >
              <Input.Password autoComplete={mode === "login" ? "current-password" : "new-password"} />
            </Form.Item>
            <Button block type="primary" htmlType="submit" loading={loading}>
              {mode === "login" ? "Sign in" : "Create account"}
            </Button>
          </Form>
        </Card>
      </section>
    </main>
  );
}

function FoodCatalog({ onCartChanged }) {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  const loadRestaurants = useCallback(async () => {
    setLoading(true);
    try {
      setRestaurants(await getRestaurants());
    } catch (error) {
      message.error(error.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadRestaurants();
  }, [loadRestaurants]);

  const filteredRestaurants = useMemo(() => {
    const query = search.trim().toLowerCase();
    if (!query) {
      return restaurants;
    }
    return restaurants
      .map((restaurant) => ({
        ...restaurant,
        menu_items: (restaurant.menu_items || []).filter((item) =>
          `${item.name} ${item.description || ""}`.toLowerCase().includes(query)
        )
      }))
      .filter((restaurant) => {
        return restaurant.name.toLowerCase().includes(query) || restaurant.menu_items.length > 0;
      });
  }, [restaurants, search]);

  const onAddToCart = async (item) => {
    try {
      await addToCart(item.id);
      message.success(`${item.name} added to cart`);
      onCartChanged();
    } catch (error) {
      message.error(error.message);
    }
  };

  return (
    <section className="page-section">
      <div className="section-heading">
        <div>
          <Text className="eyebrow">EXPLORE</Text>
          <Title level={2}>Restaurant menus</Title>
        </div>
        <Input.Search
          allowClear
          className="catalog-search"
          placeholder="Search dishes or descriptions"
          onChange={(event) => setSearch(event.target.value)}
        />
      </div>
      {loading ? (
        <div className="centered-loading"><Spin size="large" /></div>
      ) : filteredRestaurants.length === 0 ? (
        <Empty description="No matching dishes found" />
      ) : (
        <div className="restaurant-grid">
          {filteredRestaurants.map((restaurant) => (
            <article className="restaurant-section" key={restaurant.id}>
              <div className="restaurant-banner">
                {restaurant.image_url ? (
                  <img src={restaurant.image_url} alt={restaurant.name} />
                ) : (
                  <div className="image-placeholder">Restaurant</div>
                )}
                <div>
                  <Title level={3}>{restaurant.name}</Title>
                  <Text type="secondary">{restaurant.address}</Text>
                </div>
              </div>
              <div className="menu-grid">
                {(restaurant.menu_items || []).map((item) => (
                  <Card key={item.id} className="menu-card" size="small">
                    {item.image_url && (
                      <img className="menu-image" src={item.image_url} alt={item.name} />
                    )}
                    <div className="menu-card-content">
                      <Text strong>{item.name}</Text>
                      <Paragraph className="menu-description" type="secondary" ellipsis={{ rows: 2 }}>
                        {item.description || "No description provided."}
                      </Paragraph>
                      <div className="menu-card-footer">
                        <Text strong>{money(item.price)}</Text>
                        <Button
                          type="primary"
                          size="small"
                          icon={<PlusOutlined />}
                          aria-label={`Add ${item.name} to cart`}
                          onClick={() => onAddToCart(item)}
                        />
                      </div>
                    </div>
                  </Card>
                ))}
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

function CartDrawer({ open, onClose, onOrderCreated, cartVersion }) {
  const [cart, setCart] = useState(null);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState();
  const [loading, setLoading] = useState(false);
  const [checkoutOpen, setCheckoutOpen] = useState(false);
  const [paymentOpen, setPaymentOpen] = useState(false);
  const [paymentForm] = Form.useForm();

  const loadCart = useCallback(async () => {
    setLoading(true);
    try {
      const [cartData, methods] = await Promise.all([getCart(), getPaymentMethods()]);
      setCart(cartData);
      setPaymentMethods(methods);
      setSelectedPaymentMethod((current) => current || methods[0]?.id);
    } catch (error) {
      message.error(error.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (open) {
      loadCart();
    }
  }, [open, cartVersion, loadCart]);

  const changeQuantity = async (item, quantity) => {
    if (quantity === null || quantity === undefined) {
      return;
    }
    try {
      await updateCartItem(item.id, quantity);
      await loadCart();
    } catch (error) {
      message.error(error.message);
    }
  };

  const deleteItem = async (item) => {
    try {
      await removeCartItem(item.id);
      await loadCart();
    } catch (error) {
      message.error(error.message);
    }
  };

  const savePaymentMethod = async (values) => {
    try {
      const method = await addPaymentMethod(values);
      setPaymentMethods((previous) => [...previous, method]);
      setSelectedPaymentMethod(method.id);
      setPaymentOpen(false);
      paymentForm.resetFields();
      message.success(`Saved ${method.brand} ending in ${method.last_four}`);
    } catch (error) {
      message.error(error.message);
    }
  };

  const finishCheckout = async () => {
    if (!selectedPaymentMethod) {
      message.warning("Choose or add a demo payment method first");
      return;
    }
    try {
      const order = await checkout(selectedPaymentMethod);
      message.success(`Order #${order.id} created`);
      setCheckoutOpen(false);
      onClose();
      onOrderCreated();
    } catch (error) {
      message.error(error.message);
    }
  };

  const items = cart?.order_items || [];
  return (
    <>
      <Drawer title="Your cart" width={440} open={open} onClose={onClose}>
        {loading ? (
          <div className="centered-loading"><Spin /></div>
        ) : items.length === 0 ? (
          <Empty description="Your cart is empty" />
        ) : (
          <>
            <List
              dataSource={items}
              renderItem={(item) => (
                <List.Item className="cart-line">
                  <div className="cart-line-main">
                    <Text strong>{item.menu_item_name}</Text>
                    <Text type="secondary">{money(item.price)} each</Text>
                  </div>
                  <div className="cart-line-actions">
                    <InputNumber
                      min={0}
                      value={item.quantity}
                      onChange={(value) => changeQuantity(item, value)}
                    />
                    <Popconfirm
                      title="Remove this item?"
                      onConfirm={() => deleteItem(item)}
                    >
                      <Button danger type="text" icon={<DeleteOutlined />} aria-label="Remove cart item" />
                    </Popconfirm>
                  </div>
                </List.Item>
              )}
            />
            <Divider />
            <Statistic title="Order total" value={cart?.total_price || 0} precision={2} prefix="$" />
            <Button block type="primary" size="large" onClick={() => setCheckoutOpen(true)}>
              Checkout
            </Button>
          </>
        )}
      </Drawer>

      <Modal
        title="Checkout"
        open={checkoutOpen}
        okText="Place order"
        onCancel={() => setCheckoutOpen(false)}
        onOk={finishCheckout}
      >
        <Paragraph type="secondary">
          This is a demo checkout. The application stores only card brand and last four digits.
        </Paragraph>
        <Select
          className="full-width"
          placeholder="Choose a payment method"
          value={selectedPaymentMethod}
          onChange={setSelectedPaymentMethod}
          options={paymentMethods.map((method) => ({
            value: method.id,
            label: `${method.brand} ending in ${method.last_four}`
          }))}
        />
        <Button type="link" onClick={() => setPaymentOpen(true)}>
          Add demo payment method
        </Button>
      </Modal>

      <Modal
        title="Add demo payment method"
        open={paymentOpen}
        onCancel={() => setPaymentOpen(false)}
        okText="Save"
        onOk={() => paymentForm.submit()}
      >
        <Form form={paymentForm} layout="vertical" onFinish={savePaymentMethod}>
          <Form.Item name="card_holder" label="Card holder" rules={[{ required: true }]}>
            <Input placeholder="Alex Chen" />
          </Form.Item>
          <Form.Item
            name="card_number"
            label="Demo card number"
            rules={[{ required: true, message: "Use a 12 to 19 digit test number" }]}
          >
            <Input placeholder="4242 4242 4242 4242" />
          </Form.Item>
          <div className="form-two-columns">
            <Form.Item name="expiry_month" label="Month" rules={[{ required: true }]}>
              <InputNumber min={1} max={12} className="full-width" />
            </Form.Item>
            <Form.Item name="expiry_year" label="Year" rules={[{ required: true }]}>
              <InputNumber min={2026} max={2100} className="full-width" />
            </Form.Item>
          </div>
        </Form>
      </Modal>
    </>
  );
}

function OrderHistory({ version }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    const loadOrders = async () => {
      setLoading(true);
      try {
        const data = await getOrders();
        if (active) {
          setOrders(data);
        }
      } catch (error) {
        if (active) {
          message.error(error.message);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };
    loadOrders();
    return () => {
      active = false;
    };
  }, [version]);

  if (loading) {
    return <div className="centered-loading"><Spin size="large" /></div>;
  }
  if (orders.length === 0) {
    return <Empty description="No completed orders yet" />;
  }

  return (
    <section className="page-section narrow-section">
      <div className="section-heading">
        <div>
          <Text className="eyebrow">ACCOUNT</Text>
          <Title level={2}>Order history</Title>
        </div>
      </div>
      <Collapse
        items={orders.map((order) => ({
          key: order.id,
          label: (
            <div className="order-summary">
              <Space>
                <Text strong>Order #{order.id}</Text>
                <Tag color="green">{order.status}</Tag>
              </Space>
              <Space>
                <Text type="secondary">{new Date(order.created_at).toLocaleString()}</Text>
                <Text strong>{money(order.total_price)}</Text>
              </Space>
            </div>
          ),
          children: (
            <>
              <Descriptions size="small" column={2}>
                <Descriptions.Item label="Payment">
                  {order.payment_brand ? `${order.payment_brand} ending in ${order.payment_last_four}` : "Unavailable"}
                </Descriptions.Item>
                <Descriptions.Item label="Items">{order.line_items.length}</Descriptions.Item>
              </Descriptions>
              <List
                size="small"
                dataSource={order.line_items}
                renderItem={(item) => (
                  <List.Item>
                    <Text>{item.menu_item_name} x {item.quantity}</Text>
                    <Text>{money(item.price * item.quantity)}</Text>
                  </List.Item>
                )}
              />
            </>
          )
        }))}
      />
    </section>
  );
}

function AdminPanel() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editor, setEditor] = useState(null);
  const [form] = Form.useForm();

  const loadRestaurants = useCallback(async () => {
    setLoading(true);
    try {
      setRestaurants(await getRestaurants());
    } catch (error) {
      message.error(error.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadRestaurants();
  }, [loadRestaurants]);

  const openEditor = (kind, restaurantId = null, entity = null) => {
    setEditor({ kind, restaurantId, entity });
    form.setFieldsValue(entity || {});
  };

  const closeEditor = () => {
    setEditor(null);
    form.resetFields();
  };

  const submitEditor = async (values) => {
    try {
      if (editor.kind === "restaurant") {
        if (editor.entity) {
          await updateRestaurant(editor.entity.id, values);
        } else {
          await createRestaurant(values);
        }
      } else if (editor.entity) {
        await updateMenuItem(editor.entity.id, values);
      } else {
        await createMenuItem(editor.restaurantId, values);
      }
      message.success("Saved");
      closeEditor();
      await loadRestaurants();
    } catch (error) {
      message.error(error.message);
    }
  };

  const removeRestaurant = async (id) => {
    try {
      await deleteRestaurant(id);
      message.success("Restaurant deleted");
      await loadRestaurants();
    } catch (error) {
      message.error(error.message);
    }
  };

  const removeMenuItem = async (id) => {
    try {
      await deleteMenuItem(id);
      message.success("Menu item deleted");
      await loadRestaurants();
    } catch (error) {
      message.error(error.message);
    }
  };

  const restaurantColumns = [
    { title: "Name", dataIndex: "name" },
    { title: "Address", dataIndex: "address" },
    { title: "Phone", dataIndex: "phone" },
    {
      title: "Actions",
      render: (_, restaurant) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => openEditor("restaurant", null, restaurant)}>
            Edit
          </Button>
          <Popconfirm title="Delete this restaurant and its menu?" onConfirm={() => removeRestaurant(restaurant.id)}>
            <Button size="small" danger icon={<DeleteOutlined />}>Delete</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const menuColumns = (restaurantId) => [
    { title: "Name", dataIndex: "name" },
    { title: "Description", dataIndex: "description", ellipsis: true },
    { title: "Price", dataIndex: "price", render: money },
    {
      title: "Actions",
      render: (_, item) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => openEditor("menu", restaurantId, item)}>
            Edit
          </Button>
          <Popconfirm title="Delete this menu item?" onConfirm={() => removeMenuItem(item.id)}>
            <Button size="small" danger icon={<DeleteOutlined />}>Delete</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const isRestaurantEditor = editor?.kind === "restaurant";
  return (
    <section className="page-section">
      <div className="section-heading">
        <div>
          <Text className="eyebrow">ADMINISTRATION</Text>
          <Title level={2}>Restaurant catalog</Title>
        </div>
        <Button type="primary" onClick={() => openEditor("restaurant")}>Add restaurant</Button>
      </div>
      <Table
        rowKey="id"
        loading={loading}
        columns={restaurantColumns}
        dataSource={restaurants}
        expandable={{
          expandedRowRender: (restaurant) => (
            <div className="nested-table">
              <Button size="small" type="primary" onClick={() => openEditor("menu", restaurant.id)}>
                Add menu item
              </Button>
              <Table
                rowKey="id"
                size="small"
                pagination={false}
                columns={menuColumns(restaurant.id)}
                dataSource={restaurant.menu_items || []}
              />
            </div>
          )
        }}
      />

      <Modal
        title={
          !editor
            ? ""
            : `${editor.entity ? "Edit" : "Add"} ${isRestaurantEditor ? "restaurant" : "menu item"}`
        }
        open={Boolean(editor)}
        onCancel={closeEditor}
        onOk={() => form.submit()}
      >
        <Form form={form} layout="vertical" onFinish={submitEditor}>
          <Form.Item name="name" label="Name" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          {isRestaurantEditor ? (
            <>
              <Form.Item name="address" label="Address" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
              <Form.Item name="phone" label="Phone"><Input /></Form.Item>
            </>
          ) : (
            <>
              <Form.Item name="description" label="Description"><Input.TextArea rows={3} /></Form.Item>
              <Form.Item name="price" label="Price" rules={[{ required: true }]}>
                <InputNumber min={0} step={0.01} className="full-width" />
              </Form.Item>
            </>
          )}
          <Form.Item name="image_url" label="Image URL"><Input /></Form.Item>
        </Form>
      </Modal>
    </section>
  );
}

function RecommendationDrawer({ open, onClose, onCartChanged }) {
  const [input, setInput] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const requestRecommendation = async () => {
    if (!input.trim()) {
      return;
    }
    setLoading(true);
    try {
      setResult(await getRecommendations(input));
    } catch (error) {
      message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  const addRecommendation = async (item) => {
    try {
      await addToCart(item.menu_item_id);
      message.success(`${item.name} added to cart`);
      onCartChanged();
    } catch (error) {
      message.error(error.message);
    }
  };

  return (
    <Drawer title="Food assistant" width={420} open={open} onClose={onClose}>
      <Paragraph type="secondary">
        Describe a craving or budget, for example: “spicy food under $15”.
      </Paragraph>
      <Space.Compact className="full-width">
        <Input
          value={input}
          onChange={(event) => setInput(event.target.value)}
          onPressEnter={requestRecommendation}
          placeholder="What are you in the mood for?"
        />
        <Button type="primary" icon={<RobotOutlined />} loading={loading} onClick={requestRecommendation} />
      </Space.Compact>
      {loading && <div className="centered-loading"><Spin /></div>}
      {result && (
        <div className="recommendation-result">
          <Paragraph>{result.summary}</Paragraph>
          {result.recommendations.length === 0 ? (
            <Empty description="No matching item found" />
          ) : (
            <List
              dataSource={result.recommendations}
              renderItem={(item) => (
                <List.Item
                  actions={[
                    <Button type="link" onClick={() => addRecommendation(item)}>Add</Button>
                  ]}
                >
                  <List.Item.Meta
                    avatar={
                      item.image_url ? <img className="recommendation-image" src={item.image_url} alt={item.name} /> : null
                    }
                    title={<>{item.name} <Text type="secondary">{money(item.price)}</Text></>}
                    description={item.reason}
                  />
                </List.Item>
              )}
            />
          )}
          <Text type="secondary" className="assistant-disclaimer">{result.disclaimer}</Text>
        </div>
      )}
    </Drawer>
  );
}

function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [checkingSession, setCheckingSession] = useState(true);
  const [cartOpen, setCartOpen] = useState(false);
  const [assistantOpen, setAssistantOpen] = useState(false);
  const [cartVersion, setCartVersion] = useState(0);
  const [orderVersion, setOrderVersion] = useState(0);

  const loadSession = useCallback(async () => {
    try {
      setCurrentUser(await getCurrentUser());
    } catch {
      setCurrentUser(null);
    } finally {
      setCheckingSession(false);
    }
  }, []);

  useEffect(() => {
    loadSession();
  }, [loadSession]);

  const onLogout = async () => {
    try {
      await logout();
      setCurrentUser(null);
    } catch (error) {
      message.error(error.message);
    }
  };

  if (checkingSession) {
    return <div className="app-loading"><Spin size="large" /></div>;
  }
  if (!currentUser) {
    return <AuthPanel onAuthenticated={loadSession} />;
  }

  const cartChanged = () => setCartVersion((version) => version + 1);
  const orderCreated = () => {
    cartChanged();
    setOrderVersion((version) => version + 1);
  };

  const tabItems = [
    { key: "browse", label: "Browse", children: <FoodCatalog onCartChanged={cartChanged} /> },
    { key: "orders", label: "Orders", children: <OrderHistory version={orderVersion} /> }
  ];
  if (currentUser.is_admin) {
    tabItems.push({ key: "admin", label: "Admin", children: <AdminPanel /> });
  }

  return (
    <ConfigProvider theme={{ token: { colorPrimary: "#0f766e", borderRadius: 6 } }}>
      <Layout className="app-layout">
        <Header className="app-header">
          <div className="brand">OnlineOrder</div>
          <div className="header-actions">
            <Text className="header-user">{currentUser.email}</Text>
            {currentUser.is_admin && <Tag color="gold">ADMIN</Tag>}
            <Badge dot>
              <Button
                aria-label="Open cart"
                icon={<ShoppingCartOutlined />}
                onClick={() => setCartOpen(true)}
              />
            </Badge>
            <Button aria-label="Log out" icon={<LogoutOutlined />} onClick={onLogout} />
          </div>
        </Header>
        <Content className="app-content">
          <Tabs className="main-tabs" defaultActiveKey="browse" items={tabItems} />
        </Content>
      </Layout>
      <Button
        className="assistant-button"
        type="primary"
        shape="circle"
        size="large"
        icon={<RobotOutlined />}
        aria-label="Open food assistant"
        onClick={() => setAssistantOpen(true)}
      />
      <CartDrawer
        open={cartOpen}
        onClose={() => setCartOpen(false)}
        onOrderCreated={orderCreated}
        cartVersion={cartVersion}
      />
      <RecommendationDrawer
        open={assistantOpen}
        onClose={() => setAssistantOpen(false)}
        onCartChanged={cartChanged}
      />
    </ConfigProvider>
  );
}

export default App;
