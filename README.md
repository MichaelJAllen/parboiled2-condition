# parboiled2-condition
Test code to experiement with conditional fields in parboiled2. This code relates to a [question on StackOverflow](https://stackoverflow.com/questions/48897117/parboiled2-how-to-process-dependent-fields).

This test parser attempts to read records of the following two forms:

    boolean [integer] quoted-string

such that the _integer_ field is only present if the _Boolean_ field is `true`; if the _Boolean_ field is `false`, then the _integer_ field (and its preceeding whitespace) will not be present and should not be parsed.

For example, the following are valid records:

    true 51 "This is some text"
    false "This is also some text"

However, while the parser makes the correct decisions (accepting valid input and rejecting invalid input), it seems to do so for the wrong reasons. The parser was run against the following inputs (see `Main.scala`):

    true 52 "Should be Success with Some 52"
    false "Should be Success with None"
    false 25 "Should be Failure as 25 not a quoted string"
    true ha "Should be Failure as ha not an integer"
    true 9874923489234234898234823748374 "Should be Failure as number not a valid integer"

Here's the output (use `sbt run` from the project's root directory):

    Success! Values read are Some(52), Should be Success with Some 52
    Success! Values read are None, Should be Success with None
    Failure: Invalid input "false 2", expected dependentFields, ws or quotedString (line 1, column 1):
    false 25 "Should be Failure as 25 not a quoted string"
    ^
    Failure: Invalid input 'h', expected wsChar or intField (line 1, column 6):
    true ha "Should be Failure as ha not an integer"
         ^
    Failure: Invalid input ' ', expected digit or test (line 1, column 37):
    true 9874923489234234898234823748374 "Should be Failure as number not a valid integer"
                                        ^
So, the parser correctly accepts the first two records, and correctly rejects the following three. However, look at the reasons for the failures:

1. The third record is rejected because the input `false 2` was encountered when it expected `dependentFields, ws or quotedString`. This seems wrong, to me. It should have accepted `false` and rejected `2` as input and claimed to have expected `ws or quotedString` only - the `dependentFields` record should have succeeded and should have matched the initial `false` record.
1. The fourth record is rejected correctly because `ha` is not an integer. This is fine.
1. The fifth record is rejected correctly because the large integer value cannot be converted into an `Int`. This is fine, but the explanation is misleading, because the cursor is at the end of the integer, instead of at the start.

I'm particularly interested in knowing why the third record was rejected the way that it was, while the second record was accepted. Also, on the `master` branch of this code, the `conditional` rule uses a `~!~` cut operator to separate the `test` action from the execution of the supplied rule. On the `nocut` branch, I changed this to a `~` operator - but the results are the same. Why? Am I doing something wrong? What am I missing?

If you have answers, you can reach me at mike at hindsight consulting dot com.
