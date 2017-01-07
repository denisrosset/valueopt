package valueopt.valueoptimpl

import scala.language.higherKinds

import valueopt.OptNone

private[valueopt] class Types {

  type Opt[+A] >: OptNone.type <: AnyRef

  type OptSome[+A] <: Opt[A]

}
