/*
 * Filters.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

import common.SessionDelayFilter

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.api.http.EnabledFilters

// There seems to be no easy way to do this in Java
class Filters @Inject() (enabledFilters: EnabledFilters, sessionDelayFilter: SessionDelayFilter)
  extends DefaultHttpFilters(enabledFilters.filters :+ sessionDelayFilter: _*)