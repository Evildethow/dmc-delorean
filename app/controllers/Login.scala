package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import views._

object Login extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    ) verifying ("Invalid email or password", result => result match {
      case (emailValue, passwordValue) => users.isAuthenticated(emailValue, passwordValue)
    })
  )

  def login = Action { implicit request => Ok(html.login(loginForm)) }

  def authenticateLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Home.gettingStarted).withSession("email" -> user._1)
    )
  }

  def logout = Action { Redirect(routes.Home.index).withNewSession }
}
