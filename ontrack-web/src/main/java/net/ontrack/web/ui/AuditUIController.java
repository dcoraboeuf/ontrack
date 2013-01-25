package net.ontrack.web.ui;

import java.util.List;

import net.ontrack.service.AuditService;
import net.ontrack.service.model.Audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuditUIController {

	private final AuditService auditService;

	@Autowired
	public AuditUIController(AuditService auditService) {
		this.auditService = auditService;
	}
	
	@RequestMapping(value = "/ui/audit/all", method = RequestMethod.GET)
	public @ResponseBody List<Audit> all (@RequestParam(required = false, defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "20") int count) {
		return auditService.all(offset, count);
	}

}
