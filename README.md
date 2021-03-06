## Opt - AnyVal Option-like type

Opt is a utility library that provides a fast Option-like type.

The differences of `Opt` with the standard library `Option` are:

- `Opt` is designed to replace `Option` in hot code paths, or for library methods designed to be used in hot code paths.

- `Opt` is not fully referentially transparent: the `get` method throws a `NullPointerException` 

- After the project leaves beta status, the core module, including the structure of the `Opt` value class itself will not change, guaranteeing binary compatibility. 

- Methods such as `getOrElse`, `fold`, ... are implemented using macros, and thus produce efficient code that avoids passing closures.

- `Opt` *can* store `null` by using a trick.

- `Opt` always allocates less memory than `Option`. It is a value class that erases to a reference type when storing reference types; for primitive types, it stores their boxed versions and erases to a reference type as well. In constrast, `Some[Int]` allocates an `Some` instance containing a boxed `Int` (which could lead or not to an additional allocation).

- Pattern matching on `Opt` is provided using name-based extractors, to avoid boxing during pattern match (while `Option` is an algebraic datatype represented by `sealed Option` and the final `Some` and `None` types). Code example:

```scala

def testPatternMatch: Unit = {

	val nonEmptyOpt: Opt[Int] = Opt(2)
	val emptyOpt: Opt[Int] = Opt.none[Int] // or OptNone. Opt.none[A] is of type Opt[A]
	
    nonEmptyOpt match {
      case OptSome(x) => println("Success!")
      case OptNone => ??? // is not taken
    }
	
    emptyOpt match {
      case OptSome(x) => ??? // is not taken
      case OptNone => println("Success!")
    }
	
}
```

### Comparable projects

- [scala-unboxed-option](https://github.com/sjrd/scala-unboxed-option) is our main source of inspiration

- Most complete alternative, including specialized versions for primitive types: [arosenberger/nalloc](https://github.com/arosenberger/nalloc)

- [xuwei-k/opt](https://github.com/xuwei-k/opt)

- [rklaehn/valueclassoption](https://github.com/rklaehn/valueclassoption)

### Caveats

Code is offered as-is, with no implied warranty of any kind. Comments,
criticisms, and/or praise are welcome.

Copyright 2015-2017 Rüdiger Klaehn, Erik Osheim, Denis Rosset, Tom Switzer

A full list of contributors can be found in [AUTHORS.md](AUTHORS.md).

The MIT software license is attached in the [COPYING](COPYING) file.
