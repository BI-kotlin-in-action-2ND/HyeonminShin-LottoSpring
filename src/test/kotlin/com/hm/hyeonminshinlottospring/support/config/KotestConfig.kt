package com.hm.hyeonminshinlottospring.support.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

/**
 * [https://effortguy.tistory.com/475]
 *
 * # KotestConfig
 *
 * kotest를 사용해서 spring의 @Transactional 애노테이션을 사용할 때,
 * 롤백(rollback)이 제대로 되지 않아 사용하는 설정.
 *
 * 해당 설정을 사용하게 되면 `SpringTestLifecycleMode.Test`,
 * 즉 가장 아래의 nested가 아니라 큰 단위(예. DescribeSpec에서 describe) 기준으로 묶여서
 * 트랜잭션이 발생한다.
 *
 * 결국 이 설정은 테스트마다 트랜잭션 롤백을 적용시키려 하는 것이다.
 *
 * ## 주의사항
 *
 * 위 내용을 이해했다면 해당 테스트 내에서 사용할 각 전역 변수들의 초기화를 롤백하기 위해서는
 * `val`을 통해 선언할게 아니라 `lateinit var`로 설정한 뒤 `beforeEach` 함수 내에서 초기화해주어야 한다!
 */

class KotestConfig : AbstractProjectConfig() {
    // 트랜잭션 롤백 단위 설정
    override fun extensions(): List<Extension> = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

    // coroutine 테스트 환경 및 디버그 설정
    override val coroutineTestScope = true
    override val coroutineDebugProbes = true
}
