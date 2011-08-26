package ogham.bbm

import scalaxb._
import bbmanager._

trait BBMServer {
	val remote = new PublicServiceSoap12Bindings with SoapClients with DispatchHttpClients {}		
}