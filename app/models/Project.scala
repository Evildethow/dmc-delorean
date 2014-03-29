package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.Play.current

case class Project(name: String, id: Option[Int] = None)

class Projects(tag: Tag) extends Table[Project](tag, "PROJECTS") {
  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)

  def * = (name, id) <> (Project.tupled, Project.unapply)
}

object projects extends TableQuery(new Projects(_)) {

  private val projectAutoInc = projects returning projects.map(_.id) into { case (p, id) => p.copy(id = id) }

  def create(project: Project, user: User) : Project = {
    DB withSession { implicit session =>
      val createdProject = projectAutoInc.insert(project)
      projectMembers.create(user, createdProject)
      createdProject
    }
  }

  private def users(projectId: Column[Option[Int]]) =
    for {
      pm <- projectMembers
      p <- pm.projectJoin if p.id === projectId
      u <- pm.userJoin
    } yield u
  private val usersCompiled = Compiled(users _)

  def findUsers(project: Project) = { DB withSession { implicit session => usersCompiled(project.id).run } }
}

private case class ProjectMember(userId: Option[Int] = None, projectId: Option[Int] = None)

private class ProjectsMembers(tag: Tag) extends Table[ProjectMember](tag, "PROJECTS_MEMBERS") {
  def userId = column[Option[Int]]("USER_ID")
  def projectId = column[Option[Int]]("PROJECT_ID")

  def * = (userId, projectId) <> (ProjectMember.tupled, ProjectMember.unapply)

  def userFK = foreignKey("USER_FK", userId, users)(_.id)
  def projectFK = foreignKey("PROJECT_FK", projectId, projects)(_.id)
  def userJoin = users.filter(_.id === userId)
  def projectJoin = projects.filter(_.id === projectId)
}

private object projectMembers extends TableQuery(new ProjectsMembers(_)) {

  def create(user: User, project: Project) = {
    DB withSession { implicit session =>
      this += ProjectMember(user.id, project.id)
    }
  }
}


