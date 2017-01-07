package valueopt.valueoptimpl

private[valueopt] final class OptWrappedNone(
                            val depth: Int, // depth of nesting
                            val unwrap: Any // ONone or WrappedNone
                          ) {
  lazy val wrap: OptWrappedNone = new OptWrappedNone(depth + 1, this)
  private[this] val stringRepr: String = ("OptSome(" * depth) + "OptNone" + (")" * depth)
  override def toString(): String = stringRepr
  override def equals(other: Any) = other match {
    case wn: OptWrappedNone => depth == wn.depth
    case _ => false
  }
  override def hashCode = depth
}
