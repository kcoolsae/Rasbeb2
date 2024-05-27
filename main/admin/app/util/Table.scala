/*
 * Table.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package util

import be.ugent.caagt.play.binders.PSF
import play.twirl.api.{Html, HtmlFormat}

abstract class Table(psf: PSF) extends _root_.be.ugent.caagt.play.util.Table(psf){

  override def columnheader(field: String)(html: Html): HtmlFormat.Appendable = views.html.tables.column_header(this, field)(html)

  override def searchfield(name: String, placeHolder: String): HtmlFormat.Appendable = views.html.tables.search_field(psf.getFilter,name,placeHolder)
}
