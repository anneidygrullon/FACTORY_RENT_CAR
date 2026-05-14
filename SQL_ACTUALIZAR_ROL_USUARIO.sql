USE [FACTORY]
GO

-- Agregar columna rol a TBL_USUARIO
ALTER TABLE TBL_USUARIO ADD rol VARCHAR(20) NOT NULL DEFAULT 'admin';
GO

-- Actualizar usuarios existentes con roles
UPDATE TBL_USUARIO SET rol = 'admin' WHERE nombre = 'admin';
UPDATE TBL_USUARIO SET rol = 'chofer' WHERE nombre LIKE '%chofer%' OR nombre LIKE '%driver%';
UPDATE TBL_USUARIO SET rol = 'carwasher' WHERE nombre LIKE '%carwash%' OR nombre LIKE '%lavador%';
UPDATE TBL_USUARIO SET rol = 'mecanico' WHERE nombre LIKE '%mecanico%' OR nombre LIKE '%mechanic%';
UPDATE TBL_USUARIO SET rol = 'gerente' WHERE nombre LIKE '%gerente%' OR nombre LIKE '%manager%';
GO
