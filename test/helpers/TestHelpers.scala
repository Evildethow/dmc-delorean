package helpers

import play.api.test.Helpers._
import play.api.test.FakeApplication

trait TestHelpers {
  def appWithMemoryDatabase : FakeApplication = {
    FakeApplication(additionalConfiguration = inMemoryDatabase())
  }
}
