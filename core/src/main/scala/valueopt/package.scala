import scala.language.implicitConversions

import valueopt.valueoptimpl._

package object valueopt extends Types {

  val OptNone = new OptNoneImpl()

  implicit def OptOps[A](opt: Opt[A]): OptOps[A] = new OptOps[A](opt.asInstanceOf[valueopt.Opt[A]])

  object Opt {

    @inline def apply[A](x: A): Opt[A] =
      if (x == null) OptNone.asInstanceOf[Opt[A]]
      else x match {
        case value: OptNoneImpl => value.wrap.asInstanceOf[Opt[A]]
        case value: OptWrappedNone => value.wrap.asInstanceOf[Opt[A]]
        case _ => x.asInstanceOf[Opt[A]]
      }

  }

  object OptSome extends OptSomeImpl {

    @inline def apply[A](x: A): OptSome[A] = x match {
      case value: OptNoneImpl => value.wrap.asInstanceOf[OptSome[A]]
      case value: OptWrappedNone => value.wrap.asInstanceOf[OptSome[A]]
      case _ => x.asInstanceOf[OptSome[A]]
    }

  }

}
