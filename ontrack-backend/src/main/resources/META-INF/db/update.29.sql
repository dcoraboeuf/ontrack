-- Project authorizations
CREATE TABLE PROJECT_AUTHORIZATION (
  PROJECT INTEGER NOT NULL,
  ACCOUNT INTEGER NOT NULL,
  ROLE VARCHAR(40) NOT NULL,
  CONSTRAINT PROJECT_AUTHORIZATION_PK PRIMARY KEY (PROJECT, ACCOUNT),
  CONSTRAINT PROJECT_AUTHORIZATION_FK_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID) ON DELETE CASCADE,
  CONSTRAINT PROJECT_AUTHORIZATION_FK_ACCOUNT FOREIGN KEY (ACCOUNT) REFERENCES ACCOUNTS (ID) ON DELETE CASCADE
);

-- @rollback
DROP TABLE IF EXISTS PROJECT_AUTHORIZATION;
