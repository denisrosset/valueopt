package valueopt.valueoptimpl

import valueopt.Opt

class OptSomeImpl {

  def unapply[A](opt: Opt[A]): Option[A] =
    if (self.isInstanceOf[OptNoneImpl]) None else Some(opt.asInstanceOf[A])

}
