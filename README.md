## Opt - AnyVal Option-like type

Opt is a utility library that provides a fast Option-like type.

The differences of `Opt` with the standard library `Option` are:

- `Opt` to replace `Option` in hot code paths,

- `Opt` cannot store `null`; `null` is conflated with the `Opt.empty` case,

- `Opt` is not designed to be nested, nor to play well as part of a higher-kinded type,

- `Opt` always allocates less memory than `Option`. It is a value class that erases to AnyRef when storing reference types; for primitive types, it stores their boxed versions, but `Opt` itself does not add any supplemental boxing,

- pattern matching on `Opt` is provided using name-based extractors (to avoid boxing during pattern match), while `Option` is an algebraic datatype represented by `sealed Option` and the final `Some` and `None` types,

- some of the helper methods are implemented using macros to avoid closure allocation,

- `Opt` boxes on Scala 2.10 due to the implementation of forwarder methods.

### Caveats

Code is offered as-is, with no implied warranty of any kind. Comments,
criticisms, and/or praise are welcome.

Copyright 2015-2017 Rüdiger Klaehn, Erik Osheim, Denis Rosset, Tom Switzer

A full list of contributors can be found in [AUTHORS.md](AUTHORS.md).

The MIT software license is attached in the [COPYING](COPYING) file.
