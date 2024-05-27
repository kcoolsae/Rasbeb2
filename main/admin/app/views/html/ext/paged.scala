/*
 * paged.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package views.html.ext

import be.ugent.caagt.play.util.Table
import play.i18n.Messages
import play.twirl.api.Html

object paged {

  // cannot be programmed as a template, play/twirl does not support bound generic type parameters

  def apply[T <: Table](table: T, count: Int)(pre: => Html)(header: T => Html)(body: => Html)(footer: T => Html)(implicit messages: Messages) =
    new Html(List(
      Html("<div class='paged-table'>"),
      views.html.tables.form_with_parts(table, count)(pre)(header(table))(body)(footer(table)),
      views.html.tables.post(table, count),
      Html("</div>")
    ))

}
