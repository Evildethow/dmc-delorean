package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.Play.current

case class User(email: String, password: String, id: Option[Int] = None)

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
  def email = column[String]("EMAIL", O.NotNull)
  def password = column[String]("PASSWORD", O.NotNull)

  def * = (email, password, id) <>(User.tupled, User.unapply)
}

object users extends TableQuery(new Users(_)) {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reads: Reads[User] = (
    (__ \ "email").read[String] and
      (__ \ "password").read[String] and
      (__ \ "id").readNullable[Int]
    )(User.apply _)

  implicit val writes: Writes[User] = (
    (__ \ "email").write[String] and
      (__ \ "password").write[String] and
      (__ \ "id").writeNullable[Int]
    )(unlift(User.unapply))

  def list(): Seq[User] = {
    DB withSession { implicit session =>
      val query = for {
        u <- users
      } yield u
      query.list
    }
  }

  def isAuthenticated(email: String, password: String) = {
    DB withSession { implicit session =>
      filter(_.email === email).filter(_.password === password).exists.run
    }
  }

  private val usersAutoInc = users returning users.map(_.id) into {
    case (u, id) => u.copy(id = id)
  }

  def create(user: User): User = {
    DB withSession {
      implicit session => usersAutoInc.insert(user)
    }
  }

  private val existsCompiled = {
    def query(email: Column[String]) = filter(_.email === email).exists
    Compiled(query _)
  }

  def exists(email: String) = {
    DB withSession { implicit session => existsCompiled(email).run }
  }
}

object UserJSON {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reads: Reads[User] = (
    (__ \ "email").read[String] and
      (__ \ "password").read[String] and
      (__ \ "id").readNullable[Int]
    )(User.apply _)

  implicit val writes: Writes[User] = (
    (__ \ "email").write[String] and
      (__ \ "password").write[String] and
      (__ \ "id").writeNullable[Int]
    )(unlift(User.unapply))
}