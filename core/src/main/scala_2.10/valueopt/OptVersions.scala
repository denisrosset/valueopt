package valueopt

trait OptVersions {
  def unapply[A](n: Opt[A]): Option[A] = if (n.isEmpty) None else Some(n.ref)
}

object OptVersions {
  trait Base extends AnyRef {
    self: Opt[_] =>
    override def equals(other: Any): Boolean = scala2_10equals(other)
    override def hashCode: Int = scala2_10hashCode
  }
}
