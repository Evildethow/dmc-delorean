package controllers

import play.api.mvc._
import play.api.libs.json._
import models.users._

object Application extends Controller {

  def listUsers = Action {
    val json = Json.toJson(list())
    Ok(json)
  }
}

trait Secured {

  private def username(request: RequestHeader) = request.session.get("email")
  private def onUnauthorised(request: RequestHeader) = Results.Redirect(routes.Login.login())

  def isAuthenticated(f: String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorised) { user =>
    Action(request => f(user)(request))
  }
}