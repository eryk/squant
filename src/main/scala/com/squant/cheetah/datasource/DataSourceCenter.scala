package com.squant.cheetah.datasource

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

//todo different source download
object DataSourceCenter extends App {

  val system = ActorSystem("AkkaScalaActorSystem")

  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher


  system.scheduler.schedule(Duration.Zero, 1 days, () => {
    TushareDataSource.update();
  })
  system.scheduler.schedule(Duration.Zero, 1 days, () => {
    SinaDataSource.update();
  })
  system.scheduler.schedule(Duration.Zero, 1 days, () => {
    THSDataSource.update();
  })

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val route =
    get {
      pathSingleSlash {
        complete("<html><body>Hello world!</body></html>")
      } ~
        path("ping") {
          complete("PONG!")
        } ~
        path("crash") {
          sys.error("BOOM!")
        }
    }

  // `route` will be implicitly converted to `Flow` using `RouteResult.route2HandlerFlow`
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
}
