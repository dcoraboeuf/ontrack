-- DB versioning
CREATE TABLE SVNVERSION (
	VALUE INTEGER NOT NULL,
	UPDATED TIMESTAMP NOT NULL
);

-- Revisions

CREATE TABLE REVISION (
	REVISION INTEGER NOT NULL,
	AUTHOR VARCHAR(40) NOT NULL,
	CREATION TIMESTAMP NOT NULL,
	MESSAGE VARCHAR(500) NOT NULL,
	BRANCH VARCHAR(200) NULL,
	CONSTRAINT PK_REVISION PRIMARY KEY (REVISION)
);

-- Indexes the revision branch path
CREATE INDEX IDX_REVISION_BRANCH ON REVISION (BRANCH);

-- Merge relationship between the revisions
CREATE TABLE MERGE_REVISION (
	REVISION INTEGER NOT NULL,
	TARGET INTEGER NOT NULL,
	CONSTRAINT PK_MERGE_REVISION PRIMARY KEY (REVISION, TARGET),
	CONSTRAINT FK_MERGE_REVISION_TARGET FOREIGN KEY (TARGET) REFERENCES REVISION(REVISION) ON DELETE CASCADE
);

-- Indexes the MERGE_REVISION table
CREATE INDEX IDX_MERGE_REVISION_REVISION ON MERGE_REVISION (REVISION);
CREATE INDEX IDX_MERGE_REVISION_TARGET ON MERGE_REVISION (TARGET);

-- Copy events
CREATE TABLE SVNCOPYEVENT (
	REVISION INTEGER NOT NULL,
	COPYFROMPATH VARCHAR(400) NOT NULL,
	COPYFROMREVISION INTEGER NOT NULL,
	COPYTOPATH VARCHAR(400) NOT NULL,
	CONSTRAINT PK_SVNCOPYEVENT PRIMARY KEY (REVISION, COPYTOPATH),
	CONSTRAINT FK_SVNCOPYEVENT_REVISION FOREIGN KEY (REVISION) REFERENCES REVISION (REVISION) ON DELETE CASCADE
);

CREATE INDEX IDX_SVNCOPYEVENT_COPYTOPATH ON SVNCOPYEVENT ( COPYTOPATH );

-- Stop events
CREATE TABLE SVNSTOPEVENT (
	REVISION INTEGER NOT NULL,
	PATH VARCHAR(400) NOT NULL,
	CONSTRAINT PK_SVNSTOPEVENT PRIMARY KEY (REVISION, PATH),
	CONSTRAINT FK_SVNSTOPEVENT_REVISION FOREIGN KEY (REVISION) REFERENCES REVISION (REVISION) ON DELETE CASCADE
);

-- Indexation of issues
CREATE TABLE REVISION_ISSUE (
	REVISION INTEGER NOT NULL,
	ISSUE VARCHAR(20) NOT NULL,
	CONSTRAINT PK_REVISION_ISSUE PRIMARY KEY (REVISION, ISSUE),
	CONSTRAINT FK_REVISION_ISSUE_REVISION FOREIGN KEY (REVISION) REFERENCES REVISION (REVISION) ON DELETE CASCADE
);

CREATE INDEX IDX_REVISION_ISSUE_ISSUE ON REVISION_ISSUE ( ISSUE );
CREATE INDEX IDX_REVISION_ISSUE_REVISION ON REVISION_ISSUE ( REVISION );