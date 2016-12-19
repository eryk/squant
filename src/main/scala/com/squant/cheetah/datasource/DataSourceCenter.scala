package com.squant.cheetah.datasource

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.squant.cheetah.DataEngine
import io.circe.generic.auto._
import io.circe.syntax._

import scala.io.StdIn

//todo different source download
object DataSourceCenter extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val route =
    get {
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>文档</h1>"))
      } ~
        path("symbols") {
          complete(HttpEntity(ContentTypes.`application/json`, DataEngine.symbols().asJson.toString))
        } ~
        path("realtime" / Remaining) { code: String =>
          complete(DataEngine.realtime(code).asJson.toString)
        }
    }

  // `route` will be implicitly converted to `Flow` using `RouteResult.route2HandlerFlow`
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done


  //  system.scheduler.schedule(Duration.Zero, 1 days,  new Runnable {
  //    override def run(): Unit = {
  //      TushareDataSource.update()
  //    }
  //  })
  //
  //  system.scheduler.schedule(Duration.Zero, 1 days, new Runnable {
  //    override def run(): Unit = {
  //      SinaDataSource.update();
  //    }
  //  })
  //
  //  system.scheduler.schedule(Duration.Zero, 1 days, new Runnable {
  //    override def run(): Unit = {
  //      THSDataSource.update();
  //    }
  //  })
}
