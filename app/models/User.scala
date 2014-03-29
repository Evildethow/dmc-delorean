package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.Play.current

case class User(email: String, password: String, id: Option[Int] = None)

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
  def email = column[String]("EMAIL", O.NotNull)
  def password = column[String]("PASSWORD", O.NotNull)

  def * = (email, password, id) <> (User.tupled, User.unapply)
}

object users extends TableQuery(new Users(_)) {

  private def isAuthenticatedQuery(email: Column[String], password: Column[String]) = {
    filter(_.email === email).filter(_.password === password).exists
  }
  private val isAuthenticatedCompiled = Compiled(isAuthenticatedQuery _)

  def isAuthenticated(email: String, password: String) : Boolean = {
    DB withSession { implicit session => isAuthenticatedCompiled(email, password).run }
  }

  private val usersAutoInc = users returning users.map(_.id) into { case (u, id) => u.copy(id = id) }

  def create(user: User) : User = { DB withSession { implicit session => usersAutoInc.insert(user) } }

  private def existsQuery(email: Column[String]) = { filter(_.email === email).exists }
  private val existsCompiled = Compiled(existsQuery _)

  def exists(email: String) = { DB withSession { implicit session => existsCompiled(email).run } }


}