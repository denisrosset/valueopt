package valueopt

object Opt extends OptVersions {

  // the OptVersions trait will define a `unapply` method that either
  // - returns Opt[A] itself for Scala >= 2.11, using the name-based extractor pattern
  // - returns an `Option[A]` for Scala 2.10 to satisfy the unapply contract 

  @inline final def apply[A](a: A): Opt[A] = new Opt(a)

  @inline final def empty[A]: Opt[A] = new Opt[A](null.asInstanceOf[A])

  // implementations of equals and hashCode overriden in OptVersions.Base, to be
  // used when `Opt` is boxed
  @inline final def equalsImpl(lhs: Opt[_], rhs: Any): Boolean = rhs match {
    case that: Opt[_] => lhs.ref == that.ref
    case _ => false
  }

  @inline final def hashCodeImpl(lhs: Opt[_]): Int = if (lhs.ref == null) -1 else lhs.ref.hashCode

}

class Opt[+A](val ref: A) extends OptVersions.Base {
  // OptVersions.Base is a universal trait (for Scala >= 2.11) or a standard trait (for Scala = 2.10)
  // that overrides hashCode and equals, to be used when `Opt` is itself made concrete

  @inline final def isDefined: Boolean = ref != null

  @inline final def nonEmpty: Boolean = ref != null

  // used for name-based extractors
  @inline final def isEmpty: Boolean = ref == null

  // for name-based extractors
  @inline final def get: A = {
    ref.getClass // to throw NullPointerException on empty `Opt`; this is how javac writes some null checks,
                 // see https://github.com/scala/scala-dev/issues/107
    ref
  }

  override def toString: String =
    if (ref == null) "Opt.empty" else s"Opt($ref)"

}
