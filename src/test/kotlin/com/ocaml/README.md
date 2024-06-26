# Testing Reference

Tests for IDE Features

|              | Status | Note                    |
|--------------|--------|-------------------------|
| Annotator    | ✅      | LET/VAL/TYPE colors     |
| Colors       | N/A    |                         |
| Commenter    | ✅      | Line and block comments |
| Files        | N/A    |                         |
| Highlight    | N/A    |                         |
| LineMarkers  | ✅      | Implemented/implements  |
| Presentation | ✅      | Tested Let/Val          |
| Settings     | N/A    |                         |
| Spelling     | N/A    |                         |
| Structure    | ✅      | Test Let/Val/Type       |
| Template     | ❌      | Test context            |
| Typing       | N/A    |                         |

Tests for Parser

|      | Parser | Mixin | Stubs | Indexes |
|------|--------|-------|-------|---------|
| File | N/A    | N/A   | N/A   | ❌       |
| Let  | ❌      | ✅     | ✅     | ✅       |
| Val  | ❌      | ❌     | ❌     | ❌       |
| Type | ❌      | ❌     | ❌     | ❌       |

Notes

* `val _ : unit`: invalid
* `val _0 : unit`: valid