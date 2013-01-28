

package net.ontrack.web.ui

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

import net.ontrack.core.model.ProjectCreationForm
import net.ontrack.core.model.ProjectGroupCreationForm
import net.ontrack.core.model.ProjectGroupSummary
import net.ontrack.core.model.ProjectSummary
import net.ontrack.service.model.Event
import net.ontrack.service.model.EventType
import net.ontrack.web.test.AbstractWebTest

import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

class ManageUIControllerTest extends AbstractWebTest {
	
	@Test
	void createProjectGroup() {
		this.mockMvc.perform(
			post("/ui/manage/projectgroup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(new ProjectGroupCreationForm("GRP1", "My first group")))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath('$.id').value(greaterThan(0)))
			.andExpect(jsonPath('$.name').value("GRP1"))
			.andExpect(jsonPath('$.description').value("My first group"))
		// Gets the resulting list
		String content = this.mockMvc.perform(get("/ui/manage/projectgroup/all").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn().response.contentAsString
		List<ProjectGroupSummary> summaries = parse(content, List.class)
		assert summaries.find { it.name == "GRP1" } != null
		assert summaries.find { it.description == "My first group" } != null
	}
	
	@Test
	void createProject() {
		this.mockMvc.perform(
			post("/ui/manage/project")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(new ProjectCreationForm("PRJ1", "My first project")))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath('$.id').value(greaterThan(0)))
			.andExpect(jsonPath('$.name').value("PRJ1"))
			.andExpect(jsonPath('$.description').value("My first project"))
		// Gets the resulting list
		String json = this.mockMvc.perform(get("/ui/manage/project/all").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn().response.contentAsString
		List<ProjectSummary> summaries = parse(json, List.class)
		def summary = summaries.find { it.name == "PRJ1" }
		assert summary != null
		assert summary.description == "My first project"
		// Gets the event
		json = this.mockMvc.perform(get("/ui/event/all").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn().response.contentAsString
		List<Event> events = parse(json, List.class)
		assert events != null && !events.empty
		def e = events.get(0)
		assert e != null
		assert summary.id == e.sourceId
		assert EventType.PROJECT_CREATED.name() == e.eventType
	}

}
