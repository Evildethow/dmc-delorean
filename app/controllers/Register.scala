package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import views._

object Register extends Controller {

  val registerForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    ) verifying ("User already exists", result => result match {
      case (emailValue, passwordValue) => !users.exists(emailValue)
    })
  )

  def register = Action { implicit request => Ok(html.register(registerForm)) }

  def authenticateRegister = Action { implicit request =>
    registerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.register(formWithErrors)),
      user => {
        users.create(User(user._1, user._2))
        Redirect(routes.Home.gettingStarted).withSession("email" -> user._1)
      }
    )
  }
}
