package models

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import helpers.TestHelpers

@RunWith(classOf[JUnitRunner])
class UserSpec extends Specification with TestHelpers {

  "User object" should {

    "authenticate existing users" in new WithApplication(appWithMemoryDatabase) {
      DB withSession { implicit session => users += User("foo", "bar") }

      users.isAuthenticated("foo", "bar") mustEqual true
    }

    "not authenticate users that do not exist" in new WithApplication(appWithMemoryDatabase) {
      DB withSession { implicit session => users += User("foo", "bar") }

      users.isAuthenticated("foo", "bar2") mustEqual false
    }

    "create new users" in new WithApplication(appWithMemoryDatabase) {
      val newUser = users.create(User("foo", "bar"))
      val newUser2 = users.create(User("foo", "bar2"))

      newUser.id.get mustEqual 1
      newUser.email mustEqual "foo"
      newUser.password mustEqual "bar"
      newUser2.id.get mustEqual 2
      newUser2.email mustEqual "foo"
      newUser2.password mustEqual "bar2"
    }

  }

}
