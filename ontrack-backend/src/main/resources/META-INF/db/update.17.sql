-- Flag for the events having been sent
-- Previous entries remain NULL - they will be counted as false
ALTER TABLE EVENTS ADD SENT BOOLEAN;
