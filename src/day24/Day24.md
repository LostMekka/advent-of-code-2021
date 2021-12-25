I solved this puzzle by hand, since I needed to analyze the input extensively to make sense of the task.
After analyzing for way too long, I almost saw the solution, and it didn't make sense to start coding anymore``.

This is what it came down to:
The given program consists of 14 blocks with 3 variable parameters each.
Each block does the following:
```
x = z % 26 + b != i                    
// x=1 if condition is true, x=0 otherwise
z = z / a * (25 * x + 1) + (i + c) * x
```

- `i` is the input for this block. (each block reads one input digit)
- `a`, `b` and `c` are the variable parameters of the block.
  - `a` is only either `1` or `26`
  - if `a==1`, then `b<0`; if `a==26`, then `b>=10`
  - `c` is always positive or zero

This means, there are two kinds of blocks:
- a multiply block `(a==1`) does `z = z * 26 + i + c`,
- and a reducing block (`a==26`) that does `z = Z / 26`, but only, if the condition for `x` evaluates to false. If The condition fails to evaluate to false, the block becomes a multiply block.

There is one more reducing block than there are multiplying blocks, so every reducing block is needed, to get the accumulator `z` to zero at the end. The Conditions restrain the input digits, as the inputs are dependent on each other.

My puzzle input had the following parameters:
```
            a    b   c
block 1:    1   10   2
block 2:    1   15  16
block 3:    1   14   9
block 4:    1   15   0
block 5:   26   -8   1
block 6:    1   10  12
block 7:   26  -16   6
block 8:   26   -4   6
block 9:    1   11   3
block 10:  26   -3   5
block 11:   1   12   9
block 12:  26   -7   3
block 13:  26  -15   2
block 14:  26   -7   3
```

With a bit of reducing, this is what my MONAD program did:
```
z1 = i1 + 2
z2 = z1 * 26 + i2 + 16
z3 = z2 * 26 + i3 + 9
z4 = z3 * 26 + i4
z5 = z4 / 26 = z3        (i5 == z4%26-8 == i4+0-8 == i4-8)
z6 = z5 * 26 + i6 + 12
z7 = z6 / 26 = z3        (i7 == z6%26-16 == i6+12-16 == i6-4)
z8 = z7 / 26 = z2        (i8 == z3%26-4 == i3+9-4 == i3+5)
z9 = z8 * 26 + i9 + 3
z10 = z9 / 26 = z2       (i10 == z9%26-3 == i9+3-3 == i9)
z11 = z10 * 26 + i11 + 9
z12 = z11 / 26 = z2      (i12 == z11%26-7 == i11+9-7 == i11+2)
z13 = z12 / 26 = z1      (i13 == z2%26-15 == i2+16-15 == i2+1)
z14 = z13 / 26 = 0       (i14 == z1%26-7 == i1+2-7 == i1-5)
```

And with that, I could get the complete constraints on the input digits:
```
i1  -> 6..9 (i14 + 5)
i2  -> 1..8 (i13 - 1)
i3  -> 1..4 (i8  - 5)
i4  -> 9..9 (i5  + 8)
i5  -> 1..1 (i4  - 8)
i6  -> 5..9 (i7  + 4)
i7  -> 1..5 (i6  - 4)
i8  -> 6..9 (i3  + 5)
i9  -> 1..9 (i10 + 0)
i10 -> 1..9 (19  + 0)
i11 -> 1..7 (i12 - 2)
i12 -> 3..9 (i11 + 2)
i13 -> 2..9 (i2  + 1)
i14 -> 1..4 (i1  - 5)
```

This means the solutions for my puzzle input were:
- `98491959997994` as the largest valid input number
- `61191516111321` as the smallest valid input number
