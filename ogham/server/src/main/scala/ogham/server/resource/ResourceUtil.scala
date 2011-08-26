package ogham.server.resource

import sjson.json.Serializer

trait ResourceUtil {
	def serializeAsJSON(obj: AnyRef): String = new String(Serializer.SJSON.out(obj))

	def deserializeJSON[T](json: Array[Byte])(implicit m: Manifest[T]): T =
		Serializer.SJSON.in[T](json)(m)
}