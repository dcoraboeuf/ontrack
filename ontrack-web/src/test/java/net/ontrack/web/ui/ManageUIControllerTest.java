package net.ontrack.web.ui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.ontrack.core.model.EventType;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.web.gui.model.GUIEvent;
import net.ontrack.web.test.AbstractWebTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Iterator;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ManageUIControllerTest extends AbstractWebTest {

    // FIXME Needs authentication
    @Test
    @Ignore
    public void createProject() throws Exception {
        this.mockMvc.perform(
                post("/ui/manage/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ProjectCreationForm("PRJ1", "My first project")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(greaterThan(0)))
                .andExpect(jsonPath("$.name").value("PRJ1"))
                .andExpect(jsonPath("$.description").value("My first project"));
        // Gets the resulting list
        String json = this.mockMvc.perform(get("/ui/manage/project").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        Iterable<ProjectSummary> summaries = parseList(json, ProjectSummary.class);
        ProjectSummary summary = Iterables.find(
                summaries,
                new Predicate<ProjectSummary>() {
                    @Override
                    public boolean apply(ProjectSummary it) {
                        return StringUtils.equals("PRJ1", it.getName());
                    }
                },
                null
        );
        assertNotNull(summary);
        assertEquals("My first project", summary.getDescription());
        // Gets the event
        json = this.mockMvc.perform(get("/gui/event").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        Iterable<GUIEvent> events = parseList(json, GUIEvent.class);
        assertNotNull(events);
        Iterator<GUIEvent> i = events.iterator();
        assertTrue(i.hasNext());
        GUIEvent e = i.next();
        assertNotNull(e);
        assertEquals(EventType.PROJECT_CREATED, e.getEventType());
        assertEquals("Project <a class=\"event-entity\" href=\"gui/project/PRJ1\">PRJ1</a> has been created.", e.getHtml());

        // Gets the project by name
        ProjectSummary project = getCall("/ui/manage/project/PRJ1", ProjectSummary.class);
        assertEquals(project.getId(), summary.getId());
    }

}
