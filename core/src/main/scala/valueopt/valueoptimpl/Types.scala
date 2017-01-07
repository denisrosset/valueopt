package valueopt.valueoptimpl

import scala.language.higherKinds

import valueopt.OptNone

private[valueopt] class Types {

  type Opt[+A] >: OptNoneImpl <: AnyRef

  type OptSome[+A] <: Opt[A]

}
