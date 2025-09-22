-- Crear tabla usuario
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    foto_url VARCHAR(500) DEFAULT 'https://via.placeholder.com/150x150?text=Usuario'
);

-- Insertar usuarios de prueba
INSERT INTO usuario (email, password, nombre, foto_url) VALUES
('pedro@email.com', 'pedro123', 'Pedro', 'https://i.pinimg.com/736x/10/57/3e/10573ec315e1cf251da8f2330935feeb.jpg'),
('nicolas@email.com', 'nico456', 'Nicolas', 'https://i.pinimg.com/736x/84/9a/b5/849ab5b120e7f1f95bf67be7d33c0c10.jpg'),
('sergio@email.com', 'sergio789', 'Sergio', 'https://i.pinimg.com/736x/de/65/e2/de65e24e2d5c0ecb45140548e54853f5.jpg'),
('santiago@email.com', 'santi2024', 'Santiago', 'https://i.pinimg.com/1200x/9a/9d/6e/9a9d6e72d4d9ee564f1412fca600cfa1.jpg');