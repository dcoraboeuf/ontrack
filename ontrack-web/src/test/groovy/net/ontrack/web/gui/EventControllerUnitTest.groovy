package net.ontrack.web.gui

import net.ontrack.core.model.Entity
import net.ontrack.core.model.EntityStub
import net.ontrack.core.model.EventType
import net.ontrack.core.model.ExpandedEvent
import net.sf.jstring.support.StringsLoader

import org.joda.time.DateTime
import org.junit.Test

class EventControllerUnitTest {
	
	@Test
	void createLinkHref_project_group () {
		EventController controller = new EventController(null, null)
		def href = controller.createLinkHref(Entity.PROJECT_GROUP, new EntityStub(5, "My project group"))
		assert """gui/project_group/5""" == href
	}
	
	@Test
	void createLinkHref_project () {
		EventController controller = new EventController(null, null)
		def href = controller.createLinkHref(Entity.PROJECT, new EntityStub(1001, "My project"))
		assert """gui/project/1001""" == href
	}
	
	@Test
	void createLink_project_group_no_alternative () {
		EventController controller = new EventController(null, null)
		def href = controller.createLink(Entity.PROJECT_GROUP, new EntityStub(2001, "My > project group"), null)
		assert """<a href="gui/project_group/2001">My &gt; project group</a>""" == href
	}
	
	@Test
	void createLink_project_group_alternative () {
		EventController controller = new EventController(null, null)
		def href = controller.createLink(Entity.PROJECT_GROUP, new EntityStub(2001, "My > project group"), "te>st")
		assert """<a href="gui/project_group/2001">te&gt;st</a>""" == href
	}
	
	@Test
	void createLink_project_no_alternative () {
		EventController controller = new EventController(null, null)
		def href = controller.createLink(Entity.PROJECT, new EntityStub(1001, "My > project"), null)
		assert """<a href="gui/project/1001">My &gt; project</a>""" == href
	}
	
	@Test
	void createLink_project_alternative () {
		EventController controller = new EventController(null, null)
		def href = controller.createLink(Entity.PROJECT, new EntityStub(1001, "My > project"), "te>st")
		assert """<a href="gui/project/1001">te&gt;st</a>""" == href
	}
	
	@Test
	void expandToken_entity_project () {
		EventController controller = new EventController(null, null)
		def value = controller.expandToken('$PROJECT$', new ExpandedEvent(1, EventType.PROJECT_CREATED, new DateTime()).withEntity(Entity.PROJECT, new EntityStub(1, "My project")))
		assert """<a href="gui/project/1">My project</a>""" == value
	}
	
	@Test
	void expandToken_entity_project_group () {
		EventController controller = new EventController(null, null)
		def value = controller.expandToken('$PROJECT_GROUP$', new ExpandedEvent(1, EventType.PROJECT_GROUP_CREATED, new DateTime()).withEntity(Entity.PROJECT_GROUP, new EntityStub(1, "My project group")))
		assert """<a href="gui/project_group/1">My project group</a>""" == value
	}
	
	@Test
	void expandToken_entity_project_group_with_alternative () {
		EventController controller = new EventController(null, null)
		def value = controller.expandToken('$PROJECT_GROUP|this group$', new ExpandedEvent(1, EventType.PROJECT_GROUP_CREATED, new DateTime()).withEntity(Entity.PROJECT_GROUP, new EntityStub(1, "My project group")))
		assert """<a href="gui/project_group/1">this group</a>""" == value
	}
	
	@Test(expected = IllegalStateException)
	void expandToken_entity_project_not_found () {
		EventController controller = new EventController(null, null)
		controller.expandToken('$PROJECT$', new ExpandedEvent(1, EventType.PROJECT_CREATED, new DateTime()))		
	}
	
	@Test
	void toGUIEvent_one_entity () {
		def strings = StringsLoader.auto(Locale.ENGLISH, Locale.FRENCH)
		EventController controller = new EventController(null, strings)
		def now = new DateTime()
		def event = controller.toGUIEvent(new ExpandedEvent(10, EventType.PROJECT_CREATED, now).withEntity(Entity.PROJECT, new EntityStub(1001, "My project")), Locale.ENGLISH)
		assert event != null
		assert event.id == 10
		assert event.eventType == EventType.PROJECT_CREATED
		assert event.timestamp == now
		assert 'Project <a href="gui/project/1001">My project</a> has been created.' == event.html
	}

}
