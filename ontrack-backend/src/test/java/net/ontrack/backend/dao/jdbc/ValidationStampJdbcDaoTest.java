package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.AbstractBackendTest;
import net.ontrack.backend.dao.ValidationStampDao;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BranchSummary;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ValidationStampJdbcDaoTest extends AbstractBackendTest {

    @Autowired
    private ValidationStampDao dao;

    @Test
    public void deletion_start() throws Exception {
        // Parent branch
        BranchSummary branch = doCreateBranch();
        // Initial validation stamps
        int st1 = dao.createValidationStamp(branch.getId(), "ST1", "1");
        int st2 = dao.createValidationStamp(branch.getId(), "ST2", "2");
        int st3 = dao.createValidationStamp(branch.getId(), "ST3", "3");
        int st4 = dao.createValidationStamp(branch.getId(), "ST4", "4");
        int st5 = dao.createValidationStamp(branch.getId(), "ST5", "5");
        // Deletion
        Ack ack = dao.deleteValidationStamp(st1);
        assertTrue("Deletion OK", ack.isSuccess());
        // Checks the number of validation stamps
        assertEquals(4, dao.findByBranch(branch.getId()).size());
        // Checks the new ordering
        assertEquals(1, dao.getById(st2).getOrderNb());
        assertEquals(2, dao.getById(st3).getOrderNb());
        assertEquals(3, dao.getById(st4).getOrderNb());
        assertEquals(4, dao.getById(st5).getOrderNb());
    }

    @Test
    public void deletion_middle() throws Exception {
        // Parent branch
        BranchSummary branch = doCreateBranch();
        // Initial validation stamps
        int st1 = dao.createValidationStamp(branch.getId(), "ST1", "1");
        int st2 = dao.createValidationStamp(branch.getId(), "ST2", "2");
        int st3 = dao.createValidationStamp(branch.getId(), "ST3", "3");
        int st4 = dao.createValidationStamp(branch.getId(), "ST4", "4");
        int st5 = dao.createValidationStamp(branch.getId(), "ST5", "5");
        // Deletion
        Ack ack = dao.deleteValidationStamp(st3);
        assertTrue("Deletion OK", ack.isSuccess());
        // Checks the number of validation stamps
        assertEquals(4, dao.findByBranch(branch.getId()).size());
        // Checks the new ordering
        assertEquals(1, dao.getById(st1).getOrderNb());
        assertEquals(2, dao.getById(st2).getOrderNb());
        assertEquals(3, dao.getById(st4).getOrderNb());
        assertEquals(4, dao.getById(st5).getOrderNb());
    }

    @Test
    public void deletion_end() throws Exception {
        // Parent branch
        BranchSummary branch = doCreateBranch();
        // Initial validation stamps
        int st1 = dao.createValidationStamp(branch.getId(), "ST1", "1");
        int st2 = dao.createValidationStamp(branch.getId(), "ST2", "2");
        int st3 = dao.createValidationStamp(branch.getId(), "ST3", "3");
        int st4 = dao.createValidationStamp(branch.getId(), "ST4", "4");
        int st5 = dao.createValidationStamp(branch.getId(), "ST5", "5");
        // Deletion
        Ack ack = dao.deleteValidationStamp(st5);
        assertTrue("Deletion OK", ack.isSuccess());
        // Checks the number of validation stamps
        assertEquals(4, dao.findByBranch(branch.getId()).size());
        // Checks the new ordering
        assertEquals(1, dao.getById(st1).getOrderNb());
        assertEquals(2, dao.getById(st2).getOrderNb());
        assertEquals(3, dao.getById(st3).getOrderNb());
        assertEquals(4, dao.getById(st4).getOrderNb());
    }

    @Test
    public void reordering_back() throws Exception {
        // Parent branch
        BranchSummary branch = doCreateBranch();
        // Initial validation stamps
        int st1 = dao.createValidationStamp(branch.getId(), "ST1", "1");
        int st2 = dao.createValidationStamp(branch.getId(), "ST2", "2");
        int st3 = dao.createValidationStamp(branch.getId(), "ST3", "3");
        int st4 = dao.createValidationStamp(branch.getId(), "ST4", "4");
        int st5 = dao.createValidationStamp(branch.getId(), "ST5", "5");
        // Reordering
        Ack ack = dao.moveValidationStamp(st4, 0);
        assertTrue("Move OK", ack.isSuccess());
        // Checks the new ordering
        assertEquals(1, dao.getById(st4).getOrderNb());
        assertEquals(2, dao.getById(st1).getOrderNb());
        assertEquals(3, dao.getById(st2).getOrderNb());
        assertEquals(4, dao.getById(st3).getOrderNb());
        assertEquals(5, dao.getById(st5).getOrderNb());
    }

    @Test
    public void reordering_forward() throws Exception {
        // Parent branch
        BranchSummary branch = doCreateBranch();
        // Initial validation stamps
        int st1 = dao.createValidationStamp(branch.getId(), "ST1", "1");
        int st2 = dao.createValidationStamp(branch.getId(), "ST2", "2");
        int st3 = dao.createValidationStamp(branch.getId(), "ST3", "3");
        int st4 = dao.createValidationStamp(branch.getId(), "ST4", "4");
        int st5 = dao.createValidationStamp(branch.getId(), "ST5", "5");
        // Reordering
        Ack ack = dao.moveValidationStamp(st2, 3);
        assertTrue("Move OK", ack.isSuccess());
        // Checks the new ordering
        assertEquals(1, dao.getById(st1).getOrderNb());
        assertEquals(2, dao.getById(st3).getOrderNb());
        assertEquals(3, dao.getById(st4).getOrderNb());
        assertEquals(4, dao.getById(st2).getOrderNb());
        assertEquals(5, dao.getById(st5).getOrderNb());
    }

    @Test
    public void reordering_none() throws Exception {
        // Parent branch
        BranchSummary branch = doCreateBranch();
        // Initial validation stamps
        int st1 = dao.createValidationStamp(branch.getId(), "ST1", "1");
        int st2 = dao.createValidationStamp(branch.getId(), "ST2", "2");
        int st3 = dao.createValidationStamp(branch.getId(), "ST3", "3");
        int st4 = dao.createValidationStamp(branch.getId(), "ST4", "4");
        int st5 = dao.createValidationStamp(branch.getId(), "ST5", "5");
        // Reordering
        Ack ack = dao.moveValidationStamp(st2, 1);
        assertFalse("Move NOK", ack.isSuccess());
        // Checks the new ordering
        assertEquals(1, dao.getById(st1).getOrderNb());
        assertEquals(2, dao.getById(st2).getOrderNb());
        assertEquals(3, dao.getById(st3).getOrderNb());
        assertEquals(4, dao.getById(st4).getOrderNb());
        assertEquals(5, dao.getById(st5).getOrderNb());
    }

}
