-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: tregodb
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `administrador`
--

DROP TABLE IF EXISTS `administrador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrador` (
  `password` varchar(255) DEFAULT NULL,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_usuario`),
  CONSTRAINT `FKpt2bj0l5q4npigarogy7p1834` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrador`
--

LOCK TABLES `administrador` WRITE;
/*!40000 ALTER TABLE `administrador` DISABLE KEYS */;
INSERT INTO `administrador` VALUES ('$2a$10$g3sk88p1yajMiy5KYWbhA.MQeWnMjYOeaslRVKatsCtNDGDAMeGHy',1),('$2a$10$demoDemoDemoDemoDemoDe.HashFicticioParaPruebas123456',10);
/*!40000 ALTER TABLE `administrador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `articulo`
--

DROP TABLE IF EXISTS `articulo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `articulo` (
  `id_producto` int NOT NULL,
  PRIMARY KEY (`id_producto`),
  CONSTRAINT `FKny54huh5fcvoi16pwc8q7ptxr` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `articulo`
--

LOCK TABLES `articulo` WRITE;
/*!40000 ALTER TABLE `articulo` DISABLE KEYS */;
INSERT INTO `articulo` VALUES (2),(5),(9),(13),(17),(20),(41),(42),(43),(46),(52),(57),(59);
/*!40000 ALTER TABLE `articulo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carrito`
--

DROP TABLE IF EXISTS `carrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito` (
  `id_carrito` int NOT NULL AUTO_INCREMENT,
  `id_restaurante` int DEFAULT NULL,
  `total` double DEFAULT NULL,
  `uid_cliente` varchar(255) NOT NULL,
  PRIMARY KEY (`id_carrito`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrito`
--

LOCK TABLES `carrito` WRITE;
/*!40000 ALTER TABLE `carrito` DISABLE KEYS */;
INSERT INTO `carrito` VALUES (10,3,150,'kPXY0zKb02hLYQHH01gVHiqPn753'),(13,13,390,'mIwb9lEevqXjIC765bGkPSe4Ovm2'),(44,NULL,0,'WY4cund9wsWoDBz8u1gOjn05bRf1'),(50,13,630,'U3v9RuFlGZgYo1eNlERcbZF39PP2'),(51,15,140,'XzaWNR4f91W57C1S5tfhCB7nG4K3');
/*!40000 ALTER TABLE `carrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `fcm_token` varchar(255) DEFAULT NULL,
  `habilitado` bit(1) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `uid_cliente` varchar(255) DEFAULT NULL,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_usuario`),
  CONSTRAINT `FKetx0tojxf5yevxcyt6qb526x5` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES ('cGKU7gLgSf6CFb6KLTUWr0:APA91bG06sUTedhqtD0szrjZWn7UrodkVjExDt0Y4a5peO8eBrpKfLRGMaxtIrflQqhgdX7aA7mFoBX3s4H6y01PlvQNVKcpvQ3FsS059Sbjpf5Vem-aIMk',_binary '','+59896022499','WY4cund9wsWoDBz8u1gOjn05bRf1',2),(NULL,_binary '',NULL,'EGWGVsrHKhOdeiSFw291uejVtPD3',4),('fLPN0umqR6CTm59462mJUF:APA91bFRqVisQFKV2htLYyml-PGPq_cQpamNNdJIhleaLn8-1XOU6LtPVDmHlLFp38gW2frkXWUbjLHtWq31lVK1Ye0i99cQAcoKRrx3NrZbxM5DCmhjOFo',_binary '','59892436399','kPXY0zKb02hLYQHH01gVHiqPn753',5),('fIuN10uaQgOrVlhS27IQlI:APA91bGjfUfgcxZ_IQoKgRnPIKIpep0j90ZNtSY3SvnuWPIbK7fhu313ssmBTlqsb8T98To1wqRydYAR1vMyzDxYLyfXjwDgCLwVX_I6Yd2hgdy58xHVCLw',_binary '',NULL,'l8cJ9NHR66Pb5KGTUsaEyePOiDy2',6),(NULL,_binary '',NULL,'NCRPRv5j2wWd3oCWh4yWQNGHrMz1',7),('fTfVRRuPSM-dpoFFniqptO:APA91bHLjK6jsO9rHsGpIDkeJ7S-W8qcOyCwnLb19Nb2M2QBp6k3FlmxOZUqYZyKfpS299RPxcLjkrtO_9FqSz-8vJj2pP4-5YVpeVOM07EbyYEthp9MjnQ',_binary '',NULL,'laOatBRXfqhWBCZeeMPW4eN5Qr93',8),(NULL,_binary '','098100100','uid-lucia-seed',16),(NULL,_binary '\0','098200200','uid-martin-seed',17),(NULL,_binary '\0','098300300','uid-sofia-seed',18),(NULL,_binary '\0','098400400','uid-diego-seed',19),(NULL,_binary '',NULL,'mIwb9lEevqXjIC765bGkPSe4Ovm2',21),(NULL,_binary '',NULL,'YnOBS4fdtlebOtUzUfUhlOFtqJn2',22),('fLPN0umqR6CTm59462mJUF:APA91bFRqVisQFKV2htLYyml-PGPq_cQpamNNdJIhleaLn8-1XOU6LtPVDmHlLFp38gW2frkXWUbjLHtWq31lVK1Ye0i99cQAcoKRrx3NrZbxM5DCmhjOFo',_binary '',NULL,'dmtegT5947Z4WeFq5Yily3AbrLt2',24),(NULL,_binary '','+59892436399','sPc3K5JP70U5CsSY2hEnQpRvslv1',28),(NULL,_binary '',NULL,'HuKA61D0JAMd8GARdZRJadWsec53',32),(NULL,_binary '',NULL,'U3v9RuFlGZgYo1eNlERcbZF39PP2',35),(NULL,_binary '',NULL,'XzaWNR4f91W57C1S5tfhCB7nG4K3',36);
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente_direcciones`
--

DROP TABLE IF EXISTS `cliente_direcciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente_direcciones` (
  `cliente_id_usuario` int NOT NULL,
  `apartamento` varchar(255) DEFAULT NULL,
  `calle` varchar(255) DEFAULT NULL,
  `esquina` varchar(255) DEFAULT NULL,
  `latitud` double DEFAULT NULL,
  `longitud` double DEFAULT NULL,
  `numero` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  KEY `FKdw4e38fyd668jlg32aicfdh8l` (`cliente_id_usuario`),
  CONSTRAINT `FKdw4e38fyd668jlg32aicfdh8l` FOREIGN KEY (`cliente_id_usuario`) REFERENCES `cliente` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente_direcciones`
--

LOCK TABLES `cliente_direcciones` WRITE;
/*!40000 ALTER TABLE `cliente_direcciones` DISABLE KEYS */;
INSERT INTO `cliente_direcciones` VALUES (4,'','Liga Federal','',-34.8180138,-56.2800382,'1755 BIS','Casa'),(5,'','Ramón Márquez','',-34.8731899,-56.1845543,'3353','Casa'),(16,'301','Av. Italia','Pereira',-34.90889,-56.1421,'2500','Casa'),(17,NULL,'Rambla Rep. del Peru','Pena',-34.919,-56.151,'1300','Trabajo'),(18,'5B','Canelones','Tristan Narvaja',-34.9042,-56.1802,'1820','Casa'),(22,'45','Tres Cruces','4535',-34.892518,-56.1671127,'1234','{[\'\"select * from usuarios\"\']}'),(2,'1','18 De Julio',NULL,-34.0082394,-57.6461326,'123','Casa'),(2,NULL,'Avenida 8 de Octubre',NULL,-34.8977082,-56.1662188,'2285','Casa2'),(2,NULL,'Avenida General Rivera','Tomás de Tezanos',-34.902335,-56.173578,'3729','Utu'),(8,'302','Daniel Muñoz',NULL,-34.8963328,-56.1737261,'2047','Casa'),(6,'1','Comandante Braga',NULL,-34.8859421,-56.1594575,'2676','Casa'),(6,'6','Avenida 8 de Octubre',NULL,-34.8974267,-56.1659394,'2266','Trabajo'),(6,'6','Comandante Braga',NULL,-34.885908,-56.1593702,'2677','Oficina'),(6,NULL,'Francisco Maciel',NULL,-34.7339423,-56.037993,'22','Suarez'),(6,NULL,'Comandante Braga',NULL,-34.884839,-56.161616,'2234','Casa 2'),(6,NULL,'Avenida General Rivera',NULL,-34.8992917,-56.1314938,'3729 BIS','Utu');
/*!40000 ALTER TABLE `cliente_direcciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `combo`
--

DROP TABLE IF EXISTS `combo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `combo` (
  `id_producto` int NOT NULL,
  PRIMARY KEY (`id_producto`),
  CONSTRAINT `FK9kub5p6j0mgr8eybvm1pmarvj` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `combo`
--

LOCK TABLES `combo` WRITE;
/*!40000 ALTER TABLE `combo` DISABLE KEYS */;
INSERT INTO `combo` VALUES (14),(37),(40),(53);
/*!40000 ALTER TABLE `combo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `combo_productos_incluidos`
--

DROP TABLE IF EXISTS `combo_productos_incluidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `combo_productos_incluidos` (
  `combo_id_producto` int NOT NULL,
  `productos_incluidos_id_producto` int NOT NULL,
  KEY `FKl9yldg0xxt1xu0yq407p14vhd` (`productos_incluidos_id_producto`),
  KEY `FK3vykvgbu31k6imy2ymxo8pe5q` (`combo_id_producto`),
  CONSTRAINT `FK3vykvgbu31k6imy2ymxo8pe5q` FOREIGN KEY (`combo_id_producto`) REFERENCES `combo` (`id_producto`),
  CONSTRAINT `FKl9yldg0xxt1xu0yq407p14vhd` FOREIGN KEY (`productos_incluidos_id_producto`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `combo_productos_incluidos`
--

LOCK TABLES `combo_productos_incluidos` WRITE;
/*!40000 ALTER TABLE `combo_productos_incluidos` DISABLE KEYS */;
INSERT INTO `combo_productos_incluidos` VALUES (14,10),(14,12),(14,13),(37,36),(37,35),(37,34),(37,33),(37,32),(37,31),(40,26),(40,27),(40,28),(40,34),(40,33),(53,25),(53,23);
/*!40000 ALTER TABLE `combo_productos_incluidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comentario`
--

DROP TABLE IF EXISTS `comentario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comentario` (
  `id_comentario` int NOT NULL AUTO_INCREMENT,
  `calificacion` int DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `texto` varchar(255) DEFAULT NULL,
  `cliente_id` int DEFAULT NULL,
  `restaurante_id` int DEFAULT NULL,
  PRIMARY KEY (`id_comentario`),
  UNIQUE KEY `UK24su4gqh3omid5awjk17qhyln` (`cliente_id`,`restaurante_id`),
  KEY `FK59dq16giucflsklavr7meo1ef` (`restaurante_id`),
  CONSTRAINT `FK59dq16giucflsklavr7meo1ef` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurante` (`id_usuario`),
  CONSTRAINT `FKq5ennpu64v3f2795090yfd0gq` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comentario`
--

LOCK TABLES `comentario` WRITE;
/*!40000 ALTER TABLE `comentario` DISABLE KEYS */;
INSERT INTO `comentario` VALUES (1,3,'2026-06-22 18:34:19.909528','Rico a medias',4,3),(2,1,'2026-06-23 17:29:21.490439','No compren aca',2,3),(3,5,'2026-06-15 00:04:54.000000','Excelente asado, abundante y bien servido.',16,11),(4,5,'2026-06-18 00:04:55.000000','La pizza llego caliente y muy rica. Recomendado.',17,12),(5,3,'2026-06-21 00:04:55.000000','Buena hamburguesa pero tardo bastante el envio.',18,13),(6,4,'2026-06-22 00:04:55.000000','Opciones veganas muy completas, volvere.',16,14),(7,5,'2026-06-23 00:04:56.000000','El mejor helado artesanal de la zona.',17,15),(8,4,'2026-07-22 00:25:05.161342','Muy rico',2,34),(9,5,'2026-07-24 23:14:23.136926','Muy rico',2,37);
/*!40000 ALTER TABLE `comentario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrediente`
--

DROP TABLE IF EXISTS `ingrediente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingrediente` (
  `id_ingrediente` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `restaurante_id` int DEFAULT NULL,
  PRIMARY KEY (`id_ingrediente`),
  KEY `FK14e4r92rhuean1jrb96bujv6w` (`restaurante_id`),
  CONSTRAINT `FK14e4r92rhuean1jrb96bujv6w` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurante` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingrediente`
--

LOCK TABLES `ingrediente` WRITE;
/*!40000 ALTER TABLE `ingrediente` DISABLE KEYS */;
INSERT INTO `ingrediente` VALUES (1,'Sal',3),(2,'Lechuga',3),(3,'Tomate',3),(4,'Queso Chedar',3),(5,'Gas',3),(6,'Asado de tira',11),(7,'Chorizo',11),(8,'Morcilla',11),(9,'Provolone',11),(10,'Ensalada criolla',11),(11,'Muzzarella',12),(12,'Salsa de tomate',12),(13,'Jamon',12),(14,'Aceitunas',12),(15,'Albahaca',12),(16,'Medallon de carne',13),(17,'Queso cheddar',13),(18,'Bacon',13),(19,'Lechuga',13),(20,'Tomate',13),(21,'Cebolla caramelizada',13),(22,'Garbanzos',14),(23,'Palta',14),(24,'Quinoa',14),(25,'Tofu',14),(26,'Dulce de leche',15),(27,'Chocolate',15),(28,'Frutilla',15),(29,'Ketchup',3),(30,'Mayonesa',3),(31,'Alioli',3),(32,'Salsas Barbacoa',3),(33,'Huevo',3),(34,'Jamon',3),(35,'Cebolla',3),(36,'Perejil',3),(37,'Ajo',3),(38,'Pepinillos',3),(39,'Sésamo',9),(40,'Queso crema',9),(41,'Salmón',9),(42,'Palta',9),(43,'Calabaza confitada',9),(44,'Wasabi',9),(45,'Gari',9),(46,'Nueces',9),(47,'Chips de boniato',9),(48,'Ciboulette',9),(49,'Salsa ceviche',9),(50,'Alga nori',9),(51,'Pepinillo',25),(52,'Lechuga',25),(53,'Pepinillos',26),(54,'Lechuga',26),(55,'Tomate',26),(56,'Pepenillos',27),(57,'Tomate',27),(58,'Lechuga',27),(59,'Jamón',3),(60,'Salsa de tomate',3),(61,'Muzzarella',3),(62,'Tomate',33),(63,'Pepinillos',33),(64,'Lechuga',33),(65,'Azucar',33),(66,'Pepinillo',34),(67,'Lechuga',34),(68,'Tomate',34),(69,'pepinillo',3),(70,'Pepinillo',37),(71,'Tomate',37);
/*!40000 ALTER TABLE `ingrediente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `linea_carrito`
--

DROP TABLE IF EXISTS `linea_carrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linea_carrito` (
  `id_linea` int NOT NULL AUTO_INCREMENT,
  `cantidad` int DEFAULT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `precio_unitario` float NOT NULL,
  `carrito_id` int DEFAULT NULL,
  `producto_id` int DEFAULT NULL,
  PRIMARY KEY (`id_linea`),
  KEY `FKlgw8d02epn0xe9pt9kqbfm2vc` (`carrito_id`),
  KEY `FKjxbncju4x50bhcwolq3ms690d` (`producto_id`),
  CONSTRAINT `FKjxbncju4x50bhcwolq3ms690d` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `FKlgw8d02epn0xe9pt9kqbfm2vc` FOREIGN KEY (`carrito_id`) REFERENCES `carrito` (`id_carrito`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `linea_carrito`
--

LOCK TABLES `linea_carrito` WRITE;
/*!40000 ALTER TABLE `linea_carrito` DISABLE KEYS */;
INSERT INTO `linea_carrito` VALUES (29,1,NULL,150,10,1),(36,1,'Isdaio',390,13,11),(119,2,'',315,50,10),(123,1,'',140,51,19);
/*!40000 ALTER TABLE `linea_carrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `linea_carrito_ingredientes_a_quitar`
--

DROP TABLE IF EXISTS `linea_carrito_ingredientes_a_quitar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linea_carrito_ingredientes_a_quitar` (
  `linea_carrito_id` int NOT NULL,
  `ingrediente_id` int NOT NULL,
  KEY `FKndbnbqpxis6losc9f5rntcy1u` (`ingrediente_id`),
  KEY `FKgnaxj7mp2iut7b7qoqmdjis3` (`linea_carrito_id`),
  CONSTRAINT `FKgnaxj7mp2iut7b7qoqmdjis3` FOREIGN KEY (`linea_carrito_id`) REFERENCES `linea_carrito` (`id_linea`),
  CONSTRAINT `FKndbnbqpxis6losc9f5rntcy1u` FOREIGN KEY (`ingrediente_id`) REFERENCES `ingrediente` (`id_ingrediente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `linea_carrito_ingredientes_a_quitar`
--

LOCK TABLES `linea_carrito_ingredientes_a_quitar` WRITE;
/*!40000 ALTER TABLE `linea_carrito_ingredientes_a_quitar` DISABLE KEYS */;
/*!40000 ALTER TABLE `linea_carrito_ingredientes_a_quitar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oferta`
--

DROP TABLE IF EXISTS `oferta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oferta` (
  `id_oferta` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `descuento` float NOT NULL,
  `fecha_fin` datetime(6) DEFAULT NULL,
  `fecha_inicio` datetime(6) DEFAULT NULL,
  `url_imagen` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_oferta`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oferta`
--

LOCK TABLES `oferta` WRITE;
/*!40000 ALTER TABLE `oferta` DISABLE KEYS */;
INSERT INTO `oferta` VALUES (1,'Porque nadie lo pidio',25,'2026-06-24 00:00:00.000000','2026-06-22 00:00:00.000000','https://res.cloudinary.com/diswgj0ld/image/upload/v1782174407/bd85f6f870f771b0f847b99cf4fb671c.jpg'),(2,'2x1 en hamburguesas - solo esta semana',25,'2026-07-25 00:04:24.000000','2026-06-24 00:04:24.000000','https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=1000&q=80&auto=format&fit=crop'),(3,'Pizza grande 20% off',20,'2026-07-25 00:04:24.000000','2026-06-24 00:04:24.000000','https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=1000&q=80&auto=format&fit=crop'),(4,'Postres a mitad de precio',50,'2026-07-25 00:04:25.000000','2026-06-24 00:04:25.000000','https://images.unsplash.com/photo-1488477181946-6428a0291777?w=1000&q=80&auto=format&fit=crop'),(5,'Vivi el mundial con un gramajo',25,'2026-07-03 00:00:00.000000','2026-06-25 00:00:00.000000','https://res.cloudinary.com/diswgj0ld/image/upload/v1782489025/ofertaGramajo.png'),(6,'Oferta invierno factory',20,'2026-07-31 00:00:00.000000','2026-06-23 00:00:00.000000','https://res.cloudinary.com/diswgj0ld/image/upload/v1782496287/salmonRoll.jpg'),(7,'Oferta invierno factory',30,'2026-07-30 00:00:00.000000','2026-06-23 00:00:00.000000','https://res.cloudinary.com/diswgj0ld/image/upload/v1782496430/rollatun.jpg'),(8,'Oferta invierno factory',20,'2026-07-31 00:00:00.000000','2026-06-23 00:00:00.000000','https://res.cloudinary.com/diswgj0ld/image/upload/v1782496287/salmonRoll.jpg'),(9,'Solo Por Julio',20,'2026-07-24 00:00:00.000000','2026-07-15 00:00:00.000000','https://res.cloudinary.com/diswgj0ld/image/upload/v1782501549/furai.jpg');
/*!40000 ALTER TABLE `oferta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id_pago` int NOT NULL AUTO_INCREMENT,
  `fecha_pago` datetime(6) DEFAULT NULL,
  `id_transaccion` varchar(255) DEFAULT NULL,
  `metodo_de_pago` enum('MercadoPago') DEFAULT NULL,
  `moneda` enum('UYU') DEFAULT NULL,
  `monto` float NOT NULL,
  `nro_ultim_dig_tarjeta` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_pago`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
INSERT INTO `pago` VALUES (1,'2026-06-21 17:08:48.426391',NULL,'MercadoPago','UYU',150,NULL),(2,'2026-06-21 17:12:40.275996','164363890321','MercadoPago','UYU',150,'3704'),(3,'2026-06-21 17:26:38.346751','165186044340','MercadoPago','UYU',150,'3704'),(4,'2026-06-21 18:09:10.337789','165189981050','MercadoPago','UYU',150,'3704'),(5,'2026-06-21 22:42:03.394738',NULL,'MercadoPago','UYU',150,NULL),(6,'2026-06-21 22:46:10.281797','165220291528','MercadoPago','UYU',150,'3704'),(7,'2026-06-22 12:20:53.529593','165274099066','MercadoPago','UYU',300,'3704'),(8,'2026-06-22 16:29:42.092234','165313883292','MercadoPago','UYU',150,'3704'),(9,'2026-06-22 16:37:15.319826','165315560274','MercadoPago','UYU',150,'0604'),(10,'2026-06-22 18:21:56.971456','164508396333','MercadoPago','UYU',150,'3704'),(11,'2026-06-22 18:52:18.492135','165333478952','MercadoPago','UYU',50,'3704'),(12,'2026-06-22 19:35:51.509502',NULL,'MercadoPago','UYU',250,NULL),(13,'2026-06-22 19:36:24.567906','165339037704','MercadoPago','UYU',250,'3704'),(14,'2026-06-22 22:36:20.967412','164546164677','MercadoPago','UYU',150,'3704'),(15,'2026-06-23 00:22:23.160364','165382622008','MercadoPago','UYU',50,'3704'),(20,'2026-06-23 16:13:11.739768','164639626149','MercadoPago','UYU',112.5,'3704'),(26,'2026-06-23 20:15:50.177964','164673031363','MercadoPago','UYU',112.5,'3704'),(27,'2026-06-23 23:23:41.496049','165526542796','MercadoPago','UYU',112.5,'3704'),(28,'2026-06-24 01:24:43.311987','164717393063','MercadoPago','UYU',50,'Billetera Electronica'),(29,'2026-06-15 00:04:56.000000','SEED-PED-0001','MercadoPago','UYU',1380,'4242'),(30,'2026-06-18 00:04:58.000000','SEED-PED-0002','MercadoPago','UYU',1044,'5588'),(31,'2026-06-24 23:35:00.000000','SEED-PED-0003','MercadoPago','UYU',825,'1111'),(32,'2026-06-24 23:50:03.000000','SEED-PED-0004','MercadoPago','UYU',610,'7777'),(33,'2026-06-23 00:05:04.000000','SEED-PED-0005','MercadoPago','UYU',650,'9090'),(34,'2026-06-25 00:00:06.000000','SEED-PED-0006','MercadoPago','UYU',440,'3030'),(35,'2026-06-25 16:44:20.269916','165765837184','MercadoPago','UYU',1290,'3704'),(36,'2026-06-25 16:45:12.164965','164939612163','MercadoPago','UYU',1290,'3704'),(39,'2026-06-25 19:55:31.377820','165791249626','MercadoPago','UYU',2580,'3704'),(40,'2026-06-25 20:15:57.309484','164967908235','MercadoPago','UYU',1670,'3704'),(41,'2026-06-25 20:48:42.858217','164972909729','MercadoPago','UYU',150,'3704'),(42,'2026-06-25 23:21:46.769603','164998027697','MercadoPago','UYU',1290,'3704'),(43,'2026-06-26 19:47:41.265224','165946175610','MercadoPago','UYU',672,'3704'),(44,'2026-06-26 20:54:28.106960','165957454550','MercadoPago','UYU',436,'3704'),(45,'2026-06-26 21:32:05.171043','165964598608','MercadoPago','UYU',209,'3704'),(46,'2026-06-26 21:39:58.039943','165138007231','MercadoPago','UYU',530,'3704'),(47,'2026-06-26 22:35:03.673559','165148332129','MercadoPago','UYU',520,'3704'),(48,'2026-06-26 22:51:55.687580','165151208841','MercadoPago','UYU',336,'3704'),(49,'2026-06-26 23:15:56.754567','165155442369','MercadoPago','UYU',240,'3704'),(50,'2026-06-26 23:39:41.445311','165159313607','MercadoPago','UYU',300,'3704'),(51,'2026-06-26 23:44:24.189092','165988188034','MercadoPago','UYU',446,'3704'),(52,'2026-06-27 02:07:40.604418','166007046262','MercadoPago','UYU',209,'3704'),(54,'2026-07-02 16:58:09.407724','166783895688','MercadoPago','UYU',150,'3704'),(55,'2026-07-02 21:49:16.116525','166014154015','MercadoPago','UYU',390,'3704'),(56,'2026-07-02 22:03:16.737017','166896204198','MercadoPago','UYU',1290,'3704'),(57,'2026-07-02 22:18:22.680824','166898798246','MercadoPago','UYU',1290,'3704'),(58,'2026-07-02 22:43:15.965601','166892844756','MercadoPago','UYU',1290,'3704'),(59,'2026-07-02 22:43:51.879373','166895802660','MercadoPago','UYU',432,'3704'),(60,'2026-07-02 22:44:45.348036','166903098312','MercadoPago','UYU',209,'3704'),(61,'2026-07-02 22:45:57.630763','166898072562','MercadoPago','UYU',432,'3704'),(62,'2026-07-02 22:51:28.549376','166898584584','MercadoPago','UYU',1290,'3704'),(63,'2026-07-02 22:52:21.266124','166901998474','MercadoPago','UYU',315,'3704'),(64,'2026-07-02 22:53:02.413295','166899340550','MercadoPago','UYU',410,'3704'),(65,'2026-07-02 22:54:04.285752','166899078626','MercadoPago','UYU',209,'3704'),(66,'2026-07-02 22:54:44.233778','166898642594','MercadoPago','UYU',315,'3704'),(67,'2026-07-03 02:35:12.256081','166896205920','MercadoPago','UYU',209,'3704'),(68,'2026-07-03 02:44:40.967813','166900257740','MercadoPago','UYU',1290,'3704'),(69,'2026-07-03 02:45:09.491824','166903089674','MercadoPago','UYU',209,'3704'),(70,'2026-07-03 02:49:17.149012','166897303980','MercadoPago','UYU',1290,'3704'),(71,'2026-07-03 02:50:17.396643','166896482018','MercadoPago','UYU',209,'3704'),(72,'2026-07-03 02:51:50.427262','166897189948','MercadoPago','UYU',1290,'3704'),(73,'2026-07-03 02:52:52.492768','166898643950','MercadoPago','UYU',209,'3704'),(74,'2026-07-04 01:36:11.620607','166262322507','MercadoPago','UYU',315,'0604'),(75,'2026-07-04 01:37:10.603413','166260521177','MercadoPago','UYU',1290,'3704'),(76,'2026-07-04 20:16:01.750250','167212577720','MercadoPago','UYU',410,'3704'),(77,'2026-07-04 20:17:35.494589','167211782058','MercadoPago','UYU',1290,'3704'),(79,'2026-07-09 17:05:30.870288','167144070095','MercadoPago','UYU',315,'3704'),(80,'2026-07-10 23:05:30.805733','167372176933','MercadoPago','UYU',250,'3704'),(81,'2026-07-16 22:03:05.232303','168282320935','MercadoPago','UYU',309,'3704'),(82,'2026-07-21 17:39:28.673589','169872746406','MercadoPago','UYU',250,'3704'),(83,'2026-07-21 22:58:35.039653','169921556658','MercadoPago','UYU',260,'3704'),(84,'2026-07-22 00:23:16.844902','169053914603','MercadoPago','UYU',400,'3704'),(85,'2026-07-22 00:28:06.926363','169053239109','MercadoPago','UYU',260,'3704'),(87,'2026-07-24 17:05:28.342511',NULL,'MercadoPago','UYU',460,NULL),(88,'2026-07-24 23:12:14.399846','170413705076','MercadoPago','UYU',400,'3704'),(89,'2026-07-24 23:17:07.086489','169531462539','MercadoPago','UYU',260,'3704');
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido` (
  `id_pedido` int NOT NULL AUTO_INCREMENT,
  `apartamento` varchar(255) DEFAULT NULL,
  `calle` varchar(255) DEFAULT NULL,
  `esquina` varchar(255) DEFAULT NULL,
  `latitud` double DEFAULT NULL,
  `longitud` double DEFAULT NULL,
  `numero` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `estado` enum('Cancelado','EnCamino','EnPreparacion','Entregado','Pagado','PagoRechazado','Reembolsado','Solicitado') NOT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_expiracion` datetime(6) DEFAULT NULL,
  `horario_entrega` datetime(6) DEFAULT NULL,
  `razon_cancelacion` varchar(255) DEFAULT NULL,
  `tiempo_preparacion` int DEFAULT NULL,
  `total` float NOT NULL,
  `cliente_id` int DEFAULT NULL,
  `pago_id` int DEFAULT NULL,
  `reclamo_id` int DEFAULT NULL,
  `restaurante_id` int DEFAULT NULL,
  PRIMARY KEY (`id_pedido`),
  UNIQUE KEY `UKfibo078ch1xjrp3bcq4piov4e` (`pago_id`),
  UNIQUE KEY `UKnox5axmdl3bctumfslw0ifrxa` (`reclamo_id`),
  KEY `FK3eud5cqmgsnltyk704hu3qj71` (`restaurante_id`),
  KEY `idx_pedido_cliente_fecha` (`cliente_id`,`fecha_creacion`),
  CONSTRAINT `FK30s8j2ktpay6of18lbyqn3632` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id_usuario`),
  CONSTRAINT `FK3eud5cqmgsnltyk704hu3qj71` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurante` (`id_usuario`),
  CONSTRAINT `FKkcpitr7lxw3ky3oaqmlqit7d1` FOREIGN KEY (`pago_id`) REFERENCES `pago` (`id_pago`),
  CONSTRAINT `FKoin26tyn1lae2b4kpu80vqxlk` FOREIGN KEY (`reclamo_id`) REFERENCES `reclamo` (`id_reclamo`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` VALUES (1,'','Liga Federal','',-34.8180138,-56.2800382,'1755 BIS','Liga Federal 1755 BIS','Cancelado','2026-06-21 17:08:48.412102',NULL,NULL,'Expirado: plazo de pago de 24 horas vencido',NULL,150,4,1,NULL,3),(2,'','Paysandú','',-34.9018,-56.1859,'1440','','Reembolsado','2026-06-21 17:12:13.897781',NULL,NULL,NULL,NULL,150,2,2,NULL,3),(3,'','Paysandú','',-34.9018,-56.1859,'1440','','Reembolsado','2026-06-21 17:26:27.600594',NULL,NULL,NULL,NULL,150,2,3,NULL,3),(4,'','Paysandú','',-34.9018,-56.1859,'1440','','Reembolsado','2026-06-21 18:08:47.468931',NULL,NULL,NULL,NULL,150,2,4,NULL,3),(5,NULL,'Liga Federal',NULL,-34.8183388,-56.2795247,'1755 BIS','','Cancelado','2026-06-21 22:42:03.368382',NULL,NULL,'Expirado: plazo de pago de 24 horas vencido',NULL,150,2,5,NULL,3),(6,NULL,'Liga Federal',NULL,-34.8183353,-56.2795205,'1755 BIS','','Reembolsado','2026-06-21 22:45:46.315964',NULL,NULL,NULL,NULL,150,2,6,NULL,3),(7,'','Liga Federal','',-34.8180138,-56.2800382,'1755 BIS','Liga Federal 1755 BIS','Entregado','2026-06-22 12:20:05.669025',NULL,'2026-06-22 12:23:30.067914',NULL,40,300,4,7,1,3),(8,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Reembolsado','2026-06-22 16:28:43.869843',NULL,NULL,NULL,172,150,2,8,NULL,3),(9,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Reembolsado','2026-06-22 16:36:52.089608',NULL,NULL,NULL,NULL,150,2,9,NULL,3),(10,'','Liga Federal','',-34.8180138,-56.2800382,'1755 BIS','Liga Federal 1755 BIS','Reembolsado','2026-06-22 18:21:45.708651',NULL,NULL,NULL,40,150,4,10,NULL,3),(11,'','Liga Federal','',-34.8180138,-56.2800382,'1755 BIS','Liga Federal 1755 BIS','Entregado','2026-06-22 18:52:07.833924',NULL,'2026-06-22 18:53:30.396699',NULL,20,50,4,11,NULL,3),(12,NULL,'Comandante Braga',NULL,-34.8859447,-56.1594475,'2673','','Cancelado','2026-06-22 19:35:51.501479',NULL,NULL,'Expirado: plazo de pago de 24 horas vencido',NULL,250,6,12,NULL,3),(13,NULL,'Comandante Braga',NULL,-34.8859447,-56.1594475,'2673','','Reembolsado','2026-06-22 19:36:08.363621',NULL,NULL,NULL,NULL,250,6,13,NULL,3),(14,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Entregado','2026-06-22 22:36:10.231831',NULL,'2026-06-22 22:39:04.957711',NULL,172,150,2,14,NULL,3),(15,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Reembolsado','2026-06-23 00:22:14.192258',NULL,NULL,NULL,152,50,2,15,NULL,3),(20,NULL,'Chaná',NULL,-34.9039059,-56.1645012,'2384','','Reembolsado','2026-06-23 16:12:52.026613',NULL,NULL,NULL,NULL,112.5,2,20,NULL,3),(26,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Reembolsado','2026-06-23 20:15:33.059715',NULL,NULL,NULL,NULL,112.5,2,26,NULL,3),(27,NULL,'Comandante Braga',NULL,-34.8859618,-56.1594589,'2673','','Reembolsado','2026-06-23 23:23:21.469142',NULL,NULL,NULL,NULL,112.5,6,27,NULL,3),(28,NULL,'Comandante Braga',NULL,-34.8859843,-56.159474,'2673','','Reembolsado','2026-06-24 01:24:29.325984',NULL,NULL,NULL,NULL,50,6,28,NULL,3),(29,'301','Av. Italia','Pereira',-34.90889,-56.1421,'2500','Casa','Entregado','2026-06-15 00:04:57.000000','2026-06-16 00:04:57.000000','2026-06-15 00:49:57.000000',NULL,35,1380,16,29,2,11),(30,NULL,'Rambla Rep. del Peru','Pena',-34.919,-56.151,'1300','Trabajo','Entregado','2026-06-18 00:04:59.000000','2026-06-19 00:04:59.000000','2026-06-18 00:44:59.000000',NULL,24,1044,17,30,NULL,12),(31,'5B','Canelones','Tristan Narvaja',-34.9042,-56.1802,'1820','Casa','EnCamino','2026-06-24 23:35:01.000000','2026-06-25 23:05:01.000000','2026-06-25 00:20:01.000000',NULL,18,825,18,31,NULL,13),(32,'301','Av. Italia','Pereira',-34.90889,-56.1421,'2500','Casa','EnPreparacion','2026-06-24 23:50:03.000000','2026-06-25 23:05:03.000000','2026-06-25 00:35:03.000000',NULL,14,610,16,32,NULL,14),(33,NULL,'Rambla Rep. del Peru','Pena',-34.919,-56.151,'1300','Trabajo','Cancelado','2026-06-23 00:05:05.000000','2026-06-24 00:05:05.000000',NULL,'El local cerro antes de preparar el pedido',NULL,650,17,33,NULL,13),(34,'5B','Canelones','Tristan Narvaja',-34.9042,-56.1802,'1820','Casa','Pagado','2026-06-25 00:00:06.000000','2026-06-25 23:05:06.000000','2026-06-25 00:45:06.000000',NULL,NULL,440,18,34,NULL,15),(35,NULL,'Comandante Braga',NULL,-34.8859445,-56.1594633,'2673','','Reembolsado','2026-06-25 16:44:04.638553',NULL,NULL,NULL,NULL,1290,6,35,NULL,3),(36,NULL,'Comandante Braga',NULL,-34.8859445,-56.1594633,'2673','','Reembolsado','2026-06-25 16:44:58.126276',NULL,NULL,NULL,NULL,1290,6,36,NULL,3),(39,NULL,'Comandante Braga',NULL,-34.885952,-56.1594731,'2673','','Reembolsado','2026-06-25 19:55:14.721427',NULL,NULL,NULL,NULL,2580,6,39,NULL,11),(40,NULL,'Comandante Braga',NULL,-34.8859594,-56.1594666,'2673','','Reembolsado','2026-06-25 20:15:32.024591',NULL,NULL,NULL,NULL,1670,6,40,NULL,11),(41,NULL,'Comandante Braga',NULL,-34.8859355,-56.1594646,'2673','','Entregado','2026-06-25 20:48:29.013573',NULL,'2026-06-25 21:39:13.130606',NULL,23,150,6,41,NULL,3),(42,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Pagado','2026-06-25 23:21:35.156281',NULL,NULL,NULL,NULL,1290,2,42,NULL,11),(43,NULL,'Comandante Braga',NULL,-34.8859835,-56.1594846,'2673','','Reembolsado','2026-06-26 19:47:25.467167',NULL,NULL,NULL,20,672,6,43,3,9),(44,NULL,'Comandante Braga',NULL,-34.8859408,-56.1594738,'2673','','Entregado','2026-06-26 20:54:10.272266',NULL,'2026-06-26 20:57:47.825723',NULL,21,436,6,44,NULL,9),(45,NULL,'Comandante Braga',NULL,-34.8859428,-56.1594735,'2673','','Entregado','2026-06-26 21:31:43.787454',NULL,'2026-06-26 21:39:00.386246',NULL,21,209,6,45,NULL,9),(46,NULL,'Comandante Braga',NULL,-34.8859611,-56.1594595,'2673','','Entregado','2026-06-26 21:39:43.423106',NULL,'2026-06-26 21:41:09.496320',NULL,20,530,6,46,NULL,9),(47,NULL,'Comandante Braga',NULL,-34.8859117,-56.1594021,'2673','','Reembolsado','2026-06-26 22:34:49.706078',NULL,NULL,NULL,NULL,520,6,47,NULL,9),(48,'','Comandante Braga','',-34.885927,-56.1594429,'2676','','Reembolsado','2026-06-26 22:51:45.759992',NULL,NULL,NULL,21,336,6,48,NULL,9),(49,NULL,'Comandante Braga',NULL,-34.8859117,-56.1594021,'2673','','Reembolsado','2026-06-26 23:15:23.438849',NULL,NULL,NULL,20,240,6,49,4,9),(50,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Entregado','2026-06-26 23:39:28.124533',NULL,'2026-06-26 23:40:37.549797',NULL,172,300,2,50,NULL,3),(51,'6','Comandante Braga',NULL,-34.885908,-56.1593702,'2677','Oficina','Reembolsado','2026-06-26 23:44:10.311425',NULL,NULL,NULL,20,446,6,51,5,9),(52,NULL,'Comandante Braga',NULL,-34.8858744,-56.1594674,'2673','','Reembolsado','2026-06-27 02:07:24.977857',NULL,NULL,NULL,NULL,209,6,52,NULL,9),(54,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Reembolsado','2026-07-02 16:57:58.109020',NULL,NULL,NULL,172,150,2,54,NULL,3),(55,NULL,'Comandante Braga',NULL,-34.88597,-56.1594543,'2673','','Reembolsado','2026-07-02 21:48:59.536098',NULL,NULL,NULL,NULL,390,6,55,NULL,13),(56,NULL,'Comandante Braga',NULL,-34.8859875,-56.1594579,'2673','','Reembolsado','2026-07-02 22:02:59.200916',NULL,NULL,NULL,NULL,1290,6,56,NULL,13),(57,NULL,'Comandante Braga',NULL,-34.8859707,-56.1594339,'2673','','Reembolsado','2026-07-02 22:18:09.526674',NULL,NULL,NULL,NULL,1290,6,57,NULL,13),(58,NULL,'Comandante Braga',NULL,-34.8859899,-56.1594092,'2673','','Reembolsado','2026-07-02 22:43:01.770652',NULL,NULL,NULL,NULL,1290,6,58,NULL,13),(59,NULL,'Comandante Braga',NULL,-34.8859899,-56.1594092,'2673','','Reembolsado','2026-07-02 22:43:38.213871',NULL,NULL,NULL,NULL,432,6,59,NULL,13),(60,NULL,'Comandante Braga',NULL,-34.8859899,-56.1594092,'2673','','Reembolsado','2026-07-02 22:44:31.397191',NULL,NULL,NULL,NULL,209,6,60,NULL,9),(61,NULL,'Comandante Braga',NULL,-34.8859899,-56.1594092,'2673','','Reembolsado','2026-07-02 22:45:44.177448',NULL,NULL,NULL,NULL,432,6,61,NULL,12),(62,NULL,'Comandante Braga',NULL,-34.8859288,-56.1594438,'2673','','Reembolsado','2026-07-02 22:51:15.902870',NULL,NULL,NULL,NULL,1290,6,62,NULL,11),(63,NULL,'Comandante Braga',NULL,-34.8859288,-56.1594438,'2673','','Reembolsado','2026-07-02 22:52:04.185983',NULL,NULL,NULL,NULL,315,6,63,NULL,13),(64,NULL,'Comandante Braga',NULL,-34.8859288,-56.1594438,'2673','','Reembolsado','2026-07-02 22:52:49.178556',NULL,NULL,NULL,NULL,410,6,64,NULL,13),(65,NULL,'Comandante Braga',NULL,-34.8859288,-56.1594438,'2673','','Reembolsado','2026-07-02 22:53:50.370417',NULL,NULL,NULL,NULL,209,6,65,NULL,9),(66,NULL,'Comandante Braga',NULL,-34.8859288,-56.1594438,'2673','','Reembolsado','2026-07-02 22:54:30.254841',NULL,NULL,NULL,NULL,315,6,66,NULL,9),(67,NULL,'Comandante Braga',NULL,-34.8859347,-56.1594615,'2673','','Reembolsado','2026-07-03 02:34:56.947481',NULL,NULL,NULL,21,209,6,67,6,9),(68,NULL,'Comandante Braga',NULL,-34.8859634,-56.1594566,'2673','','Reembolsado','2026-07-03 02:44:27.552081',NULL,NULL,NULL,NULL,1290,6,68,NULL,9),(69,NULL,'Comandante Braga',NULL,-34.8859634,-56.1594566,'2673','','Reembolsado','2026-07-03 02:44:56.241524',NULL,NULL,NULL,NULL,209,6,69,NULL,9),(70,'1','Comandante Braga','',-34.8859421,-56.1594575,'2676','Comandante Braga 2676','Reembolsado','2026-07-03 02:48:54.542524',NULL,NULL,NULL,NULL,1290,6,70,NULL,11),(71,'1','Comandante Braga','',-34.8859421,-56.1594575,'2676','Comandante Braga 2676','Reembolsado','2026-07-03 02:50:01.113779',NULL,NULL,NULL,NULL,209,6,71,NULL,9),(72,'1','Comandante Braga','',-34.8859421,-56.1594575,'2676','Comandante Braga 2676','Reembolsado','2026-07-03 02:51:30.446946',NULL,NULL,NULL,NULL,1290,6,72,NULL,11),(73,'1','Comandante Braga','',-34.8859421,-56.1594575,'2676','Comandante Braga 2676','Reembolsado','2026-07-03 02:52:40.431784',NULL,NULL,NULL,NULL,209,6,73,NULL,11),(74,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Pagado','2026-07-04 01:35:56.235865',NULL,NULL,NULL,NULL,315,2,74,NULL,13),(75,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Reembolsado','2026-07-04 01:37:00.981822',NULL,NULL,NULL,NULL,1290,2,75,NULL,11),(76,NULL,'Comandante Braga',NULL,-34.8859435,-56.1594571,'2673','','Reembolsado','2026-07-04 20:15:43.778071',NULL,NULL,NULL,21,410,6,76,7,9),(77,NULL,'Comandante Braga',NULL,-34.8859435,-56.1594571,'2673','','Reembolsado','2026-07-04 20:17:21.780466',NULL,NULL,NULL,NULL,1290,6,77,NULL,11),(79,'1','Comandante Braga',NULL,-34.8859421,-56.1594575,'2676','Casa','Reembolsado','2026-07-09 17:05:09.733158',NULL,NULL,NULL,NULL,315,6,79,NULL,13),(80,'1','18 De Julio','',-34.0082394,-57.6461326,'123','18 De Julio 123','Reembolsado','2026-07-10 23:05:21.259720',NULL,NULL,NULL,NULL,250,2,80,NULL,27),(81,NULL,'Comandante Braga',NULL,-34.8859182,-56.1594013,'2673','','Reembolsado','2026-07-16 22:02:48.380128',NULL,NULL,NULL,20,309,6,81,8,9),(82,'','Avenida General Rivera','Tomás de Tezanos',-34.902335,-56.173578,'3729','Avenida General Rivera 3729','Entregado','2026-07-21 17:38:04.753657',NULL,'2026-07-21 17:41:03.244722',NULL,38,250,2,82,NULL,33),(83,NULL,'Avenida General Rivera',NULL,-34.8993694,-56.131822,'3729 BIS','','Reembolsado','2026-07-21 22:58:17.145798',NULL,NULL,NULL,36,260,6,83,9,3),(84,'','Avenida General Rivera','Tomás de Tezanos',-34.902335,-56.173578,'3729','Avenida General Rivera 3729','Entregado','2026-07-22 00:23:04.663465',NULL,'2026-07-22 00:24:18.298355',NULL,23,400,2,84,NULL,34),(85,NULL,'Avenida General Rivera',NULL,-34.8994406,-56.1314384,'3729 BIS','Utu','Reembolsado','2026-07-22 00:27:50.333629',NULL,NULL,NULL,36,260,6,85,10,3),(87,'','Julio Herrera y Reissig','',-34.91891891891892,-56.16602103383463,'565','','Solicitado','2026-07-24 17:05:28.332937','2026-07-25 17:05:28.332937',NULL,NULL,NULL,460,36,87,NULL,14),(88,'','Avenida General Rivera','Tomás de Tezanos',-34.902335,-56.173578,'3729','Avenida General Rivera 3729','Entregado','2026-07-24 23:11:58.122927',NULL,'2026-07-24 23:13:30.177653',NULL,23,400,2,88,NULL,37),(89,NULL,'Avenida General Rivera',NULL,-34.899274,-56.1316289,'3729 BIS','','Reembolsado','2026-07-24 23:16:47.162572',NULL,NULL,NULL,36,260,6,89,11,3);
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plato`
--

DROP TABLE IF EXISTS `plato`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plato` (
  `tiempo_preparacion_minutos` int DEFAULT NULL,
  `id_producto` int NOT NULL,
  PRIMARY KEY (`id_producto`),
  CONSTRAINT `FKlhijo4adftl0rtetbjgg4dgmq` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plato`
--

LOCK TABLES `plato` WRITE;
/*!40000 ALTER TABLE `plato` DISABLE KEYS */;
INSERT INTO `plato` VALUES (20,1),(35,3),(12,4),(22,6),(24,7),(10,8),(15,10),(18,11),(12,12),(14,15),(10,16),(5,18),(6,19),(25,21),(30,22),(25,23),(20,24),(30,25),(20,26),(20,27),(20,28),(20,29),(20,30),(20,31),(20,32),(20,33),(20,34),(20,35),(20,36),(20,38),(20,39),(25,44),(20,45),(20,47),(20,48),(30,49),(30,50),(30,51),(20,54),(1,55),(20,56),(20,58);
/*!40000 ALTER TABLE `plato` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plato_ingredientes`
--

DROP TABLE IF EXISTS `plato_ingredientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plato_ingredientes` (
  `plato_id_producto` int NOT NULL,
  `ingredientes_id_ingrediente` int NOT NULL,
  KEY `FKm4t4hcie1y8evw1qaw8ndoti8` (`ingredientes_id_ingrediente`),
  KEY `FK6grr1yiwy1yyuxage0sfbtrqr` (`plato_id_producto`),
  CONSTRAINT `FK6grr1yiwy1yyuxage0sfbtrqr` FOREIGN KEY (`plato_id_producto`) REFERENCES `plato` (`id_producto`),
  CONSTRAINT `FKm4t4hcie1y8evw1qaw8ndoti8` FOREIGN KEY (`ingredientes_id_ingrediente`) REFERENCES `ingrediente` (`id_ingrediente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plato_ingredientes`
--

LOCK TABLES `plato_ingredientes` WRITE;
/*!40000 ALTER TABLE `plato_ingredientes` DISABLE KEYS */;
INSERT INTO `plato_ingredientes` VALUES (3,6),(3,7),(3,8),(3,10),(4,9),(6,11),(6,12),(7,11),(7,12),(7,15),(10,16),(10,17),(10,19),(10,20),(11,16),(11,17),(11,18),(11,21),(15,22),(15,23),(15,24),(16,25),(3,6),(3,7),(3,8),(3,10),(4,9),(6,11),(6,12),(7,11),(7,12),(7,15),(10,16),(10,17),(10,19),(10,20),(11,16),(11,17),(11,18),(11,21),(15,22),(15,23),(15,24),(16,25),(22,29),(22,30),(22,31),(24,34),(24,35),(24,36),(24,37),(24,33),(27,43),(27,44),(27,45),(28,42),(28,44),(28,45),(28,39),(26,39),(26,40),(26,41),(26,42),(29,46),(29,44),(29,45),(30,44),(30,45),(30,47),(33,47),(33,48),(34,49),(34,46),(35,50),(36,50),(44,44),(44,45),(45,51),(45,52),(47,53),(47,54),(48,56),(48,57),(25,29),(25,4),(25,38),(1,1),(1,2),(1,3),(1,4),(49,59),(49,60),(49,61),(50,2),(50,3),(50,33),(50,30),(50,29),(54,62),(54,63),(55,65),(56,66),(56,67),(51,3),(51,29),(51,30),(51,33),(23,32),(58,70),(58,71);
/*!40000 ALTER TABLE `plato_ingredientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `disponible` bit(1) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `oferta_activa` bit(1) NOT NULL,
  `precio` float NOT NULL,
  `url_imagen` varchar(255) DEFAULT NULL,
  `oferta_id` int DEFAULT NULL,
  `restaurante_id` int DEFAULT NULL,
  `subcategoria_id` int DEFAULT NULL,
  PRIMARY KEY (`id_producto`),
  KEY `FKqwncnlqbfdnr162cv07higxn8` (`oferta_id`),
  KEY `FKo074wjiokigucsm0t0mayvmde` (`restaurante_id`),
  KEY `FK7pxra7tvdpke7288skg47g3ht` (`subcategoria_id`),
  CONSTRAINT `FK7pxra7tvdpke7288skg47g3ht` FOREIGN KEY (`subcategoria_id`) REFERENCES `sub_categoria` (`id_sub_categoria`),
  CONSTRAINT `FKo074wjiokigucsm0t0mayvmde` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurante` (`id_usuario`),
  CONSTRAINT `FKqwncnlqbfdnr162cv07higxn8` FOREIGN KEY (`oferta_id`) REFERENCES `oferta` (`id_oferta`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Una bomba de hamburguesa',_binary '','Monstruo',_binary '\0',150,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782061155/a-closeup-magazine-quality-shot-of-a-luscious-hambu-free-photo.jpg',1,3,11),(2,'Pepsi 354ml',_binary '','Refresco',_binary '\0',50,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782425090/pepsicero.webp',NULL,3,7),(3,'Asado, chorizo, morcilla y achuras. Incluye guarnicion.',_binary '','Parrillada para dos',_binary '\0',1290,'https://images.unsplash.com/photo-1544025162-d76694265947?w=900&q=80&auto=format&fit=crop',NULL,11,3),(4,'Queso provolone fundido con oregano.',_binary '','Provolone a la parrilla',_binary '\0',380,'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=900&q=80&auto=format&fit=crop',NULL,11,4),(5,'Agua sin gas.',_binary '','Agua mineral 600ml',_binary '\0',90,'https://images.unsplash.com/photo-1560023907-5f339617ea30?w=900&q=80&auto=format&fit=crop',NULL,11,7),(6,'Masa a la piedra, salsa y muzzarella.',_binary '','Pizza muzzarella',_binary '\0',540,'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=900&q=80&auto=format&fit=crop',3,12,3),(7,'Tomate, ajo, albahaca y muzzarella.',_binary '','Pizza napolitana',_binary '\0',620,'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=900&q=80&auto=format&fit=crop',3,12,3),(8,'Porcion de faina de garbanzo.',_binary '','Faina',_binary '\0',160,'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=900&q=80&auto=format&fit=crop',NULL,12,5),(9,'Bebida cola retornable.',_binary '','Coca-Cola 1.5L',_binary '\0',180,'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=900&q=80&auto=format&fit=crop',NULL,12,7),(10,'Medallon 150g, cheddar, lechuga y tomate.',_binary '','Hamburguesa clasica',_binary '\0',420,'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=900&q=80&auto=format&fit=crop',2,13,3),(11,'Doble medallon, bacon y cebolla caramelizada.',_binary '','Hamburguesa bacon',_binary '\0',520,'https://images.unsplash.com/photo-1550547660-d9450f859349?w=900&q=80&auto=format&fit=crop',2,13,3),(12,'Porcion grande con cascara.',_binary '','Papas rusticas',_binary '\0',230,'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=900&q=80&auto=format&fit=crop',NULL,13,5),(13,'Bebida lima-limon.',_binary '','Sprite 500ml',_binary '\0',110,'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=900&q=80&auto=format&fit=crop',NULL,13,7),(14,'Hamburguesa clasica + papas rusticas + Sprite.',_binary '','Combo clasico',_binary '\0',650,'https://images.unsplash.com/photo-1550547660-d9450f859349?w=900&q=80&auto=format&fit=crop',NULL,13,3),(15,'Quinoa, garbanzos, palta y vegetales asados.',_binary '','Bowl de quinoa',_binary '\0',460,'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=900&q=80&auto=format&fit=crop',NULL,14,3),(16,'Lechuga, croutons, aderezo de anacardos y tofu.',_binary '','Ensalada cesar vegana',_binary '\0',390,'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=900&q=80&auto=format&fit=crop',NULL,14,6),(17,'Exprimido del dia, 400ml.',_binary '','Jugo natural de naranja',_binary '\0',150,'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=900&q=80&auto=format&fit=crop',NULL,14,7),(18,'Hasta 3 gustos a eleccion.',_binary '','Cuarto de helado',_binary '\0',320,'https://images.unsplash.com/photo-1501443762994-82bd5dba89dc?w=900&q=80&auto=format&fit=crop',4,15,8),(19,'Helado, dulce de leche, brownie y crema.',_binary '','Copa Crema especial',_binary '\0',280,'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=900&q=80&auto=format&fit=crop',4,15,8),(20,'Pote familiar para llevar.',_binary '','Pote 1 litro',_binary '\0',540,'https://images.unsplash.com/photo-1501443762994-82bd5dba89dc?w=900&q=80&auto=format&fit=crop',NULL,15,8),(21,'Papas rusticas con salsa brava estilo Español',_binary '','Patatas Bravas',_binary '\0',210,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782423809/bravas.jpg',NULL,3,9),(22,'Clasicas papas noisette 100% caceras',_binary '','Papas noisette',_binary '\0',260,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782424803/noisette.webp',NULL,3,9),(23,'Ricos y crujientes aros de cebolla',_binary '','Aros de cebolla',_binary '\0',190,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782440020/arosCebolla.jpg',NULL,3,4),(24,'Papas paille con jamon, cebolla, huevo, ajo y perejil',_binary '','Revuelto gramajo',_binary '\0',240,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782488445/gramajo.jpg',5,3,9),(25,'Hamburguesa simple con queso cheddar, ketchup y pepinillos\n',_binary '','Cheeseburger',_binary '\0',210,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782489479/cheesburger.jpg',NULL,3,11),(26,'Temaki relleno de arroz, salmon y palta',_binary '','Temaki Salmon',_binary '\0',209,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782494928/temakiSalmon.jpg',NULL,9,10),(27,'Roll de 6 piezas, relleno de langostino grillado, queso crema, zuquini y palta, con cobertura de calabaza confitada. ',_binary '','Langostino roll',_binary '\0',410,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782496064/langostino.jpg',NULL,9,10),(28,'Roll de 6 piezas relleno de salmón, queso crema, palta y cibulette, cubierto de palta',_binary '','Roll Salmón',_binary '',420,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782496287/salmonRoll.jpg',8,9,10),(29,'Roll de 6 piezas de atún fresco, palta, queso crema, cubierta de nueces',_binary '','Roll atún',_binary '',480,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782496430/rollatun.jpg',7,9,10),(30,'Roll de 6 piezas rellenas de salmón cocido, y queso crema, cubiero de chips de boniato.',_binary '','Roll salmón cocido',_binary '\0',410,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782497104/salmoncocido.jpg',NULL,9,10),(31,'Nigiri de salmon: Arroz y salmón',_binary '','Nigiri salmon',_binary '\0',100,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782500597/nigiri.jpg',NULL,9,10),(32,'Nigiri de atún fresco',_binary '','Nigiri atún',_binary '\0',110,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782500706/nigiriatun.jpg',NULL,9,10),(33,'2 Nigiris de corvina grillada con chips de boniato y cibulette',_binary '','Nigiri Osaka',_binary '\0',210,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782501072/ozaca.jpg',NULL,9,10),(34,'2 Nigiris de atun con salsa de ceviche y nueces',_binary '','Nigiri Hokkaido',_binary '\0',210,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782501182/Hokkaido.webp',NULL,9,10),(35,'Nigiri de pulpo grillado',_binary '','Nigiri Pulpo',_binary '\0',130,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782501335/pulpo.jpg',NULL,9,10),(36,'Nigiri de langostino empanado',_binary '','Nigiri Furai',_binary '\0',140,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782501549/furai.jpg',9,9,10),(37,'Tabla especial de 8 nigiris de la casa',_binary '','Tabla de nigiris',_binary '\0',810,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782501767/tabla.jpg',NULL,9,10),(38,'Roll de 6 piezas de sushi maki de atun, palta, cibulette y zucchini.',_binary '','Roll maki atún',_binary '\0',420,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782501882/maki%20atun.jpg',NULL,9,10),(39,'Roll de 6 piezas de sushi maki de salmon, queso crema y vibulette',_binary '','Roll maki salmón',_binary '\0',430,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782502031/makisalmon.jpg',NULL,9,10),(40,'Combo de 24 piezas de sushi',_binary '','Combo Asakusa',_binary '\0',1150,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782502308/tablasushi.jpg',NULL,9,10),(41,'Sake 180 ml',_binary '','Sake',_binary '\0',490,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782502344/sake.jpg',NULL,9,7),(42,'Sprite 600ml',_binary '','Sprite 600ml',_binary '\0',110,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782502442/sprite.jpg',NULL,9,2),(43,'Refresco matcha 240ml',_binary '','Refresco matcha',_binary '\0',260,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782502614/macha.jpg',NULL,9,2),(44,'Roll de 6 piezas de salmón cocido empanado con panko y frito',_binary '','Hot roll Salmón',_binary '\0',480,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782510740/hotroll.jpg',NULL,9,10),(45,'La Mejor hambu de la historia',_binary '','Megaton',_binary '\0',200,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782516941/Plato1.png',NULL,25,1),(46,'Coca',_binary '','Coca-cola',_binary '\0',70,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782516964/images.jpg',NULL,25,2),(47,'La Mejor hambu',_binary '','Megalodon',_binary '\0',700,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782516941/Plato1.png',NULL,26,1),(48,'La Mejor Hambu de la historia te partis la boca',_binary '','Monstruo',_binary '\0',250,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782516941/Plato1.png',NULL,27,1),(49,'Milanesa de ternera con jamón, muzzarella y salsa de tomate, acompañada de papas fritas.',_binary '','Milanesa Napolitana',_binary '\0',420,'https://res.cloudinary.com/diswgj0ld/image/upload/v1784298564/milanapo.jpg',NULL,3,14),(50,'Milanesa de ternera al pan con tomate, lechuga y huevo, acompañada de papas fritas',_binary '','Mila de carne al pan',_binary '\0',380,'https://res.cloudinary.com/diswgj0ld/image/upload/v1784298789/milaalpan.jpg',NULL,3,14),(51,'Milanesa de pollo al pan con tomate, lechuga y huevo, acompañada de papas fritas',_binary '\0','Mila de pollo al pan',_binary '\0',380,'https://res.cloudinary.com/diswgj0ld/image/upload/v1784298789/milaalpan.jpg',NULL,3,14),(52,'Patricia en lata de 473ml',_binary '','Patricia 473ml',_binary '\0',100,'https://res.cloudinary.com/diswgj0ld/image/upload/v1784307912/patricia473.webp',NULL,3,2),(53,'Cheeseburger mas aros de cebolla',_binary '','Combo Burger',_binary '\0',760,'https://res.cloudinary.com/diswgj0ld/image/upload/v1784308101/comboburger.jpg',NULL,3,1),(54,'La Mejor Hambu de la historia te partis la boca',_binary '','Monstruo',_binary '\0',250,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782516941/Plato1.png',NULL,33,1),(55,'Coca 750ml',_binary '','CocaCola',_binary '\0',75,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782516964/images.jpg',NULL,33,7),(56,'La Mejor Hambu de la historia te partis la boca',_binary '','Monstrueo',_binary '\0',250,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782516941/Plato1.png',NULL,34,1),(57,'750ml',_binary '','Coca',_binary '\0',75,'https://res.cloudinary.com/diswgj0ld/image/upload/v1784679688/coca.jpg',NULL,34,7),(58,'La mejor hambu',_binary '','Monstruo',_binary '\0',250,'https://res.cloudinary.com/diswgj0ld/image/upload/v1782516941/Plato1.png',NULL,37,1),(59,'750ml',_binary '','CocaCola',_binary '\0',75,'https://res.cloudinary.com/diswgj0ld/image/upload/v1784679688/coca.jpg',NULL,37,7);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto_pedido`
--

DROP TABLE IF EXISTS `producto_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_pedido` (
  `id_producto_pedido` int NOT NULL AUTO_INCREMENT,
  `cantidad` int DEFAULT NULL,
  `comentario_cliente` varchar(255) DEFAULT NULL,
  `precio_suma` float NOT NULL,
  `pedido_id` int DEFAULT NULL,
  `producto_id` int DEFAULT NULL,
  PRIMARY KEY (`id_producto_pedido`),
  KEY `FKs7y9f12xlx4xop8pbm9y5wklg` (`pedido_id`),
  KEY `FKchs8ufpumoy4b3bhxnhh982o9` (`producto_id`),
  CONSTRAINT `FKchs8ufpumoy4b3bhxnhh982o9` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `FKs7y9f12xlx4xop8pbm9y5wklg` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id_pedido`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto_pedido`
--

LOCK TABLES `producto_pedido` WRITE;
/*!40000 ALTER TABLE `producto_pedido` DISABLE KEYS */;
INSERT INTO `producto_pedido` VALUES (1,1,'',150,1,1),(2,1,'',150,2,1),(3,1,'',150,3,1),(4,1,'',150,4,1),(5,1,NULL,150,5,1),(6,1,NULL,150,6,1),(7,2,'',300,7,1),(8,1,'',150,8,1),(9,1,'',150,9,1),(10,1,'',150,10,1),(11,1,'',50,11,2),(12,2,NULL,100,12,2),(13,1,NULL,150,12,1),(14,2,NULL,100,13,2),(15,1,NULL,150,13,1),(16,1,'',150,14,1),(17,1,'',50,15,2),(24,1,NULL,112.5,20,1),(32,1,'',112.5,26,1),(33,1,NULL,112.5,27,1),(34,1,NULL,50,28,2),(35,1,'Sin sal en la carne',1290,29,3),(36,1,NULL,90,29,5),(37,2,NULL,864,30,6),(38,1,NULL,180,30,9),(39,1,'Sin cebolla',390,31,11),(40,1,NULL,230,31,12),(41,1,NULL,110,31,13),(42,1,NULL,460,32,15),(43,1,NULL,150,32,17),(44,1,NULL,650,33,14),(45,1,'3 gustos: dulce de leche, chocolate, frutilla',160,34,18),(46,1,NULL,140,34,19),(47,1,NULL,1290,35,3),(48,1,NULL,1290,36,3),(51,2,NULL,2580,39,3),(52,1,NULL,380,40,4),(53,1,NULL,1290,40,3),(54,1,NULL,150,41,1),(55,1,'',1290,42,3),(56,1,NULL,336,43,29),(57,1,NULL,336,43,28),(58,1,NULL,336,44,29),(59,1,NULL,100,44,31),(60,1,NULL,209,45,26),(61,1,NULL,110,46,32),(62,1,NULL,210,46,34),(63,1,NULL,210,46,33),(64,1,NULL,410,47,30),(65,1,NULL,110,47,32),(66,1,'',336,48,28),(67,1,NULL,130,49,35),(68,1,NULL,110,49,42),(69,2,'',300,50,1),(70,1,NULL,336,51,29),(71,1,NULL,110,51,42),(72,1,NULL,209,52,26),(74,1,'',150,54,1),(75,1,NULL,390,55,11),(76,1,NULL,1290,56,3),(77,1,NULL,1290,57,3),(78,1,NULL,1290,58,3),(79,1,NULL,432,59,6),(80,1,NULL,209,60,26),(81,1,NULL,432,61,6),(82,1,NULL,1290,62,3),(83,1,NULL,315,63,10),(84,1,NULL,410,64,27),(85,1,NULL,209,65,26),(86,1,NULL,315,66,10),(87,1,NULL,209,67,26),(88,1,NULL,1290,68,3),(89,1,NULL,209,69,26),(90,1,'',1290,70,3),(91,1,'',209,71,26),(92,1,'',1290,72,3),(93,1,'',209,73,26),(94,1,'',315,74,10),(95,1,'',1290,75,3),(96,1,NULL,410,76,27),(97,1,NULL,1290,77,3),(101,1,NULL,315,79,10),(102,1,'',250,80,48),(103,1,NULL,209,81,26),(104,1,NULL,100,81,31),(105,1,'no sal',250,82,54),(106,1,NULL,210,83,25),(107,1,NULL,50,83,2),(108,1,'sin sal',250,84,56),(109,2,'',150,84,57),(110,1,NULL,210,85,25),(111,1,NULL,50,85,2),(113,1,'',460,87,15),(114,1,'sin sal',250,88,58),(115,2,'',150,88,59),(116,1,NULL,210,89,25),(117,1,NULL,50,89,2);
/*!40000 ALTER TABLE `producto_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto_pedido_ingredientesaquitar`
--

DROP TABLE IF EXISTS `producto_pedido_ingredientesaquitar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_pedido_ingredientesaquitar` (
  `producto_pedido_id_producto_pedido` int NOT NULL,
  `ingredientesaquitar_id_ingrediente` int NOT NULL,
  KEY `FK5ii4ja9h2suxyurwxalita8kh` (`ingredientesaquitar_id_ingrediente`),
  KEY `FKritpgdqr745qccmpcjjv0k3pj` (`producto_pedido_id_producto_pedido`),
  CONSTRAINT `FK5ii4ja9h2suxyurwxalita8kh` FOREIGN KEY (`ingredientesaquitar_id_ingrediente`) REFERENCES `ingrediente` (`id_ingrediente`),
  CONSTRAINT `FKritpgdqr745qccmpcjjv0k3pj` FOREIGN KEY (`producto_pedido_id_producto_pedido`) REFERENCES `producto_pedido` (`id_producto_pedido`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto_pedido_ingredientesaquitar`
--

LOCK TABLES `producto_pedido_ingredientesaquitar` WRITE;
/*!40000 ALTER TABLE `producto_pedido_ingredientesaquitar` DISABLE KEYS */;
INSERT INTO `producto_pedido_ingredientesaquitar` VALUES (1,1),(2,1),(3,1),(4,1),(7,1),(8,2),(9,2),(13,4),(13,3),(15,4),(15,3),(16,2),(24,4),(32,2),(47,10),(47,8),(47,7),(47,6),(53,7),(69,1),(74,2),(102,56),(105,62),(108,66),(113,23),(114,70);
/*!40000 ALTER TABLE `producto_pedido_ingredientesaquitar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reclamo`
--

DROP TABLE IF EXISTS `reclamo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reclamo` (
  `id_reclamo` int NOT NULL AUTO_INCREMENT,
  `estado` enum('Pendiente','Rechazado','Resuelto') DEFAULT NULL,
  `fecha_reclamo` datetime(6) DEFAULT NULL,
  `motivo_rechazo` varchar(255) DEFAULT NULL,
  `texto` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_reclamo`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reclamo`
--

LOCK TABLES `reclamo` WRITE;
/*!40000 ALTER TABLE `reclamo` DISABLE KEYS */;
INSERT INTO `reclamo` VALUES (1,'Rechazado','2026-06-22 18:33:56.367619','jodete','hrorible todo podrido'),(2,'Resuelto','2026-06-16 00:04:57.000000',NULL,'Faltaba la guarnicion en el pedido.'),(3,'Resuelto','2026-06-26 19:48:41.742165',NULL,'Nunca llego'),(4,'Resuelto','2026-06-26 23:17:15.894448',NULL,'Esta demorado más de 1 hora'),(5,'Resuelto','2026-06-26 23:45:29.434496',NULL,'El pedido esta demorado 1 hora'),(6,'Resuelto','2026-07-03 02:42:31.944968',NULL,'Ggggg'),(7,'Resuelto','2026-07-04 20:16:26.756769',NULL,'Frío'),(8,'Resuelto','2026-07-16 22:03:35.707487',NULL,'Dimora mucho'),(9,'Resuelto','2026-07-21 22:59:12.423519',NULL,'Ta demorando'),(10,'Resuelto','2026-07-22 00:29:08.703648',NULL,'Esta demorando más de 1 hora'),(11,'Resuelto','2026-07-24 23:17:51.551587',NULL,'Esta tardando más de 1 hora');
/*!40000 ALTER TABLE `reclamo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurante`
--

DROP TABLE IF EXISTS `restaurante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurante` (
  `abierto` bit(1) NOT NULL,
  `calificacion_prom` float NOT NULL,
  `categoria` enum('Chiveteria','ComidaRapida','Heladeria','Otros','Panaderia','Parrillada','Pizza','Postres','Rotiseria','Vegano') DEFAULT NULL,
  `cuentahabilitada` bit(1) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `apartamento` varchar(255) DEFAULT NULL,
  `calle` varchar(255) DEFAULT NULL,
  `esquina` varchar(255) DEFAULT NULL,
  `latitud` double DEFAULT NULL,
  `longitud` double DEFAULT NULL,
  `numero` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `foto_portada` varchar(255) DEFAULT NULL,
  `habilitado` bit(1) NOT NULL,
  `hora_apertura` time DEFAULT NULL,
  `hora_cierre` time DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `radio_entrega` int DEFAULT NULL,
  `rut` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `id_usuario` int NOT NULL,
  `cierre_programado` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  CONSTRAINT `FK649bddibya24v9gipay5php2o` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurante`
--

LOCK TABLES `restaurante` WRITE;
/*!40000 ALTER TABLE `restaurante` DISABLE KEYS */;
INSERT INTO `restaurante` VALUES (_binary '\0',2,'Otros',_binary '','Restaurante de Comida Rapida las mejores Hambus.','','Avenida 18 de Julio','Ejido',-34.8987971,-56.1678555,'1455','','https://res.cloudinary.com/diswgj0ld/image/upload/v1782058718/d3d47605-d8cb-489a-a7a9-e95c3c87f4ac.png',_binary '','09:00:00','23:00:00','$2a$10$vhh9Shaet.Ck2xSTbo4Xo.4aPEd5gQ0WkMMUS0SWM1fIYyvGOJvkW',40,'219876543210','+598096022499',3,NULL),(_binary '\0',0,'Otros',_binary '','Restaurante de Sushi estilo tradicional japonés, nuestros productos son frescos traidos en el dia del puerto.','','Avenida 8 de Octubre','',-34.8861256,-56.1574295,'2236','','https://res.cloudinary.com/diswgj0ld/image/upload/v1782494589/portadaTokio.png',_binary '','12:00:00','22:18:00','$2a$10$oo5VNAHKF/CdXnjXfy8QsuApybJKfooGYcd/m28NO0XWI0iuYbwQ.',40,'141214151619','+59897963369',9,NULL),(_binary '',5,'Parrillada',_binary '','Asado uruguayo a la lenia, achuras y pasta casera.',NULL,'Av. 18 de Julio','Yi',-34.90642,-56.18915,'1450','Local','https://images.unsplash.com/photo-1544025162-d76694265947?w=1200&q=80&auto=format&fit=crop',_binary '','11:30:00','23:30:00','$2a$10$demoHashParrillaFicticioParaPruebas1234567890abc',40,'210000010011','099111222',11,NULL),(_binary '',5,'Pizza',_binary '','Pizza a la piedra y fainia. Receta napolitana tradicional.',NULL,'Ejido','San Jose',-34.90495,-56.19102,'1234','Local','https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=1200&q=80&auto=format&fit=crop',_binary '','18:00:00','00:30:00','$2a$10$demoHashNapoliFicticioParaPruebas1234567890abcde',40,'210000020022','099333444',12,NULL),(_binary '',3,'ComidaRapida',_binary '','Hamburguesas smash, papas rusticas y milkshakes.','PB','Av. Brasil','Cavia',-34.91357,-56.15498,'2710','Local','https://images.unsplash.com/photo-1550547660-d9450f859349?w=1200&q=80&auto=format&fit=crop',_binary '','12:00:00','23:59:00','$2a$10$demoHashBurgerFicticioParaPruebas1234567890abcde',40,'210000030033','099555666',13,NULL),(_binary '',4,'Vegano',_binary '','Cocina plant-based, bowls y jugos naturales.',NULL,'Bvar. Espania','Br. Artigas',-34.91011,-56.15721,'2300','Local','https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=1200&q=80&auto=format&fit=crop',_binary '','10:00:00','22:00:00','$2a$10$demoHashVeganoFicticioParaPruebas1234567890abcde',40,'210000040044','099777888',14,NULL),(_binary '',5,'Heladeria',_binary '','Helados artesanales y postres helados.',NULL,'26 de Marzo','Coronel Mora',-34.91548,-56.15203,'1180','Local','https://images.unsplash.com/photo-1488477181946-6428a0291777?w=1200&q=80&auto=format&fit=crop',_binary '','13:00:00','23:00:00','$2a$10$demoHashHeladosFicticioParaPruebas1234567890abc',40,'210000050055','099999000',15,NULL),(_binary '\0',0,NULL,_binary '','Milanesa Queen llegó para coronar el verdadero sabor. Milanesas doradas, crujientes y llenas de sabor, preparadas con ingredientes de calidad y porciones que conquistan desde el primer bocado.','','Daniel Muñoz','Defensa',-34.8961806,-56.171583,'2049','','https://res.cloudinary.com/diswgj0ld/image/upload/w_800,h_350,c_fill,g_auto/v1782360703/milanesa-napolitana.webp',_binary '',NULL,NULL,'$2a$10$lbza24XAvbWCJtWaVKxiM.2hC39AB.hvyKAWd/4HxZZ5sriuiQyPa',40,'210121230019','59898054043',20,NULL),(_binary '\0',0,'ComidaRapida',_binary '\0','Restaurante especializado en comida rápida y platos caseros, con delivery y retiro en el local.','','18 de Julio','Ejido',-32.601008,-53.3862263,'1234','','https://res.cloudinary.com/diswgj0ld/image/upload/v1782516873/Articulo1.png',_binary '','09:20:00','00:08:00','$2a$10$opoSukI2gyKWqJJWTX7r2uPvhUbptp06vthVly.PuYHUcxXNF42e.',100,'102938475610','59891234567',25,NULL),(_binary '\0',0,NULL,_binary '\0','Restaurante especializado en comida rápida y platos caseros, con delivery y retiro en el local.','','Bulevar General Artigas','Chana',-34.9039336,-56.1638568,'1223','','https://res.cloudinary.com/diswgj0ld/image/upload/v1782516873/Articulo1.png',_binary '','00:00:00','23:00:00','$2a$10$DN3xLvVtvB/2xJuMWdkOQO8q0hisSAO.cxgR9o0nP0mEdEHZFJdJy',40,'102938475610','59891234567',26,NULL),(_binary '\0',0,NULL,_binary '\0','EL Mejor Restaurante de Comida Rapida que existe.','','Avenida 8 de Octubre','Pasaje Pernas',-34.8968448,-56.1658775,'1234','','https://res.cloudinary.com/diswgj0ld/image/upload/v1782516873/Articulo1.png',_binary '','09:00:00','20:08:20','$2a$10$WSpLydXcQCLi21oIZb8sfOWv4C5h0sSokNHLgC8ikLVmBo0D2DGDG',40,'483920175641','+59899456782',27,NULL),(_binary '\0',0,'Otros',_binary '\0','EL Mejor Restaurante de Comida Rapida que existe.','','Liga Federal','',-34.817933,-56.2799,'1234','','https://res.cloudinary.com/diswgj0ld/image/upload/v1782516873/Articulo1.png',_binary '','09:00:00','20:18:23','$2a$10$TqjXpBDlcZ8Y7j66PjURHe8/g20jU/oH.Km1VzaxipxG4jUrL3J4a',10,'483920175641','+59899456782',33,NULL),(_binary '\0',4,'Otros',_binary '','EL Mejor Restaurante de Comida Rapida que existe.','','Avenida 8 de Octubre','',-34.8974267,-56.1659394,'1234','','https://res.cloudinary.com/diswgj0ld/image/upload/v1782434705/Portada.png',_binary '','09:00:00','23:00:00','$2a$10$bRErapva0LNnKReExXRuB.yH71FAAl7AA3W63nMx5XXiQag9lkkYi',40,'483920175641','+59899456782',34,NULL),(_binary '\0',5,'Otros',_binary '','EL Mejor Restaurante de Comida Rapida que existe.','','Avenida 8 de Octubre','',-34.8888029,-56.159669,'1234','','https://res.cloudinary.com/diswgj0ld/image/upload/v1782434705/Portada.png',_binary '','09:00:00','23:00:00','$2a$10$3N8FlHRLYs/Bnqv9RDSkeeokYW.WGZXdmVMt/uspR.WA49M1AaLq6',40,'483920175641','+59899456782',37,NULL);
/*!40000 ALTER TABLE `restaurante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sub_categoria`
--

DROP TABLE IF EXISTS `sub_categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_categoria` (
  `id_sub_categoria` int NOT NULL AUTO_INCREMENT,
  `categoria` enum('Bebida','Ensalada','Entrada','Guarnicion','Otros','Postre','Principal') DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `url_imagen` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_sub_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_categoria`
--

LOCK TABLES `sub_categoria` WRITE;
/*!40000 ALTER TABLE `sub_categoria` DISABLE KEYS */;
INSERT INTO `sub_categoria` VALUES (1,'Principal','ComidaRapida','https://res.cloudinary.com/diswgj0ld/image/upload/v1782061248/380722770_117009517_1706x1280.jpg'),(2,'Bebida','Cervesas','https://res.cloudinary.com/diswgj0ld/image/upload/v1784306755/cerveza_z5yu22.webp'),(3,'Principal','Platos principales','https://images.unsplash.com/photo-1544025162-d76694265947?w=600&q=80&auto=format&fit=crop'),(4,'Entrada','Aperitivos','https://res.cloudinary.com/diswgj0ld/image/upload/v1784307407/aperitivos_ign5xi.jpg'),(5,'Guarnicion','Guarniciones','https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=600&q=80&auto=format&fit=crop'),(6,'Ensalada','Ensaladas','https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80&auto=format&fit=crop'),(7,'Bebida','Refrescos','https://res.cloudinary.com/diswgj0ld/image/upload/v1784307008/refrescos_cauhic.jpg'),(8,'Postre','Helados','https://res.cloudinary.com/diswgj0ld/image/upload/v1784306412/helado_hj6o6h.jpg'),(9,'Guarnicion','Fritas','https://res.cloudinary.com/diswgj0ld/image/upload/v1782424391/fritasrescalado.png'),(10,'Otros','Sushi','https://res.cloudinary.com/diswgj0ld/image/upload/v1782494692/sushiplato.avif'),(11,'Principal','Hamburguesas','https://res.cloudinary.com/diswgj0ld/image/upload/v1784297901/burguer.jpg'),(12,'Principal','Pizzas','https://res.cloudinary.com/diswgj0ld/image/upload/v1784297978/pizza.jpg'),(13,'Principal','Empanadas','https://res.cloudinary.com/diswgj0ld/image/upload/v1784298156/empanadas.jpg'),(14,'Principal','Milanesas','https://res.cloudinary.com/diswgj0ld/image/upload/v1784298450/mila.jpg');
/*!40000 ALTER TABLE `sub_categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `foto_perfil` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `rol` enum('Administrador','Cliente','Restaurante') DEFAULT NULL,
  PRIMARY KEY (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'admin@test.com',NULL,'DefaultAdmin','Administrador'),(2,'ezequielmedina23@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocJ-bwg_f9p4hXks6D8-_NF6Q9JmJU67si03gAKDf3l5_DTGfP1x=s96-c','Ezequiel Medina','Cliente'),(3,'neimex23@gmail.com','https://res.cloudinary.com/diswgj0ld/image/upload/v1782058718/d3d47605-d8cb-489a-a7a9-e95c3c87f4ac.png','RestoNei','Restaurante'),(4,'neimex23@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocLmk_fqciwRiqjPjN0xlucNjNaRook8cPdO6oTHR1bxR1bYwuML=s96-c','Ezequiel Medina (Neimex23)','Cliente'),(5,'damasomail@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocIQSE80DB_1UcBciT4PKoGxvXVHCEcT24cbbMPZo7VJWTUuEQ=s96-c','Dámaso Tor','Cliente'),(6,'alexiswlcg@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocJ5GODtWLxQrGkrn4KFf3s-mwxx0mDGcTg0BPc_D-lEfKaGWwiz=s96-c','Alexis La Cruz','Cliente'),(7,'horacioduarte1995@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocLin3TCplCUYBAkseL8XnDtpF61NHIT52CC_0igd8QPh_pQ_G0=s96-c','Horacio Duarte','Cliente'),(8,'maikol.valentin97@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocKIeNmTwdeOJzhC5Fp3w624o16M-8jfSc_O8D9IXQ7iuTx3x2Tj=s96-c','Maikol Brion','Cliente'),(9,'ale_xis2008@hotmail.com','https://res.cloudinary.com/diswgj0ld/image/upload/v1782494348/tokio.png','Tokio Factory','Restaurante'),(10,'admin@trego.seed',NULL,'Admin Trego','Administrador'),(11,'parrilla@trego.seed','https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600&q=80&auto=format&fit=crop','La Parrilla del Centro','Restaurante'),(12,'napoli@trego.seed','https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&q=80&auto=format&fit=crop','Pizzeria Napoli','Restaurante'),(13,'burger@trego.seed','https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&q=80&auto=format&fit=crop','Burger House','Restaurante'),(14,'vegano@trego.seed','https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80&auto=format&fit=crop','Verde Vegano','Restaurante'),(15,'helados@trego.seed','https://www.alacarta.com.uy/wp-content/gallery/grot-helados-artesanales-y-naturales/alacarta-heladeria-grot-01.jpg','Heladeria Crema','Restaurante'),(16,'lucia@trego.seed','https://i.pravatar.cc/300?img=45','Lucia Fernandez','Cliente'),(17,'martin@trego.seed','https://i.pravatar.cc/300?img=12','Martin Suarez','Cliente'),(18,'sofia@trego.seed','https://i.pravatar.cc/300?img=47','Sofia Rodriguez','Cliente'),(19,'diego@trego.seed','https://i.pravatar.cc/300?img=15','Diego Pereyra','Cliente'),(20,'maikol-valentin@hotmail.com','https://res.cloudinary.com/diswgj0ld/image/upload/w_200,h_200,c_fill,g_auto/v1782360750/MilangaQueenLogo.png','Milanga Queen','Restaurante'),(21,'liuzzijm@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocJqFRAQgIf2xMSsnJUTUEaoCVqoczJF0Kqz10UePaiamWwx91U=s96-c','Juan Manuel Liuzzi Dolz','Cliente'),(22,'delosscarlos2001@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocInfD0vDsjHBhnAooUj9nXUpPt9EJlS6NrJphQZ62mJV3YRQplp=s96-c','Carlos Julio de los Santos Jansons','Cliente'),(24,'mastersken270482@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocKoXPMJOHohsG6CDj0vELG29q1RrXGE2-_Yy_Q5_tlW_ikLjg=s96-c','Ken Masters','Cliente'),(25,'yegipev758@divahd.com','https://res.cloudinary.com/diswgj0ld/image/upload/v1782434704/Perfil.png','ElPepe','Restaurante'),(26,'joxoto1636@divahd.com','https://res.cloudinary.com/diswgj0ld/image/upload/v1782434704/Perfil.png','ElPepe2','Restaurante'),(27,'gotaji2381@duvips.com','https://res.cloudinary.com/diswgj0ld/image/upload/v1782434704/Perfil.png','ELPEPE','Restaurante'),(28,'tordamaso@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocIq9U0MnYK4pBc3MH7aYHtZ_MQXYOvCrX2jDkjVSBSuEPmAag=s96-c','Dámaso Tor','Cliente'),(32,'fgfrois@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocKhY-CGOnbHSc_Q-CbfMHRsE_z0kGb-ykC8MU6OEYWWht5dpFRW=s96-c','Federico Gómez Frois','Cliente'),(33,'gross.bison.gcqc@hidepost.net','https://res.cloudinary.com/diswgj0ld/image/upload/v1782434704/Perfil.png','ELPepe','Restaurante'),(34,'solid.sailfish.wgpw@hidepost.net','https://res.cloudinary.com/diswgj0ld/image/upload/v1782434704/Perfil.png','ElPepe2','Restaurante'),(35,'joaquin777leites@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocIwV2KdI45KgHXk8ZTryXxpSBQkGq5jI-3ta3zdjj86VrxZjg=s96-c','Joaquin','Cliente'),(36,'monmart65@gmail.com','https://lh3.googleusercontent.com/a/ACg8ocIXu969sj2tywObsADWD0Vb3kX3i3DWOpIcbrY23asPlyw7ig=s96-c','Mónica Martínez','Cliente'),(37,'tense.asp.rztw@hidepost.net','https://res.cloudinary.com/diswgj0ld/image/upload/v1782434704/Perfil.png','DonPepe','Restaurante');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'tregodb'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-25 15:25:58
