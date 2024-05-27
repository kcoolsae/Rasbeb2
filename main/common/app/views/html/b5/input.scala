/*
 * input.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package views.html.b5

import play.twirl.api.Html
import views.html.b5.B5.{FieldInformation, TagInformation}

object input {

  def apply(field: play.api.data.Field, b5class: String, args: (String, Any)*)(elementBuilder: B5.TagInformation => Html)(implicit fieldBuilder: B5.FieldBuilder, messages: play.api.i18n.Messages): Html = {

    val id : String = args.filter(_._1 == "id").map(_._2.toString).take(1).orElse(Seq(field.id))(0)
    val classes = b5class +: (args.filter(_._1 == "class").map(_._2.toString) ++ (if (field.hasErrors) Some("is-invalid") else None))
    fieldBuilder(
      FieldInformation(
        id,
        field,
        elementBuilder(TagInformation(id, field.name, getValue(args).orElse(field.value), classes, filterArgs(args: _*))),
        args.toMap,
        messages
      )
    )
  }

  def apply (name: String, b5class: String, args: (String, Any)*)(elementBuilder: B5.TagInformation => Html)(implicit fieldBuilder: B5.FieldBuilder, messages: play.api.i18n.Messages): Html = {

    val id: String = args.filter(_._1 == "id").map(_._2.toString).take(1).orElse(Seq(name))(0)
    val classes = b5class +: args.filter(_._1 == "class").map(_._2.toString)
    fieldBuilder(
      FieldInformation(
        id,
        null,
        elementBuilder(TagInformation(id, name, getValue(args), classes, filterArgs(args: _*))),
        args.toMap,
        messages
      )
    )
  }

  private def filterArgs(args: (String, Any)*) = {
    args.filter(arg => arg._1 != "class" && arg._1 != "id" && !arg._1.startsWith("_"))
  }

  private def getValue(args: Seq[(String, Any)]): Option[String] = {
    Option(args.filter(_._1 == "value").map(_._2.toString).take(1).orElse(Seq(null))(0))
  }
}
