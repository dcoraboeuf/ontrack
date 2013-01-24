

package net.ontrack.web.ui

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import net.ontrack.core.model.ProjectGroupCreationForm
import net.ontrack.web.test.AbstractWebTest

import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

class ManageUIControllerTest extends AbstractWebTest {
	
	@Test
	void http404() {
		this.mockMvc.perform(get("/ui/404"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	void createProjectGroup() {
		this.mockMvc.perform(
			post("/ui/manage/projectGroup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(new ProjectGroupCreationForm("GRP1", "My first group")))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print());
//			.andExpect(content().contentType("application/json"))
//			.andExpect(jsonPath('$.id').value(greaterThan(0)))
//			.andExpect(jsonPath('$.name').value("GRP1"))
//			.andExpect(jsonPath('$.description').value("My first group"));
	}

}
