-- Promoted run
CREATE TABLE PROMOTED_RUN (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	PROMOTION_LEVEL INTEGER NOT NULL,
	BUILD INTEGER NOT NULL,
	DESCRIPTION VARCHAR(1000) NULL,
	CONSTRAINT PK_PROMOTED_RUN PRIMARY KEY (ID),
	CONSTRAINT FK_PROMOTED_RUN_PROMOTION_LEVEL FOREIGN KEY (PROMOTION_LEVEL) REFERENCES PROMOTION_LEVEL (ID) ON DELETE CASCADE,
	CONSTRAINT FK_PROMOTED_RUN_BUILD FOREIGN KEY (BUILD) REFERENCES BUILD (ID) ON DELETE CASCADE,
	CONSTRAINT UQ_PROMOTED_RUN UNIQUE (PROMOTION_LEVEL, BUILD)
);

-- @mysql
-- See update 26
