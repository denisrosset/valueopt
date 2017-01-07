package valueopt.valueoptimpl

import valueopt.{Opt, OptNone}

class OptSomeImpl {

  def unapply[A](opt: Opt[A]): Option[A] =
    if (opt.isInstanceOf[OptNone.type]) None else Some(opt.asInstanceOf[A])

}
