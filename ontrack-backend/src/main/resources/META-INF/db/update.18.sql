ALTER TABLE PROMOTION_LEVEL ADD COLUMN AUTOPROMOTE BOOLEAN NULL;
UPDATE PROMOTION_LEVEL SET AUTOPROMOTE = FALSE;
ALTER TABLE PROMOTION_LEVEL ALTER COLUMN AUTOPROMOTE SET NOT NULL;