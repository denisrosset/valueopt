package valueopt.valueoptimpl

import valueopt.{Opt, OptNone}

class OptSomeImpl {

  def unapply[A](opt: Opt[A]): OptUnapply[A] = new OptUnapply[A](opt: Opt[A])

}

class OptUnapply[A](val self: Opt[A]) extends AnyVal {

  @inline def isEmpty: Boolean = self.isInstanceOf[OptNone.type]

  @inline def get: A = (self: Any) match {
    case none: OptWrappedNone => none.unwrap.asInstanceOf[A]
    case _: OptNone.type => throw new NoSuchElementException("OptNone.get")
    case _ => self.asInstanceOf[A]
  }

}
