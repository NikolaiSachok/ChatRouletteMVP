package com.video.chat.v.love.chatroulette.utils.comparator

open class IdEntity(open val id: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IdEntity
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id
    }
}