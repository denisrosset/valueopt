package valueopt

object OptMacrosCompat {

  type Context = scala.reflect.macros.Context

  def freshTermName[C <: Context](c: C)(s: String) =
    c.universe.newTermName(c.fresh(s))

  def termName[C <: Context](c: C)(s: String) =
    c.universe.newTermName(s)

  def typeCheck[C <: Context](c: C)(t: c.Tree) =
    c.typeCheck(t)

  def resetLocalAttrs[C <: Context](c: C)(t: c.Tree) =
    c.resetLocalAttrs(t)

  def setOrig[C <: Context](c: C)(tt: c.universe.TypeTree, t: c.Tree) =
    tt.setOriginal(t)

  def predef[C <: Context](c: C): c.Tree = {
    import c.universe._
    q"scala.this.Predef"
  }

}
