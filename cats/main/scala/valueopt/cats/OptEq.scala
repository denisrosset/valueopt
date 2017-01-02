package opt
package cats

final class OptEq[A](implicit ev: Eq[A]) extends Eq[Opt[A]] {
  def eqv(x: Opt[A], y: Opt[A]): Boolean =
    if (x.isEmpty) y.isEmpty
    else if (y.isEmpty) x.isEmpty
    else ev.eqv(x.ref, y.ref)
}

object instances {

  implicit def optStdEqForOpt[A:Eq]: Eq[Opt[A]] = new OptEq[A]

}
