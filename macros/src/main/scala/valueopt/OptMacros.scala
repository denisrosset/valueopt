package valueopt

object OptMacros {

  import OptMacrosCompat._

  def findLhs(c: Context): c.Tree = {
    import c.universe._
    c.prefix.tree match {
      case Apply(TypeApply(_, _), List(lhs)) => lhs
      case t => c.abort(c.enclosingPosition, "Cannot extract subject of operation (tree = %s)" format t)
    }
  }

  def filter[A:c.WeakTypeTag](c: Context)(f: c.Expr[A => Boolean]): c.Expr[Opt[A]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagA = implicitly[c.WeakTypeTag[A]]
    c.Expr[Opt[A]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty || $f($lhs.ref)) $lhs else _root_.valueopt.Opt.empty[$tagA]
""")
  }

  def filterNot[A:c.WeakTypeTag](c: Context)(f: c.Expr[A => Boolean]): c.Expr[Opt[A]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagA = implicitly[c.WeakTypeTag[A]]
    c.Expr[Opt[A]](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty && !$f($lhs.ref)) $lhs else _root_.valueopt.Opt.empty[$tagA]
""")
  }

  def map[A, B:c.WeakTypeTag](c: Context)(f: c.Expr[A => B]): c.Expr[Opt[B]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[Opt[B]](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) Opt[$tagB]($f($lhs.ref)) else _root_.valueopt.Opt.empty[$tagB]
""")
  }

  def flatMap[A, B:c.WeakTypeTag](c: Context)(f: c.Expr[A => Opt[B]]): c.Expr[Opt[B]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[Opt[B]](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) $f($lhs.ref) else _root_.valueopt.Opt.empty[$tagB]
""")
  }

  def flatten[A, B:c.WeakTypeTag](c: Context)(ev: c.Expr[A <:< Opt[B]]): c.Expr[Opt[B]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[Opt[B]](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) $ev($lhs.ref) else _root_.valueopt.Opt.empty[$tagB]
""")
  }

  def fold[A, B:c.WeakTypeTag](c: Context)(ifEmpty: c.Expr[B])(f: c.Expr[A => B]): c.Expr[B] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[B](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) $f($lhs.ref) else $ifEmpty
""")
  }

  def getOrElse[A, B >: A:c.WeakTypeTag](c: Context)(ifEmpty: c.Expr[B]): c.Expr[B] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[B](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) $lhs.ref else $ifEmpty
""")
  }

  def orNull[A, B >: A](c: Context)(ev: c.Expr[Null <:< B]): c.Expr[B] = {
    import c.universe._
    val lhsArg = findLhs(c)
    c.Expr[B](q"$lhsArg.ref")
  }

  def iterator[A:c.WeakTypeTag](c: Context): c.Expr[Iterator[A]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagA = implicitly[c.WeakTypeTag[A]]
    c.Expr[Iterator[A]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) _root_.scala.collection.Iterator.empty else _root_.scala.collection.Iterator.single[$tagA]($lhs.ref)
""")
  }

  def toOption[A:c.WeakTypeTag](c: Context): c.Expr[Option[A]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagA = implicitly[c.WeakTypeTag[A]]
    c.Expr[Option[A]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) _root_.scala.None else _root_.scala.Some[$tagA]($lhs.ref)
""")
  }

  def toList[A:c.WeakTypeTag](c: Context): c.Expr[List[A]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val tagA = implicitly[c.WeakTypeTag[A]]
    c.Expr[List[A]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) _root_.scala.collection.immutable.Nil 
else _root_.scala.collection.immutable.::[$tagA]($lhs.ref, _root_.scala.collection.immutable.Nil)
""")
  }

  def collect[A, B:c.WeakTypeTag](c: Context)(pf: c.Expr[PartialFunction[A, B]]): c.Expr[Opt[B]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    val pfCache = freshTermName(c)("pfCache$")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[Opt[B]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) Opt.empty[$tagB]
else {
  val $pfCache = $pf
  if ($pfCache.isDefinedAt($lhs.ref)) _root_.valueopt.Opt[$tagB]($pfCache($lhs.ref)) 
  else _root_.valueopt.Opt.empty[$tagB]
}
""")
  }

  def contains[A, A1 >: A](c: Context)(elem: c.Expr[A1]): c.Expr[Boolean] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    c.Expr[Boolean](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) false else ($lhs.ref == $elem)
""")
  }

  def exists[A](c: Context)(p: c.Expr[A => Boolean]): c.Expr[Boolean] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    c.Expr[Boolean](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) false else $p($lhs.ref)
""")
  }

  def forall[A](c: Context)(p: c.Expr[A => Boolean]): c.Expr[Boolean] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    c.Expr[Boolean](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) true else $p($lhs.ref)
""")
  }

  def foreach[A, U](c: Context)(f: c.Expr[A => U]): c.Expr[Unit] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    c.Expr[Unit](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) $f($lhs.ref)
""")
  }

  def toRight[A, X](c: Context)(left: c.Expr[X]): c.Expr[Either[X, A]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    c.Expr[Either[X, A]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) _root_.scala.util.Left($left) else _root_.scala.util.Right($lhs.ref)
""")
  }

  def toLeft[A, X](c: Context)(right: c.Expr[X]): c.Expr[Either[A, X]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs$")
    c.Expr[Either[A, X]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) _root_.scala.util.Right($right) else _root_.scala.util.Left($lhs.ref)
""")
  }


}
