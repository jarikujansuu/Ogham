package scalaxb

trait HttpClients {
  val httpClient: HttpClient

  trait HttpClient {
    def request(in: String, address: java.net.URI, action: Option[java.net.URI]): String
  }
}

case class Fault[+A](original: Any, detail: Option[A]) {
  def asFault[B: scalaxb.XMLFormat] = Fault(original, detail map {
    case x: soapenvelope12.Detail => x.any.head.value match {
      case node: scala.xml.Node => scalaxb.fromXML[B](node)
      case _ => error("unsupported fault: " + toString)
    }
    case _ => error("unsupported fault: " + toString)
  })
}

trait SoapClients { this: HttpClients =>
  import soapenvelope12.{Envelope, Body, Detail}

  lazy val soapClient: SoapClient = new SoapClient {}
  val baseAddress: java.net.URI

  trait SoapClient {
    val SOAP_ENVELOPE_URI = "http://www.w3.org/2003/05/soap-envelope"

    def soapRequest(in: Option[Envelope], scope: scala.xml.NamespaceBinding,
                    address: java.net.URI, webMethod: String, action: Option[java.net.URI]): Envelope = {
      val merged = scalaxb.toScope(((Some("soap12") -> "http://www.w3.org/2003/05/soap-envelope") ::
        scalaxb.fromScope(scope)).distinct: _*)
      val r = in map  { scalaxb.toXML(_, Some(SOAP_ENVELOPE_URI), Some("Envelope"), merged) match {
        case elem: scala.xml.Elem => elem
        case x => error("unexpected non-elem: " + x.toString)
      }}
      val s = httpClient.request(r map {_.toString} getOrElse {""}, address, action)
      val response = scala.xml.XML.loadString(s)
      scalaxb.fromXML[Envelope](response)
    }

    def requestResponse(in: scala.xml.NodeSeq, scope: scala.xml.NamespaceBinding,
                        address: java.net.URI, webMethod: String, action: Option[java.net.URI]):
        Either[Fault[Detail], scala.xml.Node] = {
      val envelope = Envelope(None, Body(Seq(DataRecord(None, None, in)), Map()), Map())
      buildResponse(soapRequest(Some(envelope), scope, address, webMethod, action))
    }

    def soapResponse(location: Option[String], params: Map[String, Any],
                     address: java.net.URI, webMethod: String, action: Option[java.net.URI]):
        Either[Fault[Detail], scala.xml.Node] = {
      buildResponse(soapRequest(None, scala.xml.TopScope, address, webMethod, action))
    }

    def buildResponse(soapResponse: Envelope):
        Either[Fault[Detail], scala.xml.Node] = soapResponse.Body.any.headOption match {
      case Some(DataRecord(_, _, x: scala.xml.Elem)) if (x.label == "Fault") &&
          (x.scope.getURI(x.prefix) == SOAP_ENVELOPE_URI) =>
        val fault = scalaxb.fromXML[soapenvelope12.Fault](x)
        Left(Fault(fault, fault.Detail))
      case Some(DataRecord(_, _, x: scala.xml.Elem)) => Right(x)
      case _ => error("unexpected response: " + soapResponse.toString)
    }
  }
}
