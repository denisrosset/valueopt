package valueopt.valueoptimpl

import valueopt.Opt

final class OptNoneImpl private[valueopt] {

  private[valueopt] val wrap: OptWrappedNone = new OptWrappedNone(1, this)

  override def toString(): String = "ONone"

  override def equals(other: Any) = other.isInstanceOf[OptNoneImpl]

  override def hashCode = 0xDEADDEAD

  @inline def unapply[A](opt: Opt[A]): Boolean = opt.isInstanceOf[OptNoneImpl]

}
