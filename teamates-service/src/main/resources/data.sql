-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS stores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    store_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id VARCHAR(50) NOT NULL,
    store_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (store_id) REFERENCES stores(id)
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL
);


-- Insert Singapore stores
INSERT INTO stores (store_name, location) VALUES
('Ceylon Tea House Orchard', 'Orchard Road, Singapore'),
('Ceylon Tea House Marina', 'Marina Bay, Singapore'),
('Ceylon Tea House Sentosa', 'Sentosa Island, Singapore');

-- Insert tea products
INSERT INTO products (product_code, name, category, price) VALUES
('TEA-001', 'Ceylon Black Tea', 'Black Tea', 12.99),
('TEA-002', 'Ceylon Green Tea', 'Green Tea', 14.99),
('TEA-003', 'Ceylon Earl Grey', 'Flavored Tea', 16.99),
('TEA-004', 'Ceylon Jasmine Tea', 'Floral Tea', 15.99),
('TEA-005', 'Ceylon Oolong Tea', 'Oolong Tea', 18.99),
('TEA-006', 'Ceylon White Tea', 'White Tea', 22.99),
('TEA-007', 'Ceylon Chai', 'Spiced Tea', 13.99),
('TEA-008', 'Ceylon Mint Tea', 'Herbal Tea', 11.99);
