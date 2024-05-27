/*
 * B5.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package views.html.b5

import play.api.data.FormError
import play.twirl.api.{Html, Template1}

import scala.language.implicitConversions

/**
 * Contains some auxiliary scala code for the @b5 templates.
 */
private[b5]
object B5 {
  /**
   * Convert additional arguments of a field element to tag attributes
   */
  def toHtmlArgs(args: Seq[(String, Any)]): Html = {
    Html(
      args.map {
          case (s, None) => s
          case (s, v) => s + "=\"" + play.twirl.api.HtmlFormat.escape(v.toString).body + "\""
        }.mkString(" "))
  }

  /**
   * Contains the information needed by a field constructor.
   *
   * @param id       identifier
   * @param field    original form field (or null)
   * @param html     HTML for the input field proper
   * @param args     Additional arguments
   * @param messages Messages used in translation
   */
  case class FieldInformation(id: String,
                              field: play.api.data.Field,
                              html: Html,
                              args: Map[String, Any],
                              messages: play.api.i18n.Messages
                             ) {
    // adapted from views.html.helper.FieldElements

    /**
     * To be shown as additional information to the user. If args contains
     * the key _showConstraints -> true then also info from the constraints
     * is used
     */
    def infos: Seq[Any] = {
      args.get("_help").map(m => Seq(messages(m.toString))).getOrElse {
        if (args.get("_showConstraints") match {
          case Some(true) => field != null
          case _ => false
        }) {
          // removed i18n translation applied to the arguments
          field.constraints.map(c => messages(c._1, c._2: _*)) ++
            field.format.map(f => messages(f._1, f._2: _*))
        } else Nil
      }
    }

    /**
     * To be shown as error messages related to the field. Constraint errors
     * can be suppressed by setting key _showErrors to false
     */
    def errors: Seq[Any] = {
      (args.get("_error") match {
        // removed i18n applied to the arguments
        case Some(Some(FormError(_, message, args))) =>
          Some(messages(message.toString, args: _*))
        case Some(FormError(_, message, args)) =>
          Some(messages(message.toString, args: _*))
        case Some(None) => None
        case Some(value) => Some(messages(value.toString))
        case _ => None
      }).map(Seq(_)).getOrElse {
        if (
          args.get("_showErrors") match {
            case Some(false) => false
            case _ => field != null
          }
        ) {
            field.errors.map(e => messages(e.message, e.args: _*))
        } else {
          Nil
        }
      }
    }

    def hasErrors: Boolean = errors.nonEmpty

    def label: Any = {
      args.get("_label").map(l => messages(l.toString)).getOrElse(if (field!= null)messages(field.label) else "")
    }

//    def hasName: Boolean = args.contains("_name")

    def name: Any = {
      args.get("_name").map(n => messages(n.toString)).getOrElse(if (field!= null)messages(field.name) else "")
    }

  }

  /**
   * All information needed to produce a form element tag, like input, textarea, ...
   *
   * @param id      id of the field
   * @param name    name of the field
   * @param value   value of the field
   * @param classes CSS classes for the field
   * @param args    Additional arguments
   */
  case class TagInformation(
                             id: String,
                             name: String,
                             value: Option[String],
                             classes: Seq[String],
                             args: Seq[(String, Any)]
                           ) {
    def classArg: String = classes.mkString(" ")
  }

  /**
   * Builds a field from field information
   */
  trait FieldBuilder {
    def apply(info: FieldInformation): Html
  }

  object FieldBuilder {
    // same structure as helper.FieldConstructor

    implicit val defaultFieldBuilder: FieldBuilder = FieldBuilder(views.html.b5._vertical.f)

    def apply(f: FieldInformation => Html): FieldBuilder = (information: FieldInformation) => f(information)

    implicit def inlineFieldBuilder(f: FieldInformation => Html): FieldBuilder = FieldBuilder(f)

    implicit def templateAsFieldBuilder(t: Template1[FieldInformation, Html]): FieldBuilder = FieldBuilder(t.render)
  }
}
