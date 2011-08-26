package scalaxb

trait DispatchHttpClients extends HttpClients {
  val httpClient = new DispatchHttpClient {}

  trait DispatchHttpClient extends HttpClient {
    import dispatch._

    def request(in: String, address: java.net.URI, action: Option[java.net.URI]): String = {
      val header = Map(action.toList map { x => ("SOAPAction", "\"%s\"".format(x.toString)) }: _*)

      println("härpäke" + (url(address.toString) << (in, "text/xml") <:< header as_str)) // TODO Testi
      
      val http = new Http
      http(url(address.toString) << (in, "text/xml") <:< header as_str)
    }
  }
}
