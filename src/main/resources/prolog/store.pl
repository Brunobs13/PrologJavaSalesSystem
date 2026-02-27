% Customers: customer(Id, Name, District, LoyaltyYears).
customer(1, 'Ana Costa', 'Lisbon', 6).
customer(2, 'Bruno Reis', 'Porto', 3).
customer(3, 'Carla Silva', 'Coimbra', 8).
customer(4, 'David Rocha', 'Faro', 2).
customer(5, 'Eva Martins', 'Setubal', 10).

% Inventory: item(Id, Name, Category, Price, Stock).
item(101, 'Laptop Pro 14', 'electronics', 1499.90, 12).
item(102, 'Mechanical Keyboard', 'electronics', 129.50, 40).
item(103, 'Noise Cancelling Headphones', 'electronics', 199.90, 30).
item(201, 'Office Chair Ergo', 'furniture', 259.00, 15).
item(202, 'Standing Desk', 'furniture', 449.00, 10).
item(301, 'Notebook Premium', 'stationery', 14.90, 120).
item(302, 'Pen Set', 'stationery', 9.90, 200).

% Category discounts: category_discount(Category, Rate).
category_discount('electronics', 0.12).
category_discount('furniture', 0.08).
category_discount('stationery', 0.03).

% Loyalty discounts: loyalty_discount(MinYears, Rate).
loyalty_discount(0, 0.00).
loyalty_discount(3, 0.02).
loyalty_discount(5, 0.04).
loyalty_discount(8, 0.06).
loyalty_discount(10, 0.08).

% Shipping costs by district: shipping_cost(District, Cost).
shipping_cost('Lisbon', 4.90).
shipping_cost('Porto', 5.90).
shipping_cost('Coimbra', 6.50).
shipping_cost('Faro', 8.90).
shipping_cost('Setubal', 5.20).
shipping_cost('Braga', 6.10).
