package valueopt

object Opt extends OptVersions {

  def apply[A](a: A): Opt[A] = new Opt(a)

  @inline def empty[A]: Opt[A] = new Opt[A](null.asInstanceOf[A])

}

class Opt[+A](val ref: A) extends OptVersions.Base {
  def scala2_10hashCode: Int = ref.hashCode
  def scala2_10equals(other: Any): Boolean = other match {
    case that: Opt[_] => ref == that.ref
    case _ => false
  }
  def isDefined: Boolean = ref != null
  def nonEmpty: Boolean = ref != null
  def isEmpty: Boolean = ref == null

  def get: A = {
    ref.getClass // to throw NullPointerException early
    ref
  }

  override def toString: String =
    if (ref == null) "Opt.empty" else s"Opt($ref)"
}
