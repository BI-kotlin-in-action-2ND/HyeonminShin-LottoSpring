package com.hm.hyeonminshinlottospring.domain.user.domain

import com.hm.hyeonminshinlottospring.domain.lotto.domain.info.LottoPrice
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Table(name = "users")
@SequenceGenerator(
    name = "USER_SEQ_GENERATOR",
    sequenceName = "USER_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
@Entity
class User(
    // 현재는 유저가 이름으로 구분되어지게끔 만들었습니다. 이후 로그인 ID로 구분하면 좋을것 같습니다.
    @Column(nullable = false, updatable = false, unique = true)
    val userName: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val userRole: UserRole = UserRole.ROLE_USER,
    money: Int = 0,
    @Id
    @Column(columnDefinition = "NUMERIC(19, 0)")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ_GENERATOR")
    val id: Long = 0L,
) {
    @Column(nullable = false)
    var money: Int = money
        protected set

    fun addMoney(addMoney: Int): Int {
        require(addMoney > 0) { "0원 이상의 금액을 입력해주세요." }
        money += addMoney
        return money
    }

    fun withdrawMoney(withdrawMoney: Int): Int {
        require(withdrawMoney > 0) { "출금할 값을 양수로 입력해주세요." }
        require(withdrawMoney <= money) { "${this.id}: 현재 소지한 금액($money${LottoPrice.UNIT})보다 적은 금액을 입력해주세요." }
        money -= withdrawMoney
        return money
    }
}
