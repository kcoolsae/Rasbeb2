/*
 * Filters.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

import common.SessionDelayFilter
import play.api.http.{DefaultHttpFilters, EnabledFilters}

import javax.inject.Inject

// There seems to be no easy way to do this in Java
class Filters @Inject()(enabledFilters: EnabledFilters, sessionDelayFilter: SessionDelayFilter)
  extends DefaultHttpFilters(enabledFilters.filters :+ sessionDelayFilter: _*)