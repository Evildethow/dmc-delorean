package controllers

import play.api.mvc._

object Home extends Controller {

  def index = Action { implicit request => Ok(views.html.index()) }
  def about = Action { implicit request => Ok(views.html.about()) }
  def gettingStarted = Action { implicit request => Ok(views.html.getting_started()) }

}
