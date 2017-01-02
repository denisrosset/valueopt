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

    def toOption: Option[A] = macro OptMacros.toOption[A]

    def toList: List[A] = macro OptMacros.toList[A]

    def collect[B](pf: PartialFunction[A, B]): Opt[B] = macro OptMacros.collect[A, B]

    def contains[A1 >: A](elem: A1): Boolean = macro OptMacros.contains[A, A1]

    def exists(p: A => Boolean): Boolean = macro OptMacros.exists[A]

    def forall(p: A => Boolean): Boolean = macro OptMacros.forall[A]

    def foreach[U](f: A => U): Unit = macro OptMacros.foreach[A, U]

    def toRight[X](left: => X): Either[X, A] = macro OptMacros.toRight[A, X]

    def toLeft[X](right: => X): Either[A, X] = macro OptMacros.toLeft[A, X]

  }

  implicit def OptMethods[A](o: Opt[A]): OptMethods[A] = new OptMethods[A](o)

}