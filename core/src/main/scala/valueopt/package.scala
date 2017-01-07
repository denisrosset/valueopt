import scala.language.implicitConversions

import valueopt.valueoptimpl._

package object valueopt extends Types {

  object OptNone {
    private[valueopt] val wrap: OptWrappedNone = new OptWrappedNone(1, this)
    override def toString(): String = "OptNone"
    override def equals(other: Any) = this eq (other.asInstanceOf[AnyRef])
    override def hashCode = 0xDEADDEAD
  }

  implicit def OptOps[A](opt: Opt[A]): OptOps[A] = new OptOps[A](opt.asInstanceOf[valueopt.Opt[A]])

  object Opt {

    @inline def some[A](x: A): Opt[A] = (OptSome(x): Opt[A])

    @inline def none[A]: Opt[A] = OptNone

    @inline def apply[A](x: A): Opt[A] =
      if (x == null) OptNone.asInstanceOf[Opt[A]]
      else x match {
        case value: OptNone.type => value.wrap.asInstanceOf[Opt[A]]
        case value: OptWrappedNone => value.wrap.asInstanceOf[Opt[A]]
        case _ => x.asInstanceOf[Opt[A]]
      }

  }

  object OptSome extends OptSomeImpl {

    @inline def apply[A](x: A): OptSome[A] = x match {
      case value: OptNone.type => value.wrap.asInstanceOf[OptSome[A]]
      case value: OptWrappedNone => value.wrap.asInstanceOf[OptSome[A]]
      case _ => x.asInstanceOf[OptSome[A]]
    }

  }

}
