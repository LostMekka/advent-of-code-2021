infix fun <T> T.shouldBe(other: T) {
    check(this == other) { "check failed: $this != $other" }
}
