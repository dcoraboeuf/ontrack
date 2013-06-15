-- Validation stamp image
ALTER TABLE VALIDATION_STAMP ADD COLUMN IMAGE BINARY(4096) NULL;
-- @mysql
-- See update 26
