/*
 * fieldBuilder.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package views.html.b5

import views.html.b5.B5.FieldBuilder

object fieldBuilder {

  def plain = FieldBuilder(views.html.b5._plain.f)
  def vertical = FieldBuilder(views.html.b5._vertical.f)
}
