package controllers

import play.api.mvc._

object Application extends Controller {

}

trait Secured {

  private def username(request: RequestHeader) = request.session.get("email")
  private def onUnauthorised(request: RequestHeader) = Results.Redirect(routes.Login.login)

  def isAuthenticated(f: String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorised) { user =>
    Action(request => f(user)(request))
  }
}