-- Flag for the events having been sent
-- Previous entries remain NULL - they will be counted as false
ALTER TABLE EVENTS ADD SENT BOOLEAN;
-- Sets all previous entry to true, in order to prevent them being sent again
UPDATE EVENTS SET SENT = TRUE;
-- Considers all previous authors as being not correct since the foreign key was never present
UPDATE EVENTS SET AUTHOR_ID = NULL WHERE AUTHOR_ID NOT IN (SELECT ID FROM ACCOUNTS);
ALTER TABLE EVENTS ADD CONSTRAINT FK_EVENT_AUTHOR FOREIGN KEY (AUTHOR_ID) REFERENCES ACCOUNTS (ID) ON DELETE SET NULL;
-- The FK from the 'subscription' table to the 'accounts' one was forgotten!
DELETE FROM SUBSCRIPTION  WHERE ACCOUNT NOT IN (SELECT ID FROM ACCOUNTS);
ALTER TABLE SUBSCRIPTION ADD CONSTRAINT FK_SUBSCRIPTION_ACCOUNT FOREIGN KEY (ACCOUNT) REFERENCES ACCOUNTS (ID) ON DELETE CASCADE;
-- @mysql
-- See update 26
