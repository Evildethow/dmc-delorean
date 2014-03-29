package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import helpers.TestHelpers

@RunWith(classOf[JUnitRunner])
class ProjectSpec extends Specification with TestHelpers {

  "Project object" should {
    "create new projects and associate users with projects" in new WithApplication(appWithMemoryDatabase) {
      val userA = users.create(User("usera@some.com", "password"))
      val userB = users.create(User("userb@some.com", "password"))

      val projectA = projects.create(Project("projectA"), userA)
      val projectB = projects.create(Project("projectB"), userB)
      val expectedUsersForProjectA = projects.findUsers(projectA)
      val expectedUsersForProjectB = projects.findUsers(projectB)

      projectA.id.get mustEqual 1
      projectB.id.get mustEqual 2
      projectA.name mustEqual "projectA"
      projectB.name mustEqual "projectB"
      expectedUsersForProjectA.contains(userA) mustEqual true
      expectedUsersForProjectA.contains(userB) mustEqual false
      expectedUsersForProjectB.contains(userB) mustEqual true
      expectedUsersForProjectB.contains(userA) mustEqual false
    }
  }
}
