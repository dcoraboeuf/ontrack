package net.ontrack.web.gui

import static java.lang.Object.*
import net.ontrack.core.model.Entity
import net.ontrack.core.model.EntityStub
import net.ontrack.core.model.EventType
import net.ontrack.core.model.ExpandedEvent
import net.ontrack.web.ui.EventController
import net.sf.jstring.support.StringsLoader

import org.joda.time.DateTime
import org.junit.Test

class EventControllerUnitTest {
	
	@Test
	void createLinkHref_project_group () {
		EventController controller = dummy()
		def href = controller.createLinkHref(Entity.PROJECT_GROUP, new EntityStub(Entity.PROJECT_GROUP, 5, "GROUP3"), 1, [:])
		assert """gui/project_group/GROUP3""" == href
	}
	
	@Test
	void createLinkHref_project () {
		EventController controller = dummy()
		def href = controller.createLinkHref(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ5"), 1, [:])
		assert """gui/project/PROJ5""" == href
	}
	
	@Test
	void createLink_project_group_no_alternative () {
		EventController controller = dummy()
		def href = controller.createLink(Entity.PROJECT_GROUP, new EntityStub(Entity.PROJECT_GROUP, 2001, "GROUP2"), null, 1, [:])
		assert """<a class="event-entity" href="gui/project_group/GROUP2">GROUP2</a>""" == href
	}
	
	@Test
	void createLink_project_group_alternative () {
		EventController controller = dummy()
		def href = controller.createLink(Entity.PROJECT_GROUP, new EntityStub(Entity.PROJECT_GROUP, 2001, "GROUP1"), "te>st", 1, [:])
		assert """<a class="event-entity" href="gui/project_group/GROUP1">te&gt;st</a>""" == href
	}
	
	@Test
	void createLink_project_no_alternative () {
		EventController controller = dummy()
		def href = controller.createLink(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ4"), null, 1, [:])
		assert """<a class="event-entity" href="gui/project/PROJ4">PROJ4</a>""" == href
	}
	
	@Test
	void createLink_project_alternative () {
		EventController controller = dummy()
		def href = controller.createLink(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ3"), "te>st", 1, [:])
		assert """<a class="event-entity" href="gui/project/PROJ3">te&gt;st</a>""" == href
	}
	
	@Test
	void createLink_branch() {
		EventController controller = dummy()
		def href = controller.createLink(Entity.BRANCH, new EntityStub(Entity.BRANCH, 2001, "1.x"), null, 1, Collections.singletonMap(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ3")))
		assert """<a class="event-entity" href="gui/branch/PROJ3/1.x">1.x</a>""" == href
	}
	
	// TODO Use a proper exception
	@Test(expected = IllegalStateException)
	void createLink_branch_no_project() {
		EventController controller = dummy()
		controller.createLink(Entity.BRANCH, new EntityStub(Entity.BRANCH, 2001, "1.x"), null, 1, Collections.emptyMap())
	}
	
	@Test
	void expandToken_entity_project () {
		EventController controller = dummy()
		def value = controller.expandToken('$PROJECT$', new ExpandedEvent(1, EventType.PROJECT_CREATED, new DateTime()).withEntity(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1, "PROJ1")))
		assert """<a class="event-entity" href="gui/project/PROJ1">PROJ1</a>""" == value
	}
	
	@Test
	void expandToken_entity_project_group () {
		EventController controller = dummy()
		def value = controller.expandToken('$PROJECT_GROUP$', new ExpandedEvent(1, EventType.PROJECT_GROUP_CREATED, new DateTime()).withEntity(Entity.PROJECT_GROUP, new EntityStub(Entity.PROJECT_GROUP, 1, "GROUP1")))
		assert """<a class="event-entity" href="gui/project_group/GROUP1">GROUP1</a>""" == value
	}
	
	@Test
	void expandToken_entity_project_group_with_alternative () {
		EventController controller = dummy()
		def value = controller.expandToken('$PROJECT_GROUP|this group$', new ExpandedEvent(1, EventType.PROJECT_GROUP_CREATED, new DateTime()).withEntity(Entity.PROJECT_GROUP, new EntityStub(Entity.PROJECT_GROUP, 1, "GROUP2")))
		assert """<a class="event-entity" href="gui/project_group/GROUP2">this group</a>""" == value
	}
	
	@Test(expected = IllegalStateException)
	void expandToken_entity_project_not_found () {
		EventController controller = dummy()
		controller.expandToken('$PROJECT$', new ExpandedEvent(1, EventType.PROJECT_CREATED, new DateTime()))		
	}
	
	@Test
	void expandToken_value_only () {
		EventController controller = dummy()
		def value = controller.expandToken('$project$', new ExpandedEvent(1, EventType.PROJECT_DELETED, new DateTime()).withValue("project", "My > project"))
		assert """<span class="event-value">My &gt; project</span>""" == value
	}
	
	@Test
	void toGUIEvent_one_entity () {
		def strings = StringsLoader.auto(Locale.ENGLISH, Locale.FRENCH)
		EventController controller = new EventController(null, strings, null)
		def event = controller.toGUIEvent(new ExpandedEvent(10, EventType.PROJECT_CREATED, new DateTime(2013,1,30,10,5,30)).withEntity(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ2")), Locale.ENGLISH, new DateTime(2013,1,30,11,10,45))
		assert event != null
		assert event.id == 10
		assert event.eventType == EventType.PROJECT_CREATED
		assert event.timestamp == "Jan 30, 2013 10:05:30 AM"
		assert event.elapsed == "1 hour ago"
		assert 'Project <a class="event-entity" href="gui/project/PROJ2">PROJ2</a> has been created.' == event.html
	}
	
	@Test
	void toGUIEvent_two_entities () {
		def strings = StringsLoader.auto(Locale.ENGLISH, Locale.FRENCH)
		EventController controller = new EventController(null, strings, null)
		def event = controller.toGUIEvent(
			new ExpandedEvent(10, EventType.BRANCH_CREATED,
				new DateTime(2013,1,30,10,5,30))
				.withEntity(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ1"))
				.withEntity(Entity.BRANCH, new EntityStub(Entity.BRANCH, 2001, "1.x")),
			Locale.ENGLISH, new DateTime(2013,1,30,11,10,45))
		assert event != null
		assert event.id == 10
		assert event.eventType == EventType.BRANCH_CREATED
		assert event.timestamp == "Jan 30, 2013 10:05:30 AM"
		assert event.elapsed == "1 hour ago"
		assert 'Branch <a class="event-entity" href="gui/branch/PROJ1/1.x">1.x</a> has been created for the <a class="event-entity" href="gui/project/PROJ1">PROJ1</a> project.' == event.html
	}
	
	EventController dummy() {
		return new EventController(null, null, null)
	}

}
