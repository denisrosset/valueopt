package object valueopt {

  class OptMethods[A](val lhs: Opt[A]) {

    @inline def ref: A = lhs.ref

    def filter(f: A => Boolean): Opt[A] = macro OptMacros.filter[A]

    def filterNot(f: A => Boolean): Opt[A] = macro OptMacros.filterNot[A]

    def withFilter(f: A => Boolean): Opt[A] = macro OptMacros.filter[A]

    def map[B](f: A => B): Opt[B] = macro OptMacros.map[A, B]

    def flatMap[B](f: A => Opt[B]): Opt[B] = macro OptMacros.flatMap[A, B]

    def flatten[B](implicit ev: A <:< Opt[B]): Opt[B] = macro OptMacros.flatten[A, B]

    def fold[B](ifEmpty: => B)(f: A => B): B = macro OptMacros.fold[A, B]

    def getOrElse[B >: A](ifEmpty: => B): B = macro OptMacros.getOrElse[A, B]

    def orNull[B >: A](implicit ev: Null <:< B): B = macro OptMacros.orNull[A, B]

    def iterator: Iterator[A] = macro OptMacros.iterator[A]

    def toOption: Option[A] = if (ref == null) None else Some(ref)

    def toList: List[A] = if (ref == null) Nil else (ref :: Nil)

    def collect[B](pf: PartialFunction[A, B]): Opt[B] =
      if (ref == null) Opt.empty[B]
      else if (pf.isDefinedAt(ref)) Opt(pf(ref))
      else Opt.empty[B]

    def contains[A1 >: A](elem: A1): Boolean = if (ref == null) false else ref == elem

    def exists(p: A => Boolean): Boolean = if (ref == null) false else p(ref)

    def forall(p: A => Boolean): Boolean = if (ref == null) true else p(ref)

    def foreach[U](f: A => U): Unit = if (ref != null) f(ref)

    def toRight[X](left: => X): Either[X, A] =
      if (ref == null) Left(left) else Right(ref)

    def toLeft[X](right: => X): Either[A, X] =
      if (ref == null) Right(right) else Left(ref)

  }

  implicit def OptMethods[A](o: Opt[A]): OptMethods[A] = new OptMethods[A](o)

}
