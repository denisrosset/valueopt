package valueopt

trait OptVersions {
  // name-based extractor, cf. http://hseeberger.github.io/blog/2013/10/04/name-based-extractors-in-scala-2-dot-11/
  def unapply[A](n: Opt[A]): Opt[A] = n
}

object OptVersions {

  trait Base extends Any {
    self: Opt[_] =>
    override def equals(other: Any): Boolean = Opt.equalsImpl(self, other)
    override def hashCode: Int = Opt.hashCodeImpl(self)
  }

}
