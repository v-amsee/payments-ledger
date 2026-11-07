# ADR-001: Money type

Decision: Store money as a long in minor units (pence) plus an ISO 4217 currency code.

Rejected:
- double: binary floating point can't represent most decimal fractions exactly;
  errors accumulate and transactions stop summing to zero.
- BigDecimal (for storage): exact, but verbose and slower (object per value),
  and storage never needs fractions of a penny.

Consequences:
- The API converts at the edge: "12.50" in → 1250 internally → "12.50" out.
- double/float are banned anywhere money flows.
- Arithmetic uses Math.addExact so overflow fails loudly.
- Calculations needing fractions (interest, FX) use BigDecimal, then round
  once, with an explicit rounding mode, back to long.
- Currencies differ in minor units (JPY has 0, GBP has 2): out of scope for now, GBP only.