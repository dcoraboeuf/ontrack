package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.ValidationStampDao;
import net.ontrack.extension.api.support.StartupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBFixReorderingValidationStamps implements StartupService {

    private final Logger logger = LoggerFactory.getLogger(DBFixReorderingValidationStamps.class);
    private final ValidationStampDao validationStampDao;

    @Autowired
    public DBFixReorderingValidationStamps(ValidationStampDao validationStampDao) {
        this.validationStampDao = validationStampDao;
    }

    @Override
    public int startupOrder() {
        return 10;
    }

    @Override
    public void start() {
        long start = System.currentTimeMillis();
        logger.info("[DBFixReorderingValidationStamps] Re-ordering all validation stamps...");
        validationStampDao.reorderAll();
        long end = System.currentTimeMillis();
        logger.info("[DBFixReorderingValidationStamps] Re-ordering all validation stamps took {} ms", (end - start));
    }
}
