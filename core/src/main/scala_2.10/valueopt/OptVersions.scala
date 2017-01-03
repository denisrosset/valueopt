package valueopt

trait OptVersions {
  def unapply[A](n: Opt[A]): Option[A] = if (n.isEmpty) None else Some(n.ref)
}

object OptVersions {

  trait Base extends AnyRef {
    self: Opt[_] =>
    override def equals(other: Any): Boolean = Opt.equalsImpl(self, other)
    override def hashCode: Int = Opt.hashCodeImpl(self)
  }

}
