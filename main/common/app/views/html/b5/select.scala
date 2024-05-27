/*
 * select.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package views.html.b5

import play.twirl.api.Html

object select {

  def apply(field: play.api.data.Field, options: Seq[(String, String)], args: (String, Any)*)(implicit handler: B5.FieldBuilder, messages: play.api.i18n.MessagesProvider): Html =
    views.html.b5._select(field, options, args: _*)(handler, messages)

  def apply(field: play.api.data.Field, enumClass: Class[_ <: Enum[_]], i18nPrefix: String, args: (String, Any)*)(implicit handler: B5.FieldBuilder, messages: play.api.i18n.MessagesProvider): Html =
    apply(field,
      enumClass.getEnumConstants.map(_.name).map(name => (name, i18nPrefix + "." + name)).toSeq,
      args: _*)(handler, messages)

}