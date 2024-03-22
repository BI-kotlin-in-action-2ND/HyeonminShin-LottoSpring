package com.hm.hyeonminshinlottospring.domain.lotto.domain.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.SortedSet

@Converter
class LottoNumberAttributeConverter : AttributeConverter<SortedSet<Int>, String> {
    override fun convertToDatabaseColumn(entityValue: SortedSet<Int>?): String? {
        return entityValue?.joinToString(",")
    }

    override fun convertToEntityAttribute(databaseValue: String?): SortedSet<Int>? {
        return databaseValue?.split(",")?.map { it.toInt() }?.toSortedSet()
    }
}
