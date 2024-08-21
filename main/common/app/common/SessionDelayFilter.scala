/*
 * SessionDelayFilter.scala
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package common

import org.apache.pekko.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Filter that renews the session with everey request
 * @see https://stackoverflow.com/questions/50170244/how-to-extend-or-change-the-session-timeout-value-i-e-session-maxage-in-play-fr
 */
class SessionDelayFilter @Inject()(implicit override val mat: Materializer, exec: ExecutionContext) extends Filter {
    // there does not seem an easy way to do this in Java instead

    override def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
      nextFilter(requestHeader).map { result =>
        result.withSession(result.session(requestHeader))
      }
    }
}
