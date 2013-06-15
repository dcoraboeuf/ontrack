-- Adding the extension column to the 'properties' table
ALTER TABLE PROPERTIES ADD COLUMN EXTENSION VARCHAR(40) NOT NULL BEFORE NAME;
-- @mysql
-- See update 26
