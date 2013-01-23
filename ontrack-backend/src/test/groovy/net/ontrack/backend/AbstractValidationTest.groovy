package net.ontrack.backend

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import net.ontrack.core.validation.ValidationException
import net.ontrack.test.AbstractIntegrationTest
import net.sf.jstring.Strings

import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractValidationTest extends AbstractIntegrationTest {
	
	@Autowired
	Strings strings
	
	def validateNOK (String message, Closure<Void> action) {
		try {
			action.call()
			Assert.fail("Validation should have failed")
		} catch (ValidationException ex) {
			Assert.assertEquals(message, ex.getLocalizedMessage(strings, Locale.ENGLISH))
		}
	}

}
