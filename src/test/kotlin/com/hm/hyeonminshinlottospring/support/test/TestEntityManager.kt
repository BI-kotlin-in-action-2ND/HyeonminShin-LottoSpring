package com.hm.hyeonminshinlottospring.support.test

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

fun TestEntityManager.flushAndClear() {
    flush()
    clear()
}
