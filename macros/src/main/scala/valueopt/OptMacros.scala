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
    val lhs = freshTermName(c)("lhs")
    val tagA = implicitly[c.WeakTypeTag[A]]
    c.Expr[Opt[A]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty || $f($lhs.ref)) $lhs else _root_.valueopt.Opt.empty[$tagA]
""")
  }

  def filterNot[A:c.WeakTypeTag](c: Context)(f: c.Expr[A => Boolean]): c.Expr[Opt[A]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs")
    val tagA = implicitly[c.WeakTypeTag[A]]
    c.Expr[Opt[A]](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty && !$f($lhs.ref)) $lhs else _root_.valueopt.Opt.empty[$tagA]
""")
  }

  def map[A, B:c.WeakTypeTag](c: Context)(f: c.Expr[A => B]): c.Expr[Opt[B]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[Opt[B]](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) Opt[$tagB]($f($lhs.ref)) else _root_.valueopt.Opt.empty[$tagB]
""")
  }

  def flatMap[A, B:c.WeakTypeTag](c: Context)(f: c.Expr[A => Opt[B]]): c.Expr[Opt[B]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[Opt[B]](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) $f($lhs.ref) else _root_.valueopt.Opt.empty[$tagB]
""")
  }

  def flatten[A, B:c.WeakTypeTag](c: Context)(ev: c.Expr[A <:< Opt[B]]): c.Expr[Opt[B]] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[Opt[B]](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) $ev($lhs.ref) else _root_.valueopt.Opt.empty[$tagB]
""")
  }

  def fold[A, B:c.WeakTypeTag](c: Context)(ifEmpty: c.Expr[B])(f: c.Expr[A => B]): c.Expr[B] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs")
    val tagB = implicitly[c.WeakTypeTag[B]]
    c.Expr[B](q"""
val $lhs = $lhsArg
if ($lhs.nonEmpty) $f($lhs.ref) else $ifEmpty
""")
  }

  def getOrElse[A, B >: A:c.WeakTypeTag](c: Context)(ifEmpty: c.Expr[B]): c.Expr[B] = {
    import c.universe._
    val lhsArg = findLhs(c)
    val lhs = freshTermName(c)("lhs")
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
    val lhs = freshTermName(c)("lhs")
    val tagA = implicitly[c.WeakTypeTag[A]]
    c.Expr[Iterator[A]](q"""
val $lhs = $lhsArg
if ($lhs.isEmpty) _root_.scala.collection.Iterator.empty else _root_.scala.collection.Iterator.single[$tagA]($lhs.ref)
""")
  }



}
