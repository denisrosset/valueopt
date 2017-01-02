package valueopt

object Opt extends OptVersions {

  @inline final def apply[A](a: A): Opt[A] = new Opt(a)

  @inline final def empty[A]: Opt[A] = new Opt[A](null.asInstanceOf[A])

}

class Opt[+A](val ref: A) extends OptVersions.Base {
  @inline final def scala2_10hashCode: Int = ref.hashCode
  @inline final def scala2_10equals(other: Any): Boolean = other match {
    case that: Opt[_] => ref == that.ref
    case _ => false
  }
  @inline final def isDefined: Boolean = ref != null
  @inline final def nonEmpty: Boolean = ref != null
  @inline final def isEmpty: Boolean = ref == null

  @inline final def get: A = {
    ref.getClass // to throw NullPointerException early
    ref
  }

  override def toString: String =
    if (ref == null) "Opt.empty" else s"Opt($ref)"
}
