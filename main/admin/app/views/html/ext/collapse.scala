/*
 * collapse.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package views.html.ext

import common.Deputy
import play.twirl.api.Html

object collapse {

  def form (call: play.api.mvc.Call, formId: String, buttonCaption: String = "common.caption.save", collapsed: Boolean = true)(content: => Html)(implicit deputy: Deputy): Html =
    views.html.ext._collapse_form.render (call, formId, buttonCaption, collapsed,content,deputy)

  def button(formId: String, buttonCaption: String)(implicit deputy: Deputy): Html =
    views.html.ext._collapse_button.render (formId, buttonCaption, deputy)
    
}
